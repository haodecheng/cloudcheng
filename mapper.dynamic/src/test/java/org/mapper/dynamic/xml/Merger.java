/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.mapper.dynamic.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * 类Merger.java的实现描述：TODO 类实现描述 
 * @author decheng.haodch Jul 29, 2013 2:55:37 PM
 */
public class Merger {
    private static final QName prodName = new QName("product");
    private static final QName pidName = new QName("pid");
    public static void main(String[] args)
       throws FileNotFoundException, XMLStreamException {
          
       // Use  the reference implementation for the  XML input factory
//       System.setProperty(
//          "javax.xml.stream.XMLInputFactory",
//          "com.bea.xml.stream.MXParserFactory");
       // Create the XML input factory
       XMLInputFactory factory = XMLInputFactory.newInstance();
       // Create XML event reader 1
       XMLEventReader r1 = 
          factory.createXMLEventReader(new FileReader("prodList1.xml"));
       // Create XML event reader 2
       XMLEventReader r2 = 
          factory.createXMLEventReader(new FileReader("prodList2.xml"));
       // Create the output factory
       XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
       // Create XML event writer
       XMLEventWriter xmlw = xmlof.createXMLEventWriter(System.out);
       // Read to first <product> element in document 1
       // and output to result document
       String pid1 = readToNextElement(r1, xmlw, false);
       // Read to first <product> element in document 1
       // without writing to result document
       String pid2 = readToNextElement(r2, null, false);
       // Loop over both XML input streams
       while (pid1 != null || pid2 != null) {
          // Compare merge criteria
          if (pid2 == null || (pid1 != null && pid1.compareTo(pid2) <= 0))
             // Continue in document 1
             pid1 = readToNextElement(r1, xmlw, pid2 == null);
          else
             // Continue in document 2
             pid2 = readToNextElement(r2, xmlw, pid1 == null);
       }
       xmlw.close();
    }
    /**
     * @param reader - the document reader
     * @param writer - the document writer
     * @param processEnd - forces the document end to be written
     * @return - the next merge criterion value
     * @throws XMLStreamException
     */
    private static String readToNextElement(XMLEventReader reader,
          XMLEventWriter writer, boolean processEnd) throws XMLStreamException {
       // Nesting level
       int level = 0;
       while (true) {
          // Read event to be written to result document
          XMLEvent event = reader.nextEvent();
          // Avoid double processing of document end
          if (!processEnd)
             switch (event.getEventType()) {
                case XMLEvent.START_ELEMENT :
                   ++level;
                   break;
                case XMLEvent.END_ELEMENT :
                   if (--level < 0)
                      return null;
                   break;
             }
          // Output event
          if (writer != null)
             writer.add(event);
          // Look at next event
          event = reader.peek();
          switch (event.getEventType()) {
             case XMLEvent.START_ELEMENT :
                // Start element - stop at <product> element
                QName name = event.asStartElement().getName();
                if (name.equals(prodName)) {
                   return event
                      .asStartElement()
                      .getAttributeByName(pidName)
                      .getValue();
                }
                break;
             case XMLEvent.END_DOCUMENT :
                // Stop at end of document
                return null;
          }
       }
    }
}
