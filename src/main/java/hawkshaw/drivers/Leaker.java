package hawkshaw.drivers;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hawkshaw.DualThreadedManagedCache;
import hawkshaw.ManagedCache;
import hawkshaw.throttles.Constant;
import hawkshaw.throttles.GammaDist;
import hawkshaw.throttles.LongTime;
import hawkshaw.throttles.NumberProducer;

/**
 * A driver that constantly leaks a small ammount of memory
 *
 */
public class Leaker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Leaker.class); 
    private long runTime;
    private DualThreadedManagedCache leaker;
    private int leakRate; // In bytes per second

    public Leaker(long runTime, int leakRate) {
        this.runTime = runTime;
        this.leakRate = leakRate;
    }

    // A thread that sits in the background leaking
    private void createLeaker() {
        int leakPeriodInMilliSec = 50;

        int leakVolume = leakRate / (1000 / leakPeriodInMilliSec);// in bytes per ms

        // create at a constant rate
        NumberProducer createAt = new Constant(leakPeriodInMilliSec);

        // never remove
        NumberProducer deleteAt = new LongTime();
        LOGGER.debug("Starting Leaker");

        leaker = new DualThreadedManagedCache(deleteAt, createAt, leakVolume);

        // leak a large number of objects
        leaker.startAllocation(Integer.MAX_VALUE);
    }

    // Creates non-leaking churn of objects
    private void executeChurner() {

        BigInteger now = new BigInteger(Long.toString(System.currentTimeMillis()));
        BigInteger end = now.add(new BigInteger(Long.toString(runTime)));
        while (now.compareTo(end) < 0) {
            NumberProducer createAt = GammaDist.of((int) System.currentTimeMillis(), 2.0, 2.0);
            NumberProducer deleteAt = GammaDist.of((int) System.currentTimeMillis(), 1.0, 2.0);

            ManagedCache manager = new ManagedCache(deleteAt, createAt, 1024);
            manager.startAllocation(50000);
            manager.join();
            now = new BigInteger(Long.toString(System.currentTimeMillis()));
        }
    }

    private void run() {
        createLeaker();
        executeChurner();
        leaker.terminate();
    }

    // TODO sanitise args, issue #3
    public static void main(String[] args) {

        long runTime = Long.MAX_VALUE;
        int leakRate = 1 * 1024 * 1024; // default to 4Kb/s

        if (args.length != 0) {
            runTime = Long.parseLong(args[0]) * 1000;
            leakRate = Integer.parseInt(args[1]);
        }
        Leaker sm = new Leaker(runTime, leakRate);
        sm.run();
    }

}
