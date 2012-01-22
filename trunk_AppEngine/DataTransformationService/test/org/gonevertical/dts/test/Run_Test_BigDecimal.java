package org.gonevertical.dts.test;

import java.math.BigDecimal;

public class Run_Test_BigDecimal {

  public static void main(String[] args) {
    
    BigDecimal bd = new BigDecimal("1000.00");
    String s = bd.toString();
    
    System.out.println("BigDecimal: " + s);
    
  }
  
}
