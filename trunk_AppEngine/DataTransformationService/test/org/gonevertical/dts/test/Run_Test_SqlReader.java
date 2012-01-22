package org.gonevertical.dts.test;

import java.util.ArrayList;

import org.gonevertical.dts.lib.StringUtil;

public class Run_Test_SqlReader {
	
	public static void main(String[] args) {
		
		new Run_Test_SqlReader().run();
		
		
	}
	
	private void run() {
		String value = "a='b', 12azc='1, dd=''dfda,11''22', d=NULL ,e='abc''d , flr2',zz='e''n,d'";
		
		/*
		String regex = "(.*?=.*?'.+?'([\040]+)?)(,|$)";
		String[] as = StringUtil.getValues(regex, value);
		
		for (int i=0; i < as.length; i++) {
			System.out.println(i + ". " + as[i].trim());
		}
		*/
		System.out.println(" ");
		
		String[] as = readSqlSplit(value);
		
		for (int i=0; i < as.length; i++) {
			System.out.println(i + ". " + as[i].trim());
		}
		
		System.out.println("end");
		
		
	}
	
	private String[] readSqlSplit(String value) {
		
		if (value == null) {
			return null;
		}
		
		ArrayList<String> p = new ArrayList<String>();
		String s = "";
		boolean encapsulate = false;
		int marker = 0;
		for (int i=0; i < value.length(); i++) {
			
			char c = value.charAt(i);
			
			String sc = Character.toString(c);
			
			System.out.println("char: " + sc);
			
			if (encapsulate == true && marker == i-1) {
				//skip
				System.out.println("\tskip");
			} else if (sc.equals("'") && encapsulate == false) {
				encapsulate = true;
				marker = i;
				System.out.println("\tencapsulate = true");
			} else if (sc.equals("'") && encapsulate == true) {
				encapsulate = false;
				System.out.println("\tencapsulate = false");
			}
			
			if (encapsulate == false && sc.equals(",")) {
				System.out.println("\tadding " + s);
				p.add(s);
				s = "";
			} else {
				s += sc;
				System.out.println("\t ssss:" + s);
			}
			
			System.out.print("");
		}
		
		p.add(s);
		
		if (p.size() == 0) {
			return null;
		}
		
		String [] a = new String[p.size()];
		p.toArray(a);
		
		return a;
	}
	
}
