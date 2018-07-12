/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ public class BeanDescriptor
/*   4:    */   extends FeatureDescriptor
/*   5:    */ {
/*   6:    */   private Class<?> beanClass;
/*   7:    */   private Class<?> customizerClass;
/*   8:    */   
/*   9:    */   public BeanDescriptor(Class<?> beanClass, Class<?> customizerClass)
/*  10:    */   {
/*  11: 44 */     if (beanClass == null) {
/*  12: 45 */       throw new NullPointerException();
/*  13:    */     }
/*  14: 47 */     setName(getShortClassName(beanClass));
/*  15: 48 */     this.beanClass = beanClass;
/*  16: 49 */     this.customizerClass = customizerClass;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public BeanDescriptor(Class<?> beanClass)
/*  20:    */   {
/*  21: 63 */     this(beanClass, null);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public Class<?> getCustomizerClass()
/*  25:    */   {
/*  26: 74 */     return this.customizerClass;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Class<?> getBeanClass()
/*  30:    */   {
/*  31: 85 */     return this.beanClass;
/*  32:    */   }
/*  33:    */   
/*  34:    */   private String getShortClassName(Class<?> leguminaClass)
/*  35:    */   {
/*  36: 98 */     if (leguminaClass == null) {
/*  37: 99 */       return null;
/*  38:    */     }
/*  39:101 */     String beanClassName = leguminaClass.getName();
/*  40:102 */     int lastIndex = beanClassName.lastIndexOf(".");
/*  41:103 */     return lastIndex == -1 ? beanClassName : beanClassName.substring(lastIndex + 1);
/*  42:    */   }
/*  43:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.BeanDescriptor

 * JD-Core Version:    0.7.0.1

 */