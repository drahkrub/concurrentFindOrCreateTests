package de.idon.sandbox.service;

import de.idon.sandbox.domain.Bean;

/**
 * @author Burkhard Graves
 */
public interface BeanService {

    void deleteAll();

    Bean findOrCreateWithOnDuplicateUpdate(String name);

    Bean findOrCreateWithInsertIgnore(String name);
    
    Bean findOrCreateWithSelect(String name);

    Bean findOrCreateWithTableLock(String name);
}
