package com.teamawesomemcpants.flickrdownloader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Created by scampbell on 4/4/15.
 */
public class Config {
    private String nsid = "";
    private String apiKey = "";
    private String apiSecert = "";
    private String directory = "";
    private String authKey = "";

    private static Config instance = null;

    protected Config() {

    }

    public String getNsid() { return this.nsid; }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getApiSecert() {
        return this.apiSecert;
    }

    public String getDirectory() {
        return this.directory;
    }

    public String getAuthKey() { return  this.authKey; }

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
            try {
                instance.getPropValues();
            } catch (IOException ioe) {
                Logger log = Logger.getLogger("com.teamawesomemcpants.flickrdownloader");
                log.fatal(ioe);
            }
        }
        return instance;
    }

    public void getPropValues() throws IOException {
        String result = "";
        Properties properties = new Properties();
        String propertiesFileName = "config.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);

        if(inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propertiesFileName + "' not found in the classpath");
        }

        this.nsid = properties.getProperty("nsid");
        this.apiKey = properties.getProperty("apiKey");
        this.apiSecert = properties.getProperty("apiSecert");
        this.directory = properties.getProperty("directoryToStore");
        this.authKey = properties.getProperty("authKey");
    }
}
