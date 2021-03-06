package com.alipay.api.beans;


import android.media.Image;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;


class StandardBeanInfo
        extends SimpleBeanInfo {
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_SET = "set";
    private static final String PREFIX_ADD = "add";
    private static final String PREFIX_REMOVE = "remove";
    private static final String SUFFIX_LISTEN = "Listener";
    private static final String STR_NORMAL = "normal";
    private static final String STR_INDEXED = "indexed";
    private static final String STR_VALID = "valid";
    private static final String STR_INVALID = "invalid";
    private static final String STR_PROPERTY_TYPE = "PropertyType";
    private static final String STR_IS_CONSTRAINED = "isConstrained";
    private static final String STR_SETTERS = "setters";
    private static final String STR_GETTERS = "getters";
    private boolean explicitMethods = false;
    private boolean explicitProperties = false;
    private boolean explicitEvents = false;
    private BeanInfo explicitBeanInfo = null;
    private EventSetDescriptor[] events = null;
    private MethodDescriptor[] methods = null;
    private PropertyDescriptor[] properties = null;
    private BeanDescriptor beanDescriptor = null;
    BeanInfo[] additionalBeanInfo = null;
    private Class<?> beanClass;
    private int defaultEventIndex = -1;
    private int defaultPropertyIndex = -1;
    private static PropertyComparator comparator = new PropertyComparator();
    private Object[] icon = new Object[4];
    private boolean canAddPropertyChangeListener;
    private boolean canRemovePropertyChangeListener;

    StandardBeanInfo(Class<?> beanClass, BeanInfo explicitBeanInfo, Class<?> stopClass)
            throws IntrospectionException {
        this.beanClass = beanClass;
        if (explicitBeanInfo != null) {
            this.explicitBeanInfo = explicitBeanInfo;
            this.events = explicitBeanInfo.getEventSetDescriptors();
            this.methods = explicitBeanInfo.getMethodDescriptors();
            this.properties = explicitBeanInfo.getPropertyDescriptors();
            this.defaultEventIndex = explicitBeanInfo.getDefaultEventIndex();
            if ((this.defaultEventIndex < 0) || (this.defaultEventIndex >= this.events.length)) {
                this.defaultEventIndex = -1;
            }
            this.defaultPropertyIndex = explicitBeanInfo.getDefaultPropertyIndex();
            if ((this.defaultPropertyIndex < 0) ||
                    (this.defaultPropertyIndex >= this.properties.length)) {
                this.defaultPropertyIndex = -1;
            }
            this.additionalBeanInfo = explicitBeanInfo.getAdditionalBeanInfo();
            for (int i = 0; i < 4; i++) {
                this.icon[i] = explicitBeanInfo.getIcon(i + 1);
            }
            if (this.events != null) {
                this.explicitEvents = true;
            }
            if (this.methods != null) {
                this.explicitMethods = true;
            }
            if (this.properties != null) {
                this.explicitProperties = true;
            }
        }
        if (this.methods == null) {
            this.methods = introspectMethods();
        }
        if (this.properties == null) {
            this.properties = introspectProperties(stopClass);
        }
        if (this.events == null) {
            this.events = introspectEvents();
        }
    }

    public BeanInfo[] getAdditionalBeanInfo() {
        return null;
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
        return this.events;
    }

    public MethodDescriptor[] getMethodDescriptors() {
        return this.methods;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return this.properties;
    }

    public BeanDescriptor getBeanDescriptor() {
        if (this.beanDescriptor == null) {
            if (this.explicitBeanInfo != null) {
                this.beanDescriptor = this.explicitBeanInfo.getBeanDescriptor();
            }
            if (this.beanDescriptor == null) {
                this.beanDescriptor = new BeanDescriptor(this.beanClass);
            }
        }
        return this.beanDescriptor;
    }

    public int getDefaultEventIndex() {
        return this.defaultEventIndex;
    }

    public int getDefaultPropertyIndex() {
        return this.defaultPropertyIndex;
    }

    public Image getIcon(int iconKind) {
        return (Image) this.icon[(iconKind - 1)];
    }

    void mergeBeanInfo(BeanInfo beanInfo, boolean force)
            throws IntrospectionException {
        if ((force) || (!this.explicitProperties)) {
            PropertyDescriptor[] superDescs = beanInfo.getPropertyDescriptors();
            if (superDescs != null) {
                if (getPropertyDescriptors() != null) {
                    this.properties = mergeProps(superDescs, beanInfo
                            .getDefaultPropertyIndex());
                } else {
                    this.properties = superDescs;
                    this.defaultPropertyIndex = beanInfo.getDefaultPropertyIndex();
                }
            }
        }
        if ((force) || (!this.explicitMethods)) {
            MethodDescriptor[] superMethods = beanInfo.getMethodDescriptors();
            if (superMethods != null) {
                if (this.methods != null) {
                    this.methods = mergeMethods(superMethods);
                } else {
                    this.methods = superMethods;
                }
            }
        }
        if ((force) || (!this.explicitEvents)) {
            EventSetDescriptor[] superEvents = beanInfo
                    .getEventSetDescriptors();
            if (superEvents != null) {
                if (this.events != null) {
                    this.events = mergeEvents(superEvents, beanInfo
                            .getDefaultEventIndex());
                } else {
                    this.events = superEvents;
                    this.defaultEventIndex = beanInfo.getDefaultEventIndex();
                }
            }
        }
    }

    private PropertyDescriptor[] mergeProps(PropertyDescriptor[] superDescs, int superDefaultIndex)
            throws IntrospectionException {
        HashMap<String, PropertyDescriptor> subMap = internalAsMap(this.properties);
        String defaultPropertyName = null;
        if ((this.defaultPropertyIndex >= 0) &&
                (this.defaultPropertyIndex < this.properties.length)) {
            defaultPropertyName = this.properties[this.defaultPropertyIndex].getName();
        } else if ((superDefaultIndex >= 0) &&
                (superDefaultIndex < superDescs.length)) {
            defaultPropertyName = superDescs[superDefaultIndex].getName();
        }
        for (int i = 0; i < superDescs.length; i++) {
            PropertyDescriptor superDesc = superDescs[i];
            String propertyName = superDesc.getName();
            if (!subMap.containsKey(propertyName)) {
                subMap.put(propertyName, superDesc);
            } else {
                Object value = subMap.get(propertyName);

                Method subGet = ((PropertyDescriptor) value).getReadMethod();
                Method subSet = ((PropertyDescriptor) value).getWriteMethod();
                Method superGet = superDesc.getReadMethod();
                Method superSet = superDesc.getWriteMethod();

                Class<?> superType = superDesc.getPropertyType();
                Class<?> superIndexedType = null;
                Class<?> subType = ((PropertyDescriptor) value).getPropertyType();
                Class<?> subIndexedType = null;
                if ((value instanceof IndexedPropertyDescriptor)) {
                    subIndexedType =
                            ((IndexedPropertyDescriptor) value).getIndexedPropertyType();
                }
                if ((superDesc instanceof IndexedPropertyDescriptor)) {
                    superIndexedType =
                            ((IndexedPropertyDescriptor) superDesc).getIndexedPropertyType();
                }
                if (superIndexedType == null) {
                    PropertyDescriptor subDesc = (PropertyDescriptor) value;
                    if (subIndexedType == null) {
                        if ((subType != null) && (superType != null) &&
                                (subType.getName() != null) &&
                                (subType.getName().equals(superType.getName()))) {
                            if ((superGet != null) && (
                                    (subGet == null) || (superGet.equals(subGet)))) {
                                subDesc.setReadMethod(superGet);
                            }
                            if ((superSet != null) && (
                                    (subSet == null) || (superSet.equals(subSet)))) {
                                subDesc.setWriteMethod(superSet);
                            }
                            if ((subType == Boolean.TYPE) && (subGet != null) &&
                                    (superGet != null) &&
                                    (superGet.getName().startsWith("is"))) {
                                subDesc.setReadMethod(superGet);
                            }
                        } else if (((subGet == null) || (subSet == null)) &&
                                (superGet != null)) {
                            subDesc = new PropertyDescriptor(propertyName,
                                    superGet, superSet);
                            if (subGet != null) {
                                String subGetName = subGet.getName();
                                Method method = null;
                                MethodDescriptor[] introspectMethods = introspectMethods();
                                for (MethodDescriptor methodDesc : introspectMethods) {
                                    method = methodDesc.getMethod();
                                    if ((method != subGet) &&
                                            (subGetName.equals(method
                                                    .getName()))) {
                                        if ((method.getParameterTypes().length == 0) &&
                                                (method.getReturnType() == superType)) {
                                            subDesc.setReadMethod(method);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if ((superType != null) &&
                                (superType.isArray())) {
                            if (superType.getComponentType().getName().equals(subIndexedType.getName())) {
                                if ((subGet == null) && (superGet != null)) {
                                    subDesc.setReadMethod(superGet);
                                }
                                if ((subSet == null) && (superSet != null)) {
                                    subDesc.setWriteMethod(superSet);
                                }
                            }
                        }
                        if ((subIndexedType == Boolean.TYPE) &&
                                (superType == Boolean.TYPE)) {
                            Method subIndexedSet = ((IndexedPropertyDescriptor) subDesc)
                                    .getIndexedWriteMethod();
                            if ((subGet == null) && (subSet == null) &&
                                    (subIndexedSet != null) && (superGet != null)) {
                                try {
                                    subSet = this.beanClass.getDeclaredMethod(
                                            subIndexedSet.getName(), new Class[]{Boolean.TYPE});
                                } catch (Exception localException) {
                                }
                                if (subSet != null) {
                                    subDesc = new PropertyDescriptor(propertyName,
                                            superGet, subSet);
                                }
                            }
                        }
                    }
                    subMap.put(propertyName, subDesc);
                } else if (subIndexedType == null) {
                    if ((subType != null) &&
                            (subType.isArray())) {
                        if (subType.getComponentType().getName().equals(superIndexedType.getName())) {
                            if (subGet != null) {
                                superDesc.setReadMethod(subGet);
                            }
                            if (subSet != null) {
                                superDesc.setWriteMethod(subSet);
                            }
                            subMap.put(propertyName, superDesc);
                        }
                    }
                    if ((subGet == null) || (subSet == null)) {
                        Class<?> beanSuperClass = this.beanClass.getSuperclass();
                        String methodSuffix = capitalize(propertyName);
                        Method method = null;
                        if (subGet == null) {
                            if (subType == Boolean.TYPE) {
                                try {
                                    method =
                                            beanSuperClass.getDeclaredMethod("is" +
                                                    methodSuffix, new Class[0]);
                                } catch (Exception localException1) {
                                }
                            } else {
                                try {
                                    method =
                                            beanSuperClass.getDeclaredMethod("get" +
                                                    methodSuffix, new Class[0]);
                                } catch (Exception localException2) {
                                }
                            }
                            if ((method != null) &&
                                    (!Modifier.isStatic(method
                                            .getModifiers()))) {
                                if (method.getReturnType() == subType) {
                                    ((PropertyDescriptor) value).setReadMethod(method);
                                }
                            }
                        } else {
                            try {
                                method = beanSuperClass.getDeclaredMethod(
                                        "set" + methodSuffix, new Class[]{subType});
                            } catch (Exception localException3) {
                            }
                            if ((method != null) &&
                                    (!Modifier.isStatic(method
                                            .getModifiers()))) {
                                if (method.getReturnType() == Void.TYPE) {
                                    ((PropertyDescriptor) value).setWriteMethod(method);
                                }
                            }
                        }
                    }
                    subMap.put(propertyName, (PropertyDescriptor) value);
                } else if (subIndexedType.getName().equals(
                        superIndexedType.getName())) {
                    IndexedPropertyDescriptor subDesc = (IndexedPropertyDescriptor) value;
                    if ((subGet == null) && (superGet != null)) {
                        subDesc.setReadMethod(superGet);
                    }
                    if ((subSet == null) && (superSet != null)) {
                        subDesc.setWriteMethod(superSet);
                    }
                    IndexedPropertyDescriptor superIndexedDesc = (IndexedPropertyDescriptor) superDesc;
                    if ((subDesc.getIndexedReadMethod() == null) &&
                            (superIndexedDesc.getIndexedReadMethod() != null)) {
                        subDesc.setIndexedReadMethod(superIndexedDesc
                                .getIndexedReadMethod());
                    }
                    if ((subDesc.getIndexedWriteMethod() == null) &&
                            (superIndexedDesc.getIndexedWriteMethod() != null)) {
                        subDesc.setIndexedWriteMethod(superIndexedDesc
                                .getIndexedWriteMethod());
                    }
                    subMap.put(propertyName, subDesc);
                }
                label1105:
                mergeAttributes((PropertyDescriptor) value, superDesc);
            }
        }
        PropertyDescriptor[] theDescs = new PropertyDescriptor[subMap.size()];
        subMap.values().toArray(theDescs);
        if ((defaultPropertyName != null) && (!this.explicitProperties)) {
            for (int i = 0; i < theDescs.length; i++) {
                if (defaultPropertyName.equals(theDescs[i].getName())) {
                    this.defaultPropertyIndex = i;
                    break;
                }
            }
        }
        return theDescs;
    }

    private String capitalize(String name) {
        if (name == null) {
            return null;
        }
        if ((name.length() == 0) || ((name.length() > 1) && (Character.isUpperCase(name.charAt(1))))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private static void mergeAttributes(PropertyDescriptor subDesc, PropertyDescriptor superDesc) {
        subDesc.hidden |= superDesc.hidden;
        subDesc.expert |= superDesc.expert;
        subDesc.preferred |= superDesc.preferred;
        subDesc.bound |= superDesc.bound;
        subDesc.constrained |= superDesc.constrained;
        subDesc.name = superDesc.name;
        if ((subDesc.shortDescription == null) &&
                (superDesc.shortDescription != null)) {
            subDesc.shortDescription = superDesc.shortDescription;
        }
        if ((subDesc.displayName == null) && (superDesc.displayName != null)) {
            subDesc.displayName = superDesc.displayName;
        }
    }

    private MethodDescriptor[] mergeMethods(MethodDescriptor[] superDescs) {
        HashMap<String, MethodDescriptor> subMap = internalAsMap(this.methods);
        for (MethodDescriptor superMethod : superDescs) {
            String methodName = getQualifiedName(superMethod.getMethod());
            MethodDescriptor method = (MethodDescriptor) subMap.get(methodName);
            if (method == null) {
                subMap.put(methodName, superMethod);
            } else {
                method.merge(superMethod);
            }
        }
        MethodDescriptor[] theMethods = new MethodDescriptor[subMap.size()];
        subMap.values().toArray(theMethods);
        return theMethods;
    }

    private EventSetDescriptor[] mergeEvents(EventSetDescriptor[] otherEvents, int otherDefaultIndex) {
        HashMap<String, EventSetDescriptor> subMap = internalAsMap(this.events);
        String defaultEventName = null;
        if ((this.defaultEventIndex >= 0) && (this.defaultEventIndex < this.events.length)) {
            defaultEventName = this.events[this.defaultEventIndex].getName();
        } else if ((otherDefaultIndex >= 0) &&
                (otherDefaultIndex < otherEvents.length)) {
            defaultEventName = otherEvents[otherDefaultIndex].getName();
        }
        for (EventSetDescriptor event : otherEvents) {
            String eventName = event.getName();
            EventSetDescriptor subEvent = (EventSetDescriptor) subMap.get(eventName);
            if (subEvent == null) {
                subMap.put(eventName, event);
            } else {
                subEvent.merge(event);
            }
        }
        EventSetDescriptor[] theEvents = new EventSetDescriptor[subMap.size()];
        subMap.values().toArray(theEvents);
        if ((defaultEventName != null) && (!this.explicitEvents)) {
            for (int i = 0; i < theEvents.length; i++) {
                if (defaultEventName.equals(theEvents[i].getName())) {
                    this.defaultEventIndex = i;
                    break;
                }
            }
        }
        return theEvents;
    }

    private static HashMap<String, PropertyDescriptor> internalAsMap(PropertyDescriptor[] propertyDescs) {
        HashMap<String, PropertyDescriptor> map = new HashMap<>();
        for (int i = 0; i < propertyDescs.length; i++) {
            map.put(propertyDescs[i].getName(), propertyDescs[i]);
        }
        return map;
    }

    private static HashMap<String, MethodDescriptor> internalAsMap(MethodDescriptor[] theDescs) {
        HashMap<String, MethodDescriptor> map = new HashMap<>();
        for (int i = 0; i < theDescs.length; i++) {
            String qualifiedName = getQualifiedName(theDescs[i].getMethod());
            map.put(qualifiedName, theDescs[i]);
        }
        return map;
    }

    private static HashMap<String, EventSetDescriptor> internalAsMap(EventSetDescriptor[] theDescs) {
        HashMap<String, EventSetDescriptor> map = new HashMap<>();
        for (int i = 0; i < theDescs.length; i++) {
            map.put(theDescs[i].getName(), theDescs[i]);
        }
        return map;
    }

    private static String getQualifiedName(Method method) {
        String qualifiedName = method.getName();
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; i++) {
                qualifiedName = qualifiedName + "_" + paramTypes[i].getName();
            }
        }
        return qualifiedName;
    }

    private MethodDescriptor[] introspectMethods() {
        return introspectMethods(false, this.beanClass);
    }

    private MethodDescriptor[] introspectMethods(boolean includeSuper) {
        return introspectMethods(includeSuper, this.beanClass);
    }

    private MethodDescriptor[] introspectMethods(boolean includeSuper, Class<?> introspectorClass) {
        Method[] basicMethods = includeSuper ? introspectorClass.getMethods() :
                introspectorClass.getDeclaredMethods();
        if ((basicMethods == null) || (basicMethods.length == 0)) {
            return null;
        }
        ArrayList<MethodDescriptor> methodList = new ArrayList<>(
                basicMethods.length);
        for (int i = 0; i < basicMethods.length; i++) {
            int modifiers = basicMethods[i].getModifiers();
            if (Modifier.isPublic(modifiers)) {
                MethodDescriptor theDescriptor = new MethodDescriptor(
                        basicMethods[i]);
                methodList.add(theDescriptor);
            }
        }
        int methodCount = methodList.size();
        MethodDescriptor[] theMethods = (MethodDescriptor[]) null;
        if (methodCount > 0) {
            theMethods = new MethodDescriptor[methodCount];
            theMethods = (MethodDescriptor[]) methodList.toArray(theMethods);
        }
        return theMethods;
    }

    private PropertyDescriptor[] introspectProperties(Class<?> stopClass)
            throws IntrospectionException {
        MethodDescriptor[] methodDescriptors = introspectMethods();
        if (methodDescriptors == null) {
            return null;
        }
        ArrayList<MethodDescriptor> methodList = new ArrayList<>();
        for (int index = 0; index < methodDescriptors.length; index++) {
            int modifiers = methodDescriptors[index].getMethod().getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                methodList.add(methodDescriptors[index]);
            }
        }
        int methodCount = methodList.size();
        MethodDescriptor[] theMethods = (MethodDescriptor[]) null;
        if (methodCount > 0) {
            theMethods = new MethodDescriptor[methodCount];
            theMethods = (MethodDescriptor[]) methodList.toArray(theMethods);
        }
        if (theMethods == null) {
            return null;
        }
        HashMap<String, HashMap> propertyTable = new HashMap(theMethods.length);
        for (int i = 0; i < theMethods.length; i++) {
            introspectGet(theMethods[i].getMethod(), propertyTable);
            introspectSet(theMethods[i].getMethod(), propertyTable);
        }
        fixGetSet(propertyTable);


        MethodDescriptor[] allMethods = introspectMethods(true);
        MethodDescriptor method;
        if (stopClass != null) {
            MethodDescriptor[] excludeMethods = introspectMethods(true,
                    stopClass);
            if (excludeMethods != null) {
            }
        }
        for (int i = 0; i < allMethods.length; i++) {
            introspectPropertyListener(allMethods[i].getMethod());
        }
        ArrayList<PropertyDescriptor> propertyList = new ArrayList<>();
        for (Map.Entry<String, HashMap> entry : propertyTable.entrySet()) {
            String propertyName = (String) entry.getKey();
            HashMap table = entry.getValue();
            if (table != null) {
                String normalTag = (String) table.get("normal");
                String indexedTag = (String) table.get("indexed");
                if ((normalTag != null) || (indexedTag != null)) {
                    Method get = (Method) table.get("normalget");
                    Method set = (Method) table.get("normalset");
                    Method indexedGet = (Method) table.get("indexedget");
                    Method indexedSet = (Method) table.get("indexedset");

                    PropertyDescriptor propertyDesc = null;
                    if (indexedTag == null) {
                        propertyDesc = new PropertyDescriptor(propertyName, get, set);
                    } else {
                        try {
                            propertyDesc = new IndexedPropertyDescriptor(propertyName,
                                    get, set, indexedGet, indexedSet);
                        } catch (IntrospectionException e) {
                            propertyDesc = new IndexedPropertyDescriptor(propertyName,
                                    null, null, indexedGet, indexedSet);
                        }
                    }
                    if ((this.canAddPropertyChangeListener) && (this.canRemovePropertyChangeListener)) {
                        propertyDesc.setBound(true);
                    } else {
                        propertyDesc.setBound(false);
                    }
                    if (table.get("isConstrained") == Boolean.TRUE) {
                        propertyDesc.setConstrained(true);
                    }
                    propertyList.add(propertyDesc);
                }
            }
        }
        PropertyDescriptor[] theProperties = new PropertyDescriptor[propertyList
                .size()];
        propertyList.toArray(theProperties);
        return theProperties;
    }

    private boolean isInSuper(MethodDescriptor method, MethodDescriptor[] excludeMethods) {
        for (MethodDescriptor m : excludeMethods) {
            if (method.getMethod().equals(m.getMethod())) {
                return true;
            }
        }
        return false;
    }

    private void introspectPropertyListener(Method theMethod) {
        String methodName = theMethod.getName();
        Class[] param = theMethod.getParameterTypes();
        if (param.length != 1) {
            return;
        }
        if ((methodName.equals("addPropertyChangeListener")) &&
                (param[0].equals(PropertyChangeListener.class))) {
            this.canAddPropertyChangeListener = true;
        }
        if ((methodName.equals("removePropertyChangeListener")) &&
                (param[0].equals(PropertyChangeListener.class))) {
            this.canRemovePropertyChangeListener = true;
        }
    }

    private static void introspectGet(Method theMethod, HashMap<String, HashMap> propertyTable) {
        String methodName = theMethod.getName();
        int prefixLength = 0;
        if (methodName == null) {
            return;
        }
        if (methodName.startsWith("get")) {
            prefixLength = "get".length();
        }
        if (methodName.startsWith("is")) {
            prefixLength = "is".length();
        }
        if (prefixLength == 0) {
            return;
        }
        String propertyName = Introspector.decapitalize(methodName.substring(prefixLength));
        if (!isValidProperty(propertyName)) {
            return;
        }
        Class propertyType = theMethod.getReturnType();
        if ((propertyType == null) || (propertyType == Void.TYPE)) {
            return;
        }
        if ((prefixLength == 2) &&
                (propertyType != Boolean.TYPE)) {
            return;
        }
        Class[] paramTypes = theMethod.getParameterTypes();
        if ((paramTypes.length > 1) || (
                (paramTypes.length == 1) && (paramTypes[0] != Integer.TYPE))) {
            return;
        }
        HashMap table = propertyTable.get(propertyName);
        if (table == null) {
            table = new HashMap();
            propertyTable.put(propertyName, table);
        }
        ArrayList<Method> getters = (ArrayList) table.get("getters");
        if (getters == null) {
            getters = new ArrayList<>();
            table.put("getters", getters);
        }
        getters.add(theMethod);
    }

    private static void introspectSet(Method theMethod, HashMap<String, HashMap> propertyTable) {
        String methodName = theMethod.getName();
        if (methodName == null) {
            return;
        }
        Class returnType = theMethod.getReturnType();
        if (returnType != Void.TYPE) {
            return;
        }
        if ((methodName == null) || (!methodName.startsWith("set"))) {
            return;
        }
        String propertyName = Introspector.decapitalize(methodName.substring("set".length()));
        if (!isValidProperty(propertyName)) {
            return;
        }
        Class[] paramTypes = theMethod.getParameterTypes();
        if ((paramTypes.length == 0) || (paramTypes.length > 2) || (
                (paramTypes.length == 2) && (paramTypes[0] != Integer.TYPE))) {
            return;
        }
        HashMap table = propertyTable.get(propertyName);
        if (table == null) {
            table = new HashMap();
            propertyTable.put(propertyName, table);
        }
        ArrayList<Method> setters = (ArrayList) table.get("setters");
        if (setters == null) {
            setters = new ArrayList<>();
            table.put("setters", setters);
        }
        Class[] exceptions = theMethod.getExceptionTypes();
        setters.add(theMethod);
    }

    private void fixGetSet(HashMap<String, HashMap> propertyTable)
            throws IntrospectionException {
    }

    private EventSetDescriptor[] introspectEvents()
            throws IntrospectionException {
        MethodDescriptor[] theMethods = introspectMethods();
        if (theMethods == null) {
            return null;
        }
        HashMap<String, HashMap> eventTable = new HashMap(
                theMethods.length);
        for (int i = 0; i < theMethods.length; i++) {
            introspectListenerMethods("add", theMethods[i].getMethod(),
                    eventTable);
            introspectListenerMethods("remove", theMethods[i].getMethod(),
                    eventTable);
            introspectGetListenerMethods(theMethods[i].getMethod(), eventTable);
        }
        ArrayList<EventSetDescriptor> eventList = new ArrayList<>();
        for (Map.Entry<String, HashMap> entry : eventTable.entrySet()) {
            HashMap table = entry.getValue();
            Method add = (Method) table.get("add");
            Method remove = (Method) table.get("remove");
            if ((add != null) && (remove != null)) {
                Method get = (Method) table.get("get");
                Class<?> listenerType = (Class) table.get("listenerType");
                Method[] listenerMethods = (Method[]) table.get("listenerMethods");
                EventSetDescriptor eventSetDescriptor = new EventSetDescriptor(
                        Introspector.decapitalize((String) entry.getKey()), listenerType, listenerMethods, add,
                        remove, get);

                eventSetDescriptor.setUnicast(table.get("isUnicast") != null);
                eventList.add(eventSetDescriptor);
            }
        }
        EventSetDescriptor[] theEvents = new EventSetDescriptor[eventList
                .size()];
        eventList.toArray(theEvents);

        return theEvents;
    }

    private static void introspectListenerMethods(String type, Method theMethod, HashMap<String, HashMap> methodsTable) {
        String methodName = theMethod.getName();
        if (methodName == null) {
            return;
        }
        if ((!methodName.startsWith(type)) ||
                (!methodName.endsWith("Listener"))) {
            return;
        }
        String listenerName = methodName.substring(type.length());
        String eventName = listenerName.substring(0, listenerName
                .lastIndexOf("Listener"));
        if ((eventName == null) || (eventName.length() == 0)) {
            return;
        }
        Class[] paramTypes = theMethod.getParameterTypes();
        if ((paramTypes == null) || (paramTypes.length != 1)) {
            return;
        }
        Class<?> listenerType = paramTypes[0];
        if (!EventListener.class.isAssignableFrom(listenerType)) {
            return;
        }
        if (!listenerType.getName().endsWith(listenerName)) {
            return;
        }
        HashMap table = methodsTable.get(eventName);
        if (table == null) {
            table = new HashMap();
        }
        if (table.get("listenerType") == null) {
            table.put("listenerType", listenerType);
            table.put("listenerMethods",
                    introspectListenerMethods(listenerType));
        }
        table.put(type, theMethod);
        if (type.equals("add")) {
            Class[] exceptionTypes = theMethod.getExceptionTypes();
            if (exceptionTypes != null) {
                for (int i = 0; i < exceptionTypes.length; i++) {
                    if (exceptionTypes[i].getName().equals(
                            TooManyListenersException.class.getName())) {
                        table.put("isUnicast", "true");
                        break;
                    }
                }
            }
        }
        methodsTable.put(eventName, table);
    }

    private static Method[] introspectListenerMethods(Class<?> listenerType) {
        Method[] methods = listenerType.getDeclaredMethods();
        ArrayList<Method> list = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            Class[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length == 1) {
                if (EventObject.class.isAssignableFrom(paramTypes[0])) {
                    list.add(methods[i]);
                }
            }
        }
        Method[] matchedMethods = new Method[list.size()];
        list.toArray(matchedMethods);
        return matchedMethods;
    }

    private static void introspectGetListenerMethods(Method theMethod, HashMap<String, HashMap> methodsTable) {
        String type = "get";

        String methodName = theMethod.getName();
        if (methodName == null) {
            return;
        }
        if ((!methodName.startsWith(type)) ||
                (!methodName.endsWith("Listeners"))) {
            return;
        }
        String listenerName = methodName.substring(type.length(), methodName
                .length() - 1);
        String eventName = listenerName.substring(0, listenerName
                .lastIndexOf("Listener"));
        if ((eventName == null) || (eventName.length() == 0)) {
            return;
        }
        Class[] paramTypes = theMethod.getParameterTypes();
        if ((paramTypes == null) || (paramTypes.length != 0)) {
            return;
        }
        Class returnType = theMethod.getReturnType();
        if ((returnType.getComponentType() == null) ||
                (!returnType.getComponentType().getName().endsWith(
                        listenerName))) {
            return;
        }
        HashMap table = methodsTable.get(eventName);
        if (table == null) {
            table = new HashMap();
        }
        table.put(type, theMethod);
        methodsTable.put(eventName, table);
    }

    private static boolean isValidProperty(String propertyName) {
        return (propertyName != null) && (propertyName.length() != 0);
    }

    private static class PropertyComparator
            implements Comparator<PropertyDescriptor> {
        public int compare(PropertyDescriptor object1, PropertyDescriptor object2) {
            return object1.getName().compareTo(object2.getName());
        }
    }

    void init() {
        if (this.events == null) {
            this.events = new EventSetDescriptor[0];
        }
        if (this.properties == null) {
            this.properties = new PropertyDescriptor[0];
        }
        if (this.properties != null) {
            String defaultPropertyName = this.defaultPropertyIndex != -1 ? this.properties[this.defaultPropertyIndex]
                    .getName() :
                    null;
            Arrays.sort(this.properties, comparator);
            if (defaultPropertyName != null) {
                for (int i = 0; i < this.properties.length; i++) {
                    if (defaultPropertyName.equals(this.properties[i].getName())) {
                        this.defaultPropertyIndex = i;
                        break;
                    }
                }
            }
        }
    }
}