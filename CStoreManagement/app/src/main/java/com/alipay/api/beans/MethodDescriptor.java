/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ 
/*   5:    */ public class MethodDescriptor
/*   6:    */   extends FeatureDescriptor
/*   7:    */ {
/*   8:    */   private Method method;
/*   9:    */   private ParameterDescriptor[] parameterDescriptors;
/*  10:    */   
/*  11:    */   public MethodDescriptor(Method method, ParameterDescriptor[] parameterDescriptors)
/*  12:    */   {
/*  13: 50 */     if (method == null) {
/*  14: 51 */       throw new NullPointerException();
/*  15:    */     }
/*  16: 53 */     this.method = method;
/*  17: 54 */     this.parameterDescriptors = parameterDescriptors;
/*  18:    */     
/*  19: 56 */     setName(method.getName());
/*  20:    */   }
/*  21:    */   
/*  22:    */   public MethodDescriptor(Method method)
/*  23:    */   {
/*  24: 72 */     if (method == null) {
/*  25: 73 */       throw new NullPointerException();
/*  26:    */     }
/*  27: 75 */     this.method = method;
/*  28:    */     
/*  29: 77 */     setName(method.getName());
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Method getMethod()
/*  33:    */   {
/*  34: 88 */     return this.method;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public ParameterDescriptor[] getParameterDescriptors()
/*  38:    */   {
/*  39:100 */     return this.parameterDescriptors;
/*  40:    */   }
/*  41:    */   
/*  42:    */   void merge(MethodDescriptor anotherMethod)
/*  43:    */   {
/*  44:104 */     super.merge(anotherMethod);
/*  45:105 */     if (this.method == null) {
/*  46:106 */       this.method = anotherMethod.method;
/*  47:    */     }
/*  48:108 */     if (this.parameterDescriptors == null) {
/*  49:109 */       this.parameterDescriptors = anotherMethod.parameterDescriptors;
/*  50:    */     }
/*  51:    */   }
/*  52:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.MethodDescriptor

 * JD-Core Version:    0.7.0.1

 */