

&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-aff.xml" height="100" width="740" border="0" /&gt;


# Spread Sheet Importing #
> This is my (rough) way of importing data into App Engine from a Google Spreadsheet. Most of these are not refined, but work great! By the way, be sure to do this in a task.

```
  private void importData_People() {

    schoolId = getSchoolId();

    // TODO
    int offsetRow = 1;
    int limitRow = 10000;

    dbSpread = new Db_Feed_Spreadsheet(sp, username, password);

    WorksheetEntry worksheet = dbSpread.getWorksheet("SLP_student_parent_list_svs", 0);
    if (worksheet == null) {
      return;
    }

    URL url = worksheet.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = dbSpread.getService().getFeed(url, CellFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    if (feed == null) {
      return;
    }

    String grade = null;
    String student = null;
    String parent1 = null;
    String parent2 = null;

    int row = 0;
    List<CellEntry> entries = feed.getEntries();
    for (CellEntry entry : entries) {

      Cell cell = entry.getCell();
      row = cell.getRow();
      int c = cell.getCol();
      String v = cell.getValue();

      System.out.println("row=" + row + " c=" + c + " v=" + v);


      if ((row-1) >= offsetRow || (row-1) <= limitRow) {


        if (c == 1) {
          grade = cell.getValue();
        }

        if (c == 2) {
          student = cell.getValue();
        }

        if (c == 3) {
          parent1 = cell.getValue();
        }

        if (c == 4) {
          parent2 = cell.getValue();
        }

        if (c == 5) {

          save(row, grade, student, parent1, parent2);

          grade = null;
          student = null;
          parent1 = null;
          parent2 = null;
        }

      }

    }

  }
```

