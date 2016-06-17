/*
* vwd KL
* Created by zzhao on 6/16/16 1:46 PM
*/
package com.vwd.sbs.reactor.repo;

/**
 * @author zzhao
 */
public interface BlockingRepo<T> {

    void save(T value);

    T findFirst();

    Iterable<T> findAll();

    T findById(String id);
}
