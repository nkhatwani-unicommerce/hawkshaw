package hawkshaw.drivers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hawkshaw.ManagedCache;
import hawkshaw.throttles.Constant;
import hawkshaw.throttles.GammaDist;
import hawkshaw.throttles.NumberProducer;

public class SimpleMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMain.class);
    
    private static final int LEAK_SIZE_IN_BYTES = 4;

    public static void run(int numObjectsToAllocate) {
        NumberProducer createAt = new Constant(0);
        NumberProducer deleteAt = GammaDist.of(1234567, 5.0, 2.0);
        LOGGER.debug("Starting LCM");
        ManagedCache manager = new ManagedCache(deleteAt, createAt, LEAK_SIZE_IN_BYTES);
        manager.startAllocation(numObjectsToAllocate);
        LOGGER.debug("All enqueued");
    }

    public static void main(String[] args) {
        run(1500000);
    }

    private SimpleMain() {}

}
