package de.idon.sandbox.service;

import de.idon.sandbox.domain.Bean;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Burkhard Graves
 */
@Service
@Transactional(readOnly = true)
public class BeanServiceImpl implements BeanService {

    private final static Logger LOGGER = LoggerFactory.getLogger(BeanServiceImpl.class);

    private final EntityManager entityManager;
    private final BeanRepository beanRepository;

    public BeanServiceImpl(EntityManager entityManager, BeanRepository beanRepository) {
        this.entityManager = entityManager;
        this.beanRepository = beanRepository;
    }

    @Override
    @Transactional
    public void deleteAll() {
        beanRepository.deleteAll();
    }

    @Override
    @Transactional
    public Bean findOrCreateWithOnDuplicateUpdate(final String name) {
        beanRepository.findOrCreateWithOnDuplicateUpdate(name);
        return beanRepository.findByName(name);
    }

    @Override
    @Transactional
    public Bean findOrCreateWithInsertIgnore(String name) {
        beanRepository.findOrCreateWithInsertIgnore(name);
        return beanRepository.findByName(name);
    }

    @Override
    @Transactional
    public Bean findOrCreateWithSelect(String name) {
        beanRepository.findOrCreateWithSelect(name);
        return beanRepository.findByName(name);

    }

    @Override
    @Transactional
    public Bean findOrCreateWithTableLock(String name) {

        Bean bean = beanRepository.findByName(name);

        if (bean != null) {
            LOGGER.info("found bean");
        } else {
            LOGGER.info("not found -> lock table");

            executeNative("LOCK TABLES bean WRITE, bean as bean0_ WRITE");

            LOGGER.info("lock aquired");

            // the bean might already be created in a different transaction,
            // therefore use "INSERT ... ON DUPLICATE UPDATE" or "INSERT IGNORE"
            // or simply search again for the bean (bonus when using the
            // search: name must not be unique!).
            bean = beanRepository.findByName(name);

            if (bean != null) {
                LOGGER.info("found bean after lock");
            } else { // still not there -> create it!
                LOGGER.info("create bean");
                bean = new Bean();
                bean.setName(name);
                beanRepository.save(bean);
                LOGGER.info("bean created!");
            }

            LOGGER.info("unlock table");

            executeNative("UNLOCK TABLES");
        }
        return bean;
    }

    private void executeNative(String sql) {
        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
