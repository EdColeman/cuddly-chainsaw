package org.apache.edcoleman.cuddly_chainsaw.jmxUtil;

import java.io.File;
import java.io.IOException;

public class DummyLauncher {

  private DummyLauncher() {
  }

  public static int exec(Class klass) throws IOException, InterruptedException {
    String javaHome = System.getProperty("java.home");
    String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
    String classpath = System.getProperty("java.class.path");
    String className = klass.getName();

    ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);

    Process process = builder.inheritIO().start();
    process.waitFor();
    return process.exitValue();
  }

}
