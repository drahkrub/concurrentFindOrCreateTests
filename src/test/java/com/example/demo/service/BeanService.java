package com.example.demo.service;

import com.example.demo.domain.Bean;

/**
 * @author Burkhard Graves
 */
public interface BeanService {

    void deleteAll();

    Bean findOrCreate(String name);

    Bean findOrCreateWithInsertIgnore(String name);
}
