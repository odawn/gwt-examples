
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;



# Writing in progress... #
> I'm putting stuff together to put in here. May 2011

  * [DemoGAEMultiFileBlobUpload](DemoGAEMultiFileBlobUpload.md) - Notes about blobbing


## Glimpse Into My Chunking Experiment ##
> I am chunking base64 from GWT client into the blobstore and decoding and writing on the fly. I haven't added error checking in yet.

### FileByteData ###
> My chunking transport object.

```
public class FileByteData implements IsSerializable {

  public static final int ENCODING_BASE64 = 1;
  public static final int ENCODING_BYTES = 2;
  
  long linkTypeId;
  long linkToThingId;
  
  private String key;
  
  private double index;
  
  private int encodingType;
  
  private String filename;
  
  private String contentType;
  
  private String base64;
  
  private byte[] filebytes;
  private int width;
  private int height;
  
  public FileByteData() {
  }
  
  public String toString() {
    String s = "";
    s += "linkTypeId=" + linkTypeId + " ";
    s += "linkToThingId=" + linkToThingId + " ";
    s += "key=" + key + " ";
    s += "index=" + index + " ";
    s += "encodingType=" + encodingType + " ";
    s += "filename" + filename + " ";
    s += "contentType=" + contentType + " ";
    s += "base64=" + base64 + " ";
    s += "filebytes=" + filebytes + " ";
    return s;
  }
  
  public void setLinkToFileThingId(long linkTypeId, long linkToThingId) {
    this.linkTypeId = linkTypeId;
    this.linkToThingId = linkToThingId; 
  }
  
  public long getLinkTypeId() {
    return linkTypeId;
  }
  
  public long getLinkToThingId() {
    return linkToThingId;
  }
  
  public void setEncodingType(int encodingType) {
    this.encodingType = encodingType;
  }
  
  public void setData(String key, double index, String filename, String contentType) {
    this.key = key;
    this.index = index;
    this.filename = filename;
    this.contentType = contentType;
  }
  
  public void setFile(String base64) {
    this.base64 = base64;
  }
  
  public void setFile(byte[] filebytes) {
    this.filebytes = filebytes;
  }
  
  public String getKey() {
    return key;
  }
  
  public double getIndex() {
    return index;
  }
  
  public int getEncodingType() {
    return encodingType;
  }
  
  public String getFileName() {
    return filename;
  }
  
  public String getContentType() {
    return contentType;
  }
  
  public String getBase64() {
    return base64;
  }
  
  public byte[] getBytes() {
    return filebytes;
  }
  
  public String getId() {
    return key + "_" + index;
  }

  public void setFacts(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
}
```

### BlobTmpJdo ###
> This is what I write the tmp blob chunks to, which has to be divisible by 4.

