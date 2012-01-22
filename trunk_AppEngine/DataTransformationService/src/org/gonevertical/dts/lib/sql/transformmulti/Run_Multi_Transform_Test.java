package org.gonevertical.dts.lib.sql.transformmulti;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Run_Multi_Transform_Test {

public static void main(String[] args) {
    
    Injector injector = Guice.createInjector(new TransformModule());
    TransformMulti queryLib = injector.getInstance(TransformMulti.class);
    
    String type = queryLib.getTransformLib_MySql().getType();
    System.out.println("TYPE: " + type);
    
    String type2 = queryLib.getTransformLib_MsSql().getType();
    System.out.println("TYPE2: " + type2);
  }
  
}
