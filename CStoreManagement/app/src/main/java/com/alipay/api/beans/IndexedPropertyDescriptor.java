/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
/*   6:    */ 
/*   7:    */ public class IndexedPropertyDescriptor
/*   8:    */   extends PropertyDescriptor
/*   9:    */ {
/*  10:    */   private Class<?> indexedPropertyType;
/*  11:    */   private Method indexedGetter;
/*  12:    */   private Method indexedSetter;
/*  13:    */   
/*  14:    */   public IndexedPropertyDescriptor(String propertyName, Class<?> beanClass, String getterName, String setterName, String indexedGetterName, String indexedSetterName)
/*  15:    */     throws IntrospectionException
/*  16:    */   {
/*  17: 56 */     super(propertyName, beanClass, getterName, setterName);
/*  18: 57 */     setIndexedByName(beanClass, indexedGetterName, indexedSetterName);
/*  19:    */   }
/*  20:    */   
/*  21:    */   private void setIndexedByName(Class<?> beanClass, String indexedGetterName, String indexedSetterName)
/*  22:    */     throws IntrospectionException
/*  23:    */   {
/*  24: 63 */     String theIndexedGetterName = indexedGetterName;
/*  25: 64 */     if (theIndexedGetterName == null)
/*  26:    */     {
/*  27: 65 */       if (indexedSetterName != null) {
/*  28: 66 */         setIndexedWriteMethod(beanClass, indexedSetterName);
/*  29:    */       }
/*  30:    */     }
/*  31:    */     else
/*  32:    */     {
/*  33: 69 */       if (theIndexedGetterName.length() == 0) {
/*  34: 70 */         theIndexedGetterName = "get" + this.name;
/*  35:    */       }
/*  36: 72 */       setIndexedReadMethod(beanClass, theIndexedGetterName);
/*  37: 73 */       if (indexedSetterName != null) {
/*  38: 74 */         setIndexedWriteMethod(beanClass, indexedSetterName, 
/*  39: 75 */           this.indexedPropertyType);
/*  40:    */       }
/*  41:    */     }
/*  42: 79 */     if (!isCompatible()) {
/*  43: 81 */       throw new IntrospectionException(Messages.getString("beans.57"));
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   private boolean isCompatible()
/*  48:    */   {
/*  49: 86 */     Class<?> propertyType = getPropertyType();
/*  50: 88 */     if (propertyType == null) {
/*  51: 89 */       return true;
/*  52:    */     }
/*  53: 91 */     Class<?> componentTypeOfProperty = propertyType.getComponentType();
/*  54: 92 */     if (componentTypeOfProperty == null) {
/*  55: 93 */       return false;
/*  56:    */     }
/*  57: 95 */     if (this.indexedPropertyType == null) {
/*  58: 96 */       return false;
/*  59:    */     }
/*  60: 99 */     return componentTypeOfProperty.getName().equals(
/*  61:100 */       this.indexedPropertyType.getName());
/*  62:    */   }
/*  63:    */   
/*  64:    */   public IndexedPropertyDescriptor(String propertyName, Method getter, Method setter, Method indexedGetter, Method indexedSetter)
/*  65:    */     throws IntrospectionException
/*  66:    */   {
/*  67:121 */     super(propertyName, getter, setter);
/*  68:122 */     if (indexedGetter != null)
/*  69:    */     {
/*  70:123 */       internalSetIndexedReadMethod(indexedGetter);
/*  71:124 */       internalSetIndexedWriteMethod(indexedSetter, true);
/*  72:    */     }
/*  73:    */     else
/*  74:    */     {
/*  75:126 */       internalSetIndexedWriteMethod(indexedSetter, true);
/*  76:127 */       internalSetIndexedReadMethod(indexedGetter);
/*  77:    */     }
/*  78:130 */     if (!isCompatible()) {
/*  79:132 */       throw new IntrospectionException(Messages.getString("beans.57"));
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public IndexedPropertyDescriptor(String propertyName, Class<?> beanClass)
/*  84:    */     throws IntrospectionException
/*  85:    */   {
/*  86:147 */     super(propertyName, beanClass);
/*  87:148 */     setIndexedByName(beanClass, "get"
/*  88:149 */       .concat(initialUpperCase(propertyName)), "set"
/*  89:150 */       .concat(initialUpperCase(propertyName)));
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setIndexedReadMethod(Method indexedGetter)
/*  93:    */     throws IntrospectionException
/*  94:    */   {
/*  95:162 */     internalSetIndexedReadMethod(indexedGetter);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setIndexedWriteMethod(Method indexedSetter)
/*  99:    */     throws IntrospectionException
/* 100:    */   {
/* 101:174 */     internalSetIndexedWriteMethod(indexedSetter, false);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Method getIndexedWriteMethod()
/* 105:    */   {
/* 106:183 */     return this.indexedSetter;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Method getIndexedReadMethod()
/* 110:    */   {
/* 111:192 */     return this.indexedGetter;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public boolean equals(Object obj)
/* 115:    */   {
/* 116:207 */     if (!(obj instanceof IndexedPropertyDescriptor)) {
/* 117:208 */       return false;
/* 118:    */     }
/* 119:211 */     IndexedPropertyDescriptor other = (IndexedPropertyDescriptor)obj;
/* 120:    */     
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:218 */     return (super.equals(other)) && (this.indexedPropertyType == null ? other.indexedPropertyType == null : this.indexedPropertyType.equals(other.indexedPropertyType)) && (this.indexedGetter == null ? other.indexedGetter == null : this.indexedGetter.equals(other.indexedGetter)) && (this.indexedSetter == null ? other.indexedSetter == null : this.indexedSetter.equals(other.indexedSetter));
/* 127:    */   }
/* 128:    */   
/* 129:    */   public int hashCode()
/* 130:    */   {
/* 131:226 */     return super.hashCode() + BeansUtils.getHashCode(this.indexedPropertyType) + 
/* 132:227 */       BeansUtils.getHashCode(this.indexedGetter) + 
/* 133:228 */       BeansUtils.getHashCode(this.indexedSetter);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public Class<?> getIndexedPropertyType()
/* 137:    */   {
/* 138:237 */     return this.indexedPropertyType;
/* 139:    */   }
/* 140:    */   
/* 141:    */   private void setIndexedReadMethod(Class<?> beanClass, String indexedGetterName)
/* 142:    */     throws IntrospectionException
/* 143:    */   {
/* 160:    */   }
/* 161:    */   
/* 162:    */   private void internalSetIndexedReadMethod(Method indexGetter)
/* 163:    */     throws IntrospectionException
/* 164:    */   {
/* 165:259 */     if (indexGetter == null)
/* 166:    */     {
/* 167:260 */       if (this.indexedSetter == null)
/* 168:    */       {
/* 169:261 */         if (getPropertyType() != null) {
/* 170:263 */           throw new IntrospectionException(
/* 171:264 */             Messages.getString("beans.5A"));
/* 172:    */         }
/* 173:266 */         this.indexedPropertyType = null;
/* 174:    */       }
/* 175:268 */       this.indexedGetter = null;
/* 176:269 */       return;
/* 177:    */     }
/* 178:272 */     if ((indexGetter.getParameterTypes().length != 1) || 
/* 179:273 */       (indexGetter.getParameterTypes()[0] != Integer.TYPE)) {
/* 180:275 */       throw new IntrospectionException(Messages.getString("beans.5B"));
/* 181:    */     }
/* 182:277 */     Class<?> indexedReadType = indexGetter.getReturnType();
/* 183:278 */     if (indexedReadType == Void.TYPE) {
/* 184:280 */       throw new IntrospectionException(Messages.getString("beans.5B"));
/* 185:    */     }
/* 186:281 */     if ((this.indexedSetter != null) && 
/* 187:282 */       (indexGetter.getReturnType() != this.indexedSetter
/* 188:283 */       .getParameterTypes()[1])) {
/* 189:285 */       throw new IntrospectionException(Messages.getString("beans.5A"));
/* 190:    */     }
/* 191:290 */     if (this.indexedGetter == null) {
/* 192:291 */       this.indexedPropertyType = indexedReadType;
/* 193:293 */     } else if (this.indexedPropertyType != indexedReadType) {
/* 194:295 */       throw new IntrospectionException(Messages.getString("beans.5A"));
/* 195:    */     }
/* 196:300 */     this.indexedGetter = indexGetter;
/* 197:    */   }
/* 198:    */   
/* 199:    */   private void setIndexedWriteMethod(Class<?> beanClass, String indexedSetterName)
/* 200:    */     throws IntrospectionException
/* 201:    */   {
/* 202:305 */     Method setter = null;
/* 203:    */     try
/* 204:    */     {
/* 205:307 */       setter = beanClass.getMethod(indexedSetterName, new Class[] {
/* 206:308 */         Integer.TYPE, getPropertyType().getComponentType() });
/* 207:    */     }
/* 208:    */     catch (SecurityException e)
/* 209:    */     {
/* 210:311 */       throw new IntrospectionException(Messages.getString("beans.5C"));
/* 211:    */     }
/* 212:    */     catch (NoSuchMethodException e)
/* 213:    */     {
/* 214:314 */       throw new IntrospectionException(Messages.getString("beans.5D"));
/* 215:    */     }
/* 216:316 */     internalSetIndexedWriteMethod(setter, true);
/* 217:    */   }
/* 218:    */   
/* 219:    */   private void setIndexedWriteMethod(Class<?> beanClass, String indexedSetterName, Class<?> argType)
/* 220:    */     throws IntrospectionException
/* 221:    */   {
/* 222:    */     try
/* 223:    */     {
/* 224:323 */       Method setter = beanClass.getMethod(indexedSetterName, new Class[] {
/* 225:324 */         Integer.TYPE, argType });
/* 226:325 */       internalSetIndexedWriteMethod(setter, true);
/* 227:    */     }
/* 228:    */     catch (NoSuchMethodException exception)
/* 229:    */     {
/* 230:328 */       throw new IntrospectionException(Messages.getString("beans.5D"));
/* 231:    */     }
/* 232:    */     catch (SecurityException exception)
/* 233:    */     {
/* 234:331 */       throw new IntrospectionException(Messages.getString("beans.5C"));
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   private void internalSetIndexedWriteMethod(Method indexSetter, boolean initialize)
/* 239:    */     throws IntrospectionException
/* 240:    */   {
/* 241:338 */     if (indexSetter == null)
/* 242:    */     {
/* 243:339 */       if (this.indexedGetter == null)
/* 244:    */       {
/* 245:340 */         if (getPropertyType() != null) {
/* 246:342 */           throw new IntrospectionException(
/* 247:343 */             Messages.getString("beans.5E"));
/* 248:    */         }
/* 249:345 */         this.indexedPropertyType = null;
/* 250:    */       }
/* 251:347 */       this.indexedSetter = null;
/* 252:348 */       return;
/* 253:    */     }
/* 254:352 */     Class[] indexedSetterArgs = indexSetter.getParameterTypes();
/* 255:353 */     if (indexedSetterArgs.length != 2) {
/* 256:355 */       throw new IntrospectionException(Messages.getString("beans.5F"));
/* 257:    */     }
/* 258:357 */     if (indexedSetterArgs[0] != Integer.TYPE) {
/* 259:359 */       throw new IntrospectionException(Messages.getString("beans.60"));
/* 260:    */     }
/* 261:364 */     Class<?> indexedWriteType = indexedSetterArgs[1];
/* 262:365 */     if ((initialize) && (this.indexedGetter == null)) {
/* 263:366 */       this.indexedPropertyType = indexedWriteType;
/* 264:368 */     } else if (this.indexedPropertyType != indexedWriteType) {
/* 265:370 */       throw new IntrospectionException(Messages.getString("beans.61"));
/* 266:    */     }
/* 267:375 */     this.indexedSetter = indexSetter;
/* 268:    */   }
/* 269:    */   
/* 270:    */   private static String initialUpperCase(String string)
/* 271:    */   {
/* 272:379 */     if (Character.isUpperCase(string.charAt(0))) {
/* 273:380 */       return string;
/* 274:    */     }
/* 275:383 */     String initial = string.substring(0, 1).toUpperCase();
/* 276:384 */     return initial.concat(string.substring(1));
/* 277:    */   }
/* 278:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.IndexedPropertyDescriptor

 * JD-Core Version:    0.7.0.1

 */