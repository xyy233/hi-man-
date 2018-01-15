/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   4:    */ import java.util.Arrays;
/*   5:    */ 
/*   6:    */ public class BeansUtils
/*   7:    */ {
/*   8: 25 */   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
/*   9:    */   public static final String NEW = "new";
/*  10:    */   public static final String NEWINSTANCE = "newInstance";
/*  11:    */   public static final String NEWARRAY = "newArray";
/*  12:    */   public static final String FORNAME = "forName";
/*  13:    */   public static final String GET = "get";
/*  14:    */   public static final String IS = "is";
/*  15:    */   public static final String SET = "set";
/*  16:    */   public static final String ADD = "add";
/*  17:    */   public static final String PUT = "put";
/*  18:    */   public static final String NULL = "null";
/*  19:    */   public static final String QUOTE = "\"\"";
/*  20:    */   private static final String EQUALS_METHOD = "equals";
/*  21:    */   
/*  22:    */   public static final int getHashCode(Object obj)
/*  23:    */   {
/*  24: 50 */     return obj != null ? obj.hashCode() : 0;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public static final int getHashCode(boolean bool)
/*  28:    */   {
/*  29: 54 */     return bool ? 1 : 0;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static String toASCIILowerCase(String string)
/*  33:    */   {
/*  34: 58 */     char[] charArray = string.toCharArray();
/*  35: 59 */     StringBuilder sb = new StringBuilder(charArray.length);
/*  36: 60 */     for (int index = 0; index < charArray.length; index++) {
/*  37: 61 */       if (('A' <= charArray[index]) && (charArray[index] <= 'Z')) {
/*  38: 62 */         sb.append((char)(charArray[index] + ' '));
/*  39:    */       } else {
/*  40: 64 */         sb.append(charArray[index]);
/*  41:    */       }
/*  42:    */     }
/*  43: 67 */     return sb.toString();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static String toASCIIUpperCase(String string)
/*  47:    */   {
/*  48: 71 */     char[] charArray = string.toCharArray();
/*  49: 72 */     StringBuilder sb = new StringBuilder(charArray.length);
/*  50: 73 */     for (int index = 0; index < charArray.length; index++) {
/*  51: 74 */       if (('a' <= charArray[index]) && (charArray[index] <= 'z')) {
/*  52: 75 */         sb.append((char)(charArray[index] - ' '));
/*  53:    */       } else {
/*  54: 77 */         sb.append(charArray[index]);
/*  55:    */       }
/*  56:    */     }
/*  57: 80 */     return sb.toString();
/*  58:    */   }
/*  59:    */   
/*  60:    */   public static boolean isPrimitiveWrapper(Class<?> wrapper, Class<?> base)
/*  61:    */   {
/*  62: 91 */     return ((base == Boolean.TYPE) && (wrapper == Boolean.class)) || ((base == Byte.TYPE) && (wrapper == Byte.class)) || ((base == Character.TYPE) && (wrapper == Character.class)) || ((base == Short.TYPE) && (wrapper == Short.class)) || ((base == Integer.TYPE) && (wrapper == Integer.class)) || ((base == Long.TYPE) && (wrapper == Long.class)) || ((base == Float.TYPE) && (wrapper == Float.class)) || ((base == Double.TYPE) && (wrapper == Double.class));
/*  63:    */   }
/*  64:    */   
/*  65: 96 */   private static final Class<?>[] EQUALS_PARAMETERS = { Object.class };
/*  66:    */   
/*  67:    */   public static boolean declaredEquals(Class<?> clazz)
/*  68:    */   {
/*  69: 99 */     for (Method declaredMethod : clazz.getDeclaredMethods()) {
/*  70:100 */       if (("equals".equals(declaredMethod.getName())) && 
/*  71:101 */         (Arrays.equals(declaredMethod.getParameterTypes(), 
/*  72:102 */         EQUALS_PARAMETERS))) {
/*  73:103 */         return true;
/*  74:    */       }
/*  75:    */     }
/*  76:106 */     return false;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static String idOfClass(Class<?> clazz)
/*  80:    */   {
/*  81:110 */     Class<?> theClass = clazz;
/*  82:111 */     StringBuilder sb = new StringBuilder();
/*  83:112 */     if (theClass.isArray()) {
/*  84:    */       do
/*  85:    */       {
/*  86:114 */         sb.append("Array");
/*  87:115 */         theClass = theClass.getComponentType();
/*  88:113 */       } while (
/*  89:    */       
/*  90:    */ 
/*  91:116 */         theClass.isArray());
/*  92:    */     }
/*  93:118 */     String clazzName = theClass.getName();
/*  94:119 */     clazzName = clazzName.substring(clazzName.lastIndexOf('.') + 1);
/*  95:120 */     return clazzName + sb.toString();
/*  96:    */   }
/*  97:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     org.apache.harmony.beans.BeansUtils

 * JD-Core Version:    0.7.0.1

 */