/*   1:    */
package com.alipay.api.beans;
/*   2:    */

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Introspector {
    public static final int IGNORE_ALL_BEANINFO = 3;
    public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
    public static final int USE_ALL_BEANINFO = 1;
    private static final String DEFAULT_BEANINFO_SEARCHPATH = "sun.beans.infos";
    private static String[] searchPath = {"sun.beans.infos"};
    private static final int DEFAULT_CAPACITY = 128;
    private static Map<Class<?>, StandardBeanInfo> theCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, StandardBeanInfo>(128));

    static String decapitalize(String name) {
        if (name == null) {
            return null;
        }
        if ((name.length() == 0) || ((name.length() > 1) && (Character.isUpperCase(name.charAt(1))))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static BeanInfo getBeanInfo(Class<?> beanClass)
            throws IntrospectionException {
        StandardBeanInfo beanInfo = (StandardBeanInfo) theCache.get(beanClass);
        if (beanInfo == null) {
            beanInfo = getBeanInfoImplAndInit(beanClass, null, 1);
            theCache.put(beanClass, beanInfo);
        }
        return beanInfo;
    }


    private static StandardBeanInfo getBeanInfoImpl(Class<?> beanClass, Class<?> stopClass, int flags)
            throws IntrospectionException {
        BeanInfo explicitInfo = null;
        if (flags == 1) {
            explicitInfo = getExplicitBeanInfo(beanClass);
        }
        StandardBeanInfo beanInfo = new StandardBeanInfo(beanClass, explicitInfo, stopClass);
        if (beanInfo.additionalBeanInfo != null) {
            for (int i = beanInfo.additionalBeanInfo.length - 1; i >= 0; i--) {
                BeanInfo info = beanInfo.additionalBeanInfo[i];
                beanInfo.mergeBeanInfo(info, true);
            }
        }
        Class<?> beanSuperClass = beanClass.getSuperclass();
        if (beanSuperClass != stopClass) {
            if (beanSuperClass == null) {
                throw new IntrospectionException(
                        "Stop class is not super class of bean class");
            }
            int superflags = flags == 2 ? 1 :
                    flags;
            BeanInfo superBeanInfo = getBeanInfoImpl(beanSuperClass, stopClass,
                    superflags);
            if (superBeanInfo != null) {
                beanInfo.mergeBeanInfo(superBeanInfo, false);
            }
        }
        return beanInfo;
    }

    private static BeanInfo getExplicitBeanInfo(Class<?> beanClass) {
        String beanInfoClassName = beanClass.getName() + "BeanInfo";
        try {
            return loadBeanInfo(beanInfoClassName, beanClass);
        } catch (Exception localException1) {
            int index = beanInfoClassName.lastIndexOf('.');
            String beanInfoName = index >= 0 ? beanInfoClassName
                    .substring(index + 1) : beanInfoClassName;
            BeanInfo theBeanInfo = null;
            BeanDescriptor beanDescriptor = null;
            for (int i = 0; i < searchPath.length; i++) {
                beanInfoClassName = searchPath[i] + "." + beanInfoName;
                try {
                    theBeanInfo = loadBeanInfo(beanInfoClassName, beanClass);
                } catch (Exception e) {
                    continue;
                }
                beanDescriptor = theBeanInfo.getBeanDescriptor();
                if ((beanDescriptor != null) &&
                        (beanClass == beanDescriptor.getBeanClass())) {
                    return theBeanInfo;
                }
            }
            if (BeanInfo.class.isAssignableFrom(beanClass)) {
                try {
                    return loadBeanInfo(beanClass.getName(), beanClass);
                } catch (Exception localException2) {
                }
            }
        }
        return null;
    }

    private static BeanInfo loadBeanInfo(String beanInfoClassName, Class<?> beanClass)
            throws Exception {
        try {
            ClassLoader cl = beanClass.getClassLoader();
            if (cl != null) {
                return
                        (BeanInfo) Class.forName(beanInfoClassName, true, beanClass.getClassLoader()).newInstance();
            }
        } catch (Exception localException) {
            try {
                return
                        (BeanInfo) Class.forName(beanInfoClassName, true, ClassLoader.getSystemClassLoader()).newInstance();
            } catch (Exception localException1) {
            }
        }
        return
                (BeanInfo) Class.forName(beanInfoClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
    }

    private static StandardBeanInfo getBeanInfoImplAndInit(Class<?> beanClass, Class<?> stopClass, int flag)
            throws IntrospectionException {
        StandardBeanInfo standardBeanInfo = getBeanInfoImpl(beanClass,
                stopClass, flag);
        standardBeanInfo.init();
        return standardBeanInfo;
    }
}