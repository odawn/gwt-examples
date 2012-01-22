package org.gonevertical.dts.lib.sql.transformmulti;

import org.gonevertical.dts.lib.sql.transformlib.MsSqlTransformLib;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;
import org.gonevertical.dts.lib.sql.transformlib.OracleTransformLib;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class TransformModule implements Module {

  @Override
  public void configure(Binder binder) {

    binder.bind(TransformLib.class).annotatedWith(Names.named("MySql")).to(MySqlTransformLib.class);
    binder.bind(TransformLib.class).annotatedWith(Names.named("MsSql")).to(MsSqlTransformLib.class);
    binder.bind(TransformLib.class).annotatedWith(Names.named("Oracle")).to(OracleTransformLib.class);

  }

}
