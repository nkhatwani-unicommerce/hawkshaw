package hawkshaw.throttles;

import hawkshaw.drivers.LowThroughput;
import hawkshaw.drivers.SimpleMain;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckGCTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckGCTest.class);

    @SuppressWarnings("static-method")
    @Test
    public void checkManagedCacheAllocationCausesGC() {
        GarbageCollectorMXBean bean = getGCMBean();
        long beforeCount = bean.getCollectionCount();
        SimpleMain.run(1000000);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            LOGGER.error("throw InterruptedException");
        }
        long afterCount = bean.getCollectionCount();
        Assert.assertTrue(afterCount > beforeCount);
    }

    @SuppressWarnings("static-method")
    @Test
    public void checkDualThreadedManagedCacheAllocationCausesGC() throws InterruptedException {
        GarbageCollectorMXBean bean = getGCMBean();
        long beforeCount = bean.getCollectionCount();
        Thread driver = new Thread(new LowThroughput(10000L));
        driver.start();
        driver.join();
        long afterCount = bean.getCollectionCount();
        Assert.assertTrue(afterCount > beforeCount);
    }

    private static GarbageCollectorMXBean getGCMBean() {
        try {
            List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
            for (GarbageCollectorMXBean bean:beans) {
                if (bean.getName().toLowerCase().contains("marksweep")) {
                    return bean;
                }
            }
            throw new Exception();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }
}
