package com.heatonresearch.httprecipes.html;

import java.io.*;
import java.util.*;

/**
 * The Heaton Research Spider Copyright 2007 by Heaton
 * Research, Inc.
 * 
 * HTTP Programming Recipes for Java ISBN: 0-9773206-6-9
 * http://www.heatonresearch.com/articles/series/16/
 * 
 * ParseHTML: This is the class that actually parses the
 * HTML and outputs HTMLTag objects and raw text.
 * 
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 * 
 * @author Jeff Heaton
 * @version 1.1
 */
public class ParseHTML
{

  /*
   * A mapping of certain HTML encoded values(i.e. &nbsp;)
   * to their actual character values.
   */
  private static Map<String, Character> charMap;

  /**
   * The stream that we are parsing from.
   */
  private PeekableInputStream source;

  /**
   * The current HTML tag. Access this property if the read
   * function returns 0.
   */
  private HTMLTag tag = new HTMLTag();

  private String lockedEndTag;

  /**
   * The constructor should be passed an InputStream that we
   * will parse from.
   * 
   * @param is
   *          An InputStream to parse from.
   */
  public ParseHTML(InputStream is)
  {
    this.source = new PeekableInputStream(is);

    if (charMap == null)
    {
      charMap = new HashMap<String, Character>();
      charMap.put("nbsp", ' ');
      charMap.put("lt", '<');
      charMap.put("gt", '>');
      charMap.put("amp", '&');
      charMap.put("quot", '\"');
      charMap.put("bull", (char) 149);
      charMap.put("trade", (char) 129);
    }
  }

  /**
   * Return the last tag found, this is normally called just
   * after the read function returns a zero.
   * 
   * @return The last HTML tag found.
   */
  public HTMLTag getTag()
  {
    return this.tag;
  }

  /**
   * Read a single character from the HTML source, if this
   * function returns zero(0) then you should call getTag to
   * see what tag was found. Otherwise the value returned is
   * simply the next character found.
   * 
   * @return The character read, or zero if there is an HTML
   *         tag. If zero is returned, then call getTag to
   *         get the next tag.
   * 
   * @throws IOException
   *           If an error occurs while reading.
   */
  public int read() throws IOException
  {
    // handle locked end tag
    if (this.lockedEndTag != null)
    {
      if (peekEndTag(this.lockedEndTag))
      {
        this.lockedEndTag = null;
      } else
      {
        return this.source.read();
      }
    }

    // look for next tag
    if (this.source.peek() == '<')
    {
      parseTag();
      if (!this.tag.isEnding()
          && (this.tag.getName().equalsIgnoreCase("script") || this.tag
              .getName().equalsIgnoreCase("style")))
      {
        this.lockedEndTag = this.tag.getName().toLowerCase();
      }
      return 0;
    } else if (this.source.peek() == '&')
    {
      return parseSpecialCharacter();
    } else
    {
      return (this.source.read());
    }
  }

  /**
   * Convert the HTML document back to a string.
   */
  @Override
  public String toString()
  {
    try
    {
      StringBuilder result = new StringBuilder();

      int ch = 0;
      StringBuilder text = new StringBuilder();
      do
      {
        ch = read();
        if (ch == 0)
        {
          if (text.length() > 0)
          {
            System.out.println("Text:" + text.toString());
            text.setLength(0);
          }
          System.out.println("Tag:" + getTag());
        } else if (ch != -1)
        {
          text.append((char) ch);
        }
      } while (ch != -1);
      if (text.length() > 0)
      {
        System.out.println("Text:" + text.toString().trim());
      }
      return result.toString();
    } catch (IOException e)
    {
      return "[IO Error]";
    }
  }

  /**
   * Parse any special characters(i.e. &nbsp);
   * 
   * @return The character that was parsed.
   * @throws IOException
   *           If a read error occurs
   */
  private char parseSpecialCharacter() throws IOException
  {
    char result = (char) this.source.read();
    int advanceBy = 0;

    // is there a special character?
    if (result == '&')
    {
      int ch = 0;
      StringBuilder buffer = new StringBuilder();

      // loop through and read special character
      do
      {
        ch = this.source.peek(advanceBy++);
        if ((ch != '&') && (ch != ';') && !Character.isWhitespace(ch))
        {
          buffer.append((char) ch);
        }

      } while ((ch != ';') && (ch != -1) && !Character.isWhitespace(ch));

      String b = buffer.toString().trim().toLowerCase();

      // did we find a special character?
      if (b.length() > 0)
      {
        if (b.charAt(0) == '#')
        {
          try
          {
            result = (char) Integer.parseInt(b.substring(1));
          } catch (NumberFormatException e)
          {
            advanceBy = 0;
          }
        } else
        {
          if (charMap.containsKey(b))
          {
            result = charMap.get(b);
          } else
          {
            advanceBy = 0;
          }
        }
      } else
      {
        advanceBy = 0;
      }
    }

    while (advanceBy > 0)
    {
      read();
      advanceBy--;
    }

    return result;
  }

