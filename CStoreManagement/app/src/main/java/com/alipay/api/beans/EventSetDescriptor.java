package com.alipay.api.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TooManyListenersException;


class EventSetDescriptor
        extends FeatureDescriptor {
    private Class<?> listenerType;
    private ArrayList<MethodDescriptor> listenerMethodDescriptors;
    private Method[] listenerMethods;
    private Method getListenerMethod;
    private Method addListenerMethod;
    private Method removeListenerMethod;
    private boolean unicast;
    private boolean inDefaultEventSet = true;

    EventSetDescriptor(String eventSetName, Class<?> listenerType, Method[] listenerMethods, Method addListenerMethod, Method removeListenerMethod, Method getListenerMethod)
            throws IntrospectionException {
        setName(eventSetName);
        this.listenerType = listenerType;

        this.listenerMethods = listenerMethods;
        if (listenerMethods != null) {
            this.listenerMethodDescriptors = new ArrayList<>();
            for (Method element : listenerMethods) {
                this.listenerMethodDescriptors.add(new MethodDescriptor(element));
            }
        }
        this.addListenerMethod = addListenerMethod;
        this.removeListenerMethod = removeListenerMethod;
        this.getListenerMethod = getListenerMethod;
        this.unicast = isUnicastByDefault(addListenerMethod);
    }

    void setUnicast(boolean unicast) {
        this.unicast = unicast;
    }

    private static boolean isUnicastByDefault(Method addMethod) {
        if (addMethod != null) {
            Class[] exceptionTypes = addMethod.getExceptionTypes();
            for (Class<?> element : exceptionTypes) {
                if (element.equals(TooManyListenersException.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    void merge(EventSetDescriptor event) {
        super.merge(event);
        if (this.addListenerMethod == null) {
            this.addListenerMethod = event.addListenerMethod;
        }
        if (this.getListenerMethod == null) {
            this.getListenerMethod = event.getListenerMethod;
        }
        if (this.listenerMethodDescriptors == null) {
            this.listenerMethodDescriptors = event.listenerMethodDescriptors;
        }
        if (this.listenerMethods == null) {
            this.listenerMethods = event.listenerMethods;
        }
        if (this.listenerType == null) {
            this.listenerType = event.listenerType;
        }
        if (this.removeListenerMethod == null) {
            this.removeListenerMethod = event.removeListenerMethod;
        }
        this.inDefaultEventSet &= event.inDefaultEventSet;
    }
}