/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.mybatis.ext.matedata;

import java.io.Serializable;

/**
 * 类colInfo.java的实现描述：TODO 类实现描述
 * 
 * @author decheng.haodch Jul 30, 2013 4:35:10 PM
 */
public class colInfo implements Serializable {

    private static final long serialVersionUID = -2805723387736219944L;

    private String            colName;
    private String            colType;

    private int               colLength;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public int getColLength() {
        return colLength;
    }

    public void setColLength(int colLength) {
        this.colLength = colLength;
    }

}
