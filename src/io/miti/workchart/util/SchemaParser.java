package io.miti.workchart.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import io.miti.workchart.model.Node;

public class SchemaParser extends DefaultHandler
{
  private List<Node> nodes = new ArrayList<Node>(5);
  private Node node = null;
  
  private boolean inURL = false;
  private boolean inID = false;
  private boolean inPW = false;
  
  
  public SchemaParser()
  {
    super();
  }
  
  
  public List<Node> getSchemas()
  {
    InputStream is = Content.getFileStream("schema.xml");
    
    // Parse the file
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = null;
    try
    {
      // Parse the file using this instance of this class
      parser = factory.newSAXParser();
      parser.parse(is, this);
    }
    catch (ParserConfigurationException e)
    {
      e.printStackTrace();
      return null;
    }
    catch (SAXException e)
    {
      e.printStackTrace();
      return null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
    
    // Close the input stream
    try
    {
      is.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return nodes;
  }
  
//  public static void main(final String[] args)
//  {
//    // Parse the XML file
//    try
//    {
//      SAXParserFactory factory = SAXParserFactory.newInstance();
//      SAXParser saxParser = factory.newSAXParser();
//      SchemaParser handler = new SchemaParser();
//      saxParser.parse("schema.xml", handler);
//      
//      // Print the list, for debugging
//      for (Node n : handler.nodes)
//      {
//        System.out.println(n.toString());
//      }
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//    }
//  }
  
  
  @Override
  public void startElement(final String uri, final String localName,
                           final String qName, final Attributes attributes)
        throws SAXException
  {
    // System.out.println("Start Element: " + qName);
    
    if (qName.equalsIgnoreCase("schema"))
    {
      String id = attributes.getValue("id");
      node = new Node();
      node.setSchema(id);
    }
    else if (qName.equalsIgnoreCase("node"))
    {
      String id = attributes.getValue("id");
      node.setNode(id);
    }
    else if (qName.equalsIgnoreCase("url"))
    {
      inURL = true;
    }
    else if (qName.equalsIgnoreCase("id"))
    {
      inID = true;
    }
    else if (qName.equalsIgnoreCase("pw"))
    {
      inPW = true;
    }
  }
  
  
  @Override
  public void endElement(final String uri, final String localName,
                         final String qName)
       throws SAXException
  {
      // System.out.println("End Element: " + qName);
      
      if (qName.equalsIgnoreCase("node"))
      {
        Node nodeObject = new Node(node);
        nodes.add(nodeObject);
      }
      else if (qName.equalsIgnoreCase("url"))
      {
        inURL = false;
      }
      else if (qName.equalsIgnoreCase("id"))
      {
        inID = false;
      }
      else if (qName.equalsIgnoreCase("pw"))
      {
        inPW = false;
      }
  }
  
  
  @Override
  public void characters(final char ch[], final int start, final int length)
    throws SAXException
  {
    // Get the value
    final String val = new String(ch, start, length).trim();
    
//    if (val.length() > 0)
//    {
//      System.out.println("Characters=>" + val + "<==");
//    }
    
    if (inURL)
    {
      node.setUrl(val);
    }
    else if (inID)
    {
      node.setId(val);
    }
    else if (inPW)
    {
      node.setPw(val);
    }
  }
}
