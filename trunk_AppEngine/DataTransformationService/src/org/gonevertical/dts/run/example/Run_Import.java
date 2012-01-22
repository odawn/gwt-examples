package org.gonevertical.dts.run.example;

import java.io.File;

import org.gonevertical.dts.data.DestinationData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.SourceData;



public class Run_Import {
	
	public static void main(String[] args) {
		run();
	}
	
	public static void run() {
		
		char commaDelimiter = ',';
		char tabDelimiter = '	';
		
		String file = "C:\\Archive\\test.csv"; // runs on linux too
		SourceData sourceData = new SourceData();
		sourceData.delimiter = tabDelimiter;
		sourceData.file = new File(file);
		
		
		DestinationData destinationData = new DestinationData();
		destinationData.dropTable = true;
		destinationData.databaseType = "MsSql";
		destinationData.host = "";
		destinationData.database = "";
		destinationData.username = "";
		destinationData.password = "";
		destinationData.port = "1433";
		destinationData.tableSchema = "dbo";
		destinationData.table = "tmp_table"; 
		
		
		FieldData[] matchFields = new FieldData[4];
		matchFields[0] = new FieldData();
		matchFields[0].sourceField = "Orig Empl Id";
		matchFields[0].destinationField = "EmployeeID";
		matchFields[1] = new FieldData();
		matchFields[1].sourceField = "R.1";
		matchFields[1].destinationField = "NPS";
		matchFields[2] = new FieldData();
		matchFields[2].sourceField = "Tech Id";
		matchFields[2].destinationField = "TechID";
		matchFields[3] = new FieldData();
		matchFields[3].sourceField = "Trouble Tkt";
		matchFields[3].destinationField = "TTN";

//		FileProcessing_V1 process = new FileProcessing_V1();
//		process.setData(sourceData, destinationData, matchFields);
		
	}
}
