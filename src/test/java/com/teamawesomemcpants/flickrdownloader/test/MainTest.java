package com.teamawesomemcpants.flickrdownloader.test;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.Transport;
import com.teamawesomemcpants.flickrdownloader.Main;
import com.teamawesomemcpants.flickrdownloader.Config;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.flickr4java.flickr.Flickr;

/**
 * Unit test for simple App.
 */
public class MainTest
    extends TestCase
{
    Config cfg;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MainTest( String testName )
    {
        super( testName );
        cfg = Config.getInstance();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( MainTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public void testCreateFlickrObject() {

        Flickr flickr = new Flickr(cfg.getApiKey(), cfg.getApiSecert(), new REST());
        Main main = new Main();
        assertEquals(flickr.getApiKey(), main.getFlickr().getApiKey());
    }

}