```
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class BlobTmpJdo {

  @NotPersistent
  private static final Logger log = Logger.getLogger(BlobTmpJdo.class.getName());

  @NotPersistent 
  private ServerPersistence sp;

  @PrimaryKey
  private String id; // random key I make up, [randomkey_index]

  @Persistent
  private String key;

  @Persistent
  private String filename;

  @Persistent
  private String contentType;

  @Persistent 
  private double index;

  @Persistent
  private byte[] filebytes;

  @Persistent
  private Text base64;

  public BlobTmpJdo() throws Exception {
    // don't use
  }

  public BlobTmpJdo(ServerPersistence sp) {
    this.sp = sp;
  }

  public void setData(FileByteData fbd) {
    this.id = fbd.getId();
    this.filename = fbd.getFileName();
    this.contentType = fbd.getContentType();
    this.key = fbd.getKey();
    this.index = fbd.getIndex(); // was loosing precision in object transport
    this.filebytes = fbd.getBytes();
    if (fbd.getBase64() != null) {
      this.base64 = new Text(fbd.getBase64());
    } else {
      base64 = null;
    }

    System.out.println("setData(): " + this.toString());
  }

  public boolean uploadTmpBlob(FileByteData fbd) {
    String allb64 = fbd.getBase64();
    long size = allb64.length();
    int chunkSize = 900; // 24900 for rpc transit, so it divides by for for decoding
    long offset = 1;
    BigDecimal index = new BigDecimal(fbd.getIndex());
    while (offset < size) {

      long limit = offset + chunkSize - 1;
      if (size <= limit) {
        limit = size;
      }

      String b64 = allb64.substring((int) offset-1, (int) limit);

      FileByteData f = new FileByteData();
      f.setData(fbd.getKey(), index.doubleValue(), fbd.getFileName(), fbd.getContentType());
      f.setFile(b64);
      saveIt(f);

      offset += chunkSize;
      if (offset > size) {
        offset = size;
      }
      index = index.add(new BigDecimal(".01"), MathContext.DECIMAL32);
    }

    return true;
  }

  private void saveIt(FileByteData fbd) {
    setData(fbd);
    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.makePersistent(this);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
  }

  public long checkTmpBlobTotalSaved(FileByteData fbd) {
    String qfilter = "key==\"" + fbd.getKey() + "\"";
    long total = 0;
    PersistenceManager pm = sp.getPersistenceManager();
    try {
      javax.jdo.Query q = pm.newQuery("select id from " + BlobTmpJdo.class.getName());
      q.setFilter(qfilter);
      List<Key> ids = (List<Key>) q.execute();
      total = ids.size();
      q.closeAll();
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } finally {
      pm.close();
    }
    //log.info("checkTmpBlobTotalSaved(): total=" + total);
    return total;
  }

  public boolean deleteTmpBlob(FileByteData fbd) {
    String qfilter = "key==\"" + fbd.getKey() + "\"";
    PersistenceManager pm = sp.getPersistenceManager();
    try {
      javax.jdo.Query q = pm.newQuery("select id from " + BlobTmpJdo.class.getName());
      q.setFilter(qfilter);
      List<String> ids = (List<String>) q.execute();
      Iterator<String> itr = ids.iterator();
      while(itr.hasNext()) {
        String id = itr.next();
        delete(id);
      }
      q.closeAll();
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } finally {
      pm.close();
    }
    return true; // TODO
  }

  private void delete(String id) {
    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      BlobTmpJdo btj = pm.getObjectById(BlobTmpJdo.class, id);
      pm.deletePersistent(btj);
      tx.commit();
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
  }

  public long writeTmpBlob(FileByteData fbd) {

    WriteBase64 wb = new WriteBase64(sp);
    wb.decodeToBlob(fbd);
    long newFileId = wb.getThingId();

    if (newFileId == 0) {
      // TODO
      return 0;
    }

    return newFileId;
  }

  /**
   * TODO stream - query, decode and write to blob store so memory isn't filled up
   *   would need to query and write to to blob during decoding
   * 
   * @param fbd
   * @return
   */

  @Deprecated
  private String query(FileByteData fbd) {
    String base64 = null;
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Query q = new Query("BlobTmpJdo");
      q.addFilter("key", FilterOperator.EQUAL, fbd.getKey());
      q.addSort("index");
      PreparedQuery e = datastore.prepare(q);
      Iterator<Entity> itr = e.asIterator();
      while (itr.hasNext()) {
        if (base64 == null) {
          base64 = "";
        }
        Entity entity = itr.next();
        Text tb64 = (Text) entity.getProperty("base64");
        if (tb64 != null) {
          base64 += tb64.getValue();
        }
        //log.info("query(): index=" + entity.getProperty("index") + " base64=" + tb64);
      }
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } 
    return base64;
  }

  public String getId() {
    return id;
  }

  public String getFilename() {
    return filename;
  }

  public String getContentType() {
    return contentType;
  }

  public String getKey() {
    return key;
  }

  public double getIndex() {
    return index;
  }

  public byte[] getBytes() {
    return filebytes;
  }

  public String getBase64() {
    if (base64 == null) {
      return null;
    }
    return base64.toString();
  }

  public String toString() {
    String s = "";
    s += "id=" + id + " ";
    s += "key=" + key + " ";
    s += "filename" + filename + " ";
    s += "contentType=" + contentType + " ";
    s += "index=" + index + " ";
    s += "filebytes=" + filebytes + " ";
    s += "base64=" + base64 + " ";
    return s;
  }

}
```


### WriteBase64 ###
> This is where I query the tmp chunks and decode while writing them to a decoded blob.

