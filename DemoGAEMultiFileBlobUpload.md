
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;



# Demo GAE Multi Batch File Blob Upload #
> This project is an example of using an japplet to process a directory and get the files and send them back to the gwt with JSNI javascript. We can communicate to the applet using javascript and back to javascript from the java. Although, I saved time and self signed the japplet to get this demo out the door. Check the war folder of GAE\_FIleUpload for the signing instructions and other stuff. This is still in the works.

# Demo #
  * [Demo](http://demogaemultifileblobupload.appspot.com/) - Multi upload demo is partially disabled b/c one could upload hundreds of files.
  * [Applet Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/GAE_FileUpload/src/com/gonevertical/upload) - Directory Chooser for GWT
  * [GWT Project Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoGAEMultiFileUpload) - GWT Project source, using the file directory chooser
  * [Virtual Tour](http://ourvirtualtours.appspot.com/#vt_view?view=6012) - This Virtual Tours files are sitting virtually in the Blob Store.
  * [GWT Adsense Wiki](http://code.google.com/p/gwt-examples/wiki/DemoGwtAdsene) - About integrating adsense into Gawakt.com Virtual Tours site.

## Goal ##
> Upload a directory recursively to GAE, then serve it virtually to the web site. GAE code space limit is 150MB, so if I put the files in big table, I can then retrieve them using a servlet, like if they where uploaded into the code space.

## First Try ##
> I tried JavaFX to do the file processing, like using the applet to get the files in the second try. Although, the thing that doesn't work with JavaFX is javascript communication on mac and other browsers. Applet or JApplet are more mature for doing things in the Java kingdom at least in reference to communication using Javascript to and from them. I would have loved to use JavaFX b/c I like there app state control, but whats the use if you can't use it with Javascript.

## Second Try ##
> I used the applet to get the files recursively in the directories tree and then sent those back from the applet to the javascript GWT application to process, although I didn't realized the file input was read only and setting the value wasn't possible, at least easily and with out security risks.

## Third Try ##
> I then moved the entire upload process to the applet including getting the blob url. I setup a blob url servlet so I could get the url for the upload. Then I use Apache commons HttpClient,HttpCore,HttpMime libraries to do the uploading of the file which works great. This app is just an example, so all the security measures are not implemented so to try out the process.

## Working Application ##
> I made a 360 degrees virtual tours site to host some of the pictures I have taken. I have used the blob store to virtually store the images. I use integrated google analytics tracking. I use my home grown oauth authentication system following the rules, except through rpc. I have also used the GWT maps api, which works awesome. I'm so pleased with GWT and all the possibilites with the Google apis stack. I also have integrated google Adsense into the application using an iframe and changing it during state changes of the virtual tours. Theres lots more I want to do with this site in time.
  * [Virtual Tours](http://ourvirtualtours.appspot.com/)
  * [360 Degree Virtual Tour](http://ourvirtualtours.appspot.com/#vt_view?view=6012) - This is a virtual tour of Diamond Head Oahu, using virtual file store in the blob store, which works great!


## Querying BlobInfo WHERE BobKey ##
> To query a BlobKey  from BlobInfo GQL, Entity and Java query formats below.

GQL Syntax
```
SELECT * FROM __BlobInfo__ WHERE __key__ = KEY('__BlobInfo__','AMIfv940prQksEQ-cbqa_T3kupYZUKj0jFS6CEWqUfW5gTjuriiJFdVsg_Z4rEBl3aldWS7ygE_Vbcl85IWRE2vtxHvB7GF5sdtE0kIkrPk6c2hsfxlqfdocpu1zeOQygEb8RslST1cF9bT37n_9X1kdQpRtu5gyPB3-AgmpZ1GtbzWyGv7Uj1M')
```

Entity Query Syntax - which the java syntax below creates.
```
SELECT * FROM __BlobInfo__ WHERE __key__ = __BlobInfo__("Nik60h9zR0zjTB_OnLG9Ow")
```

Java Entity  Query Syntax - this will get you the blobkey
```
    Entity[] entities = null;
    try {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query q = new Query("__BlobInfo__");

      if (filter.getBlobKey() != null) { // -> filter is my object I use to drill the blobkey
        Key key = KeyFactory.createKey("__BlobInfo__", filter.getBlobKey());
        q.addFilter("__key__", Query.FilterOperator.EQUAL, key);
      }

      PreparedQuery pq = datastore.prepare(q);
      List<Entity> entList = pq.asList(FetchOptions.Builder.withLimit(limit).offset(offset));

      entities = new Entity[entList.size()];
      entList.toArray(entities);
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
```

BlobInfoFactory Java Syntax
```
  BlobInfoFactory blobInfoFactory = new BlobInfoFactory(DatastoreServiceFactory.getDatastoreService());
  BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="110" width="760" border="0" /&gt;

## Blob Data Fetching ##
> Fetching blob data for.

> This is how the fetch data start and end work.
```
    long filesize = 2048;
    
    int chunkSize = 1024;
    
    long offset = 0;
    while (offset < filesize) {

      long limit = offset + chunkSize - 1;
      if (filesize < limit) {
        limit = filesize-1;
      }
      
      System.out.println("offset=" + offset + " limit=" + limit);
      
      offset += chunkSize;
      if (offset > filesize) {
        offset = filesize;
      }
    }
```

> This is the fetching in action. I also have a work around for when the file size comes back wrong from blobinfo, which happens at the moment from base64 uploads.
```
  private byte[] getImageBytes(BlobData blobData) {
    if (blobData == null) {
      return null;
    }

    BlobKey blobKey = new BlobKey(blobData.getKey());
    if (blobKey == null) {
      return null;
    }

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    long filesize = blobData.getSize();
    int chunkSize = 1024;
    long offset = 0;
    while (offset < filesize) {

      long limit = offset + chunkSize - 1;
      if (filesize < limit) {
        limit = filesize - 1;
      }

      System.out.println("offset=" + offset + " limit=" + limit + " size=" + filesize);

      byte[] b = null;
      try {
        b = blobstoreService.fetchData(blobKey, offset, limit);
      } catch (Exception e) {
        //e.printStackTrace();
        System.out.println("Ga_Service_Image.getImageBytes(): doing workaround");
        workaround(out, blobKey, offset, filesize);
        break;
      }
      try {
        if (b != null) {
          out.write(b);
        }
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }

      offset += chunkSize;
      if (offset > filesize) {
        offset = filesize;
      }
    }

    byte[] filebytes = out.toByteArray();

    System.out.println("getImageBytes(): filebytes size: " + filebytes.length + " blobData.size=" + blobData.getSize());

    return filebytes;
  }

  private void workaround(ByteArrayOutputStream out, BlobKey blobKey, long offset, long filesize) {
    int chunkSize = 1;
    long limit = 0;
    while (offset < filesize) {

      limit = offset + chunkSize - 1;
      if (filesize < limit) {
        limit = filesize;
      }

      System.out.println("offset=" + offset + " limit=" + limit + " size=" + filesize);

      byte[] b = null;
      try {
        b = blobstoreService.fetchData(blobKey, offset, limit);
      } catch (Exception e) {
        //e.printStackTrace();
        return;
      }
      try {
        out.write(b);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }

      offset += chunkSize;
      if (offset > filesize) {
        offset = filesize;
      }
    }
  }
```

> I'm testing the file channel reader. Works on the dev side so far.
```
private byte[] getImageBytes_v2(BlobData blobData) {
    if (blobData == null || blobData.getKey() == null) {
      return null;
    }

    BlobKey blobKey = new BlobKey(blobData.getKey());
    
    FileService fileService = FileServiceFactory.getFileService();
    AppEngineFile file = null;
    try {
      file = fileService.getBlobFile(blobKey);
    } catch (FileNotFoundException e) {
      log.severe("getImageBytes_V2(): Error: fileService error " + e.toString());
      e.printStackTrace();
    }
    
    if (file == null) {
      return null;
    }
    
    FileReadChannel ch = null;
    try {
      ch = fileService.openReadChannel(file, false);
    } catch (FileNotFoundException e) {
      log.severe("getImageBytes_V2(): Error: file not found " + e.toString());
      e.printStackTrace();
    } catch (LockException e) {
      log.severe("getImageBytes_V2(): Error: lock exception " + e.toString());
      e.printStackTrace();
    } catch (IOException e) {
      log.severe("getImageBytes_V2(): Error: file read channel - io exception " + e.toString());
      e.printStackTrace();
    }

    if (ch == null) {
      return null;
    }
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] array = new byte[1024];
    ByteBuffer buf = ByteBuffer.wrap(array);
    try {
      while (ch.read(buf) != -1) {
        buf.rewind();
        byte[] a = buf.array();
        try {
          out.write(a);
        } catch (Exception e) {
          log.severe("getImageBytes_V2(): Error: buffered out error " + e.toString());
          e.printStackTrace();
        }
        buf.clear();
      }
    } catch (IOException e) {
      log.severe("getImageBytes_V2(): Error: io exception reading channel " + e.toString());
      e.printStackTrace();
    }

    byte[] filebytes = out.toByteArray();
    
    return filebytes;
  }
```

## Writing to the Blob Store ##
> I've found this to be a bit easier to write to the blobstore.

> ### Upload by File ###
> > You can write to the blob store like this too.
```
  public long uploadBlob_ByFile(long linkTypeId, long linkToThingId, String fileName, String contentType, byte[] filebytes) {
    if (filebytes == null) {
      return -1;
    }
    
    log.info("uploadBlob_ByFile(): filebytes.length=" + filebytes.length);
    
    // Get a file service
    FileService fileService = FileServiceFactory.getFileService();
    
    // Create a new Blob file with mime-type
    AppEngineFile file = null;
    try {
      file = fileService.createNewBlobFile(contentType, fileName);
    } catch (IOException e) {
      log.severe("uploadBlob_ByFile(): Error 1:" + e.toString());
      e.printStackTrace();
    }
    
    if (file == null) {
      return 0;
    }

    // Open a channel to write to it
    boolean lock = true;
    FileWriteChannel writeChannel = null;
    try {
      writeChannel = fileService.openWriteChannel(file, lock);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      log.severe("uploadBlob_ByFile(): Error 2:" + e.toString());
    } catch (FinalizationException e) {
      e.printStackTrace();
    } catch (LockException e) {
      log.severe("uploadBlob_ByFile(): Error 3:" + e.toString());
      e.printStackTrace();
    } catch (IOException e) {
      log.severe("uploadBlob_ByFile(): Error 4:" + e.toString());
      e.printStackTrace();
    }
    
    ByteBuffer bb = ByteBuffer.wrap(filebytes);

    // This time we write to the channel using standard Java
    try {
      writeChannel.write(bb);
    } catch (IOException e) {
      log.severe("uploadBlob_ByFile(): Error 5:" + e.toString());
      e.printStackTrace();
    }

    // Now finalize
    try {
      writeChannel.closeFinally();
    } catch (IllegalStateException e) {
      log.severe("uploadBlob_ByFile(): Error 6:" + e.toString());
      e.printStackTrace();
    } catch (IOException e) {
      log.severe("uploadBlob_ByFile(): Error 7:" + e.toString());
      e.printStackTrace();
    }
    
    BlobKey blobKey = getBlobKey(file); //fileService.getBlobKey(file);
    
    if (blobKey == null) {
      log.severe("uploadBlob_ByFile(): Error 8: blobKey is null");
      return 0;
    }
//... 
}
```


> ### Upload By Servlet ###
> > This is one way I send a post of a file to a servlet in the blobstore. This works great for me, to get a file to the blobstore.
```
public long uploadBlob_ByServlet(long linkTypeId, long linkToThingId, String fileName, String contentType, byte[] filebytes) {
    
    blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    
    // setup url - for new blobkey
    String surl = blobstoreService.createUploadUrl("/upload");
   
    String base = sp.getUrl(true);
    if (base.contains("127")) {
      System.out.println("base added to url in uploadBlob");
      surl = base + surl; // this isn't need on production side
    }
    
    System.out.println("uploadBlob(): fetch url=" + surl);
    
    URL url = null;
    try {
      url = new URL(surl);
    } catch (MalformedURLException e) {
      log.warning("uploadBlob() Could not setup url: " + e.toString());
      e.printStackTrace();
      return 0;
    }

    String boundary = createBoundary();
    
    URLFetchService urlFetch = URLFetchServiceFactory.getURLFetchService();
    FetchOptions fetchOptions = FetchOptions.Builder.withDefaults().setDeadline(6000.00).followRedirects();
    HTTPRequest request = new HTTPRequest(url, HTTPMethod.POST, fetchOptions);
    request.addHeader(new HTTPHeader("Content-Type", "multipart/form-data; boundary=" + boundary));
    request.addHeader(new HTTPHeader("Cookie", sp.getHeader("Cookie")));
    //request.addHeader(new HTTPHeader("Host", "127.0.0.1:8888"));
    //request.addHeader(new HTTPHeader("Origin", "http://127.0.0.1:8888"));
    //request.addHeader(new HTTPHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_6; en-us) AppleWebKit/533.19.4 (KHTML, like Gecko)"));
    //request.addHeader(new HTTPHeader("Referer", "http://127.0.0.1:8888/index.html?gwt.codesvr=127.0.0.1:9997"));
    //request.addHeader(new HTTPHeader("Accept", "application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"));
    //request.addHeader(new HTTPHeader("Accept-Language", "en-us"));
    //request.addHeader(new HTTPHeader("Accept-Encoding", "gzip, deflate"));
    //request.addHeader(new HTTPHeader("Connection", "keep-alive"));
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    write(out, "--" + boundary + "\r\n");
    writeParameter(out, "oid", Long.toString(sp.getUserThingId()));
    write(out, "--" + boundary + "\r\n");
    writeParameter(out, "ltype", Long.toString(linkTypeId));
    write(out, "--" + boundary + "\r\n");
    writeParameter(out, "ltid", Long.toString(linkToThingId));
    write(out, "--" + boundary + "\r\n");
    writeImage(out, fileName, contentType, filebytes);
    write(out, "--" + boundary + "--\r\n"); // end boundary!!!!

    int len = out.toByteArray().length;
    request.addHeader(new HTTPHeader("Content-Length", "" + len));
    request.setPayload(out.toByteArray());
    
    // DEBUG
    //echo(request.getHeaders());
    //echo(request.getPayload());

    // send request
    HTTPResponse response = null;
    try {
      response = urlFetch.fetch(request);
    } catch (Exception e) {
      log.severe("uploadBlob() Error: urlFetch.fetch(request) - Response Error: " + e.toString());
      e.printStackTrace();
    }
    
    if (response == null) {
      log.warning("uploadBlob() Error: response is null");
      return 0;
    }

    String sfileid = null;
    try {
      InputStream in = new ByteArrayInputStream(response.getContent());
      BufferedInputStream bis = new BufferedInputStream(in);
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      int result = bis.read();
      while(result != -1) {
        byte b = (byte)result;
        buf.write(b);
        result = bis.read();
      }
      sfileid = buf.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    long thingFileId = parseResponse(sfileid);
    return thingFileId;
  }


  private long parseResponse(String sfileid) {
    if (sfileid == null || sfileid.trim().length() == 0) {
      log.warning(".parseResponse() Error could not parse thingId #1 sfileid=" + sfileid);
      return 0;
    }
    
    if (sfileid.toLowerCase().contains("error") == true) {
      log.warning(".parseResponse() Error could not parse thingId #2 sfileid=" + sfileid);
      return 0;
    }
    
    HashMap<String, String> params = Global_String.getParameters("&", sfileid);
    if (params == null) {
      log.warning(".parseResponse() Error could not parse thingId #3 sfileid=" + sfileid);
      return 0;
    }
    
    String stid = params.get("tid");
    long filethingId = 0;
    try {
      filethingId = Long.parseLong(stid);
    } catch (NumberFormatException e) {
      log.warning(".parseResponse() Error could not parse thingId #4 sfileid=" + sfileid);
      e.printStackTrace();
    }
    return filethingId;
  }

  private String getRandomStr() {
    return Long.toString(random.nextLong(), 36);
  }

  private String createBoundary() {
    return "----GoneVerticalBoundary" + getRandomStr() + getRandomStr();
  }        

  private void write(OutputStream os, String s) {
    try {
      os.write(s.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeParameter(OutputStream os, String key, String value) {
    write(os, "Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + value + "\r\n");
  }

  private void writeImage(OutputStream os, String fileName, String contentType, byte[] image) {
    write(os, "Content-Disposition: form-data; name=\"File\"; filename=\"" + fileName + "\"\r\n");
    write(os, "Content-Type: " + contentType + "\r\n\r\n");
    try {
      os.write(image);
    } catch (IOException e) {
      e.printStackTrace();
    }
    write(os, "\r\n");
  }
  
  private void echo(List<HTTPHeader> headers) {
    if (headers == null) {
      return;
    }

    Iterator<HTTPHeader> it = headers.iterator();
    while (it.hasNext()) {
      HTTPHeader h = it.next();
      String hh = h.getName();
      String vv = h.getValue();
      System.out.println("***Header: hh=" + hh + " vv=" + vv);
    }

  }

  private void echo(byte[] b) {
    if (b == null) {
      System.out.println("Why is this null");
      return;
    }

    System.out.println("******PAYLOAD START******");

    //byte[] b = baos.toByteArray();
    InputStream in = new ByteArrayInputStream(b);
    int data;
    try {
      data = in.read();

      while(data != -1) {
        System.out.print((char)data);
        data = in.read();
      }
      in.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("******END PAYLOAD******");
  }
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
