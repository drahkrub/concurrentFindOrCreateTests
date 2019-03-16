package de.idon.sandbox.service;

import de.idon.sandbox.domain.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Burkhard Graves
 */
public interface BeanRepository extends JpaRepository<Bean, Integer> {
    
    Bean findByName(String name);

    @Modifying
    @Query(nativeQuery = true, value = "insert into bean(name) values (:name) on duplicate key update name = name")
    public int findOrCreateWithOnDuplicateUpdate(@Param("name") String name);

    @Modifying
    @Query(nativeQuery = true, value = "insert ignore into bean(name) values (:name)")
    public int findOrCreateWithInsertIgnore(@Param("name") String name);

    @Modifying
    @Query(nativeQuery = true, value = "insert into bean(name) select :name where not exists(select 1 from bean where name=:name)")
    public int findOrCreateWithSelect(@Param("name") String name);
}
