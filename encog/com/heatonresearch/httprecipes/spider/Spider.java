package com.heatonresearch.httprecipes.spider;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import com.heatonresearch.httprecipes.spider.filter.*;
import com.heatonresearch.httprecipes.spider.workload.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * Spider: This is the main class that implements the Heaton
 * Research Spider.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class Spider
{
  /**
   * The logger.
   */
  private static Logger logger = Logger
      .getLogger("com.heatonresearch.httprecipes.spider.Spider");

  /**
   * The object that the spider reports its findings to.
   */
  private SpiderReportable report;

  /**
   * A flag that indicates if this process should be
   * canceled.
   */
  private boolean cancel = false;

  /**
   * The workload manager, the spider can use any of several
   * different workload managers. The workload manager
   * tracks all URL's found.
   */
  private WorkloadManager workloadManager;

  /**
   * The Java thread executor that will manage the thread
   * pool.
   */
  private ThreadPoolExecutor threadPool;

  /*
   * The BlockingQueue that will hold tasks for the thread
   * pool.
   */
  private BlockingQueue<Runnable> tasks;

  /**
   * The options for the spider.
   */
  private SpiderOptions options;

  /**
   * Filters used to block specific URL's.
   */
  private List<SpiderFilter> filters = new ArrayList<SpiderFilter>();

  /**
   * The time that the spider began.
   */
  private Date startTime;

  /**
   * The time that the spider ended.
   */
  private Date stopTime;

  /**
   * Construct a spider object. The options parameter
   * specifies the options for this spider. The report
   * parameter specifies the class that the spider is to
   * report progress to.
   * 
   * @param report
   *          A class that implements the SpiderReportable
   *          interface, that will receive information that
   *          the spider finds.
   * @param timeout
   *          How many miliseconds to wait for data.
   * @throws ClassNotFoundException
   *           Thrown if an error occurs while creating the
   *           workload manager.
   * @throws IllegalAccessException
   *           Thrown if an error occurs while creating the
   *           workload manager.
   * @throws InstantiationException
   *           Thrown if an error occurs while creating the
   *           workload manager.
   * @throws WorkloadException
   *           Exception thrown if there are any issues with
   *           the workload.
   */
  public Spider(SpiderOptions options, SpiderReportable report)
      throws InstantiationException, IllegalAccessException,
      ClassNotFoundException, WorkloadException
  {
    this.options = options;
    this.report = report;

    this.workloadManager = (WorkloadManager) Class.forName(
        options.workloadManager).newInstance();
    this.workloadManager.init(this);
    report.init(this);

    this.tasks = new SynchronousQueue<Runnable>();
    this.threadPool = new ThreadPoolExecutor(options.corePoolSize,
        options.maximumPoolSize, options.keepAliveTime, TimeUnit.SECONDS,
        this.tasks);
    this.threadPool
        .setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

    // add filters
    if (options.filter != null)
    {
      for (String name : options.filter)
      {
        SpiderFilter filter = (SpiderFilter) Class.forName(name).newInstance();
        this.filters.add(filter);
      }
    }

    // perform startup
    if (options.startup.equalsIgnoreCase(SpiderOptions.STARTUP_RESUME))
    {
      this.workloadManager.resume();
    } else
    {
      this.workloadManager.clear();
    }
  }

  /**
   * Add a URL for processing. Accepts a SpiderURL.
   * 
   * @param url
   * @throws WorkloadException
   *           Exception thrown if there are any issues with
   *           the workload.
   */
  public void addURL(URL url, URL source, int depth) throws WorkloadException
  {
    // check the depth
    if ((this.options.maxDepth != -1) && (depth > this.options.maxDepth))
    {
      return;
    }

    // see if it does not pass any of the filters
    for (SpiderFilter filter : this.filters)
    {
      if (filter.isExcluded(url))
      {
        return;
      }
    }

    // add the item
    if (this.workloadManager.add(url, source, depth))
    {
      StringBuilder str = new StringBuilder();
      str.append("Adding to workload: ");
      str.append(url);
      str.append("(depth=");
      str.append(depth);
      str.append(")");
      logger.fine(str.toString());
    }
  }

  /**
   * Set a flag that will cause the begin method to return
   * before it is done.
   */
  public void cancel()
  {
    this.cancel = true;
  }

  /**
   * Get the list of filters for the spider.
   * 
   * @return The list of filters for the spider.
   */
  public List<SpiderFilter> getFilters()
  {
    return this.filters;
  }

  /**
   * Get the options for this spider.
   * 
   * @return The options for this spider.
   */
  public SpiderOptions getOptions()
  {
    return this.options;
  }

  /**
   * Get the object that the spider reports to.
   * 
   * @return The object that spider reports to.
   */
  public SpiderReportable getReport()
  {
    return this.report;
  }

  /**
   * Generate basic status information about the spider.
   * 
   * @return The status of the spider.
   */
  public String getStatus()
  {
    StringBuilder result = new StringBuilder();
    result.append("Start time:");
    result.append(this.startTime.toString());
    result.append('\n');
    result.append("Stop time:");
    result.append(this.stopTime.toString());
    result.append('\n');
    result.append("Minutes Elapsed:");
    result.append((this.stopTime.getTime() - this.startTime.getTime()) / 60000);
    result.append('\n');

    return result.toString();
  }

  /**
   * Get the workload manager.
   * 
   * @return The workload manager.
   */
  public WorkloadManager getWorkloadManager()
  {
    return this.workloadManager;
  }

  /**
   * Called to start the spider.
   * 
   * @throws WorkloadException
   *           Exception thrown if there are any issues with
   *           the workload.
   * @throws InterruptedException
   *           Called if any blocking operation is
   *           interrupted.
   */
  public void process() throws WorkloadException
  {
    this.cancel = false;
    this.startTime = new Date();

    // process all hosts
    do
    {
      processHost();
    } while (this.workloadManager.nextHost() != null);

    this.threadPool.shutdown();
    this.stopTime = new Date();
  }

  public void setReport(SpiderReportable report)
  {
    this.report = report;
  }

  /**
   * Process one individual host.
   * 
   * @throws WorkloadException
   *           Exception thrown if there are any issues with
   *           the workload.
   */
  private void processHost() throws WorkloadException
  {
    URL url = null;

    String host = this.workloadManager.getCurrentHost();

    // first notify the manager
    if (!this.report.beginHost(host))
    {
      return;
    }

    // second, notify any filters of a new host
    for (SpiderFilter filter : this.filters)
    {
      try
      {
        filter.newHost(host, this.options.userAgent);
      } catch (IOException e)
      {
        logger.log(Level.INFO, "Error while reading robots.txt file:"
            + e.getMessage());
      }
    }

    // now process this host
    do
    {
      url = this.workloadManager.getWork();
      if (url != null)
      {
        SpiderWorker worker = new SpiderWorker(this, url);
        this.threadPool.execute(worker);
      } else
      {
        this.workloadManager.waitForWork(60, TimeUnit.SECONDS);
      }
    } while (((url != null) || (this.threadPool.getActiveCount() > 0))
        && !this.cancel);
  }

}