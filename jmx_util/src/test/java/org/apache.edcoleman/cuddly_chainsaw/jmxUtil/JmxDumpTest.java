package org.apache.edcoleman.cuddly_chainsaw.jmxUtil;

import com.beust.jcommander.JCommander;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import static org.junit.jupiter.api.Assertions.*;

class JmxDumpTest {

  private final Logger log = LogManager.getLogger();

  @Test public void selectByPid(){

    String[] args = {"-pid", "55"};
    JmxDump.CmdParams params = new JmxDump.CmdParams();

    JCommander.newBuilder().addObject(params).build().parse(args);

    JmxDump jmxDump = new JmxDump(params);
  }


  @Test public void selectByName() throws Exception{

    Thread t = launcher();

    String[] args = {"-name", "Dummy"};
    JmxDump.CmdParams params = new JmxDump.CmdParams();

    JCommander.newBuilder().addObject(params).build().parse(args);

    JmxDump jmxDump = new JmxDump(params);

    t.join();
  }

  @Test public void jmx() throws Exception {
    JMXServiceURL url =
        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
    JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

    log.info("jmxc: {}", jmxc);
  }

  public Thread launcher() throws Exception {

    Runnable runner = () -> {
      log.info("Runner Before");
      try {
        DummyLauncher.exec(DummyMetrics.class);
      } catch (Exception ex){
        log.error("Runner failed with exception", ex);
      }
      log.info("Runner After");
    };

    log.info("Before");
    Thread t = new Thread(runner);
    t.start();
    log.info("After");

    return t;
  }
}
