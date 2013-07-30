package org.mapper.dynamic.xml;


import java.io.File;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//Java����������������IO���� 
//Java���������������ֱ�׼���ݽṹ���� 
//XML�������ӿ� 
//XML��DOMʵ�� 

/**
 * XML�ļ��ϲ�������

 * @author GhostFromHeaven
 */
public class XMLMergeUtil {
    
    /**
     * XML�ļ��ĺϲ�����
     * @param mainFileName ���ϲ������xml�ļ����ϲ��󽫸��´��ļ�
     * @param subFilename ���ϲ���xml�ļ�
     * @return �ϲ��ɹ�����true�����򷵻�false
     * @throws Exception
     */
    public static boolean isMerging(String mainFileName, String subFilename)
            throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce); // �����쳣ʱ������쳣��Ϣ
        }
        Document doc_main = null;
        Document doc_vice = null;
        // ��ȡ����XML�ļ���Document
        try {
            doc_main = db.parse(mainFileName);
            doc_vice = db.parse(subFilename);
        } catch (DOMException dom) {
            System.err.println(dom.getMessage());
        } catch (Exception ioe) {
            System.err.println(ioe);
        }
        // ��ȡ�����ļ��ĸ��ڵ�
        
        Element root_main = doc_main.getDocumentElement();
        Element root_vice = doc_vice.getDocumentElement();
        // ������ӱ��ϲ��ļ����ڵ��µ�ÿ���ڵ�
        NodeList messageItems = root_vice.getChildNodes();
        int item_number = messageItems.getLength();
        // ���ȥ�����ڵ��µĵ�һ���ڵ㣬��ôi��3��ʼ������i��1��ʼ
        for (int i = 1; i < item_number; i = i + 2) {
            // ����dupliate()�����θ��Ʊ��ϲ�XML�ĵ��и��ڵ��µĽڵ�
            Element messageItem = (Element) messageItems.item(i);
            dupliate(doc_main, root_main, messageItem);
        }
        // ���� write To()�����ϲ��õ���Documentд��Ŀ��XML�ĵ�
        boolean isWritten = writeTo(doc_main, mainFileName);
        return isWritten;
    }
    
    /**
     *
     * @param doc_dup
     * @param father
     * @param son
     * @return
     * @throws Exception
     */
    private static boolean dupliate(Document doc_dup, Element father, Element son)
            throws Exception {
        boolean isdone = false;
        Element parentElement = null;
        
        DuplicateChildElementObject childElementObject = isChildElement(father, son);
        if(!childElementObject.isNeedDuplicate()){
            //�ڵ���ͬ���úϲ�
            isdone = true;
            parentElement = childElementObject.getElement();
        }else if(childElementObject.getElement() != null){
            parentElement = childElementObject.getElement();
        }else{
            parentElement = father;
        }
        
        String son_name = son.getNodeName();
        Element subITEM = null;
        if(!isdone){
            subITEM = doc_dup.createElement(son_name);
            // ���ƽڵ������
            if (son.hasAttributes()) {
                NamedNodeMap attributes = son.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    String attribute_name = attributes.item(i).getNodeName();
                    String attribute_value = attributes.item(i).getNodeValue();
                    subITEM.setAttribute(attribute_name, attribute_value);
                }
            }
            parentElement.appendChild(subITEM);
        }
        else{
            subITEM = parentElement;
        }
        
        // �����ӽ��
        NodeList sub_messageItems = son.getChildNodes();
        int sub_item_number = sub_messageItems.getLength();
        if (sub_item_number < 2) {
            // ���û���ӽڵ�,�򷵻�
            isdone = true;
        } else {
            for (int j = 1; j < sub_item_number; j = j + 2) {
                // ������ӽڵ�,��ݹ���ñ�����
                Element sub_messageItem = (Element) sub_messageItems.item(j);
                isdone = dupliate(doc_dup, subITEM, sub_messageItem);
            }
        }
        
        
        return isdone;
    }

    private static boolean writeTo(Document doc, String fileName) throws Exception {
        boolean isOver = false;
        DOMSource doms = new DOMSource(doc);
        File f = new File(fileName);
        StreamResult sr = new StreamResult(f);
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            Properties properties = t.getOutputProperties();
            properties.setProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperties(properties);
            t.transform(doms, sr);
            isOver = true;
        } catch (TransformerConfigurationException tce) {
            tce.printStackTrace();
        } catch (TransformerException te) {
            te.printStackTrace();
        }
        return isOver;
    }
    
    private static DuplicateChildElementObject isChildElement(Element father, Element son){
        
        DuplicateChildElementObject  childElementObject = new DuplicateChildElementObject();
        
        NodeList messageItems = father.getChildNodes();
        int item_number = messageItems.getLength();
        //���ȱ������нڵ㣬�����Ƿ�����ȫ��ͬ�Ľڵ㣬��ֹͬһ�ڵ��Ѷ�����
        for (int i = 1; i < item_number; i = i + 2) {
            Element messageItem = (Element) messageItems.item(i);
            if(!messageItem.getNodeName().equals(son.getNodeName())){
                continue;
            }
            if(messageItem.isEqualNode(son)){//ͬʱ�ж��ӽڵ��Ƿ�һ��
                childElementObject.setNeedDuplicate(false);
                childElementObject.setElement(messageItem);
                return childElementObject;
            }
        }
        for (int i = 1; i < item_number; i = i + 2) {
            Element messageItem = (Element) messageItems.item(i);
            //�жϽڵ��Ƿ���ͬһ����
            if(!messageItem.getNodeName().equals(son.getNodeName())){
                continue;
            }
            if(isEqualNode(messageItem,son)){//���жϵ�ǰ�ڵ��Ƿ�һ��
                if(hasEqualAttributes(messageItem,son)){//��ǰ�ڵ���ȫ��ͬ����Ҫ�ϲ�
                    childElementObject.setNeedDuplicate(false);
                    childElementObject.setElement(messageItem);
                    return childElementObject;
                }else{//��ǰ�ڵ�����Բ���ͬ����Ҫ�ϲ�
                    childElementObject.setNeedDuplicate(true);
                    childElementObject.setElement(father);
                    return childElementObject;
                }
            }    
        }
        //Ŀ���ĵ��ýڵ㲻���ڣ���Ҫ�ϲ���Ŀ���ĵ���
        childElementObject.setNeedDuplicate(true);
        childElementObject.setElement(father);
        return childElementObject;
    }
    
    /**
     * �ж������ڵ��Ƿ���ͬ��δ�жϽڵ������
     * @param arg0
     * @param arg
     * @return
     */
    private static boolean isEqualNode(Node arg0,Node arg) {
        if (arg == arg0) {
            return true;
        }
        if (arg.getNodeType() != arg0.getNodeType()) {
            return false;
        }

        if (arg0.getNodeName() == null) {
            if (arg.getNodeName() != null) {
                return false;
            }
        } else if (!arg0.getNodeName().equals(arg.getNodeName())) {
            return false;
        }

        if (arg0.getLocalName() == null) {
            if (arg.getLocalName() != null) {
                return false;
            }
        } else if (!arg0.getLocalName().equals(arg.getLocalName())) {
            return false;
        }

        if (arg0.getNamespaceURI() == null) {
            if (arg.getNamespaceURI() != null) {
                return false;
            }
        } else if (!arg0.getNamespaceURI().equals(arg.getNamespaceURI())) {
            return false;
        }

        if (arg0.getPrefix() == null) {
            if (arg.getPrefix() != null) {
                return false;
            }
        } else if (!arg0.getPrefix().equals(arg.getPrefix())) {
            return false;
        }

        if (arg0.getNodeValue() == null) {
            if (arg.getNodeValue() != null) {
                return false;
            }
        } else if (!arg0.getNodeValue().equals(arg.getNodeValue())) {
            return false;
        }
        return true;
    }
    
    /**
     * �жϽڵ�������Ƿ���ͬ
     * @param arg0
     * @param arg
     * @return
     */
    private static boolean hasEqualAttributes(Node arg0,Node arg) {
        
        NamedNodeMap map1 = arg0.getAttributes();
        NamedNodeMap map2 = arg.getAttributes();
        int len = map1.getLength();
        if (len != map2.getLength()) {
            return false;
        }
        
         for (int i = 0; i < len; i++) {
             Node n1 = map1.item(i);
             if(n1.getNodeName() != null){
                  Node n2 = map2.getNamedItem(n1.getNodeName());
                  if(n2 == null){
                      return false;
                  }else if(!n1.getNodeValue().equals(n2.getNodeValue())){
                      return false;
                  }
             }
         }
         return true;
    }
    public static void main(String[] args) {
        try {

            String sourcefile = "d:/a.xml"; 
            String targetfile = "d:/b.xml";
            
            boolean isdone = XMLMergeUtil.isMerging(sourcefile, targetfile);
            
            if (isdone)
                System.out.println("XML files have been merged.");
            else
                System.out.println("XML files have NOT been merged.");
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * �����ӽڵ����
 * @author Administrator
 *
 */
class DuplicateChildElementObject{
    private boolean needDuplicate = true;//��¼�ýڵ��Ƿ���Ҫ����
    private Element element = null;//��¼�ýڵ�ĸ��ڵ�

    public DuplicateChildElementObject() {
        super();
    }

    public boolean isNeedDuplicate() {
        return needDuplicate;
    }

    public void setNeedDuplicate(boolean needDuplicate) {
        this.needDuplicate = needDuplicate;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
