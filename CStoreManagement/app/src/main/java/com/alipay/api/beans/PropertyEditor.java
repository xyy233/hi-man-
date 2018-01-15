package com.alipay.api.beans;

import java.beans.PropertyChangeListener;

public abstract interface PropertyEditor
{
  public abstract void setAsText(String paramString)
    throws IllegalArgumentException;
  
  public abstract String[] getTags();
  
  public abstract String getJavaInitializationString();
  
  public abstract String getAsText();
  
  public abstract void setValue(Object paramObject);
  
  public abstract Object getValue();
  
  public abstract void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  public abstract void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);

  
  public abstract boolean supportsCustomEditor();
  
  public abstract boolean isPaintable();
}



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.PropertyEditor

 * JD-Core Version:    0.7.0.1

 */