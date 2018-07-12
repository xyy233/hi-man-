/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Constructor;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.lang.reflect.Modifier;

/*   9:    */ public class PropertyDescriptor
/*  10:    */   extends FeatureDescriptor
/*  11:    */ {
/*  12:    */   private Method getter;
/*  13:    */   private Method setter;
/*  14:    */   private Class<?> propertyEditorClass;
/*  15:    */   boolean constrained;
/*  16:    */   boolean bound;
/*  17:    */   
/*  18:    */   public PropertyDescriptor(String propertyName, Class<?> beanClass, String getterName, String setterName)
/*  19:    */     throws IntrospectionException
/*  20:    */   {
/*  21: 45 */     if (beanClass == null) {
/*  22: 46 */       throw new IntrospectionException(Messages.getString("beans.03"));
/*  23:    */     }
/*  24: 48 */     if ((propertyName == null) || (propertyName.length() == 0)) {
/*  25: 49 */       throw new IntrospectionException(Messages.getString("beans.04"));
/*  26:    */     }
/*  27: 51 */     setName(propertyName);
/*  28: 52 */     if (getterName != null)
/*  29:    */     {
/*  30: 53 */       if (getterName.length() == 0) {
/*  31: 54 */         throw new IntrospectionException(
/*  32: 55 */           "read or write method cannot be empty.");
/*  33:    */       }
/*  34:    */       try
/*  35:    */       {
/*  36: 58 */         setReadMethod(beanClass, getterName);
/*  37:    */       }
/*  38:    */       catch (IntrospectionException e)
/*  39:    */       {
/*  40: 60 */         setReadMethod(beanClass, createDefaultMethodName(propertyName, 
/*  41: 61 */           "get"));
/*  42:    */       }
/*  43:    */     }
/*  44: 64 */     if (setterName != null)
/*  45:    */     {
/*  46: 65 */       if (setterName.length() == 0) {
/*  47: 66 */         throw new IntrospectionException(
/*  48: 67 */           "read or write method cannot be empty.");
/*  49:    */       }
/*  50: 69 */       setWriteMethod(beanClass, setterName);
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   public PropertyDescriptor(String propertyName, Method getter, Method setter)
/*  55:    */     throws IntrospectionException
/*  56:    */   {
/*  57: 76 */     if ((propertyName == null) || (propertyName.length() == 0)) {
/*  58: 77 */       throw new IntrospectionException(Messages.getString("beans.04"));
/*  59:    */     }
/*  60: 79 */     setName(propertyName);
/*  61: 80 */     setReadMethod(getter);
/*  62: 81 */     setWriteMethod(setter);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public PropertyDescriptor(String propertyName, Class<?> beanClass)
/*  66:    */     throws IntrospectionException
/*  67:    */   {
/*  68: 86 */     if (beanClass == null) {
/*  69: 87 */       throw new IntrospectionException(Messages.getString("beans.03"));
/*  70:    */     }
/*  71: 89 */     if ((propertyName == null) || (propertyName.length() == 0)) {
/*  72: 90 */       throw new IntrospectionException(Messages.getString("beans.04"));
/*  73:    */     }
/*  74: 92 */     setName(propertyName);
/*  75:    */     try
/*  76:    */     {
/*  77: 94 */       setReadMethod(beanClass, 
/*  78: 95 */         createDefaultMethodName(propertyName, "is"));
/*  79:    */     }
/*  80:    */     catch (Exception e)
/*  81:    */     {
/*  82: 97 */       setReadMethod(beanClass, createDefaultMethodName(propertyName, 
/*  83: 98 */         "get"));
/*  84:    */     }
/*  85:101 */     setWriteMethod(beanClass, createDefaultMethodName(propertyName, "set"));
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setWriteMethod(Method setter)
/*  89:    */     throws IntrospectionException
/*  90:    */   {
/*  91:105 */     if (setter != null)
/*  92:    */     {
/*  93:106 */       int modifiers = setter.getModifiers();
/*  94:107 */       if (!Modifier.isPublic(modifiers)) {
/*  95:108 */         throw new IntrospectionException(Messages.getString("beans.05"));
/*  96:    */       }
/*  97:110 */       Class[] parameterTypes = setter.getParameterTypes();
/*  98:111 */       if (parameterTypes.length != 1) {
/*  99:112 */         throw new IntrospectionException(Messages.getString("beans.06"));
/* 100:    */       }
/* 101:114 */       Class<?> parameterType = parameterTypes[0];
/* 102:115 */       Class<?> propertyType = getPropertyType();
/* 103:116 */       if ((propertyType != null) && (!propertyType.equals(parameterType))) {
/* 104:117 */         throw new IntrospectionException(Messages.getString("beans.07"));
/* 105:    */       }
/* 106:    */     }
/* 107:120 */     this.setter = setter;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setReadMethod(Method getter)
/* 111:    */     throws IntrospectionException
/* 112:    */   {
/* 113:124 */     if (getter != null)
/* 114:    */     {
/* 115:125 */       int modifiers = getter.getModifiers();
/* 116:126 */       if (!Modifier.isPublic(modifiers)) {
/* 117:127 */         throw new IntrospectionException(Messages.getString("beans.0A"));
/* 118:    */       }
/* 119:129 */       Class[] parameterTypes = getter.getParameterTypes();
/* 120:130 */       if (parameterTypes.length != 0) {
/* 121:131 */         throw new IntrospectionException(Messages.getString("beans.08"));
/* 122:    */       }
/* 123:133 */       Class<?> returnType = getter.getReturnType();
/* 124:134 */       if (returnType.equals(Void.TYPE)) {
/* 125:135 */         throw new IntrospectionException(Messages.getString("beans.33"));
/* 126:    */       }
/* 127:137 */       Class<?> propertyType = getPropertyType();
/* 128:138 */       if ((propertyType != null) && (!returnType.equals(propertyType))) {
/* 129:139 */         throw new IntrospectionException(Messages.getString("beans.09"));
/* 130:    */       }
/* 131:    */     }
/* 132:142 */     this.getter = getter;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public Method getWriteMethod()
/* 136:    */   {
/* 137:146 */     return this.setter;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public Method getReadMethod()
/* 141:    */   {
/* 142:150 */     return this.getter;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean equals(Object object)
/* 146:    */   {
/* 147:155 */     boolean result = object instanceof PropertyDescriptor;
/* 148:156 */     if (result)
/* 149:    */     {
/* 150:157 */       PropertyDescriptor pd = (PropertyDescriptor)object;
/* 151:158 */       boolean gettersAreEqual = ((this.getter == null) && 
/* 152:159 */         (pd.getReadMethod() == null)) || ((this.getter != null) && 
/* 153:160 */         (this.getter.equals(pd.getReadMethod())));
/* 154:161 */       boolean settersAreEqual = ((this.setter == null) && 
/* 155:162 */         (pd.getWriteMethod() == null)) || ((this.setter != null) && 
/* 156:163 */         (this.setter.equals(pd.getWriteMethod())));
/* 157:164 */       boolean propertyTypesAreEqual = getPropertyType() == pd
/* 158:165 */         .getPropertyType();
/* 159:166 */       boolean propertyEditorClassesAreEqual = 
/* 160:167 */         getPropertyEditorClass() == pd.getPropertyEditorClass();
/* 161:168 */       boolean boundPropertyAreEqual = isBound() == pd.isBound();
/* 162:169 */       boolean constrainedPropertyAreEqual = isConstrained() == pd
/* 163:170 */         .isConstrained();
/* 164:171 */       result = (gettersAreEqual) && (settersAreEqual) && 
/* 165:172 */         (propertyTypesAreEqual) && (propertyEditorClassesAreEqual) && 
/* 166:173 */         (boundPropertyAreEqual) && (constrainedPropertyAreEqual);
/* 167:    */     }
/* 168:175 */     return result;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public int hashCode()
/* 172:    */   {
/* 173:180 */     return BeansUtils.getHashCode(this.getter) + BeansUtils.getHashCode(this.setter) + 
/* 174:181 */       BeansUtils.getHashCode(getPropertyType()) + 
/* 175:182 */       BeansUtils.getHashCode(getPropertyEditorClass()) + 
/* 176:183 */       BeansUtils.getHashCode(isBound()) + 
/* 177:184 */       BeansUtils.getHashCode(isConstrained());
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setPropertyEditorClass(Class<?> propertyEditorClass)
/* 181:    */   {
/* 182:188 */     this.propertyEditorClass = propertyEditorClass;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Class<?> getPropertyType()
/* 186:    */   {
/* 187:192 */     Class<?> result = null;
/* 188:193 */     if (this.getter != null)
/* 189:    */     {
/* 190:194 */       result = this.getter.getReturnType();
/* 191:    */     }
/* 192:195 */     else if (this.setter != null)
/* 193:    */     {
/* 194:196 */       Class[] parameterTypes = this.setter.getParameterTypes();
/* 195:197 */       result = parameterTypes[0];
/* 196:    */     }
/* 197:199 */     return result;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public Class<?> getPropertyEditorClass()
/* 201:    */   {
/* 202:203 */     return this.propertyEditorClass;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setConstrained(boolean constrained)
/* 206:    */   {
/* 207:207 */     this.constrained = constrained;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public void setBound(boolean bound)
/* 211:    */   {
/* 212:211 */     this.bound = bound;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public boolean isConstrained()
/* 216:    */   {
/* 217:215 */     return this.constrained;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public boolean isBound()
/* 221:    */   {
/* 222:219 */     return this.bound;
/* 223:    */   }
/* 224:    */   
/* 225:    */   String createDefaultMethodName(String propertyName, String prefix)
/* 226:    */   {
/* 227:223 */     String result = null;
/* 228:224 */     if (propertyName != null)
/* 229:    */     {
/* 230:225 */       String bos = BeansUtils.toASCIIUpperCase(propertyName.substring(0, 1));
/* 231:226 */       String eos = propertyName.substring(1, propertyName.length());
/* 232:227 */       result = prefix + bos + eos;
/* 233:    */     }
/* 234:229 */     return result;
/* 235:    */   }
/* 236:    */   
/* 237:    */   void setReadMethod(Class<?> beanClass, String getterName)
/* 238:    */     throws IntrospectionException
/* 239:    */   {
/* 240:    */     try
/* 241:    */     {
/* 242:235 */       Method readMethod = beanClass.getMethod(getterName, new Class[0]);
/* 243:236 */       setReadMethod(readMethod);
/* 244:    */     }
/* 245:    */     catch (Exception e)
/* 246:    */     {
/* 247:238 */       throw new IntrospectionException(e.getLocalizedMessage());
/* 248:    */     }
/* 249:    */   }
/* 250:    */   
/* 251:    */   void setWriteMethod(Class<?> beanClass, String setterName)
/* 252:    */     throws IntrospectionException
/* 253:    */   {
/* 254:244 */     Method writeMethod = null;
/* 255:    */     try
/* 256:    */     {
/* 257:246 */       if (this.getter != null)
/* 258:    */       {
/* 259:247 */         writeMethod = beanClass.getMethod(setterName, 
/* 260:248 */           new Class[] { this.getter.getReturnType() });
/* 261:    */       }
/* 262:    */       else
/* 263:    */       {
/* 264:250 */         Class<?> clazz = beanClass;
/* 265:251 */         Method[] methods = (Method[])null;
/* 266:    */         do
/* 267:    */         {
/* 268:253 */           methods = clazz.getDeclaredMethods();
/* 269:254 */           for (Method method : methods) {
/* 270:255 */             if ((setterName.equals(method.getName())) && 
/* 271:256 */               (method.getParameterTypes().length == 1))
/* 272:    */             {
/* 273:257 */               writeMethod = method;
/* 274:258 */               break;
/* 275:    */             }
/* 276:    */           }
/* 277:262 */           clazz = clazz.getSuperclass();
/* 278:252 */           if (clazz == null) {
/* 279:    */             break;
/* 280:    */           }
/* 281:252 */         } while (writeMethod == null);
/* 282:    */       }
/* 283:    */     }
/* 284:    */     catch (Exception e)
/* 285:    */     {
/* 286:266 */       throw new IntrospectionException(e.getLocalizedMessage());
/* 287:    */     }
/* 288:268 */     if (writeMethod == null) {
/* 289:269 */       throw new IntrospectionException(Messages.getString(
/* 290:270 */         "beans.64", setterName));
/* 291:    */     }
/* 292:272 */     setWriteMethod(writeMethod);
/* 293:    */   }
/* 294:    */   
/* 295:    */   public PropertyEditor createPropertyEditor(Object bean)
/* 296:    */   {
/* 297:277 */     if (this.propertyEditorClass == null) {
/* 298:278 */       return null;
/* 299:    */     }
/* 300:280 */     if (!PropertyEditor.class.isAssignableFrom(this.propertyEditorClass)) {
/* 301:283 */       throw new ClassCastException(Messages.getString("beans.48"));
/* 302:    */     }
/* 303:    */     try
/* 304:    */     {
/* 305:    */       PropertyEditor editor;
/* 306:    */       try
/* 307:    */       {
/* 308:289 */         Constructor<?> constr = this.propertyEditorClass.getConstructor(new Class[] { Object.class });
/* 309:290 */         editor = (PropertyEditor)constr.newInstance(new Object[] { bean });
/* 310:    */       }
/* 311:    */       catch (NoSuchMethodException e)
/* 312:    */       {
/* 314:293 */         Constructor<?> constr = this.propertyEditorClass.getConstructor(new Class[0]);
/* 315:294 */         editor = (PropertyEditor)constr.newInstance(new Object[0]);
/* 316:    */       }
/* 319:302 */       return editor;
/* 320:    */     }
/* 321:    */     catch (Exception e)
/* 322:    */     {
/* 317:    */       RuntimeException re;
/* 323:298 */       re = new RuntimeException(
/* 324:299 */         Messages.getString("beans.47"), e);
/* 325:300 */       throw re;
/* 326:    */     }
/* 327:    */   }
/* 328:    */ }