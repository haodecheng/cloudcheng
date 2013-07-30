/*
 * Copyright 2013 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.mybatis.ext.mapper;

import java.io.Serializable;
import java.util.List;

import org.mybatis.ext.query.param.Pageable;

/**
 * 类CrudMapper.java的实现描述：TODO 类实现描述
 * 
 * @author decheng.haodch Jul 30, 2013 10:15:37 AM
 */
public interface CrudMapper<T extends Serializable, ID extends Serializable> {

    void save(T entity);

    void save(Iterable<T> entities);

    T findOne(ID id);

    boolean exists(ID id);

    List<T> findAll();

    List<T> findByIds(Iterable<ID> ids);

    List<T> find(Pageable<T> pageable);

    long count();
    long count(Pageable<T> pageable);

    void delete(ID id);

    void deleteByIds(Iterable<ID> ids);
    
    void deleteAll();
}
