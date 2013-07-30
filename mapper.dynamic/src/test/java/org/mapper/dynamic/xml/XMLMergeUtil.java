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
//Java基础包，包含各种IO操作 
//Java基础包，包含各种标准数据结构操作 
//XML解析器接口 
//XML的DOM实现 

/**
 * XML文件合并工具类

 * @author GhostFromHeaven
 */
public class XMLMergeUtil {
    
    /**
     * XML文件的合并处理
     * @param mainFileName 待合并处理的xml文件，合并后将更新此文件
     * @param subFilename 被合并的xml文件
     * @return 合并成功返回true，否则返回false
     * @throws Exception
     */
    public static boolean isMerging(String mainFileName, String subFilename)
            throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce); // 出现异常时，输出异常信息
        }
        Document doc_main = null;
        Document doc_vice = null;
        // 获取两个XML文件的Document
        try {
            doc_main = db.parse(mainFileName);
            doc_vice = db.parse(subFilename);
        } catch (DOMException dom) {
            System.err.println(dom.getMessage());
        } catch (Exception ioe) {
            System.err.println(ioe);
        }
        // 获取两个文件的根节点
        
        Element root_main = doc_main.getDocumentElement();
        Element root_vice = doc_vice.getDocumentElement();
        // 下面添加被合并文件根节点下的每个节点
        NodeList messageItems = root_vice.getChildNodes();
        int item_number = messageItems.getLength();
        // 如果去掉根节点下的第一个节点，那么i从3开始，否则i从1开始
        for (int i = 1; i < item_number; i = i + 2) {
            // 调用dupliate()，依次复制被合并XML文档中根节点下的节点
            Element messageItem = (Element) messageItems.item(i);
            dupliate(doc_main, root_main, messageItem);
        }
        // 调用 write To()，将合并得到的Document写入目标XML文档
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
            //节点相同不用合并
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
            // 复制节点的属性
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
        
        // 复制子结点
        NodeList sub_messageItems = son.getChildNodes();
        int sub_item_number = sub_messageItems.getLength();
        if (sub_item_number < 2) {
            // 如果没有子节点,则返回
            isdone = true;
        } else {
            for (int j = 1; j < sub_item_number; j = j + 2) {
                // 如果有子节点,则递归调用本方法
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
        //首先遍历所有节点，查找是否有完全相同的节点，防止同一节点已定义多次
        for (int i = 1; i < item_number; i = i + 2) {
            Element messageItem = (Element) messageItems.item(i);
            if(!messageItem.getNodeName().equals(son.getNodeName())){
                continue;
            }
            if(messageItem.isEqualNode(son)){//同时判断子节点是否一致
                childElementObject.setNeedDuplicate(false);
                childElementObject.setElement(messageItem);
                return childElementObject;
            }
        }
        for (int i = 1; i < item_number; i = i + 2) {
            Element messageItem = (Element) messageItems.item(i);
            //判断节点是否处于同一级别
            if(!messageItem.getNodeName().equals(son.getNodeName())){
                continue;
            }
            if(isEqualNode(messageItem,son)){//仅判断当前节点是否一致
                if(hasEqualAttributes(messageItem,son)){//当前节点完全相同不需要合并
                    childElementObject.setNeedDuplicate(false);
                    childElementObject.setElement(messageItem);
                    return childElementObject;
                }else{//当前节点的属性不相同，需要合并
                    childElementObject.setNeedDuplicate(true);
                    childElementObject.setElement(father);
                    return childElementObject;
                }
            }    
        }
        //目标文档该节点不存在，需要合并到目标文档中
        childElementObject.setNeedDuplicate(true);
        childElementObject.setElement(father);
        return childElementObject;
    }
    
    /**
     * 判断两个节点是否相同，未判断节点的属性
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
     * 判断节点的属性是否相同
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
 * 复制子节点对象
 * @author Administrator
 *
 */
class DuplicateChildElementObject{
    private boolean needDuplicate = true;//记录该节点是否需要复制
    private Element element = null;//记录该节点的父节点

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
