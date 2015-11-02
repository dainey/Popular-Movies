package payment_app.mcs.com.ciniplexis;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by ogayle on 25/10/2015.
 */
public class FullTest extends TestSuite {
    public FullTest() {
        super();
    }

    public static Test runSuite() {
        return new TestSuiteBuilder(FullTest.class)
                .includeAllPackagesUnderHere().build();
    }
}
