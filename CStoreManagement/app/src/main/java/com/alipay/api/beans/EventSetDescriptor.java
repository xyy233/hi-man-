/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TooManyListenersException;

/*   4:    */
/*   5:    */
/*   6:    */
/*   7:    */ 
/*   8:    */ public class EventSetDescriptor
/*   9:    */   extends FeatureDescriptor
/*  10:    */ {
/*  11:    */   private Class<?> listenerType;
/*  12:    */   private ArrayList<MethodDescriptor> listenerMethodDescriptors;
/*  13:    */   private Method[] listenerMethods;
/*  14:    */   private Method getListenerMethod;
/*  15:    */   private Method addListenerMethod;
/*  16:    */   private Method removeListenerMethod;
/*  17:    */   private boolean unicast;
/*  18: 44 */   private boolean inDefaultEventSet = true;
/*  19:    */   
/*  20:    */   public EventSetDescriptor(Class<?> sourceClass, String eventSetName, Class<?> listenerType, String listenerMethodName)
/*  21:    */     throws IntrospectionException
/*  22:    */   {
/*  23: 49 */     checkNotNull(sourceClass, eventSetName, listenerType, 
/*  24: 50 */       listenerMethodName);
/*  25: 51 */     setName(eventSetName);
/*  26: 52 */     this.listenerType = listenerType;
/*  27:    */     
/*  28: 54 */     Method method = findListenerMethodByName(listenerMethodName);
/*  29: 55 */     checkEventType(eventSetName, method);
/*  30: 56 */     this.listenerMethodDescriptors = new ArrayList();
/*  31: 57 */     this.listenerMethodDescriptors.add(new MethodDescriptor(method));
/*  32: 58 */     this.addListenerMethod = findMethodByPrefix(sourceClass, "add", "");
/*  33: 59 */     this.removeListenerMethod = findMethodByPrefix(sourceClass, "remove", "");
/*  34: 61 */     if ((this.addListenerMethod == null) || (this.removeListenerMethod == null)) {
/*  35: 62 */       throw new IntrospectionException(Messages.getString("beans.38"));
/*  36:    */     }
/*  37: 65 */     this.getListenerMethod = findMethodByPrefix(sourceClass, "get", "s");
/*  38: 66 */     this.unicast = isUnicastByDefault(this.addListenerMethod);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public EventSetDescriptor(Class<?> sourceClass, String eventSetName, Class<?> listenerType, String[] listenerMethodNames, String addListenerMethodName, String removeListenerMethodName)
/*  42:    */     throws IntrospectionException
/*  43:    */   {
/*  44: 74 */     this(sourceClass, eventSetName, listenerType, listenerMethodNames, addListenerMethodName, removeListenerMethodName, null);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public EventSetDescriptor(Class<?> sourceClass, String eventSetName, Class<?> listenerType, String[] listenerMethodNames, String addListenerMethodName, String removeListenerMethodName, String getListenerMethodName)
/*  48:    */     throws IntrospectionException
/*  49:    */   {
/*  50: 83 */     checkNotNull(sourceClass, eventSetName, listenerType, 
/*  51: 84 */       listenerMethodNames);
/*  52:    */     
/*  53: 86 */     setName(eventSetName);
/*  54: 87 */     this.listenerType = listenerType;
/*  55:    */     
/*  56: 89 */     this.listenerMethodDescriptors = new ArrayList();
/*  57: 90 */     for (String element : listenerMethodNames)
/*  58:    */     {
/*  59: 91 */       Method m = findListenerMethodByName(element);
/*  60:    */       
/*  61:    */ 
/*  62: 94 */       this.listenerMethodDescriptors.add(new MethodDescriptor(m));
/*  63:    */     }
/*  64: 97 */     if (addListenerMethodName != null) {
/*  65: 98 */       this.addListenerMethod = findAddRemoveListenerMethod(sourceClass, 
/*  66: 99 */         addListenerMethodName);
/*  67:    */     }
/*  68:101 */     if (removeListenerMethodName != null) {
/*  69:102 */       this.removeListenerMethod = findAddRemoveListenerMethod(
/*  70:103 */         sourceClass, removeListenerMethodName);
/*  71:    */     }
/*  72:105 */     if (getListenerMethodName != null) {
/*  73:106 */       this.getListenerMethod = findGetListenerMethod(sourceClass, 
/*  74:107 */         getListenerMethodName);
/*  75:    */     }
/*  76:109 */     this.unicast = isUnicastByDefault(this.addListenerMethod);
/*  77:    */   }
/*  78:    */   
/*  79:    */   private Method findListenerMethodByName(String listenerMethodName)
/*  80:    */     throws IntrospectionException
/*  81:    */   {
/*  82:114 */     Method result = null;
/*  83:115 */     Method[] methods = this.listenerType.getMethods();
/*  84:116 */     for (Method method : methods) {
/*  85:117 */       if (listenerMethodName.equals(method.getName()))
/*  86:    */       {
/*  87:118 */         Class[] paramTypes = method.getParameterTypes();
/*  88:119 */         if ((paramTypes.length == 1) && 
/*  89:120 */           (paramTypes[0].getName().endsWith("Event")))
/*  90:    */         {
/*  91:121 */           result = method;
/*  92:122 */           break;
/*  93:    */         }
/*  94:    */       }
/*  95:    */     }
/*  96:127 */     if (result == null) {
/*  97:128 */       throw new IntrospectionException(Messages.getString("beans.31", 
/*  98:129 */         listenerMethodName, this.listenerType.getName()));
/*  99:    */     }
/* 100:131 */     return result;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public EventSetDescriptor(String eventSetName, Class<?> listenerType, Method[] listenerMethods, Method addListenerMethod, Method removeListenerMethod)
/* 104:    */     throws IntrospectionException
/* 105:    */   {
/* 106:139 */     this(eventSetName, listenerType, listenerMethods, addListenerMethod, removeListenerMethod, null);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public EventSetDescriptor(String eventSetName, Class<?> listenerType, Method[] listenerMethods, Method addListenerMethod, Method removeListenerMethod, Method getListenerMethod)
/* 110:    */     throws IntrospectionException
/* 111:    */   {
/* 112:147 */     setName(eventSetName);
/* 113:148 */     this.listenerType = listenerType;
/* 114:    */     
/* 115:150 */     this.listenerMethods = listenerMethods;
/* 116:151 */     if (listenerMethods != null)
/* 117:    */     {
/* 118:152 */       this.listenerMethodDescriptors = new ArrayList();
/* 119:154 */       for (Method element : listenerMethods) {
/* 120:159 */         this.listenerMethodDescriptors.add(new MethodDescriptor(element));
/* 121:    */       }
/* 122:    */     }
/* 123:164 */     this.addListenerMethod = addListenerMethod;
/* 124:165 */     this.removeListenerMethod = removeListenerMethod;
/* 125:166 */     this.getListenerMethod = getListenerMethod;
/* 126:167 */     this.unicast = isUnicastByDefault(addListenerMethod);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public EventSetDescriptor(String eventSetName, Class<?> listenerType, MethodDescriptor[] listenerMethodDescriptors, Method addListenerMethod, Method removeListenerMethod)
/* 130:    */     throws IntrospectionException
/* 131:    */   {
/* 132:176 */     this(eventSetName, listenerType, null, addListenerMethod, removeListenerMethod, null);
/* 133:178 */     if (listenerMethodDescriptors != null)
/* 134:    */     {
/* 135:179 */       this.listenerMethodDescriptors = new ArrayList();
/* 136:181 */       for (MethodDescriptor element : listenerMethodDescriptors) {
/* 137:182 */         this.listenerMethodDescriptors.add(element);
/* 138:    */       }
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   private void checkNotNull(Object sourceClass, Object eventSetName, Object alistenerType, Object listenerMethodName)
/* 143:    */   {
/* 144:191 */     if (sourceClass == null) {
/* 145:192 */       throw new NullPointerException(Messages.getString("beans.0C"));
/* 146:    */     }
/* 147:194 */     if (eventSetName == null) {
/* 148:195 */       throw new NullPointerException(Messages.getString("beans.53"));
/* 149:    */     }
/* 150:197 */     if (alistenerType == null) {
/* 151:198 */       throw new NullPointerException(Messages.getString("beans.54"));
/* 152:    */     }
/* 153:200 */     if (listenerMethodName == null) {
/* 154:201 */       throw new NullPointerException(Messages.getString("beans.52"));
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   private static void checkEventType(String eventSetName, Method listenerMethod)
/* 159:    */     throws IntrospectionException
/* 160:    */   {
/* 161:217 */     Class[] params = listenerMethod.getParameterTypes();
/* 162:218 */     String firstParamTypeName = null;
/* 163:219 */     String eventTypeName = prepareEventTypeName(eventSetName);
/* 164:221 */     if (params.length > 0) {
/* 165:222 */       firstParamTypeName = extractShortClassName(params[0]
/* 166:223 */         .getName());
/* 167:    */     }
/* 168:226 */     if ((firstParamTypeName == null) || 
/* 169:227 */       (!firstParamTypeName.equals(eventTypeName))) {
/* 170:228 */       throw new IntrospectionException(Messages.getString("beans.51", 
/* 171:229 */         listenerMethod.getName(), eventTypeName));
/* 172:    */     }
/* 173:    */   }
/* 174:    */   
/* 175:    */   private static String extractShortClassName(String fullClassName)
/* 176:    */   {
/* 177:238 */     int k = fullClassName.lastIndexOf('$');
/* 178:239 */     k = k == -1 ? fullClassName.lastIndexOf('.') : k;
/* 179:240 */     return fullClassName.substring(k + 1);
/* 180:    */   }
/* 181:    */   
/* 182:    */   private static String prepareEventTypeName(String eventSetName)
/* 183:    */   {
/* 184:244 */     StringBuilder sb = new StringBuilder();
/* 185:246 */     if ((eventSetName != null) && (eventSetName.length() > 0))
/* 186:    */     {
/* 187:247 */       sb.append(Character.toUpperCase(eventSetName.charAt(0)));
/* 188:249 */       if (eventSetName.length() > 1) {
/* 189:250 */         sb.append(eventSetName.substring(1));
/* 190:    */       }
/* 191:    */     }
/* 192:254 */     sb.append("Event");
/* 193:255 */     return sb.toString();
/* 194:    */   }
/* 195:    */   
/* 196:    */   public Method[] getListenerMethods()
/* 197:    */   {
/* 198:259 */     if (this.listenerMethods != null) {
/* 199:260 */       return this.listenerMethods;
/* 200:    */     }
/* 201:263 */     if (this.listenerMethodDescriptors != null)
/* 202:    */     {
/* 203:264 */       this.listenerMethods = new Method[this.listenerMethodDescriptors.size()];
/* 204:265 */       int index = 0;
/* 205:266 */       for (MethodDescriptor md : this.listenerMethodDescriptors) {
/* 206:267 */         this.listenerMethods[(index++)] = md.getMethod();
/* 207:    */       }
/* 208:269 */       return this.listenerMethods;
/* 209:    */     }
/* 210:271 */     return null;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public MethodDescriptor[] getListenerMethodDescriptors()
/* 214:    */   {
/* 215:275 */     return this.listenerMethodDescriptors == null ? null : 
/* 216:276 */       (MethodDescriptor[])this.listenerMethodDescriptors.toArray(new MethodDescriptor[0]);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public Method getRemoveListenerMethod()
/* 220:    */   {
/* 221:280 */     return this.removeListenerMethod;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public Method getGetListenerMethod()
/* 225:    */   {
/* 226:284 */     return this.getListenerMethod;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public Method getAddListenerMethod()
/* 230:    */   {
/* 231:288 */     return this.addListenerMethod;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public Class<?> getListenerType()
/* 235:    */   {
/* 236:292 */     return this.listenerType;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void setUnicast(boolean unicast)
/* 240:    */   {
/* 241:296 */     this.unicast = unicast;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void setInDefaultEventSet(boolean inDefaultEventSet)
/* 245:    */   {
/* 246:300 */     this.inDefaultEventSet = inDefaultEventSet;
/* 247:    */   }
/* 248:    */   
/* 249:    */   public boolean isUnicast()
/* 250:    */   {
/* 251:304 */     return this.unicast;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public boolean isInDefaultEventSet()
/* 255:    */   {
/* 256:308 */     return this.inDefaultEventSet;
/* 257:    */   }
/* 258:    */   
/* 259:    */   private Method findAddRemoveListenerMethod(Class<?> sourceClass, String methodName)
/* 260:    */     throws IntrospectionException
/* 261:    */   {
/* 262:    */     try
/* 263:    */     {
/* 264:326 */       return sourceClass.getMethod(methodName, new Class[] { this.listenerType });
/* 265:    */     }
/* 266:    */     catch (NoSuchMethodException e)
/* 267:    */     {
/* 268:328 */       return findAddRemoveListnerMethodWithLessCheck(sourceClass, 
/* 269:329 */         methodName);
/* 270:    */     }
/* 271:    */     catch (Exception e)
/* 272:    */     {
/* 273:331 */       throw new IntrospectionException(Messages.getString("beans.31", 
/* 274:332 */         methodName, this.listenerType.getName()));
/* 275:    */     }
/* 276:    */   }
/* 277:    */   
/* 278:    */   private Method findAddRemoveListnerMethodWithLessCheck(Class<?> sourceClass, String methodName)
/* 279:    */     throws IntrospectionException
/* 280:    */   {
/* 281:339 */     Method[] methods = sourceClass.getMethods();
/* 282:340 */     Method result = null;
/* 283:341 */     for (Method method : methods) {
/* 284:342 */       if (method.getName().equals(methodName))
/* 285:    */       {
/* 286:343 */         Class[] paramTypes = method.getParameterTypes();
/* 287:344 */         if (paramTypes.length == 1)
/* 288:    */         {
/* 289:345 */           result = method;
/* 290:346 */           break;
/* 291:    */         }
/* 292:    */       }
/* 293:    */     }
/* 294:350 */     if (result == null) {
/* 295:351 */       throw new IntrospectionException(Messages.getString("beans.31", 
/* 296:352 */         methodName, this.listenerType.getName()));
/* 297:    */     }
/* 298:354 */     return result;
/* 299:    */   }
/* 300:    */   
/* 301:    */   private Method findGetListenerMethod(Class<?> sourceClass, String methodName)
/* 302:    */   {
/* 303:    */     try
/* 304:    */     {
/* 305:367 */       return sourceClass.getMethod(methodName, new Class[0]);
/* 306:    */     }
/* 307:    */     catch (Exception e) {}
/* 308:370 */     return null;
/* 309:    */   }
/* 310:    */   
/* 311:    */   private Method findMethodByPrefix(Class<?> sourceClass, String prefix, String postfix)
/* 312:    */   {
/* 313:376 */     String shortName = this.listenerType.getName();
/* 314:377 */     if (this.listenerType.getPackage() != null) {
/* 315:378 */       shortName = shortName.substring(this.listenerType.getPackage().getName()
/* 316:379 */         .length() + 1);
/* 317:    */     }
/* 318:381 */     String methodName = prefix + shortName + postfix;
/* 319:    */     try
/* 320:    */     {
/* 321:383 */       if ("get".equals(prefix)) {
/* 322:384 */         return sourceClass.getMethod(methodName, new Class[0]);
/* 323:    */       }
/* 324:    */     }
/* 325:    */     catch (NoSuchMethodException nsme)
/* 326:    */     {
/* 327:387 */       return null;
/* 328:    */     }
/* 329:389 */     Method[] methods = sourceClass.getMethods();
/* 330:390 */     for (int i = 0; i < methods.length; i++) {
/* 331:391 */       if (methods[i].getName().equals(methodName))
/* 332:    */       {
/* 333:392 */         Class[] paramTypes = methods[i].getParameterTypes();
/* 334:393 */         if (paramTypes.length == 1) {
/* 335:394 */           return methods[i];
/* 336:    */         }
/* 337:    */       }
/* 338:    */     }
/* 339:398 */     return null;
/* 340:    */   }
/* 341:    */   
/* 342:    */   private static boolean isUnicastByDefault(Method addMethod)
/* 343:    */   {
/* 344:402 */     if (addMethod != null)
/* 345:    */     {
/* 346:403 */       Class[] exceptionTypes = addMethod.getExceptionTypes();
/* 347:404 */       for (Class<?> element : exceptionTypes) {
/* 348:405 */         if (element.equals(TooManyListenersException.class)) {
/* 349:406 */           return true;
/* 350:    */         }
/* 351:    */       }
/* 352:    */     }
/* 353:410 */     return false;
/* 354:    */   }
/* 355:    */   
/* 356:    */   void merge(EventSetDescriptor event)
/* 357:    */   {
/* 358:414 */     super.merge(event);
/* 359:415 */     if (this.addListenerMethod == null) {
/* 360:416 */       this.addListenerMethod = event.addListenerMethod;
/* 361:    */     }
/* 362:418 */     if (this.getListenerMethod == null) {
/* 363:419 */       this.getListenerMethod = event.getListenerMethod;
/* 364:    */     }
/* 365:421 */     if (this.listenerMethodDescriptors == null) {
/* 366:422 */       this.listenerMethodDescriptors = event.listenerMethodDescriptors;
/* 367:    */     }
/* 368:424 */     if (this.listenerMethods == null) {
/* 369:425 */       this.listenerMethods = event.listenerMethods;
/* 370:    */     }
/* 371:427 */     if (this.listenerType == null) {
/* 372:428 */       this.listenerType = event.listenerType;
/* 373:    */     }
/* 374:431 */     if (this.removeListenerMethod == null) {
/* 375:432 */       this.removeListenerMethod = event.removeListenerMethod;
/* 376:    */     }
/* 377:434 */     this.inDefaultEventSet &= event.inDefaultEventSet;
/* 378:    */   }
/* 379:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.EventSetDescriptor

 * JD-Core Version:    0.7.0.1

 */