package de.idon.sandbox;

import de.idon.sandbox.domain.Bean;
import de.idon.sandbox.service.BeanService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication
public class ConcurrentFindOrCreateTests {

    private static final int NUM_THREADS = 10;
    private static final String DUMMY = "dummy";

    @Autowired
    private BeanService beanService;

    @Before
    public void setUp() {
        beanService.deleteAll();
    }

    private static interface BeanGetter {
        Bean get();
    }

    @Test
    public void testWithInsertOnDuplicateUpdate() {
        startThreads(() -> {
            return beanService.findOrCreateWithOnDuplicateUpdate(DUMMY);
        });
    }

    @Test
    public void testWithInsertIgnore() {
        startThreads(() -> {
            return beanService.findOrCreateWithInsertIgnore(DUMMY);
        });
    }

    @Test
    public void testWithSelect() {
        startThreads(() -> {
            return beanService.findOrCreateWithSelect(DUMMY);
        });
    }

    @Test
    public void testWithTableLock() {
        startThreads(() -> {
            return beanService.findOrCreateWithTableLock(DUMMY);
        });
    }

    private void startThreads(BeanGetter beanGetter) {
        AtomicInteger count = new AtomicInteger();
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(NUM_THREADS);
        for (int i = NUM_THREADS; --i >= 0;) {
            new Thread(() -> {
                try {
                    startSignal.await();
                    Bean bean = beanGetter.get();
                    if (bean != null) {
                        count.incrementAndGet();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    doneSignal.countDown();
                }
            }).start();
        }
        try {
            startSignal.countDown();
            doneSignal.await();
        } catch (InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
        assertThat(count.get()).isEqualTo(NUM_THREADS);
    }
}
