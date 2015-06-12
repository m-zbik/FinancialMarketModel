package com.heatonresearch.httprecipes.spider;

import java.io.*;
import java.net.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * SpiderReportable: This interface defines a class that the
 * spider can report its findings to.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public interface SpiderReportable {

  /**
   * The types of link that can be encountered.
   */
  public enum URLType {
    HYPERLINK, IMAGE, SCRIPT, STYLE
  }

  /**
   * This function is called when the spider is ready to
   * process a new host.
   * 
   * @param host
   *          The new host that is about to be processed.
   * @return True if this host should be processed, false
   *         otherwise.
   */
  public boolean beginHost(String host);

  /**
   * Called when the spider is starting up. This method
   * provides the SpiderReportable class with the spider
   * object.
   * 
   * @param spider
   *          The spider that will be working with this
   *          object.
   */
  public void init(Spider spider);

  /**
   * Called when the spider encounters a URL.
   * 
   * @param url
   *          The URL that the spider found.
   * @param source
   *          The page that the URL was found on.
   * @param type
   *          The type of link this URL is.
   * @return True if the spider should scan for links on
   *         this page.
   */
  public boolean spiderFoundURL(URL url, URL source, URLType type);

  /**
   * Called when the spider is about to process a NON-HTML
   * URL.
   * 
   * @param url
   *          The URL that the spider found.
   * @param stream
   *          An InputStream to read the page contents from.
   * @throws IOException
   *           Thrown if an IO error occurs while processing
   *           the page.
   */
  public void spiderProcessURL(URL url, InputStream stream) 
      throws IOException;

  /**
   * Called when the spider is ready to process an HTML
   * URL.
   * 
   * @param url
   *          The URL that the spider is about to process.
   * @param parse
   *          An object that will allow you you to parse the
   *          HTML on this page.
   * @throws IOException
   *           Thrown if an IO error occurs while processing
   *           the page.
   */
  public void spiderProcessURL(URL url, SpiderParseHTML parse)
      throws IOException;

  /**
   * Called when the spider tries to process a URL but gets
   * an error.
   * 
   * @param url
   *          The URL that generated an error.
   */
  public void spiderURLError(URL url);

}