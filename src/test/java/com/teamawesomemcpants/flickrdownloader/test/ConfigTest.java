package com.teamawesomemcpants.flickrdownloader.test;

import com.teamawesomemcpants.flickrdownloader.Config;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by scampbell on 4/4/15.
 */
public class ConfigTest extends TestCase {

    public ConfigTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ConfigTest.class);
    }

    public void testLoadProperties() {
        Config cfg = Config.getInstance();
        assertTrue(cfg.getApiKey() != "");
        assertTrue(cfg.getApiSecert() != "");
        assertTrue(cfg.getDirectory() != "");
        assertTrue(cfg.getNsid() != "");
    }

}