> My helper class
```

public class Db_Feed_Spreadsheet {

  private static final Logger log = Logger.getLogger(Db_Feed_Spreadsheet.class.getName());

  private ServerPersistence sp;

  private SpreadsheetService service;

  private Db_Task_Monitor dbTaskM;

  private Db_Feed_Geocode geocoder;


  /**
   * retrieved spreadsheet list
   */
  private List<SpreadsheetEntry> spreadSheetList;


  /**
   * retrieved worksheets list from spreadsheet list
   */
  private List<WorksheetEntry> worksheetsList;

  private AppTokenJdo appToken;

  private long taskId;

  private long totalRows;

  /**
   * constructor 
   * 
   * @param sp
   */
  public Db_Feed_Spreadsheet(ServerPersistence sp) {
    this.sp = sp;
    setService();
  }
  
  public SpreadsheetService getService() {
    return service;
  }

  public Db_Feed_Spreadsheet(ServerPersistence sp, String username, String password) {
    this.sp = sp;
    setService(username, password);
  }

  private Db_Feed_Geocode getDbFeedGeocode() {
    if (geocoder == null) {
      geocoder = new Db_Feed_Geocode(sp);
    }
    return geocoder;
  }

  private Db_Task_Monitor getDbTaskMonitor() {
    if (dbTaskM == null) {
      dbTaskM = new Db_Task_Monitor(sp);
    }
    return dbTaskM;
  }

  private void setService() {
    String scope = "https://spreadsheets.google.com/feeds/";
    appToken = AppTokenStore.getToken(scope);
    if (appToken == null) {
      System.out.println("Db_Feed_Spreadsheet.setService(): no token exists yet for this feed and user");
      return;
    }

    String consumerKey = sp.getOAuthConsumerKey();
    String consumerSecret = sp.getOAuthConsumerSecret();

    String token = appToken.getAccessTokenKey();
    String tokenSecret = appToken.getAccessTokenSecret();

    GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
    oauthParameters.setOAuthConsumerKey(consumerKey);
    oauthParameters.setOAuthConsumerSecret(consumerSecret);
    oauthParameters.setOAuthToken(token);
    oauthParameters.setOAuthTokenSecret(tokenSecret);

    //System.out.println("Token " + token + " TokenSecret: " + tokenSecret + " ConsumerKey: " + consumerKey + " ConsumerSecret: " + consumerSecret);

    service = new SpreadsheetService("Gone_Vertical_LLC-CoreSystem_v1");
    try {
      service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
    } catch (OAuthException e) {
      e.printStackTrace();
    }
  }

  private void setService(String username, String password) {
    service = new SpreadsheetService("Gone_Vertical_LLC-CoreSystem_v1");
    try {
      service.setUserCredentials(username, password);
    } catch (AuthenticationException e) {
      e.printStackTrace();
    }
  }

  public SpreadSheetData getSpreadSheetData(SpreadSheetDataFilter filter) {
    if (filter == null || appToken == null || service == null) {
      return null;
    }

    SpreadSheetData ssd = new SpreadSheetData();
    ssd.setSpreadSheets(getSpreadSheets(filter));
    if (filter.getSpreadSheetKey() != null) {
      ssd.setWorkSheets(getWorkSheets(filter));
    }

    if (filter.getWorksheetKey() != null) {
      ssd.setWorksheetRowCount(getWorksheetRowCount(filter));

      // get columns field choices
      ssd.setColumns(getColumns(filter));
    }

    return ssd;
  }

  /**
   * TODO 
   * 
   * @param filter
   * @return
   */
  private RowData getColumns(SpreadSheetDataFilter filter) {
    if (filter.getWorksheetKey() == null) {
      return null;
    }
    if (worksheetsList == null) {
      worksheetsList = getWorkSheetList(filter);
    }
    WorksheetEntry worksheet = getFeed_WorksheetFromId(worksheetsList, filter.getWorksheetKey());
    if (worksheet == null) {
      return null;
    }
    RowData rowData = getCells(worksheet, filter);
    return rowData;
  }

  private long getWorksheetRowCount(SpreadSheetDataFilter filter) {
    if (filter.getWorksheetKey() == null) {
      return 0;
    }
    if (worksheetsList == null) {
      worksheetsList = getWorkSheetList(filter);
    }
    WorksheetEntry entry = getFeed_WorksheetFromId(worksheetsList, filter.getWorksheetKey());
    long rowCount = entry.getRowCount();
    return rowCount;
  }

  private HashMap<String, String> getSpreadSheets(SpreadSheetDataFilter filter) {
    spreadSheetList = getFeed_SpreadSheets_OAuth();
    if (spreadSheetList == null) {
      return null;
    }
    HashMap<String, String> hm = new HashMap<String, String>();
    for (int i=0; i < spreadSheetList.size(); i++) {
      SpreadsheetEntry sse = spreadSheetList.get(i);
      String key = sse.getId();
      String value = sse.getTitle().getPlainText();
      hm.put(key, value);
    }
    return hm;
  }

  private HashMap<String,String> getWorkSheets(SpreadSheetDataFilter filter) {
    if (filter.getSpreadSheetKey() == null) {
      return null;
    }
    SpreadsheetEntry spreadsheet = getSpreadhSheetByKey(spreadSheetList, filter.getSpreadSheetKey());
    List<WorksheetEntry> list = getWorkSheets(spreadsheet);
    if (list == null) {
      return null;
    }
    HashMap<String, String> hm = new HashMap<String, String>();
    for (int i=0; i < list.size(); i++) {
      WorksheetEntry entry = list.get(i);
      String key = entry.getId();
      String value = entry.getTitle().getPlainText();
      hm.put(key, value);
    }
    return hm;
  }

  /**
   * get spreadsheet feed - init oauth for all other feeds 
   * 
   * @return
   */
  private List<SpreadsheetEntry> getFeed_SpreadSheets_OAuth() {

    String scope = "https://spreadsheets.google.com/feeds/";
    AppTokenJdo appToken = AppTokenStore.getToken(scope);

    if (appToken == null) {
      System.out.println("Db_Feed_Spreadsheet.getFeed_SpreadSheets(): no token exists yet for this feed and user");
      return null;
    }

    String consumerKey = sp.getOAuthConsumerKey();
    String consumerSecret = sp.getOAuthConsumerSecret();

    String token = appToken.getAccessTokenKey();
    String tokenSecret = appToken.getAccessTokenSecret();

    GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
    oauthParameters.setOAuthConsumerKey(consumerKey);
    oauthParameters.setOAuthConsumerSecret(consumerSecret);
    oauthParameters.setOAuthToken(token);
    oauthParameters.setOAuthTokenSecret(tokenSecret);

    //System.out.println("Token " + token + " TokenSecret: " + tokenSecret + " ConsumerKey: " + consumerKey + " ConsumerSecret: " + consumerSecret);

    SpreadsheetService service = new SpreadsheetService("Gone_Vertical_LLC-CoreSystem_v1");
    try {
      service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
    } catch (OAuthException e) {
      e.printStackTrace();
    }

    //FeedURLFactory factory = FeedURLFactory.getDefault();
    //URL url1 = factory.getSpreadsheetsFeedUrl();

    URL url = null;
    try {                    
      url = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }

    //System.out.println(url1 + " " + url);

    SpreadsheetFeed feed = null;
    try {
      feed = service.getFeed(url, SpreadsheetFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    if (feed == null) {
      return null;
    }
    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
    return spreadsheets;
  }

  public int getSpreadhSheetsIndexByName(List<SpreadsheetEntry> spreadsheets, String name) {
    if (spreadsheets == null) {
      return -1;
    }
    int index = -1;
    for (int i = 0; i < spreadsheets.size(); i++) {
      SpreadsheetEntry entry = (SpreadsheetEntry) spreadsheets.get(i);
      TextConstruct tc = entry.getTitle();
      if (tc.getPlainText().equals(name) == true) {
        index = i;
        break;
      }
    }
    return index;
  }

  public List<SpreadsheetEntry> getSpreadSheetEntries() {
    URL url = null;
    try {                    
      url = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    SpreadsheetFeed feed = null;
    try {
      feed = service.getFeed(url, SpreadsheetFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    if (feed == null) {
      return null;
    }
    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
    return spreadsheets;
  }

  public SpreadsheetEntry getSpreadhSheetEntryByName(List<SpreadsheetEntry> entries, String name) {
    if (entries == null) {
      return null;
    }
    SpreadsheetEntry se = null;
    for (int i = 0; i < entries.size(); i++) {
      SpreadsheetEntry entry = (SpreadsheetEntry) entries.get(i);
      TextConstruct tc = entry.getTitle();
      if (tc.getPlainText().equals(name) == true) {
        se = entry;
        break;
      }
    }
    return se;
  }

  public SpreadsheetEntry getSpreadhSheetByKey(List<SpreadsheetEntry> entries, String spreadSheetId) {
    if (entries == null) {
      return null;
    }
    SpreadsheetEntry se = null;
    for (int i = 0; i < entries.size(); i++) {
      SpreadsheetEntry entry = (SpreadsheetEntry) entries.get(i);
      String id = entry.getId();
      if (id.equals(spreadSheetId) == true) {
        se = entry;
        break;
      }
    }
    return se;
  }

  public List<WorksheetEntry> getWorkSheets(SpreadsheetEntry spreadsheetEntry) {
    if (spreadsheetEntry == null) {
      return null;
    }
    try {
      worksheetsList = spreadsheetEntry.getWorksheets();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    return worksheetsList;
  }

  public int getWorksheetIndex(List<WorksheetEntry> worksheets, String name) {
    if (worksheets == null) {
      return -1;
    }
    int index = -1;
    for (int i = 0; i < worksheets.size(); i++) {
      WorksheetEntry entry = (WorksheetEntry) worksheets.get(i);
      TextContent tc = entry.getTextContent();
      if (name.equals(tc.getContent()) == true) {
        index = i;
      }
    }
    return index;
  }

  private WorksheetEntry getFeed_WorksheetFromId(List<WorksheetEntry> worksheets, String selectedId) {
    if (worksheets == null) {
      return null;
    }
    WorksheetEntry we = null;;
    for (int i = 0; i < worksheets.size(); i++) {
      WorksheetEntry entry = (WorksheetEntry) worksheets.get(i);
      String id = entry.getId();
      if (id.equals(selectedId) == true) {
        we = entry;
        break;
      }
    }
    return we;
  }

  public WorksheetEntry getWorksheetByIndex(List<WorksheetEntry> worksheets, int index) {
    if (worksheets == null) {
      return null;
    }
    WorksheetEntry entry = null;
    try {
      entry = (WorksheetEntry)  worksheets.get(index);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entry;
  }
  
  public WorksheetEntry getWorksheet(String spreadsheetName, int worksheetIndex) {
    
    List<SpreadsheetEntry> spreadsheets = getSpreadSheetEntries();
    
    SpreadsheetEntry spreadsheetEntry = null;
    if (spreadsheets != null) {
      spreadsheetEntry = getSpreadhSheetEntryByName(spreadsheets, spreadsheetName);
    } else {
      return null;
    }
    
    List<WorksheetEntry> worksheets = null;
    if (spreadsheetEntry != null) {
      worksheets = getWorkSheets(spreadsheetEntry);
    } else {
      return null;
    }
    
    WorksheetEntry worksheetEntry = null;
    if (worksheets != null) {
      worksheetEntry = getWorksheetByIndex(worksheets, worksheetIndex);
    } else {
      return null;
    }
    
    return worksheetEntry;
  }

  public URL getWorkSheetUrl(WorksheetEntry worksheet) {
    return worksheet.getCellFeedUrl();
  }

  public RowData getCells(WorksheetEntry worksheet, SpreadSheetDataFilter filter) {
    if (worksheet == null || filter == null) {
      return null;
    }
    URL url = worksheet.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = service.getFeed(url, CellFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    if (feed == null) {
      return null;
    }

    String rowId = null;
    int row = 0;
    ArrayList<CellData> cd = new ArrayList<CellData>();

    List<CellEntry> entries = feed.getEntries();
    for (CellEntry entry : entries) {
      Cell cell = entry.getCell();

      row = cell.getRow();
      int c = cell.getCol();

      if (filter.getFieldsInRow() == row) {
        rowId = entry.getId();
        String value = "[" + row + ":" + c + "]";
        if (filter.getHasFieldNames() == true) {
          value = value + "=" + cell.getValue();
        }
        CellData cellData = new CellData();
        cellData.setData(rowId, row, c, value);
        cd.add(cellData);
      }

      //System.out.println(" -- Cell(" + entry.getId() + "/" + entry.getTitle().getPlainText() + ") " +
      //"formula(" + entry.getCell().getInputValue() + ") numeric(" + entry.getCell().getNumericValue() + ") value(" + entry.getCell().getValue() + ")");
    }

    RowData rowData = null;
    if (cd.size() > 0) {
      CellData[] ac = new CellData[cd.size()];
      cd.toArray(ac);

      rowData = new RowData();
      rowData.set(rowId, row, ac);
    }

    return rowData;
  }

  public WorksheetEntry getWorkSheet(SpreadSheetDataFilter filter) {
    if (filter == null) {
      return null;
    }

    spreadSheetList = getFeed_SpreadSheets_OAuth();
    if (spreadSheetList == null) {
      return null;
    }

    if (filter.getSpreadSheetKey() == null) {
      return null;
    }
    SpreadsheetEntry spreadsheet = getSpreadhSheetByKey(spreadSheetList, filter.getSpreadSheetKey());

    if (filter.getWorksheetKey() == null) {
      return null;
    }
    worksheetsList = getWorkSheets(spreadsheet);

    if (worksheetsList == null) {
      return null;
    }
    WorksheetEntry wse = getFeed_WorksheetFromId(worksheetsList, filter.getWorksheetKey());

    return wse;
  }

  public List<WorksheetEntry> getWorkSheetList(SpreadSheetDataFilter filter) {
    if (filter == null) {
      return null;
    }
    spreadSheetList = getFeed_SpreadSheets_OAuth();
    if (spreadSheetList == null) {
      return null;
    }
    if (filter.getSpreadSheetKey() == null) {
      return null;
    }
    SpreadsheetEntry spreadsheet = getSpreadhSheetByKey(spreadSheetList, filter.getSpreadSheetKey());
    if (filter.getWorksheetKey() == null) {
      return null;
    }
    worksheetsList = getWorkSheets(spreadsheet);
    return worksheetsList;
  }


  public CellData getFirstCellWithThisValue(SpreadSheetDataFilter filter, String valueExist) {
    if (filter == null || valueExist == null) {
      return null;
    }

    WorksheetEntry worksheet = getWorkSheet(filter);
    if (worksheet == null) {
      return null;
    }

    URL url = worksheet.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = service.getFeed(url, CellFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    if (feed == null) {
      return null;
    }


    int row = 0;
    CellData cellData = null;

    List<CellEntry> entries = feed.getEntries();
    for (CellEntry entry : entries) {
      Cell cell = entry.getCell();

      String rowId = entry.getId();
      row = cell.getRow();
      int c = cell.getCol();
      String value =  cell.getValue();

      if (value.equals(valueExist)) {
        cellData = new CellData();
        cellData.setData(rowId, row, c, value);
        break;
      }

      //System.out.println(" -- Cell(" + entry.getId() + "/" + entry.getTitle().getPlainText() + ") " +
      //"formula(" + entry.getCell().getInputValue() + ") numeric(" + entry.getCell().getNumericValue() + ") value(" + entry.getCell().getValue() + ")");
    }

    return cellData;
  }

  public CellEntry getCellEntry(SpreadSheetDataFilter filter, int row, int col) {
    if (filter == null || row < 0 || col < 0) {
      return null;
    }

    WorksheetEntry worksheet = getWorkSheet(filter);
    if (worksheet == null) {
      return null;
    }

    CellEntry cellEntry = null;
    URL url = worksheet.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = service.getFeed(url, CellFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    if (feed == null) {
      return null;
    }

    List<CellEntry> entries = feed.getEntries();
    for (CellEntry entry : entries) {
      Cell cell = entry.getCell();
      int r = cell.getRow();
      int c = cell.getCol();

      if (r == row && c == col) {
        cellEntry = entry;
        break;
      }
    }

    return cellEntry;
  }

  public CellData[] getFirstCellsWithThisValue(SpreadSheetDataFilter filter, String valueExist) {
    if (filter == null || valueExist == null) {
      return null;
    }

    WorksheetEntry worksheet = getWorkSheet(filter);
    if (worksheet == null) {
      return null;
    }

    URL url = worksheet.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = service.getFeed(url, CellFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    if (feed == null) {
      return null;
    }

    int row = 0;
    ArrayList<CellData> cellData = new ArrayList<CellData>();

    List<CellEntry> entries = feed.getEntries();
    for (CellEntry entry : entries) {
      Cell cell = entry.getCell();

      String rowId = entry.getId();
      row = cell.getRow();
      int c = cell.getCol();
      String value =  cell.getValue();

      if (value.equals(valueExist)) {
        CellData cd = new CellData();
        cd.setData(rowId, row, c, value);
        cellData.add(cd);
      }

      //System.out.println(" -- Cell(" + entry.getId() + "/" + entry.getTitle().getPlainText() + ") " +
      //"formula(" + entry.getCell().getInputValue() + ") numeric(" + entry.getCell().getNumericValue() + ") value(" + entry.getCell().getValue() + ")");

    }

    CellData[] r = null;
    if (cellData != null && cellData.size() > 0) {
      r = new CellData[cellData.size()];
      cellData.toArray(r);
    }

    return r;
  }

  public void test() {

    String scope = "https://spreadsheets.google.com/feeds/";
    AppTokenJdo appToken = AppTokenStore.getToken(scope);

    String consumerKey = sp.getOAuthConsumerKey();
    String consumerSecret = sp.getOAuthConsumerSecret();

    String token = appToken.getAccessTokenKey();
    String tokenSecret = appToken.getAccessTokenSecret();

    GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
    oauthParameters.setOAuthConsumerKey(consumerKey);
    oauthParameters.setOAuthConsumerSecret(consumerSecret);
    oauthParameters.setOAuthToken(token);
    oauthParameters.setOAuthTokenSecret(tokenSecret);

    //log.info("Token " + token + " TokenSecret: " + tokenSecret + " ConsumerKey: " + consumerKey + " ConsumerSecret: " + consumerSecret);

    SpreadsheetService service = new SpreadsheetService("Gone_Vertical_LLC-CoreSystem_v1");
    try {
      service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
    } catch (OAuthException e) {
      e.printStackTrace();
    }

    URL url = null;
    try {                    
      url = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    SpreadsheetFeed feed = null;
    try {
      feed = service.getFeed(url, SpreadsheetFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
    for (int i = 0; i < spreadsheets.size(); i++) {
      SpreadsheetEntry entry = spreadsheets.get(i);
      //System.out.println("\t" + entry.getTitle().getPlainText());
    }

  }

  /**
   * get the column for filter
   * 
   * @param filter - spreadsheetfilter
   * @param columnName top field name
   * @return
   */
  public CellData getColumn(SpreadSheetDataFilter filter, String columnName) {
    if (filter == null || columnName == null) {
      return null;
    }
    CellData cellData = getFirstCellWithThisValue(filter, columnName);
    return cellData;
  }

  /**
   * create a column with field name at top, columnfield name makes it unique
   * 
   * @param filter
   * @param columnName
   * @return
   */
  public CellData createColumn_Unique(SpreadSheetDataFilter filter, String columnName) {

    // does column exist?
    CellData cellData = getFirstCellWithThisValue(filter, columnName);
    if (cellData == null) {
      cellData = new CellData();
      cellData.setData(null, filter.getFieldsInRow(), 0, columnName);
      cellData = createColumn_Append(filter, cellData);
    } 

    return cellData;
  }

  public CellData createColumn_Append(SpreadSheetDataFilter filter, CellData columnName) {

    if (filter == null || columnName == null) {
      return null;
    }

    WorksheetEntry wse = getWorkSheet(filter);
    if (wse == null) {
      return null;
    }

    wse.setColCount(wse.getColCount() + 1);
    try {
      wse.update();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    int row = (int) columnName.getRow();
    int col = (int) wse.getColCount();
    String value = columnName.getValue();

    CellEntry ce = new CellEntry(row, col, value);
    try {
      service.insert(wse.getCellFeedUrl(), ce);
      columnName.setRow(row);
      columnName.setColumn(col);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    return columnName;
  }

  /**
   * geocode column
   * 
   * TODO use a offset and limit - so to break up into shards
   * 
   * @param filter
   * @param syncColumn
   * @param limitRow 
   * @param offsetRow 
   * @param limit 
   * @param offset 
   */
  public void geocodeColumn(SpreadSheetDataFilter filter, CellData syncColumn, int offsetRow, int limitRow) {
    if (filter == null || syncColumn == null) {
      return;
    }
    totalRows = getWorksheetRowCount(filter);
    if (limitRow == 0) { // just in case the limit doesn't get set, will find out how many rows there are
      limitRow = (int) totalRows;
    }

    WorksheetEntry wse = getWorkSheet(filter);
    if (wse == null) {
      return;
    }

    URL url = wse.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = service.getFeed(url, CellFeed.class);
    } catch (IOException e) {
      log.severe("geocodeColumn(): Error: IO: " + e.toString());
    } catch (ServiceException e) {
      log.severe("geocodeColumn(): Error: ServiceException: " + e.toString());
    }
    if (feed == null) {
      log.severe("geocodeColumn(): quitting feed failed.");
      return;
    }

    // concat columns together if need be using this
    String geocode = null; 

    List<CellEntry> entries = feed.getEntries();
    //int columnCount = feed.getColCount();
    int rowChanged = 0;
    int row = 0;
    for (CellEntry entry : entries) {

      //String cellId = entry.getId();
      //DateTime editedDate = entry.getEdited();
      Cell cell = entry.getCell();
      row = cell.getRow();
      int column = cell.getCol();
      String value = cell.getValue();

      // geocode when at end
      if (rowChanged < row) {
        if ((row-1) >= offsetRow || (row-1) <= limitRow) { // only process with in the rowOffset & limit - row-1 is because it increments up by the time we get to this agian
          //log.info("geocodeColumn() info: row-1=" + (row-1) + " offsetRow=" + offsetRow + " limitRow=" + limitRow);
          processGeocode(wse, filter, geocode, row-1, syncColumn);
        }
        rowChanged = row;
        geocode = null;
      }

      // concat values in the row
      if (getInColumn(filter, row, column) != null) {
        if (geocode == null) {
          geocode = "";
        }
        geocode = geocode + " " + value;
      }

      //log.info("row=" + row + " col=" + column + " geocode=" + geocode);
    }

    // process the last row
    if (row <= limitRow) {
      processGeocode(wse, filter, geocode, row, syncColumn);
    }
  }

  public CellData getInColumn(SpreadSheetDataFilter filter, long row, long column) {
    if (filter == null) {
      return null;
    }
    CellData found = null;
    for (int i=0; i < filter.getCellData().length; i++) {
      long r = filter.getCellData()[i].getRow();
      long c = filter.getCellData()[i].getColumn(); 
      if (r < row && c == column) {
        found = filter.getCellData()[i];
        break;
      }
    }
    return found;
  }

  private void processGeocode(WorksheetEntry wse, SpreadSheetDataFilter filter, String geocode, int row, CellData syncColumn) {

    // make sure the basics are here
    if (wse == null || filter == null || syncColumn == null) {
      return;
    }

    //log.info("processGeocode(filter, geocode): row="+ row + " geocode=" + geocode);

    // make sure all editing happens below the column cell
    if (row <= filter.getFieldsInRow()) { // only process the rows after getFieldsInRow()
      return;
    }

    // get column/cell data to update sync link
    CellEntry syncCellEntry = getCellEntry(filter, (int) row, (int) syncColumn.getColumn());

    // does the value in the column have the itemId
    long itemId = 0;
    if (syncCellEntry != null) { 
      itemId = getSyncColumnCell_ItemId(syncCellEntry);
    }

    // create link to spreadsheet filter
    //getThingStuffJdo().saveStuff_ThingLink(ThingStuffTypeData.THINGSTUFFTYPE_LINK_GEODATA, filter.getThingId(), itemId);

    // update the column with value
    updateSyncColumn(wse, filter, syncColumn, row, itemId);

    // geocode
    try {
      getDbFeedGeocode().geocodeForThing(itemId, geocode);
    } catch (Exception e) {
      log.severe("Db_feed_SpreadSheet.processGeocode(): geocoder error. StackTrace: " + e.toString());
      log.log(Level.SEVERE, "Db_feed_SpreadSheet.processGeocode(): geocoder error", e);
      e.printStackTrace();
    }

    setCompleted(row);
  }

  private void setCompleted(int row) {
    BigDecimal bdRow = new BigDecimal(row);
    BigDecimal bdRowTotal = new BigDecimal(totalRows);
    if (bdRowTotal.intValue() == 0 || bdRow.intValue() == 0) {
      return;
    }
    BigDecimal bd = bdRow.divide(bdRowTotal, MathContext.DECIMAL32).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
    if (bd == null) {
      return;
    }
    //log.info("Db_Feed_Spreadsheet.setCompleted(): info: bdRow=" + bdRow.intValue() + " / bdRowTotal=" + bdRowTotal.intValue() + " = completed=" + bd.toString());
    getDbTaskMonitor().saveTask_Completed(taskId, false, new BigDecimal(bd.toString()));
  }

  /**
   * setup a new value for cell
   * 
   * @param wse
   * @param filter
   * @param cellData
   * @param row
   * @param thingId
   */
  private void updateSyncColumn(WorksheetEntry wse, SpreadSheetDataFilter filter, CellData cellData, long row, long thingId) {
    String columnName = getColumnName(filter);
    String value = columnName + QueryStringUtils.PARAMSPLITTER + "item" + QueryStringUtils.PARAMVALUESPLITTER + thingId;
    cellData.setRow(row);
    setCell_Value(wse, filter, cellData, value);
  }

  private String getColumnName(SpreadSheetDataFilter filter) {
    //Db_Maps dbM = new Db_Maps(sp); 
    //String colummnName = getColumnName(filter);
    // TODO
    return null;
  }

  /**
   * find the item=thingId
   * 
   * @param syncCellEntry
   * @return
   */
  private long getSyncColumnCell_ItemId(CellEntry syncCellEntry) {
    if (syncCellEntry == null) {
      return 0;
    }
    String value = syncCellEntry.getCell().getValue();
    if (value == null || value.trim().length() == 0) {
      return 0;
    }
    String sId = StringUtils.getValue("item" + QueryStringUtils.PARAMVALUESPLITTER + "([0-9]+)", value);
    long itemId = 0;
    try {
      itemId = Long.parseLong(sId);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    return itemId;
  }

  /**
   * clear column values
   * 
   * @param filter
   * @param column
   */
  public void clearColumn(SpreadSheetDataFilter filter, CellData column) {
    if (filter == null || column == null) {
      return;
    }
    WorksheetEntry wse = getWorkSheet(filter);
    if (wse == null) {
      return;
    }

    URL url = wse.getCellFeedUrl();
    CellFeed feed = null;
    try {
      feed = service.getFeed(url, CellFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }

    if (feed == null) {
      return;
    }

    final int inColumn = (int) column.getColumn();
    final int inRow = (int) filter.getFieldsInRow();

    List<CellEntry> entries = feed.getEntries();
    for (CellEntry entry : entries) {
      Cell cell = entry.getCell();
      int row = (int) cell.getRow();

      //System.out.println("inRow=" + inRow + " row=" + row + " column=" + cell.getCol() + " inColum=" + inColumn);

      if (row >= inRow && inColumn == cell.getCol()) {
        column.setRow(row);
        //System.out.println("********* Matches -> inRow=" + inRow + " row=" + row + " column=" + cell.getCol() + " inColum=" + inColumn);
        setCell_Value(wse, filter, column, "");
      }

    }
  }

  /**
   * set a cell value
   * 
   * @param wse
   * @param filter
   * @param cellData
   * @param value
   */
  private void setCell_Value(WorksheetEntry wse, SpreadSheetDataFilter filter, CellData cellData, String value) {
    if (wse == null || filter == null || cellData == null) {
      return;
    }

    //log.info("Db_Feed_Spreadsheet.setCell_Value(): row=" + cellData.getRow() + " col=" + cellData.getColumn() + " value=" + value);

    CellEntry syncCellEntry = new CellEntry((int) cellData.getRow(), (int) cellData.getColumn(), value);
    try {
      service.insert(wse.getCellFeedUrl(), syncCellEntry);
    } catch (IOException e) {
      log.severe("setCell_Value(): Error: IO: " + e.toString());
    } catch (ServiceException e) {
      log.severe("setCell_Value(): Error: Service:  " + e.toString());
    }
  }

  public void setTaskId(long taskId) {
    this.taskId = taskId;
  }

}
```



&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-aff.xml" height="100" width="740" border="0" /&gt;

