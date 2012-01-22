package org.gonevertical.dts.test;


public class Run_Text_Regex_Digit {

  
 public static void main(String[] args) {
   
     String s = "1234az,5678cdfg9101.112123()&**(&!@#$asdfasdfjk;kljdfowipeur";
   
     s = s.replaceAll("[^0-9\\.]", "");
    
    System.out.println("output: " + s);
    
  }
  
}
