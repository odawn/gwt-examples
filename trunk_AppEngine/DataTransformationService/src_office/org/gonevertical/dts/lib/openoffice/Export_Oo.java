/************************************************************************
 *
 * Copyright 2009 J David Eisenberg All rights reserved.
 *
 * Uses ODF Toolkit which is Copyright 2008 Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.gonevertical.dts.lib.openoffice;

import java.io.File;
import java.io.IOException;

import org.gonevertical.dts.lib.csv.Csv;
//import org.odftoolkit.odfdom.OdfFileDom;
//import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
//import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
//import org.odftoolkit.odfdom.doc.office.OdfOfficeSpreadsheet;
//import org.odftoolkit.odfdom.doc.office.OdfOfficeStyles;
//import org.odftoolkit.odfdom.doc.style.OdfStyle;
//import org.odftoolkit.odfdom.doc.style.OdfStyleParagraphProperties;
//import org.odftoolkit.odfdom.doc.style.OdfStyleTextProperties;
//import org.odftoolkit.odfdom.doc.table.OdfTable;
//import org.odftoolkit.odfdom.doc.table.OdfTableCell;
//import org.odftoolkit.odfdom.doc.table.OdfTableRow;
//import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
//import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
//import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
//import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.Node;

import com.csvreader.CsvReader;

/**
 *
 * @author J David Eisenberg
 */
