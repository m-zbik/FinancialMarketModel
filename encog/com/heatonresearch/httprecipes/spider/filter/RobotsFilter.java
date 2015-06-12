package com.heatonresearch.httprecipes.spider.filter;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * RobotsFilter: This filter causes the spider to skip URL's
 * from a robots.txt file.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class RobotsFilter implements SpiderFilter
{
  /**
   * The full URL of the robots.txt file.
   */
  private URL robotURL;

  /**
   * A list of URL's to exclude.
   */
  private List<String> exclude = new ArrayList<String>();

  /*
   * Is the parser active? It can become inactive when
   * parsing sections of the file for other user agents.
   */
  private boolean active;

  /*
   * The user agent string we are to use, null for default.
   */
  private String userAgent;

  /**
   * Returns a list of URL's to be excluded.
   * 
   * @return A vector of URL's to be excluded.
   */
  public List<String> getExclude()
  {
    return this.exclude;
  }

  /**
   * Returns the full URL of the robots.txt file.
   * 
   * @return The full URL of the robots.txt file.
   */

  public URL getRobotFile()
  {
    return this.robotURL;
  }

  /**
   * Check to see if the specified URL is to be excluded.
   * 
   * @param url
   *          The URL to be checked.
   * @return Returns true if the URL should be excluded.
   */
  public boolean isExcluded(URL url)
  {
    for (String str : this.exclude)
    {
      if (url.getFile().startsWith(str))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Called when a new host is to be processed. Hosts 
   * are processed one at a time.  SpiderFilter classes 
   * can not be shared among hosts.
   * 
   * @param host
   *          The new host.
   * @param userAgent
   *          The user agent being used by the spider. Leave
   *          null for default.
   * @throws IOException
   *           Thrown if an I/O error occurs.
   */
  public void newHost(String host, String userAgent) throws IOException
  {
    String str;
    this.active = false;
    this.userAgent = userAgent;

    this.robotURL = new URL("http", host, 80, "/robots.txt");

    URLConnection http = this.robotURL.openConnection();

    if (userAgent != null)
    {
      http.setRequestProperty("User-Agent", userAgent);
    }

    InputStream is = http.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader r = new BufferedReader(isr);

    exclude.clear();

    try
    {
      while ((str = r.readLine()) != null)
      {

        loadLine(str);
      }
    } finally
    {
      r.close();
      isr.close();
    }
  }

  /**
   * Add the specified URL to the exclude list.
   * @param str The URL to add.
   */
  private void add(String str)
  {
    if (!this.exclude.contains(str))
    {
      this.exclude.add(str);
    }
  }

  /**
   * Called internally to process each line of the
   * robots.txt file.
   * 
   * @param str
   *          The line that was read in.
   * @throws MalformedURLException
   *           Thrown if a bad URL is found.
   */
  private void loadLine(String str) throws MalformedURLException
  {
    str = str.trim();
    int i = str.indexOf(':');

    if ((str.length() == 0) || (str.charAt(0) == '#') || (i == -1))
    {
      return;
    }

    String command = str.substring(0, i);
    String rest = str.substring(i + 1).trim();
    if (command.equalsIgnoreCase("User-agent"))
    {
      this.active = false;
      if (rest.equals("*"))
      {
        this.active = true;
      } else
      {
        if ((this.userAgent != null) && rest.equalsIgnoreCase(this.userAgent))
        {
          this.active = true;
        }
      }
    }
    if (this.active)
    {
      if (command.equalsIgnoreCase("disallow"))
      {
        if (rest.trim().length() > 0)
        {
          URL url = new URL(this.robotURL, rest);
          add(url.getFile());
        }
      }
    }
  }
}
