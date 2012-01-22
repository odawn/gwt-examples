package org.gonevertical.dts.run.example;

import java.io.File;

import org.gonevertical.dts.data.DestinationData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.SourceData;


public class Run_Import_MsSql {


	private static DestinationData destinationData = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// import records
		run();

		// optimise - simple
		destinationData.optimise = true;
//		runOptimise();
	
		System.out.println("The End");
	}
	
	public static void run() {
		char delimiter = '	';
		
		String file = "C:\\Archive\\parse\\filesInthisDir";
		SourceData sourceData = new SourceData();
		sourceData.delimiter = delimiter;
		sourceData.file = new File(file);
		
		
		destinationData = new DestinationData();
		destinationData.dropTable = true;
		destinationData.optimise = false;
		
		// this will only do varchar optimisation, but if false, will do ints and dates too
		destinationData.optimise_TextOnly = true;
		destinationData.deleteEmptyColumns = true;
		destinationData.checkForExistingRecordsAndUpdate = false; 
		destinationData.optimise_RecordsToExamine = 2500;
		
		destinationData.databaseType = "MsSql";
		destinationData.host = "";
		destinationData.database = "";
		destinationData.username = "";
		destinationData.password = "";
		destinationData.port = "1433";
		destinationData.tableSchema = "dbo";
		destinationData.table = "tmp_table";

		
		
		// use these unique ids to compare to existing data
		FieldData[] idents = new FieldData[2];
		idents[0] = new FieldData();
		idents[1] = new FieldData();
		
		idents[0].sourceField = "Orig Id";
		idents[0].destinationField = "EmployeeID";
		
		idents[1].sourceField = "id";
		idents[1].destinationField = "ID";
		
		// setup idents to work with if I want to update
		destinationData.identityColumns = idents;
		
		// init object for data storage
		int matchCount = 17;
		FieldData[] matchFields = new FieldData[matchCount];
		for (int i=0; i < matchCount; i++) {
			matchFields[i] = new FieldData();
		}
		
		matchFields[0].sourceField = "Orig Id";
		matchFields[0].destinationField = "EmployeeID";
		
		matchFields[1].sourceField = "q1";
		matchFields[1].destinationField = "q1.1";
		
		matchFields[2].sourceField = "Tech Id";
		matchFields[2].destinationField = "TechID";
		
		matchFields[3].sourceField = "Trouble Tkt";
		matchFields[3].destinationField = "TTN";
		
		matchFields[4].sourceField = "QType";
		matchFields[4].destinationField = "SurveyType";
		
		matchFields[5].sourceField = "Empl ID";
		matchFields[5].destinationField = "EmployeeID";
		
		matchFields[6].sourceField = "Orig Empl Id";
		matchFields[6].destinationField = "EmployeeID";
		
		matchFields[7].sourceField = "Employee Id";
		matchFields[7].destinationField = "EmployeeID";
		
		matchFields[8].sourceField = "EmpID";	
		matchFields[8].destinationField = "EmployeeID";
		
		matchFields[9].sourceField = "OrigEmpl";
		matchFields[9].destinationField = "EmployeeID";
			
		matchFields[10].sourceField = "Ts Cps Trbl";
		matchFields[10].destinationField = "TTN";
		
		matchFields[11].sourceField = "TasCoTrblTckt";
		matchFields[11].destinationField = "TTN";

		matchFields[12].sourceField = "WizOrder";
		matchFields[12].destinationField = "TTN";
		
		matchFields[13].sourceField = "Sub Id";
		matchFields[13].destinationField = "EmployeeID";
		
		matchFields[14].sourceField = "Service Ord";
		matchFields[14].destinationField = "TTN";
		
		matchFields[15].sourceField = "Orig Emp ID";
		matchFields[15].destinationField = "EmployeeID";

		matchFields[16].sourceField = "Mast Ord#";
		matchFields[16].destinationField = "TTN2";

//		FileProcessing_V1 process = new FileProcessing_V1();
//		process.setData(sourceData, destinationData, matchFields);
	}
	
	/**
	 * optimise the table in the end
	 */
//	private static void runOptimise() {
//		Optimise_V1 o = new Optimise_V1();
//		try {
//			o.setDestinationData(destinationData);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		o.runOptimise();
//	}

}
