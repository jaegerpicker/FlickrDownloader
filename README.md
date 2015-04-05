# FlickrDownloader
Bulk flickrdownloader

A java app to download all of the current orignal size photos from a flickr account. 

# Usage

install mvn

mkdir -p src/main/resources

touch src/main/resources/config.properties

edit src/main/resources/config.properties

in that file put:
nsid=<flickr_username>
apiKey=<flickr_apiKey>
apiSecert=<flickt_apiSecert>
directoryToStore=<path_to_directory_to_store_photos>
authKey=default

The authKey will be generated the first time you run the application, you will be prompted to paste in to the command
line. I'd recommend putting it in the properties to avoid that.


mvn clean install

java flickr-downloader.jar 

# Dependicies
flickr4java
junit


# Future Work
Make it fully concurrent and fast