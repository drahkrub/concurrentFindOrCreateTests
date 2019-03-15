package de.idon.sandbox.service;

import de.idon.sandbox.domain.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Burkhard Graves
 */
public interface BeanRepository extends JpaRepository<Bean, Integer> {
    
    Bean findByName(String name);
}
