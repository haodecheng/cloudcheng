package org.mapper.dynamic.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class DomCdata {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String xmls = "<?xml version=\"1.0\" encoding=\"gb2312\"?><root><![CDATA[¼ÎÇìÄê¼ä]]></root>";
        String testXml="<test>haocheng</test>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        Document doc;

        try {
            db = dbf.newDocumentBuilder();
            InputStream x = new ByteArrayInputStream(xmls.getBytes());
           
            doc = db.parse(x);
            Element root = doc.getDocumentElement();
            System.out.print(root.getFirstChild().getNodeValue());
            System.out.print(x.toString());

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