public class Export_Oo {
//
//  /**
//   * @param args the command line arguments
//   */
//  public static void main(String[] args) {
//    Export_Oo app = new Export_Oo();
//    app.run();
//  }
//
//  private String outputFileName;
//  private OdfSpreadsheetDocument outputDocument;
//  private OdfFileDom contentDom;	// the document object model for content.xml
//  private OdfFileDom stylesDom;	// the document object model for styles.xml
//  // the office:automatic-styles element in content.xml
//  private OdfOfficeAutomaticStyles contentAutoStyles;
//  // the office:styles element in styles.xml
//  private OdfOfficeStyles stylesOfficeStyles;
//  // Save the automatically generated style names
//  private String columnStyleName;
//  //String rowStyleName;
//  private String headingStyleName;
//  private String noaaTimeStyleName;
//  private String noaaDateStyleName;
//  private String noaaTempStyleName;
//  // the office:spreadsheet element in the content.xml file
//  private OdfOfficeSpreadsheet officeSpreadsheet;
//
//  // csv reader
//  private Csv csv = new Csv();
//  private CsvReader csvRead = null;
//  
//	private SpreadSheetData[] sheet;
//
//  public Export_Oo() {
//  }
//  
//  public void exportToOpenOfficeFile(SpreadSheetData[] sheet, File out) {
//  	this.sheet = sheet;
//  	outputFileName = out.getAbsolutePath();
//  	run();
//  }
//  
//  public void run() {
//
//    setupOutputDocument();
//
//    if (outputDocument != null) {
//      cleanOutDocument();
//      addAutomaticStyles();
//
//      createTables(sheet);
//
//      saveOutputDocument();
//    }
//
//    System.out.println("Finished");
//  }
//  
//  public void createTables(SpreadSheetData[] sheet) {
//  	
//  	if (sheet == null) {
//  		System.out.println("ERROR: createTables(): sheet is null");
//  	}
//  	
//    for (int i=0; i < sheet.length; i++) {
//    	
//    	if (sheet[i] == null) {
//    		System.out.println("ERROR: createTables(): sheet[" + i + "] is null");
//    	}
//    	
//      OdfTable table = null;
//      if (sheet[i].getFile() == null) {
//      	table = createTable(sheet[i]);
//      } else {
//      	table = createTable(sheet[i].getSheetName(), sheet[i].getFile(), sheet[i].getDelimiter());
//      }
//      
//      officeSpreadsheet.appendChild(table);
//    }
//  }
//
//  public void setupOutputDocument() {
//    try {
//      outputDocument = OdfSpreadsheetDocument.newSpreadsheetDocument();
//      contentDom = outputDocument.getContentDom();
//      stylesDom = outputDocument.getStylesDom();
//      contentAutoStyles = contentDom.getOrCreateAutomaticStyles();
//      stylesOfficeStyles = outputDocument.getOrCreateDocumentStyles();
//      officeSpreadsheet = outputDocument.getContentRoot();
//    } catch (Exception e) {
//      System.err.println("Unable to create output file.");
//      System.err.println(e.getMessage());
//      outputDocument = null;
//    }
//  }
//
//  /*
//   * The default document has some content in it already (in the case
//   * of a text document, a <text:p>.  Clean out all the old stuff.
//   */
//  public void cleanOutDocument() {
//    Node childNode;
//    childNode = officeSpreadsheet.getFirstChild();
//    while (childNode != null) {
//      officeSpreadsheet.removeChild(childNode);
//      childNode = officeSpreadsheet.getFirstChild();
//    }
//  }
//
//  public void setFontWeight(OdfStyleBase style, String value) {
//    style.setProperty(OdfStyleTextProperties.FontWeight, value);
//    style.setProperty(OdfStyleTextProperties.FontWeightAsian, value);
//    style.setProperty(OdfStyleTextProperties.FontWeightComplex, value);
//  }
//
//  public void addAutomaticStyles() {
//
//    OdfStyle style;
//
//    // bold centered cells (for first row)
//    style = contentAutoStyles.newStyle(OdfStyleFamily.TableCell);
//    headingStyleName = style.getStyleNameAttribute();
//    style.setProperty(OdfStyleParagraphProperties.TextAlign, "center");
//    setFontWeight(style, "bold");
//  }
//
//  public OdfTable createTable(String sheetName, File file, char delimiter) {
//    openFileAndRead(file, delimiter);
//
//    OdfTable table = new OdfTable(contentDom);
//    table.setAttribute("table:name", sheetName);
//
//    // header columns
//    String[] columns = getColumns();
//    OdfTableRow row = getHeaderRow(contentDom, columns);
//    table.appendRow(row);
//
//    try {
//      while (csvRead.readRecord()) {
//        String[] data = null;
//        try {
//          data = csvRead.getValues();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//
//        OdfTableRow rowData = getDataRow(contentDom, data);
//        table.appendRow(rowData);
//      }
//    } catch (IOException e) {
//      System.out.println("Error: Can't loop through data!");
//      e.printStackTrace();
//    }
//
//    return table;
//  }
//  
//  public OdfTable createTable(SpreadSheetData sheet) {
//    
//    OdfTable table = new OdfTable(contentDom);
//    table.setAttribute("table:name", sheet.getSheetName());
//
//    // header columns
//    RowData[] rd = sheet.getRowData();
//    String[] columns = rd[0].getCells();
//    OdfTableRow row = getHeaderRow(contentDom, columns);
//    table.appendRow(row);
//
//    for (int i=1; i < rd.length; i++) {
//    	String[] data = rd[i].getCells();
//    	OdfTableRow rowData = getDataRow(contentDom, data);
//    	table.appendRow(rowData);
//    }
//    
//    return table;
//  }
//
//  private OdfTableRow getHeaderRow(OdfFileDom contentDom, String[] columns) {
//    OdfTableRow row = new OdfTableRow(contentDom);
//    for (int i=0; i < columns.length; i++) {
//      row.appendCell(createCell(headingStyleName, columns[i]));
//    }
//    return row;
//  }
//
//  private OdfTableRow getDataRow(OdfFileDom contentDom, String[] data) {
//    OdfTableRow row = new OdfTableRow(contentDom);
//    for (int i=0; i < data.length; i++) {
//      OdfTableCell cell = new OdfTableCell(contentDom);
//      cell.setOfficeStringValueAttribute(data[i]);
//      cell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
//      row.appendCell(cell);
//    }
//    return row;
//  }
//
//  private OdfTableCell createCell(String cellStyle, String content) {
//    OdfTableCell cell = new OdfTableCell(contentDom);
//    OdfTextParagraph paragraph = new OdfTextParagraph(contentDom, null, content);
//    cell.setTableStyleNameAttribute(cellStyle);
//
//    cell.setOfficeStringValueAttribute(content);
//    cell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
//    cell.appendChild(paragraph);
//    return cell;
//  }
//
//  public void saveOutputDocument() {
//    try {
//      outputDocument.save(outputFileName);
//    } catch (Exception e) {
//      System.err.println("Unable to save document.");
//      System.err.println(e.getMessage());
//    }
//  }
//
//  private void openFileAndRead(File file, char delimiter) {
//    csvRead = csv.open(file, delimiter);
//  }
//
//  private String[] getColumns() {
//    return csv.getColumns(csvRead);
//  }
}
