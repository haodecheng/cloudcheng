package org.mybatis.ext.matedata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 类TableInfo.java的实现描述：TODO 类实现描述
 * 
 * @author decheng.haodch Jul 29, 2013 2:19:43 PM
 */
public class TableInfo implements Serializable {

    private static final long   serialVersionUID  = 8424589024114827747L;

    private String              tableName;

    private Class<?>            pojo;

    private Map<String, String> colNameAndTypeMap = new HashMap<String, String>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getColNameAndTypeMap() {
        return colNameAndTypeMap;
    }

    public void setColNameAndTypeMap(Map<String, String> colNameAndTypeMap) {
        this.colNameAndTypeMap = colNameAndTypeMap;
    }

    public void addColNameAndType(String colName, String type) {
        colNameAndTypeMap.put(colName, type);
    }
}
