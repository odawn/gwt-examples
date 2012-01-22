package org.gonevertical.dts.run.example;

import java.util.HashMap;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.process.Transfer;


public class Run_Transfer {

  public static void main(String[] args) {
    
    DatabaseData database_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "Branflake2267", "password", "tmp");
    DatabaseData database_des = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "Branflake2267", "password", "hierarchy");
    
    String fromTable = "emp_stac";
    String toTable = "employee";
    
    FieldData[] mappedFields = new FieldData[6];
    mappedFields[0] = new FieldData();
    mappedFields[1] = new FieldData();
    mappedFields[2] = new FieldData();
    mappedFields[3] = new FieldData();
    mappedFields[4] = new FieldData();
    mappedFields[5] = new FieldData();
    //mappedFields[6] = new FieldData();
    
    mappedFields[0].sourceField = "UserId";
    mappedFields[0].destinationField = "UserId";
    mappedFields[0].onlyOverwriteBlank = true;
    mappedFields[0].isPrimaryKey = true;
    
    mappedFields[1].sourceField = "Upline";
    mappedFields[1].destinationField = "Upline";
    mappedFields[1].onlyOverwriteBlank = true;
    
    mappedFields[2].sourceField = "ID_Associate";
    mappedFields[2].destinationField = "Id";
    mappedFields[2].onlyOverwriteBlank = true;
    
    mappedFields[3].sourceField = "ID__Supervisor";
    mappedFields[3].destinationField = "Custom1"; 
    mappedFields[3].onlyOverwriteBlank = true;
    
    mappedFields[4].sourceField = "Employee_Name";
    mappedFields[4].destinationField = "NameFirst";
    mappedFields[4].onlyOverwriteBlank = true;
    mappedFields[4].regexSourceField = ".*?,[\040]+(.*)";
    
    mappedFields[5].sourceField = "Employee_Name";
    mappedFields[5].destinationField = "NameLast";
    mappedFields[5].onlyOverwriteBlank = true;
    mappedFields[5].regexSourceField = "(.*?),[\040]+.*";
    
    FieldData[] oneToMany = new FieldData[1];
    oneToMany[0] = new FieldData();
    
    // one two many relationship here
    oneToMany[0].sourceField = "SalesCode_Associate";
    oneToMany[0].destinationField = "Value";
    oneToMany[0].onlyOverwriteBlank = true;
    oneToMany[0].differentDestinationTable = "hierarchy.employee_data";
    HashMap<String,String> hard = new HashMap<String, String>();
    hard.put("DataTypeId", "6"); // sales code
    oneToMany[0].hardOneToMany = hard;
   
    Transfer transfer = new Transfer(database_src, database_des);
    transfer.transferOnlyMappedFields(fromTable, toTable, mappedFields, oneToMany);
    
  }
}
