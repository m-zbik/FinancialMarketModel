package com.heatonresearch.httprecipes.spider.workload;

import java.net.*;
import java.util.concurrent.*;

import com.heatonresearch.httprecipes.spider.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * WorkloadManager: This interface defines a workload
 * manager. A workload manager handles the lists of URLs
 * that have been processed, resulted in an error, and 
 * are waiting to be processed.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public interface WorkloadManager {
  /**
   * Add the specified URL to the workload.
   * 
   * @param url
   *          The URL to be added.
   * @param source
   *          The page that contains this URL.
   * @param depth
   *          The depth of this URL.
   * @return True if the URL was added, false otherwise.
   * @throws WorkloadException
   */
  public boolean add(URL url, URL source, int depth) throws WorkloadException;

  /**
   * Clear the workload.
   * 
   * @throws WorkloadException
   *           An error prevented the workload from being
   *           cleared.
   */
  public void clear() throws WorkloadException;

  /**
   * Determine if the workload contains the specified URL.
   * 
   * @param url
   * @return
   * @throws WorkloadException
   */
  public boolean contains(URL url) throws WorkloadException;

  /**
   * Convert the specified String to a URL. If the string is
   * too long or has other issues, throw a
   * WorkloadException.
   * 
   * @param url
   *          A String to convert into a URL.
   * @return The URL.
   * @throws WorkloadException
   *           Thrown if, The String could not be 
   *           converted.
   */
  public URL convertURL(String url) throws WorkloadException;

  /**
   * Get the current host.
   * 
   * @return The current host.
   */
  public String getCurrentHost();

  /**
   * Get the depth of the specified URL.
   * 
   * @param url
   *          The URL to get the depth of.
   * @return The depth of the specified URL.
   * @throws WorkloadException
   *           Thrown if the depth could not be found.
   */
  public int getDepth(URL url) throws WorkloadException;

  /**
   * Get the source page that contains the specified URL.
   * 
   * @param url
   *          The URL to seek the source for.
   * @return The source of the specified URL.
   * @throws WorkloadException
   *           Thrown if the source of the specified URL
   *           could not be found.
   */
  public URL getSource(URL url) throws WorkloadException;

  /**
   * Get a new URL to work on. Wait if there are no URL's
   * currently available. Return null if done with the
   * current host. The URL being returned will be marked as
   * in progress.
   * 
   * @return The next URL to work on,
   * @throws WorkloadException
   *           Thrown if the next URL could not be obtained.
   */
  public URL getWork() throws WorkloadException;

  /**
   * Setup this workload manager for the specified spider.
   * 
   * @param spider
   *          The spider using this workload manager.
   * @throws WorkloadException
   *           Thrown if there is an error setting up the
   *           workload manager.
   */
  public void init(Spider spider) throws WorkloadException;

  /**
   * Mark the specified URL as error.
   * 
   * @param url
   *          The URL that had an error.
   * @throws WorkloadException
   *           Thrown if the specified URL could not be
   *           marked.
   */
  public void markError(URL url) throws WorkloadException;

  /**
   * Mark the specified URL as successfully processed.
   * 
   * @param url
   *          The URL to mark as processed.
   * @throws WorkloadException
   *           Thrown if the specified URL could not be
   *           marked.
   */
  public void markProcessed(URL url) throws WorkloadException;

  /**
   * Move on to process the next host. This should only be
   * called after getWork returns null.
   * 
   * @return The name of the next host.
   * @throws WorkloadException
   *           Thrown if the workload manager was unable to
   *           move to the next host.
   */
  public String nextHost() throws WorkloadException;

  /**
   * Setup the workload so that it can be resumed from where
   * the last spider left the workload.
   * 
   * @throws WorkloadException
   *           Thrown if we were unable to resume the
   *           processing.
   */
  public void resume() throws WorkloadException;

  /**
   * If there is currently no work available, then wait
   * until a new URL has been added to the workload.
   * 
   * @param time
   *          The amount of time to wait.
   * @param length
   *          What time unit is being used.
   */
  public void waitForWork(int time, TimeUnit length);

  /**
   * Return true if there are no more workload units.
   * 
   * @return Returns true if there are no more workload
   *         units.
   * @throws WorkloadException
   *           Thrown if there was an error determining if
   *           the workload is empty.
   */
  public boolean workloadEmpty() throws WorkloadException;

}
