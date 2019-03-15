package de.idon.sandbox;

import de.idon.sandbox.domain.Bean;
import de.idon.sandbox.service.BeanService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

        AtomicInteger count = new AtomicInteger();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithOnDuplicateUpdate("dummy");
            if (bean != null) {
                count.incrementAndGet();
            }
        };

        startAndCheckThreads(r, count);
    }

    @Test
    public void testWithInsertIgnore() throws InterruptedException {

        AtomicInteger count = new AtomicInteger();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithInsertIgnore("dummy");
            if (bean != null) {
                count.incrementAndGet();
            }
        };

        startAndCheckThreads(r, count);
    }

    @Test
    public void testWithSelect() throws InterruptedException {

        AtomicInteger count = new AtomicInteger();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithSelect("dummy");
            if (bean != null) {
                count.incrementAndGet();
            }
        };

        startAndCheckThreads(r, count);
    }

    @Test
    public void testWithTableLock() throws InterruptedException {

        AtomicInteger count = new AtomicInteger();

        Runnable r = () -> {
            Bean bean = beanService.findOrCreateWithTableLock("dummy");
            if (bean != null) {
                count.incrementAndGet();
            }
        };

        startAndCheckThreads(r, count);
    }

    private void startAndCheckThreads(Runnable r, AtomicInteger counter) {

        final int numThreads = 10;
        List<Thread> threads = new ArrayList(numThreads);
        for (int i = numThreads; --i >= 0;) {
            threads.add(new Thread(r, "thread_" + i));
        }
        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException ignore) {
            }
        });
        assertThat(counter.get()).isEqualTo(numThreads);
    }
}
