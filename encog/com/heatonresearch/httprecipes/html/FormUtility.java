package com.heatonresearch.httprecipes.html;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Heaton Research Spider Copyright 2007 by Heaton
 * Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * FormUtility: This class is used to construct responses to
 * HTML forms. The class supports both standard HTML forms,
 * as well as multipart forms.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class FormUtility
{
  /*
   * The charset to use for URL encoding. Per URL coding
   * spec, this value should always be UTF-8.
   */
  private final static String encode = "UTF-8";

  /*
   * A Java random number generator.
   */
  private static Random random = new Random();

  /**
   * Generate a boundary for a multipart form.
   * 
   * @return The boundary.
   */
  public static String getBoundary()
  {
    return "---------------------------" + randomString() + randomString()
        + randomString();
  }

  /**
   * Parse a URL query string. Return a map of all of the
   * name value pairs.
   * 
   * @param form
   *          The query string to parse.
   * @return A map of name-value pairs.
   */
  public static Map<String, String> parse(String form)
  {
    Map<String, String> result = new HashMap<String, String>();
    StringTokenizer tok = new StringTokenizer(form, "&");
    while (tok.hasMoreTokens())
    {
      String str = tok.nextToken();
      StringTokenizer tok2 = new StringTokenizer(str, "=");
      if (!tok2.hasMoreTokens())
      {
        continue;
      }
      String left = tok2.nextToken();
      if (!tok2.hasMoreTokens())
      {
        left = encode(left);
        result.put(left, null);
        continue;
      }
      String right = tok2.nextToken();
      right = encode(right);
      result.put(left, right);
    }
    return result;
  }

  /**
   * Encode the specified string. This encodes all special
   * characters.
   * 
   * @param str
   *          The string to encode.
   * @return The encoded string.
   */
  private static String encode(String str)
  {
    try
    {
      return URLEncoder.encode(str, encode);
    } catch (UnsupportedEncodingException e)
    {
      return str;
    }
  }

  /**
   * Generate a random string, of a specified length. This
   * is used to generate the multipart boundary.
   * 
   * @return A random string.
   */
  protected static String randomString()
  {
    return Long.toString(random.nextLong(), 36);
  }

  /*
   * The boundary used for a multipart post. This field is
   * null if this is not a multipart form and has a value if
   * this is a multipart form.
   */
  private String boundary;

  /*
   * The stream to output the encoded form to.
   */
  private OutputStream os;

  /*
   * Keep track of if we're on the first form element.
   */
  private boolean first;

  /**
   * Prepare to access either a regular, or multipart, form.
   * 
   * @param os
   *          The stream to output to.
   * @param boundary
   *          The boundary to be used, or null if this is
   *          not a multipart form.
   */
  public FormUtility(OutputStream os, String boundary)
  {
    this.os = os;
    this.boundary = boundary;
  }

  /**
   * Add a file to a multipart form.
   * 
   * @param name
   *          The field name.
   * @param file
   *          The file to attach.
   * @throws IOException
   *           If any error occurs while writing.
   */
  public void add(String name, File file) throws IOException
  {
    if (this.boundary != null)
    {
      boundary();
      writeName(name);
      write("; filename=\"");
      write(file.getName());
      write("\"");
      newline();
      write("Content-Type: ");
      String type = URLConnection.guessContentTypeFromName(file.getName());
      if (type == null)
      {
        type = "application/octet-stream";
      }
      writeln(type);
      newline();

      byte[] buf = new byte[8192];
      int nread;

      InputStream in = new FileInputStream(file);
      while ((nread = in.read(buf, 0, buf.length)) >= 0)
      {
        this.os.write(buf, 0, nread);
      }

      newline();
    }
  }

  /**
   * Add a regular text field to either a regular or
   * multipart form.
   * 
   * @param name
   *          The name of the field.
   * @param value
   *          The value of the field.
   * @throws IOException
   *           If any error occurs while writing.
   */
  public void add(String name, String value) throws IOException
  {
    if (this.boundary != null)
    {
      boundary();
      writeName(name);
      newline();
      newline();
      writeln(value);
    } else
    {
      if (!this.first)
      {
        write("&");
      }
      write(encode(name));
      write("=");
      write(encode(value));
    }
    this.first = false;
  }

  /**
   * Complete the building of the form.
   * 
   * @throws IOException
   *           If any error occurs while writing.
   */
  public void complete() throws IOException
  {
    if (this.boundary != null)
    {
      boundary();
      writeln("--");
      this.os.flush();
    }
  }

  /**
   * Generate a multipart form boundary.
   * 
   * @throws IOException
   *           If any error occurs while writing.
   */
  private void boundary() throws IOException
  {
    write("--");
    write(this.boundary);
  }

  /**
   * Create a new line by displaying a carriage return and
   * linefeed.
   * 
   * @throws IOException
   *           If any error occurs while writing.
   */
  private void newline() throws IOException
  {
    write("\r\n");
  }

  /**
   * Write the specified string, without a carriage return
   * and line feed.
   * 
   * @param str
   *          The String to write.
   * @throws IOException
   *           If any error occurs while writing.
   */
  private void write(String str) throws IOException
  {
    this.os.write(str.getBytes());
  }

  /**
   * Write the name element for a multipart post.
   * 
   * @param name
   *          The name of the field.
   * @throws IOException
   *           If any error occurs while writing.
   */
  private void writeName(String name) throws IOException
  {
    newline();
    write("Content-Disposition: form-data; name=\"");
    write(name);
    write("\"");
  }

  /**
   * Write a string, with a carriage return and linefeed.
   * 
   * @param str
   *          The string to write.
   * @throws IOException
   *           If any error occurs while writing.
   */
  protected void writeln(String str) throws IOException
  {
    write(str);
    newline();
  }

}
