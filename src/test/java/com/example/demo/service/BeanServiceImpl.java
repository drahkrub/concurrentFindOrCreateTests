package com.example.demo.service;

import com.example.demo.domain.Bean;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Burkhard Graves
 */
@Service
@Transactional(readOnly = true)
public class BeanServiceImpl implements BeanService {

    private final EntityManager entityManager;
    private final BeanRepository beanRepository;

    public BeanServiceImpl(EntityManager entityManager, BeanRepository beanRepository) {
        this.entityManager = entityManager;
        this.beanRepository = beanRepository;
    }

    @Transactional
    @Override
    public void deleteAll() {
        beanRepository.deleteAll();
    }

    @Transactional
    @Override
    public Bean findOrCreate(final String name) {
        Bean bean = beanRepository.findByName(name);

        debug(bean, "findByName(\"" + name + "\")");

        if (bean == null) {
            final int id = createWithInsertOnDuplicateKeyUpdate(name);
            bean = beanRepository.findOne(id);

            debug(bean, "findOne(" + id + ")");

            if (bean == null) {
                // just for fun, nothing will be found here...
                bean = beanRepository.findByName(name);

                debug(bean, "findByName(\"" + name + "\")");
            }
        }
        return bean;
    }

    @Transactional
    @Override
    public Bean findOrCreateWithInsertIgnore(String name) {
        Bean bean = beanRepository.findByName(name);

        debug(bean, "findByName(\"" + name + "\")");

        if (bean == null) {
            createWithInsertIgnore(name);
            bean = beanRepository.findByName(name);
            debug(bean, "findByName(\"" + name + "\")");
        }
        return bean;
    }

    private void createWithInsertIgnore(final String name) {
        final int numChanged = entityManager
            .createNativeQuery("insert ignore into bean(name) values (:name)")
            .setParameter("name", name).executeUpdate();

        debug("numChanged: " + numChanged);
        debug("bean was created" + (numChanged == 0 ? " in some other thread" : "") + ".");
    }

    private int createWithInsertOnDuplicateKeyUpdate(final String name) {
        final int numChanged = entityManager.createNativeQuery(
                "insert into bean(name) values (:name)"
                        + " on duplicate key update"
                        + " id = last_insert_id(id)") // without this trick
                // its impossiple to select the id in the next step
                // if the unique constraint is violated, see e.g.
                // https://dev.mysql.com/doc/refman/5.5/en/insert-on-duplicate.html#c13067
                .setParameter("name", name).executeUpdate();

        debug("numChanged: " + numChanged);

        final int id = ((Number) entityManager.createNativeQuery(
                "select last_insert_id()"
        ).getSingleResult()).intValue();

        // I thought numChanged might be 0 but its always 1
        debug("bean was created" + (numChanged == 0 ? " in some other thread" : "") + " with id: " + id);

        return id;
    }

    private void debug(final String message) {
        System.out.println(Thread.currentThread().getName() + ": " + message);
    }

    private void debug(final Bean bean, final String message) {
        if (bean == null) {
            debug("no bean found by " + message);
        } else {
            debug("found bean by " + message);
        }
    }
}
