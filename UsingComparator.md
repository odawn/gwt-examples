
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;


# Object Array Search #

# Comparator #
> I like using Comparator for specific field search in an object. Here's how I did it below.

# Code #

Use main class to run it
```
public class RunSort {

	public static void main(String[] args) {
		
		DataFields[] fieldsData = new DataFields[4];
		fieldsData[0] = new DataFields();
		fieldsData[1] = new DataFields();
		fieldsData[2] = new DataFields();
		fieldsData[3] = new DataFields();
		
		fieldsData[0].source = "Ticket Number";
		fieldsData[0].destination = "TTN";
		
		fieldsData[1].source = "Employee ID";
		fieldsData[1].destination = "EMPID";
		
		fieldsData[2].source = "Survey";
		fieldsData[2].destination = "SurveyID";
		
		fieldsData[3].source = "Apple";
		fieldsData[3].destination = "Orange";
		
		// comparator to sort by
		Comparator<DataFields> sortBySource = new SortBySource();
		
		// sort it
		Arrays.sort(fieldsData, sortBySource);
		
		for(int i=0; i < fieldsData.length; i++) {
			System.out.println(i + ". " + fieldsData[i].source);
		}
		
		// comparator
		SortBySource searchComparator = new SortBySource();
		
		// This is what I am searching for
		DataFields searchFieldObject = new DataFields();
		searchFieldObject.source = "Survey";
		
		// this is how to find it
		int index = Arrays.binarySearch(fieldsData, searchFieldObject, searchComparator);
		
		System.out.println(searchFieldObject.source + " Found @ Index: " + index);
		
	}
	
}
```

I use this object to store data. You can also sort using Comparable. In this test, I don't use this option, but can be used Arrays.sort(fieldsData); instead of Arrays.sort(fieldsData, comparator);
```
/**
 * object to store data in
 * 
 * @author branflake2267
 *
 */
public class DataFields implements Comparable<DataFields> {

	public String source;
	public String destination;
	
	@Override
	public int compareTo(DataFields o) {
		
		int i = this.source.compareTo(o.source);
		
		return i;
	}
	
}
```

This is my Comparator interface usage. I use this to sort and search my FieldsData object instances.
```
/**
 * use this interface to sort and search
 * 
 * @author branflake2267
 *
 */
public class SortBySource implements Comparator<DataFields> {

	@Override
	public int compare(DataFields a, DataFields b) {
	
		int i = a.source.compareTo(b.source);
		
		return i;
	}
}
```

Console output when run
```
0. Apple
1. Employee ID
2. Survey
3. Ticket Number
Survey Found @ Index: 2
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
