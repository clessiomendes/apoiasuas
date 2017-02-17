package org.apoiasuas.util

import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by clessio on 08/12/2016.
 */
class SystemUtils {

    public static String systemStatistics() {
        String result = ""
        result += "JVMMaxMemory : " + StringUtils.readableLong(Runtime.getRuntime().maxMemory());
        result += "; JVMUsedMemory : " + StringUtils.readableLong(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory());

        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                    if (value.toString().isLong())
                        value = StringUtils.readableLong(value.toString().toLong());
                    if (value.toString().isDouble() || value.toString().isFloat())
                        value = (new Long (Math.round(value.toString().toDouble()*100))).toString()+"%";
                } catch (Exception e) {
                    value = "erro";
//                    value = e;
                } // try
//                System.out.println(method.getName() + " = " + value);
                if (result)
                    result += "; ";
                result += method.getName().substring(3) + ": " + value
            } // if
        } // for

        ManagementFactory.getMemoryPoolMXBeans().each { item ->
            result += "; "+ item.getName() + ": current "+ StringUtils.readableLong(item.getUsage().getUsed()) + " max " + StringUtils.readableLong(item.getUsage().getMax());
        }

//        result += ", TotalPhysicalMemorySize = " + operatingSystemMXBean.getTotalPhysicalMemorySize();
//        result += ", FreePhysicalMemorySize = " + operatingSystemMXBean.getFreePhysicalMemorySize();
//        result += ", SystemCpuLoad = " + operatingSystemMXBean.getSystemCpuLoad();
        return result;
    }
}