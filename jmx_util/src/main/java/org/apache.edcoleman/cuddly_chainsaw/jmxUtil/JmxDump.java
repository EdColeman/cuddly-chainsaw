package org.apache.edcoleman.cuddly_chainsaw.jmxUtil;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMX;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MXBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.StandardMBean;
import javax.management.modelmbean.RequiredModelMBean;
import javax.management.openmbean.OpenMBeanInfo;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Properties;

public class JmxDump {

  private final Logger log = LogManager.getLogger();

  static class CmdParams {

    @Parameter(names = {"-pid"}, description = "connect to process pid") private String pid = "";

    @Parameter(names = {"-name"}, description = "connect to process containing name")
    private String name = "";

  }

  public JmxDump(final CmdParams params) {

    if(params.pid.isEmpty() && params.name.isEmpty()){
      throw new IllegalArgumentException("Must supply either a pid or a name");
    }
    VirtualMachine jvm = null;
    try {
      // try pid first.
      if (!params.pid.isEmpty()) {
        jvm = VirtualMachine.attach(params.pid);
      } else {

        List<VirtualMachineDescriptor> jvmds = VirtualMachine.list();
        for (VirtualMachineDescriptor jvmd : jvmds) {
          log.info("id: {}, name {}", jvmd.id(), jvmd.displayName());
          String candidate = jvmd.displayName();
          if(candidate.contains(params.name)){
            jvm = VirtualMachine.attach(jvmd.id());
            if(jvm != null){
              break;
            }
          }
        }
      }


      if(jvm != null){
        log.info("B: {}", jvm.getSystemProperties());

        String s = jvm.startLocalManagementAgent();

        log.info("Local agent: {}", s);

        Properties props = new Properties();
        props.put("com.sun.management.jmxremote.port", "5000");
        // jvm.startManagementAgent(props);

        log.info("Agent running?: {}", jvm.getSystemProperties());

        JMXServiceURL url = new JMXServiceURL(s);

        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

        log.info("jmxc: {}", jmxc);

        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

        log.info("MBCount: {}", mbsc.getMBeanCount());

        int i = 0;
        for(ObjectInstance qresult : mbsc.queryMBeans(null,null)){
          log.info("Q:{} - {}", ++i, qresult);

          MBeanInfo mBeanInfo = mbsc.getMBeanInfo(qresult.getObjectName());
          MBeanAttributeInfo[] attrInfos = mBeanInfo.getAttributes();

          log.info("B:{} - {}", mBeanInfo.getClassName(), attrInfos);

        }

        ObjectName mxbeanName = new ObjectName("java.lang:type=Memory");

        MemoryMXBean mp = JMX.newMXBeanProxy(mbsc,
            mxbeanName,  MemoryMXBean.class);
        MemoryUsage queue1 = (MemoryUsage) mp.getHeapMemoryUsage();

        log.error("MMM: {}", queue1.toString());
      }

    }catch (IOException | AttachNotSupportedException | MalformedObjectNameException | InstanceNotFoundException | IntrospectionException | ReflectionException ex){
      log.fatal("Cannot attach to jvm. pid {}");
      throw new IllegalStateException(ex);
    }

  }

  public static void main(final String... argv) {

    CmdParams params = new CmdParams();

    JCommander.newBuilder().addObject(params).build().parse(argv);

    JmxDump proc = new JmxDump(params);
  }
}
