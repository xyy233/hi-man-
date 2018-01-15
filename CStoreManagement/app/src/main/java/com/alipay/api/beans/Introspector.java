/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.WeakHashMap;
/*   6:    */ 
/*   7:    */ public class Introspector
/*   8:    */ {
/*   9:    */   public static final int IGNORE_ALL_BEANINFO = 3;
/*  10:    */   public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
/*  11:    */   public static final int USE_ALL_BEANINFO = 1;
/*  12:    */   private static final String DEFAULT_BEANINFO_SEARCHPATH = "sun.beans.infos";
/*  13: 84 */   private static String[] searchPath = { "sun.beans.infos" };
/*  14:    */   private static final int DEFAULT_CAPACITY = 128;
/*  15: 89 */   private static Map<Class<?>, StandardBeanInfo> theCache = Collections.synchronizedMap(new WeakHashMap(128));
/*  16:    */   
/*  17:    */   public static String decapitalize(String name)
/*  18:    */   {
/*  19:108 */     if (name == null) {
/*  20:109 */       return null;
/*  21:    */     }
/*  22:114 */     if ((name.length() == 0) || ((name.length() > 1) && (Character.isUpperCase(name.charAt(1))))) {
/*  23:115 */       return name;
/*  24:    */     }
/*  25:118 */     char[] chars = name.toCharArray();
/*  26:119 */     chars[0] = Character.toLowerCase(chars[0]);
/*  27:120 */     return new String(chars);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public static void flushCaches()
/*  31:    */   {
/*  32:130 */     theCache.clear();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static void flushFromCaches(Class<?> clazz)
/*  36:    */   {
/*  37:140 */     if (clazz == null) {
/*  38:141 */       throw new NullPointerException();
/*  39:    */     }
/*  40:143 */     theCache.remove(clazz);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public static BeanInfo getBeanInfo(Class<?> beanClass)
/*  44:    */     throws IntrospectionException
/*  45:    */   {
/*  46:163 */     StandardBeanInfo beanInfo = (StandardBeanInfo)theCache.get(beanClass);
/*  47:164 */     if (beanInfo == null)
/*  48:    */     {
/*  49:165 */       beanInfo = getBeanInfoImplAndInit(beanClass, null, 1);
/*  50:166 */       theCache.put(beanClass, beanInfo);
/*  51:    */     }
/*  52:168 */     return beanInfo;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static BeanInfo getBeanInfo(Class<?> beanClass, Class<?> stopClass)
/*  56:    */     throws IntrospectionException
/*  57:    */   {
/*  58:192 */     if (stopClass == null) {
/*  59:194 */       return getBeanInfo(beanClass);
/*  60:    */     }
/*  61:196 */     return getBeanInfoImplAndInit(beanClass, stopClass, 1);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static BeanInfo getBeanInfo(Class<?> beanClass, int flags)
/*  65:    */     throws IntrospectionException
/*  66:    */   {
/*  67:229 */     if (flags == 1) {
/*  68:231 */       return getBeanInfo(beanClass);
/*  69:    */     }
/*  70:233 */     return getBeanInfoImplAndInit(beanClass, null, flags);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static String[] getBeanInfoSearchPath()
/*  74:    */   {
/*  75:242 */     String[] path = new String[searchPath.length];
/*  76:243 */     System.arraycopy(searchPath, 0, path, 0, searchPath.length);
/*  77:244 */     return path;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static void setBeanInfoSearchPath(String[] path)
/*  81:    */   {
/*  82:253 */     if (System.getSecurityManager() != null) {
/*  83:254 */       System.getSecurityManager().checkPropertiesAccess();
/*  84:    */     }
/*  85:256 */     searchPath = path;
/*  86:    */   }
/*  87:    */   
/*  88:    */   private static StandardBeanInfo getBeanInfoImpl(Class<?> beanClass, Class<?> stopClass, int flags)
/*  89:    */     throws IntrospectionException
/*  90:    */   {
/*  91:261 */     BeanInfo explicitInfo = null;
/*  92:262 */     if (flags == 1) {
/*  93:263 */       explicitInfo = getExplicitBeanInfo(beanClass);
/*  94:    */     }
/*  95:265 */     StandardBeanInfo beanInfo = new StandardBeanInfo(beanClass, explicitInfo, stopClass);
/*  96:267 */     if (beanInfo.additionalBeanInfo != null) {
/*  97:268 */       for (int i = beanInfo.additionalBeanInfo.length - 1; i >= 0; i--)
/*  98:    */       {
/*  99:269 */         BeanInfo info = beanInfo.additionalBeanInfo[i];
/* 100:270 */         beanInfo.mergeBeanInfo(info, true);
/* 101:    */       }
/* 102:    */     }
/* 103:275 */     Class<?> beanSuperClass = beanClass.getSuperclass();
/* 104:276 */     if (beanSuperClass != stopClass)
/* 105:    */     {
/* 106:277 */       if (beanSuperClass == null) {
/* 107:278 */         throw new IntrospectionException(
/* 108:279 */           "Stop class is not super class of bean class");
/* 109:    */       }
/* 110:280 */       int superflags = flags == 2 ? 1 : 
/* 111:281 */         flags;
/* 112:282 */       BeanInfo superBeanInfo = getBeanInfoImpl(beanSuperClass, stopClass, 
/* 113:283 */         superflags);
/* 114:284 */       if (superBeanInfo != null) {
/* 115:285 */         beanInfo.mergeBeanInfo(superBeanInfo, false);
/* 116:    */       }
/* 117:    */     }
/* 118:288 */     return beanInfo;
/* 119:    */   }
/* 120:    */   
/* 121:    */   private static BeanInfo getExplicitBeanInfo(Class<?> beanClass)
/* 122:    */   {
/* 123:292 */     String beanInfoClassName = beanClass.getName() + "BeanInfo";
/* 124:    */     try
/* 125:    */     {
/* 126:294 */       return loadBeanInfo(beanInfoClassName, beanClass);
/* 127:    */     }
/* 128:    */     catch (Exception localException1)
/* 129:    */     {
/* 130:299 */       int index = beanInfoClassName.lastIndexOf('.');
/* 131:300 */       String beanInfoName = index >= 0 ? beanInfoClassName
/* 132:301 */         .substring(index + 1) : beanInfoClassName;
/* 133:302 */       BeanInfo theBeanInfo = null;
/* 134:303 */       BeanDescriptor beanDescriptor = null;
/* 135:304 */       for (int i = 0; i < searchPath.length; i++)
/* 136:    */       {
/* 137:305 */         beanInfoClassName = searchPath[i] + "." + beanInfoName;
/* 138:    */         try
/* 139:    */         {
/* 140:307 */           theBeanInfo = loadBeanInfo(beanInfoClassName, beanClass);
/* 141:    */         }
/* 142:    */         catch (Exception e)
/* 143:    */         {
/* 144:    */           continue;
/* 145:    */         }
/* 146:312 */         beanDescriptor = theBeanInfo.getBeanDescriptor();
/* 147:313 */         if ((beanDescriptor != null) && 
/* 148:314 */           (beanClass == beanDescriptor.getBeanClass())) {
/* 149:315 */           return theBeanInfo;
/* 150:    */         }
/* 151:    */       }
/* 152:318 */       if (BeanInfo.class.isAssignableFrom(beanClass)) {
/* 153:    */         try
/* 154:    */         {
/* 155:320 */           return loadBeanInfo(beanClass.getName(), beanClass);
/* 156:    */         }
/* 157:    */         catch (Exception localException2) {}
/* 158:    */       }
/* 159:    */     }
/* 160:325 */     return null;
/* 161:    */   }
/* 162:    */   
/* 163:    */   private static BeanInfo loadBeanInfo(String beanInfoClassName, Class<?> beanClass)
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:    */     try
/* 167:    */     {
/* 168:343 */       ClassLoader cl = beanClass.getClassLoader();
/* 169:344 */       if (cl != null) {
/* 170:345 */         return 
/* 171:346 */           (BeanInfo)Class.forName(beanInfoClassName, true, beanClass.getClassLoader()).newInstance();
/* 172:    */       }
/* 173:    */     }
/* 174:    */     catch (Exception localException)
/* 175:    */     {
/* 176:    */       try
/* 177:    */       {
/* 178:352 */         return 
/* 179:353 */           (BeanInfo)Class.forName(beanInfoClassName, true, ClassLoader.getSystemClassLoader()).newInstance();
/* 180:    */       }
/* 181:    */       catch (Exception localException1) {}
/* 182:    */     }
/* 183:357 */     return 
/* 184:358 */       (BeanInfo)Class.forName(beanInfoClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
/* 185:    */   }
/* 186:    */   
/* 187:    */   private static StandardBeanInfo getBeanInfoImplAndInit(Class<?> beanClass, Class<?> stopClass, int flag)
/* 188:    */     throws IntrospectionException
/* 189:    */   {
/* 190:363 */     StandardBeanInfo standardBeanInfo = getBeanInfoImpl(beanClass, 
/* 191:364 */       stopClass, flag);
/* 192:365 */     standardBeanInfo.init();
/* 193:366 */     return standardBeanInfo;
/* 194:    */   }
/* 195:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.Introspector

 * JD-Core Version:    0.7.0.1

 */