  /**
   * Check to see if the ending tag is present.
   * @param name The type of end tag being saught.
   * @return True if the ending tag was found.
   * @throws IOException Thrown if an IO error occurs.
   */
  private boolean peekEndTag(String name) throws IOException
  {
    int i = 0;

    // pass any whitespace
    while ((this.source.peek(i) != -1)
        && Character.isWhitespace(this.source.peek(i)))
    {
      i++;
    }

    // is a tag beginning
    if (this.source.peek(i) != '<')
    {
      return false;
    } else
    {
      i++;
    }

    // pass any whitespace
    while ((this.source.peek(i) != -1)
        && Character.isWhitespace(this.source.peek(i)))
    {
      i++;
    }

    // is it an end tag
    if (this.source.peek(i) != '/')
    {
      return false;
    } else
    {
      i++;
    }

    // pass any whitespace
    while ((this.source.peek(i) != -1)
        && Character.isWhitespace(this.source.peek(i)))
    {
      i++;
    }

    // does the name match
    for (int j = 0; j < name.length(); j++)
    {
      if (Character.toLowerCase(this.source.peek(i)) != Character
          .toLowerCase(name.charAt(j)))
      {
        return false;
      }
      i++;
    }

    return true;
  }

  /**
   * Remove any whitespace characters that are next in the
   * InputStream.
   * 
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected void eatWhitespace() throws IOException
  {
    while (Character.isWhitespace((char) this.source.peek()))
    {
      this.source.read();
    }
  }

  /**
   * Parse an attribute name, if one is present.
   * 
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected String parseAttributeName() throws IOException
  {
    eatWhitespace();

    if ("\"\'".indexOf(this.source.peek()) == -1)
    {
      StringBuilder buffer = new StringBuilder();
      while (!Character.isWhitespace(this.source.peek())
          && (this.source.peek() != '=') && (this.source.peek() != '>')
          && (this.source.peek() != -1))
      {
        int ch = parseSpecialCharacter();
        buffer.append((char) ch);
      }
      return buffer.toString();
    } else
    {
      return (parseString());
    }
  }

  /**
   * Called to parse a double or single quote string.
   * 
   * @return The string parsed.
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected String parseString() throws IOException
  {
    StringBuilder result = new StringBuilder();
    eatWhitespace();
    if ("\"\'".indexOf(this.source.peek()) != -1)
    {
      int delim = this.source.read();
      while ((this.source.peek() != delim) && (this.source.peek() != -1))
      {
        if (result.length() > 1000)
        {
          break;
        }
        int ch = parseSpecialCharacter();
        if ((ch == 13) || (ch == 10))
        {
          continue;
        }
        result.append((char) ch);
      }
      if ("\"\'".indexOf(this.source.peek()) != -1)
      {
        this.source.read();
      }
    } else
    {
      while (!Character.isWhitespace(this.source.peek())
          && (this.source.peek() != -1) && (this.source.peek() != '>'))
      {
        result.append(parseSpecialCharacter());
      }
    }

    return result.toString();
  }

  /**
   * Called when a tag is detected. This method will parse
   * the tag.
   * 
   * @throws IOException
   *           If an I/O exception occurs.
   */
  protected void parseTag() throws IOException
  {
    this.tag.clear();
    StringBuilder tagName = new StringBuilder();

    this.source.read();

    // Is it a comment?
    if ((this.source.peek(0) == '!') && (this.source.peek(1) == '-')
        && (this.source.peek(2) == '-'))
    {
      while (this.source.peek() != -1)
      {
        if ((this.source.peek(0) == '-') && (this.source.peek(1) == '-')
            && (this.source.peek(2) == '>'))
        {
          break;
        }
        if (this.source.peek() != '\r')
        {
          tagName.append((char) this.source.peek());
        }
        this.source.read();
      }
      tagName.append("--");
      this.source.read();
      this.source.read();
      this.source.read();
      return;
    }

    // Find the tag name
    while (this.source.peek() != -1)
    {
      if (Character.isWhitespace((char) this.source.peek())
          || (this.source.peek() == '>'))
      {
        break;
      }
      tagName.append((char) this.source.read());
    }

    eatWhitespace();
    this.tag.setName(tagName.toString());

    // get the attributes

    while ((this.source.peek() != '>') && (this.source.peek() != -1))
    {
      String attributeName = parseAttributeName();
      String attributeValue = null;

      if (attributeName.equals("/"))
      {
        eatWhitespace();
        if (this.source.peek() == '>')
        {
          this.tag.setEnding(true);
          break;
        }
      }

      // is there a value?
      eatWhitespace();
      if (this.source.peek() == '=')
      {
        this.source.read();
        attributeValue = parseString();
      }

      this.tag.setAttribute(attributeName, attributeValue);
    }
    this.source.read();
  }

}
