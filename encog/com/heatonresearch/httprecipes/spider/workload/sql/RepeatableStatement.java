package com.heatonresearch.httprecipes.spider.workload.sql;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import com.heatonresearch.httprecipes.spider.workload.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * RepeatableStatement: This class implements a repeatable
 * statement. A repeatable statement is a regular
 * PreparedStatement that can be repeated if the connection
 * fails.
 * 
 * Additionally, the repeatable statement maintains a cache
 * of PreparedStatement objects for the threads. This
 * prevents two threads from using the same
 * PreparedStatement at the same time. To obtain a
 * PreparedStatement a thread should call the
 * obtainStatement function. Once the thread no longer needs
 * the statement, the releaseStatement method should be
 * called.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class RepeatableStatement
{

  /**
   * Simple internal class that holds the ResultSet from a
   * query.
   * 
   * @author jeff
   * 
   */
  public class Results
  {

    /**
     * The PreparedStatement that generated these results.
     */
    private PreparedStatement statement;

    /**
     * The ResultSet that was generated.
     */
    private ResultSet resultSet;

    /**
     * Construct a Results object.
     * 
     * @param statement
     *          The PreparedStatement for these results.
     * @param resultSet
     *          The ResultSet.
     */
    public Results(PreparedStatement statement, ResultSet resultSet)
    {
      this.statement = statement;
      this.resultSet = resultSet;
    }

    /**
     * Close the ResultSet.
     */
    public void close()
    {
      try
      {
        this.resultSet.close();
      } catch (SQLException e)
      {
        logger.log(Level.SEVERE, "Failed to close ResultSet", e);
      }
      releaseStatement(this.statement);
    }

    /**
     * Get the ResultSet.
     * 
     * @return The ResultSet.
     */
    public ResultSet getResultSet()
    {
      return this.resultSet;
    }
  }

  /**
   * The logger.
   */
  private static Logger logger = Logger
      .getLogger("com.heatonresearch.httprecipes.spider.workload.sql.RepeatableStatement");

  /**
   * The SQLWorkloadManager that created this object.
   */
  private SQLWorkloadManager manager;

  /**
   * The SQL for this statement.
   */
  private String sql;

  /**
   * A mutex to make sure that only one thread at a time is
   * in the process of getting a PreparedStatement assigned.
   * More than one thread at a time can have a
   * PreparedStatement, however only one can be in the
   * obtainStatement function at a time.
   */
  private Semaphore mutex;

  /**
   * The PreparedStatements that are assigned to each
   * thread.
   */
  private List<PreparedStatement> statementCache = new ArrayList<PreparedStatement>();

  /**
   * Construct a repeatable statement based on the
   * specified SQL 
   * @param sql The SQL to base this statement
   * on.
   */
  public RepeatableStatement(String sql)
  {
    this.sql = sql;
    this.mutex = new Semaphore(1);
  }

  /*
   * Close the statement.
   */
  public void close()
  {
    try
    {
      try
      {
        this.mutex.acquire();
      } catch (InterruptedException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      for (PreparedStatement statement : this.statementCache)
      {
        try
        {
          statement.close();
        } catch (SQLException e)
        {
          logger.log(Level.SEVERE, "Failed to close PreparedStatement", e);
        }
      }
    } finally
    {
      this.mutex.release();
    }
  }

  /**
   * Create the statement, so that it is ready to assign
   * PreparedStatements.
   * 
   * @param manager
   *          The manager that created this statement.
   * @throws SQLException
   *           Thrown if an exception occurs.
   */
  public void create(SQLWorkloadManager manager) throws SQLException
  {
    close();
    this.manager = manager;
  }

  /**
   * Execute SQL that does not return a result set. If an
   * error occurs, the statement will be retried until it is
   * successful. This handles broken connections.
   * 
   * @param parameters
   *          The parameters for this SQL.
   * @throws WorkloadException
   *           Thrown if the SQL cannot be executed, and
   *           retrying the statement has failed.
   */
  public void execute(Object... parameters) throws WorkloadException
  {
    PreparedStatement statement = null;

    try
    {
      statement = obtainStatement();

      for (;;)
      {
        try
        {
          for (int i = 0; i < parameters.length; i++)
          {
            if (parameters[i] == null)
            {
              statement.setNull(i, Types.INTEGER);
            } else
            {
              statement.setObject(i + 1, parameters[i]);
            }
          }
          long time = System.currentTimeMillis();
          statement.execute();
          time = System.currentTimeMillis() - time;

          return;
        } catch (SQLException e)
        {
          logger.log(Level.SEVERE, "SQL Exception", e);
          if (!(e.getCause() instanceof SQLException))
          {
            this.manager.tryOpen();
          } else
          {
            throw (new WorkloadException(e));
          }
        }
      }
    } catch (SQLException e)
    {
      throw (new WorkloadException(e));
    } finally
    {
      if (statement != null)
      {
        releaseStatement(statement);
      }
    }
  }

  /**
   * Execute an SQL query that returns a result set. If an
   * error occurs, the statement will be retried until it is
   * successful. This handles broken connections.
   * 
   * @param parameters
   *          The parameters for this SQL.
   * @return The results of the query.
   * @throws WorkloadException
   *           Thrown if the SQL cannot be executed, and
   *           retrying the statement has failed.
   */
  public Results executeQuery(Object... parameters) throws WorkloadException
  {

    for (;;)
    {
      try
      {
        PreparedStatement statement = obtainStatement();

        for (int i = 0; i < parameters.length; i++)
        {
          statement.setObject(i + 1, parameters[i]);
        }
        long time = System.currentTimeMillis();
        ResultSet rs = statement.executeQuery();
        time = System.currentTimeMillis() - time;
        // System.out.println( time + ":" + sql);
        return (new Results(statement, rs));
      } catch (SQLException e)
      {
        logger.log(Level.SEVERE, "SQL Exception", e);
        if (!(e.getCause() instanceof SQLException))
        {
          this.manager.tryOpen();
        } else
        {
          throw (new WorkloadException(e));
        }
      }
    }

  }

  /**
   * Obtain a statement. Each thread should use their own
   * statement, and then call the releaseStatement method
   * when they are done.
   * 
   * @return A PreparedStatement object.
   * @throws SQLException
   *           Thrown if the statement could not be
   *           obtained.
   */
  private PreparedStatement obtainStatement() throws SQLException
  {
    PreparedStatement result = null;

    try
    {
      this.mutex.acquire();
      if (this.statementCache.size() == 0)
      {
        result = this.manager.getConnection().prepareStatement(this.sql);
      } else
      {
        result = this.statementCache.get(0);
        this.statementCache.remove(0);
      }

    } catch (InterruptedException e)
    {
      return null;
    } finally
    {
      this.mutex.release();
    }

    return result;
  }

  /**
   * This method releases statements after the thread is
   * done with them. These statements are not closed, but
   * rather cached until another thread has need of them.
   * 
   * @param stmt
   *          The statement that is to be released.
   */
  private void releaseStatement(PreparedStatement stmt)
  {
    try
    {
      try
      {
        this.mutex.acquire();
      } catch (InterruptedException e)
      {
        return;
      }
      this.statementCache.add(stmt);
    } finally
    {
      this.mutex.release();
    }
  }
}
