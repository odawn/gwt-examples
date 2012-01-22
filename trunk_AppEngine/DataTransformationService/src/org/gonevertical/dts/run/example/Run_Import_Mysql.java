package org.gonevertical.dts.run.example;

import java.io.File;

import org.gonevertical.dts.data.DestinationData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.SourceData;


public class Run_Import_Mysql {
	
	/**
	 * start it
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		run();
	}
	
	public static void run() {
		
		char commaDelimiter = ',';
		char tabDelimiter = "\t".charAt(0);
		
		String file = "/home/branflake2267/downloads/parse/test";
		SourceData sourceData = new SourceData();
		sourceData.delimiter = commaDelimiter;
		sourceData.file = new File(file);
		
		
		DestinationData dd = new DestinationData();
		dd.dropTable = true;
		dd.checkForExistingRecordsAndUpdate = true;
		dd.optimise = true;
		dd.createIndexs = true;
		dd.deleteEmptyColumns = true;
		dd.optimise_RecordsToExamine = 1000; // 0 will do all
		dd.optimise_TextOnly = true;
		dd.databaseType = "MySql";
		dd.host = "192.168.10.91";
		dd.database = "test";
		dd.username = "test";
		dd.password = "test";
		dd.port = "3306";
		dd.table = "tmp_test"; 
		
		
		FieldData[] matchFields = new FieldData[1];
		matchFields[0] = new FieldData();
		matchFields[0].sourceField = "ID";
		matchFields[0].destinationField = "UID";

		FieldData[] idents = new FieldData[1];
		idents[0] = new FieldData();
		idents[0].sourceField = "ID";
		idents[0].destinationField = "UID";
		dd.identityColumns = idents;

//		FileProcessing_V1 process = new FileProcessing_V1();
//		process.setData(sourceData, dd, matchFields);
		
		
	}
}
