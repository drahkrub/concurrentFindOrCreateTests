package com.example.demo;

import com.example.demo.domain.Bean;
import com.example.demo.service.BeanService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication
public class OnDuplicateKeyUpdateTests {

    @Autowired
    private BeanService beanService;

    @Before
    public void setUp() {
        beanService.deleteAll();
    }

    @Test
    public void testConcurrentFindOrCreate() throws InterruptedException {

        System.out.println("\n### testConcurrentFindOrCreate ###");

        List<Bean> beans = new ArrayList<>();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreate("dummy");
            if (bean != null) {
                beans.add(bean);
            }
        };

        Thread t1 = new Thread(r, "Bob");
        Thread t2 = new Thread(r, "    Alice");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertThat(beans).hasSize(2);
    }

    @Test
    public void testConcurrentFindOrCreateWithInsertIgnore() throws InterruptedException {

        System.out.println("\n### testConcurrentFindOrCreateWithInsertIgnore ###");

        List<Bean> beans = new ArrayList<>();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithInsertIgnore("dummy");
            if (bean != null) {
                beans.add(bean);
            }
        };

        Thread t1 = new Thread(r, "Bob");
        Thread t2 = new Thread(r, "    Alice");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertThat(beans).hasSize(2);
    }
}
