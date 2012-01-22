package org.gonevertical.dts.lib.openoffice.experimental;

import org.gonevertical.dts.lib.openoffice.RowData;

public class OoSheet {

  public OoSheet() {
  }
  
  public String createSheet(String sheetName, RowData[] rowData) {
    
    String xml = "";
    
    // styles
    xml += "<table:table table:name=\""+sheetName+"\" table:style-name=\"ta1\" table:print=\"false\">";
    xml += "<table:table-column table:style-name=\"co1\" table:default-cell-style-name=\"ce1\" />";
    xml += "<table:table-column table:style-name=\"co1\" table:number-columns-repeated=\"7\" table:default-cell-style-name=\"Default\" />";
    
      // row
      for (int row=0; row < rowData.length; row++) {
        xml += "<table:table-row table:style-name=\"ro1\">";
        for (int cell=0; cell < rowData[row].cell.length; cell++) {
          xml += "<table:table-cell office:value-type=\"string\">";
            xml += "<text:p>" + rowData[row].cell[cell] + "</text:p>";
          xml += "</table:table-cell>";
        }
        xml += "</table:table-row>";
      }
      
    xml += "</table:table>";

    return xml;
  }
  
  /*
  <table:table table:name="Metric_Overall" table:style-name="ta1" table:print="false">
    <table:table-column table:style-name="co1" table:default-cell-style-name="ce1" />
    <table:table-column table:style-name="co1" table:number-columns-repeated="7" table:default-cell-style-name="Default" />
    
    <table:table-row table:style-name="ro1">
      
      <table:table-cell office:value-type="string">
        <text:p>Name</text:p>
      </table:table-cell>
      
      <table:table-cell table:style-name="ce1" office:value-type="string">
        <text:p>month1</text:p>
      </table:table-cell>
      
      <table:table-cell table:style-name="ce1"
        office:value-type="string">
        <text:p>month2</text:p>
      </table:table-cell>
      <table:table-cell table:style-name="ce1"
        office:value-type="string">
        <text:p>month3</text:p>
      </table:table-cell>
      <table:table-cell table:style-name="ce1"
        office:value-type="string">
        <text:p>month4</text:p>
      </table:table-cell>
      <table:table-cell table:style-name="ce1"
        office:value-type="string">
        <text:p>month5</text:p>
      </table:table-cell>
      <table:table-cell table:style-name="ce1"
        office:value-type="string">
        <text:p>month6</text:p>
      </table:table-cell>
      <table:table-cell table:style-name="ce1"
        office:value-type="string">
        <text:p>month7</text:p>
      </table:table-cell>
    </table:table-row>
    
    <table:table-row table:style-name="ro1">
      <table:table-cell office:value-type="string">
        <text:p>lable1</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val1</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val2</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val3</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val4</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val5</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val6</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val7</text:p>
      </table:table-cell>
    </table:table-row>
    
    <table:table-row table:style-name="ro1">
      <table:table-cell office:value-type="string">
        <text:p>lable2</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val1b</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val2b</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val3b</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val4b</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val5b</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val6b</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val7b</text:p>
      </table:table-cell>
    </table:table-row>
    
    <table:table-row table:style-name="ro1">
      <table:table-cell office:value-type="string">
        <text:p>lable3</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val1c</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val2c</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val3c</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val4c</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val5c</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val6c</text:p>
      </table:table-cell>
      <table:table-cell office:value-type="string">
        <text:p>val7c</text:p>
      </table:table-cell>
    </table:table-row>
    
  </table:table>
  */
}
