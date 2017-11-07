package com.example.demo;

import com.example.demo.domain.Bean;
import com.example.demo.service.BeanService;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ConcurrentFindOrCreateTests {

    @Autowired
    private BeanService beanService;

    @Before
    public void setUp() {
        beanService.deleteAll();
    }

    @Test
    public void testWithInsertOnDuplicateUpdate() throws InterruptedException {

        System.out.println("\n### testWithInsertOnDuplicateUpdate ###");

        List<Bean> beans = new ArrayList<>();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithOnDuplicateUpdate("dummy");
            if (bean != null) {
                beans.add(bean);
            }
        };

        startAndCheckThreads(Arrays.asList(
            new Thread(r, "Bob"),
            new Thread(r, "  Alice")
        ), beans);
    }

    @Test
    public void testWithInsertIgnore() throws InterruptedException {

        System.out.println("\n### testWithInsertIgnore ###");

        List<Bean> beans = new ArrayList<>();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithInsertIgnore("dummy");
            if (bean != null) {
                beans.add(bean);
            }
        };

        startAndCheckThreads(Arrays.asList(
            new Thread(r, "Bob"),
            new Thread(r, "  Alice")
        ), beans);
    }

    @Test
    public void testWithTableLock() throws InterruptedException {

        System.out.println("\n### testWithTableLock ###");

        List<Bean> beans = new ArrayList<>();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithTableLock("dummy");
            if (bean != null) {
                beans.add(bean);
            }
        };
        startAndCheckThreads(Arrays.asList(
            new Thread(r, "Bob"),
            new Thread(r, "  Alice"),
            new Thread(r, "    Fred"),
            new Thread(r, "      Pamela"),
            new Thread(r, "        Harvey"),
            new Thread(r, "          Marcey")
        ), beans);
    }

    private void startAndCheckThreads(List<Thread> list, List<Bean> beans) {
        list.forEach(Thread::start);
        list.forEach((t) -> {
            try { t.join(); } catch (InterruptedException ignore) {}
        });
        assertThat(beans).hasSameSizeAs(list);
    }
}
