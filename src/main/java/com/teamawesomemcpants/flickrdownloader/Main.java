package com.teamawesomemcpants.flickrdownloader;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.util.AuthStore;
import com.flickr4java.flickr.util.FileAuthStore;
import com.teamawesomemcpants.flickrdownloader.Config;
import org.apache.log4j.Logger;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Main {
    private String apiKey = "";
    private String apiSecert = "";
    private Flickr flickr;
    private Config cfg;
    private AuthStore authStore;
    private Logger logger;

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
        cfg = Config.getInstance();
        if(apiKey == "") {
            setApiKey(cfg.getApiKey());
        }
        if(apiSecert == "") {
            setApiSecert(cfg.getApiSecert());
        }
        setFlickr(new Flickr(apiKey, apiSecert, new REST()));
        logger = Logger.getLogger("com.teamawesomemcpants.Main");
        try {
            this.authStore = new FileAuthStore(new File(System.getProperty("user.home") + File.separatorChar + ".flickrAuth"));
        } catch(Exception e) {
            logger.fatal(e);
        }
    }

    public boolean authorize(){
        try {
            AuthInterface authInterface = flickr.getAuthInterface();
            Token accessToken = authInterface.getRequestToken();
            String tokenKey;
            if(cfg.getAuthKey().equals("default")) {
                String url = authInterface.getAuthorizationUrl(accessToken, Permission.READ);
                System.out.println("Follow this url to Authorise yourself on Flickr");
                System.out.println(url);
                System.out.println("Paste in the token is gives you");
                System.out.print(">>");
                tokenKey = new Scanner(System.in).nextLine();
            } else {
                tokenKey = cfg.getAuthKey();
            }

            Token requestToken = authInterface.getAccessToken(accessToken, new Verifier(tokenKey));

            Auth auth = authInterface.checkToken(requestToken);
            RequestContext.getRequestContext().setAuth(auth);
            this.authStore.store(auth);
        } catch (IOException|FlickrException e) {
            logger.fatal(e);
        }
        return true;
    }

    public void download() {
        String loc;
        if(cfg.getDirectory() != "") {
            loc = cfg.getDirectory();
        } else {
            loc = "./";
        }
        File dir = new File(loc);
        RequestContext rc = RequestContext.getRequestContext();
        String nsid = "";
        if(this.authStore != null) {
            Auth auth = this.authStore.retrieve(cfg.getNsid());
            if(auth == null) {
                this.authorize();
                //auth = this.authStore.retrieve(cfg.getNsid());
                //rc.setAuth(auth);
            } else {
                rc.setAuth(auth);
            }
            nsid = auth.getUser().getId();
        }

        PhotosetsInterface pi = flickr.getPhotosetsInterface();
        PhotosInterface photosInterface = flickr.getPhotosInterface();
        Map<String, Collection> allPhotos = new HashMap<String, Collection>();
        Iterator sets;
        try {
            sets = pi.getList(nsid).getPhotosets().iterator();

            while(sets.hasNext()) {
                com.flickr4java.flickr.photosets.Photoset set = (com.flickr4java.flickr.photosets.Photoset) sets.next();
                PhotoList photos;
                photos = pi.getPhotos(set.getId(),10000, 1);
                System.out.println(set.getTitle() + " Count: " + String.valueOf(photos.getTotal()));
                allPhotos.put(set.getTitle(), photos);
            }
        } catch (FlickrException fe) {
            logger.fatal(fe);
        }

        int notInSetPage = 1;
        Collection notInASet = new ArrayList();
        while(true) {
            try {
                Collection nis = photosInterface.getNotInSet(50, notInSetPage);
                notInASet.addAll(nis);
                if (nis.size() < 50) {
                    break;
                }
                notInSetPage++;
            } catch (FlickrException fe) {
                logger.fatal(fe);
            }
        }
        allPhotos.put("NotInASet", notInASet);
        System.out.print(allPhotos);
        Iterator allIter = allPhotos.keySet().iterator();

        while(allIter.hasNext()) {
            String setTitle = (String)allIter.next();
            String setDirectoryName = makeSafeFilename(setTitle);

            Collection currentSet = allPhotos.get(setTitle);
            Iterator setIter = currentSet.iterator();
            File setDirectory = new File(cfg.getDirectory(), setDirectoryName);
            setDirectory.mkdir();
            while(setIter.hasNext()) {
                Photo p = (Photo) setIter.next();
                String url = p.getLargeUrl();
                try {
                    URL u = new URL(url);
                    String filename = u.getFile();
                    filename = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
                    System.out.println("Now writing " + filename + " to " + setDirectory.getCanonicalPath());
                    File newFile = new File(setDirectory, filename);
                    if(!newFile.exists()) {
                        BufferedInputStream inStream = new BufferedInputStream(photosInterface.getImageAsStream(p, Size.ORIGINAL));


                        FileOutputStream fos = new FileOutputStream(newFile);

                        int read;

                        while ((read = inStream.read()) != -1) {
                            fos.write(read);
                        }
                        fos.flush();
                        fos.close();
                        inStream.close();
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
    }

    private String makeSafeFilename(String input) {
        byte[] fname = input.getBytes();
        byte[] bad = new byte[] { '\\', '/', '"' };
        byte replace = '_';
        for (int i = 0; i < fname.length; i++) {
            for (byte element : bad) {
                if (fname[i] == element) {
                    fname[i] = replace;
                }
            }
        }
        return new String(fname);
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.download();
    }
}
