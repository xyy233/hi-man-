package com.alipay.api.beans;

import android.media.Image;

public abstract interface BeanInfo
{
  public static final int ICON_COLOR_16x16 = 1;
  public static final int ICON_COLOR_32x32 = 2;
  public static final int ICON_MONO_16x16 = 3;
  public static final int ICON_MONO_32x32 = 4;
  
  public abstract PropertyDescriptor[] getPropertyDescriptors();
  
  public abstract MethodDescriptor[] getMethodDescriptors();
  
  public abstract EventSetDescriptor[] getEventSetDescriptors();
  
  public abstract BeanInfo[] getAdditionalBeanInfo();
  
  public abstract BeanDescriptor getBeanDescriptor();
  
  public abstract Image getIcon(int paramInt);
  
  public abstract int getDefaultPropertyIndex();
  
  public abstract int getDefaultEventIndex();
}



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.BeanInfo

 * JD-Core Version:    0.7.0.1

 */