```
public class WriteBase64 {
  
  private final char[] BASE64ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

  private int[] toInt = new int[128];

  private static final Logger log = Logger.getLogger(WriteBase64.class.getName());
  
  private ServerPersistence sp;

  private FileByteData fbd;

  private AppEngineFile file;

  private FileWriteChannel writeChannel;

  private long newFileThingId;

  private FileService fileService;

  private long newfileThingId;

  public WriteBase64(ServerPersistence sp) {
    this.sp = sp;
    
    for (int i=0; i < BASE64ALPHA.length; i++){
      toInt[BASE64ALPHA[i]] = i;
    }
  }
  
  public void decodeToBlob(FileByteData fbd) {
    this.fbd = fbd;
    
    initBlobWrite();
    
    query();
    
    close();
  }
  
  public long getThingId() {
    return newFileThingId;
  }
  
  private void close() {
    try {
      writeChannel.closeFinally();
    } catch (IllegalStateException e) {
      log.severe("close(): Error 6:" + e.toString());
      e.printStackTrace();
    } catch (IOException e) {
      log.severe("close(): Error 7:" + e.toString());
      e.printStackTrace();
    }
    
    BlobKey blobKey = fileService.getBlobKey(file);
    
    if (blobKey == null) {
      // TODO apply workaround by finding the filename
      return;
    }
    
    // save to my db
    //...
  }

  private void initBlobWrite() {
    fileService = FileServiceFactory.getFileService();
    try {
      file = fileService.createNewBlobFile(fbd.getContentType(), fbd.getFileName());
    } catch (IOException e) {
      log.severe("initBlobWrite(): Error 1:" + e.toString());
      e.printStackTrace();
    }
    
    boolean lock = true;
    try {
      writeChannel = fileService.openWriteChannel(file, lock);
    } catch (FileNotFoundException e) {
      log.severe("initBlobWrite(): Error 2:" + e.toString());
      e.printStackTrace();
    } catch (FinalizationException e) {
      log.severe("initBlobWrite(): Error 2.5:" + e.toString());
      e.printStackTrace();
    } catch (LockException e) {
      log.severe("initBlobWrite(): Error 3:" + e.toString());
      e.printStackTrace();
    } catch (IOException e) {
      log.severe("initBlobWrite(): Error 4:" + e.toString());
      e.printStackTrace();
    }
  }
  
  /**
   * query the chunks, start to middle chunks must be divisible by 4, except end
   * 
   * @return
   */
  private boolean query() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    try {
      Query q = new Query("BlobTmpJdo");
      q.addFilter("key", FilterOperator.EQUAL, fbd.getKey());
      q.addSort("index");
      PreparedQuery e = datastore.prepare(q);
      Iterator<Entity> itr = e.asIterator();
      while (itr.hasNext()) {
        Entity entity = itr.next();
        Text tb64 = (Text) entity.getProperty("base64");
        if (tb64 != null) {
          String b64 = tb64.getValue();
          log.info("query(): index=" + entity.getProperty("index") + " base64=" + tb64.getValue());
          decode(b64);
        } else {
          // TODO
          log.info("query(): index=" + entity.getProperty("index") + " base64=" + tb64);
        }
      }
    } catch (Exception e) {
      log.log(Level.SEVERE, "", e);
      e.printStackTrace();
    } 
    return true;
  }

  private void decode(String b64) {
    
    ByteArrayOutputStream out = null;
    int mask = 0xFF;
    int index = 0;
    
    for (int i=0; i < b64.length(); i+=4) {
      if (out == null) {
        out = new ByteArrayOutputStream();
      }
      
      int c0 = toInt[b64.charAt(i)]; // char 1
      int c1 = toInt[b64.charAt(i + 1)]; // char 2
      
      byte b1 = (byte) (((c0 << 2) | (c1 >> 4)) & mask);
      out.write(b1);
      
      index++;
      if (index >= b64.length()) {
        break;
      }
      
      int c2 = toInt[b64.charAt(i + 2)]; // char 3
      byte b2 = (byte) (((c1 << 4) | (c2 >> 2)) & mask);
      out.write(b2);
      
      index++;
      if (index >= b64.length()) {
        break;
      }
      
      int c3 = toInt[b64.charAt(i + 3)]; // char 4
      byte b3 = (byte) (((c2 << 6) | c3) & mask);
      out.write(b3);
    }
    
    if (out != null) {
      writeToBlob(out);
    }
  }

  private void writeToBlob(ByteArrayOutputStream out) {
    ByteBuffer bb = ByteBuffer.wrap(out.toByteArray());
    try {
      writeChannel.write(bb);
    } catch (IOException e) {
      log.severe("writeToBlob(): Error 5:" + e.toString());
      e.printStackTrace();
    }
  }
  
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
