package com.teamawesomemcpants.flickrdownloader;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

public class Main {
    private String apiKey = "";
    private String apiSecert = "";
    private Flickr flickr;

    public void setApiKey(String key) {
        apiKey = key;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiSecert(String secert) {
        apiSecert = secert;
    }

    public String getApiSecert() {
        return apiSecert;
    }

    public void setFlickr(Flickr obj) {
        flickr = obj;
    }

    public Flickr getFlickr() {
        return flickr;
    }

    public Main() {
        if(apiKey == "") {
            setApiKey("a9164cb5b240da037d071e2ab1f9ee77");
        }
        if(apiSecert == "") {
            setApiSecert("4e4a7dafc0e6f017");
        }
        setFlickr(new Flickr(apiKey, apiSecert, new REST()));
    }

    public static void main(String[] args) {

    }
}
