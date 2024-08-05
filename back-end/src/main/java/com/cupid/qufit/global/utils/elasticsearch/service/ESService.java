package com.cupid.qufit.global.utils.elasticsearch.service;

public interface ESService<T> {
    T save(T entity);
    T findById(Long id);
    Iterable<? extends T> findAll();
    void deleteById(Long id);
    void deleteAll();
}
