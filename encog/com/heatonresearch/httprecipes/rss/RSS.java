package com.heatonresearch.httprecipes.rss;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * The Heaton Research Spider 
 * Copyright 2007 by Heaton Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * RSS: This is the class that actually parses the 
 * RSS and builds a collection of RSSItems.  To make use
 * of this class call the load method with a URL that
 * points to RSS.
 *
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 *
 * @author Jeff Heaton
 * @version 1.1
 */
public class RSS
{
  /*
   * All of the attributes for this RSS document.
   */
  private Map<String, String> attributes = new HashMap<String, String>();

  /*
   * All RSS items, or stories, found.
   */
  private List<RSSItem> items = new ArrayList<RSSItem>();

  /**
   * Simple utility function that converts a RSS formatted date
   * into a Java date.
   * @param datestr The RSS formatted date.
   * @return A Java java.util.date
   */
  public static Date parseDate(String datestr)
  {
    try
    {
      DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
      Date date = (Date) formatter.parse(datestr);
      return date;
    } catch (Exception e)
    {
      return null;
    }
  }

  /**
   * Load the specified RSS item, or story.
   * @param item A XML node that contains a RSS item.
   */
  private void loadItem(Node item)
  {
    RSSItem rssItem = new RSSItem();
    rssItem.load(item);
    items.add(rssItem);
  }

  /**
   * Load the channle node.
   * @param channel A node that contains a channel.
   */
  private void loadChannel(Node channel)
  {
    NodeList nl = channel.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++)
    {
      Node node = nl.item(i);
      String nodename = node.getNodeName();
      if (nodename.equalsIgnoreCase("item"))
      {
        loadItem(node);
      } else
      {
        if (node.getNodeType() != Node.TEXT_NODE)
          attributes.put(nodename, RSS.getXMLText(node));
      }
    }
  }

  /**
   * Load all RSS data from the specified URL.
   * @param url URL that contains XML data.
   * @throws IOException Thrown if an IO error occurs.
   * @throws SAXException Thrown if there is an error while parsing XML.
   * @throws ParserConfigurationException Thrown if there is an XML 
   * parse config error.
   */
  public void load(URL url) throws IOException, SAXException,
      ParserConfigurationException
  {
    URLConnection http = url.openConnection();
    InputStream is = http.getInputStream();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    Document d = factory.newDocumentBuilder().parse(is);

    Element e = d.getDocumentElement();
    NodeList nl = e.getChildNodes();
    for (int i = 0; i < nl.getLength(); i++)
    {
      Node node = nl.item(i);
      String nodename = node.getNodeName();

      // RSS 2.0
      if (nodename.equalsIgnoreCase("channel"))
      {
        loadChannel(node);
      }
      // RSS 1.0
      else if (nodename.equalsIgnoreCase("item"))
      {
        loadItem(node);
      }
    }

  }

  /**
   * Simple utility method that obtains the text of an XML 
   * node.
   * @param n The XML node.
   * @return The text of the specified XML node.
   */
  public static String getXMLText(Node n)
  {
    NodeList list = n.getChildNodes();
    for (int i = 0; i < list.getLength(); i++)
    {
      Node n2 = list.item(i);
      if (n2.getNodeType() == Node.TEXT_NODE)
        return n2.getNodeValue();
    }
    return null;
  }

  /**
   * Get the list of attributes.
   * @return the attributes
   */
  public Map<String, String> getAttributes()
  {
    return attributes;
  }

  /**
   * Convert the object to a String.
   * @return The object as a String.
   */
  public String toString()
  {
    StringBuilder str = new StringBuilder();
    Set<String> set = attributes.keySet();
    for (String item : set)
    {
      str.append(item);
      str.append('=');
      str.append(attributes.get(item));
      str.append('\n');
    }
    str.append("Items:\n");
    for (RSSItem item : items)
    {
      str.append(item.toString());
      str.append('\n');
    }
    return str.toString();
  }

}
