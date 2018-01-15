/*    1:     */ package com.alipay.api.beans;
/*    2:     */ 
/*    3:     */

import android.media.Image;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

/*    4:     */
/*    5:     */
/*    6:     */
/*    7:     */
/*    8:     */
/*    9:     */
/*   10:     */
/*   11:     */
/*   12:     */
/*   13:     */
/*   14:     */
/*   15:     */
/*   16:     */ 
/*   17:     */ class StandardBeanInfo
/*   18:     */   extends SimpleBeanInfo
/*   19:     */ {
/*   20:     */   private static final String PREFIX_IS = "is";
/*   21:     */   private static final String PREFIX_GET = "get";
/*   22:     */   private static final String PREFIX_SET = "set";
/*   23:     */   private static final String PREFIX_ADD = "add";
/*   24:     */   private static final String PREFIX_REMOVE = "remove";
/*   25:     */   private static final String SUFFIX_LISTEN = "Listener";
/*   26:     */   private static final String STR_NORMAL = "normal";
/*   27:     */   private static final String STR_INDEXED = "indexed";
/*   28:     */   private static final String STR_VALID = "valid";
/*   29:     */   private static final String STR_INVALID = "invalid";
/*   30:     */   private static final String STR_PROPERTY_TYPE = "PropertyType";
/*   31:     */   private static final String STR_IS_CONSTRAINED = "isConstrained";
/*   32:     */   private static final String STR_SETTERS = "setters";
/*   33:     */   private static final String STR_GETTERS = "getters";
/*   34:  76 */   private boolean explicitMethods = false;
/*   35:  78 */   private boolean explicitProperties = false;
/*   36:  80 */   private boolean explicitEvents = false;
/*   37:  82 */   private BeanInfo explicitBeanInfo = null;
/*   38:  84 */   private EventSetDescriptor[] events = null;
/*   39:  86 */   private MethodDescriptor[] methods = null;
/*   40:  88 */   private PropertyDescriptor[] properties = null;
/*   41:  90 */   private BeanDescriptor beanDescriptor = null;
/*   42:  92 */   BeanInfo[] additionalBeanInfo = null;
/*   43:     */   private Class<?> beanClass;
/*   44:  96 */   private int defaultEventIndex = -1;
/*   45:  98 */   private int defaultPropertyIndex = -1;
/*   46: 100 */   private static PropertyComparator comparator = new PropertyComparator();
/*   47: 102 */   private Object[] icon = new Object[4];
/*   48:     */   private boolean canAddPropertyChangeListener;
/*   49:     */   private boolean canRemovePropertyChangeListener;
/*   50:     */   
/*   51:     */   StandardBeanInfo(Class<?> beanClass, BeanInfo explicitBeanInfo, Class<?> stopClass)
/*   52:     */     throws IntrospectionException
/*   53:     */   {
/*   54: 110 */     this.beanClass = beanClass;
/*   55: 128 */     if (explicitBeanInfo != null)
/*   56:     */     {
/*   57: 129 */       this.explicitBeanInfo = explicitBeanInfo;
/*   58: 130 */       this.events = explicitBeanInfo.getEventSetDescriptors();
/*   59: 131 */       this.methods = explicitBeanInfo.getMethodDescriptors();
/*   60: 132 */       this.properties = explicitBeanInfo.getPropertyDescriptors();
/*   61: 133 */       this.defaultEventIndex = explicitBeanInfo.getDefaultEventIndex();
/*   62: 134 */       if ((this.defaultEventIndex < 0) || (this.defaultEventIndex >= this.events.length)) {
/*   63: 135 */         this.defaultEventIndex = -1;
/*   64:     */       }
/*   65: 137 */       this.defaultPropertyIndex = explicitBeanInfo.getDefaultPropertyIndex();
/*   66: 138 */       if ((this.defaultPropertyIndex < 0) || 
/*   67: 139 */         (this.defaultPropertyIndex >= this.properties.length)) {
/*   68: 140 */         this.defaultPropertyIndex = -1;
/*   69:     */       }
/*   70: 142 */       this.additionalBeanInfo = explicitBeanInfo.getAdditionalBeanInfo();
/*   71: 143 */       for (int i = 0; i < 4; i++) {
/*   72: 144 */         this.icon[i] = explicitBeanInfo.getIcon(i + 1);
/*   73:     */       }
/*   74: 147 */       if (this.events != null) {
/*   75: 148 */         this.explicitEvents = true;
/*   76:     */       }
/*   77: 149 */       if (this.methods != null) {
/*   78: 150 */         this.explicitMethods = true;
/*   79:     */       }
/*   80: 151 */       if (this.properties != null) {
/*   81: 152 */         this.explicitProperties = true;
/*   82:     */       }
/*   83:     */     }
/*   84: 155 */     if (this.methods == null) {
/*   85: 156 */       this.methods = introspectMethods();
/*   86:     */     }
/*   87: 159 */     if (this.properties == null) {
/*   88: 160 */       this.properties = introspectProperties(stopClass);
/*   89:     */     }
/*   90: 163 */     if (this.events == null) {
/*   91: 164 */       this.events = introspectEvents();
/*   92:     */     }
/*   93:     */   }
/*   94:     */   
/*   95:     */   public BeanInfo[] getAdditionalBeanInfo()
/*   96:     */   {
/*   97: 170 */     return null;
/*   98:     */   }
/*   99:     */   
/*  100:     */   public EventSetDescriptor[] getEventSetDescriptors()
/*  101:     */   {
/*  102: 175 */     return this.events;
/*  103:     */   }
/*  104:     */   
/*  105:     */   public MethodDescriptor[] getMethodDescriptors()
/*  106:     */   {
/*  107: 180 */     return this.methods;
/*  108:     */   }
/*  109:     */   
/*  110:     */   public PropertyDescriptor[] getPropertyDescriptors()
/*  111:     */   {
/*  112: 185 */     return this.properties;
/*  113:     */   }
/*  114:     */   
/*  115:     */   public BeanDescriptor getBeanDescriptor()
/*  116:     */   {
/*  117: 190 */     if (this.beanDescriptor == null)
/*  118:     */     {
/*  119: 191 */       if (this.explicitBeanInfo != null) {
/*  120: 192 */         this.beanDescriptor = this.explicitBeanInfo.getBeanDescriptor();
/*  121:     */       }
/*  122: 194 */       if (this.beanDescriptor == null) {
/*  123: 195 */         this.beanDescriptor = new BeanDescriptor(this.beanClass);
/*  124:     */       }
/*  125:     */     }
/*  126: 198 */     return this.beanDescriptor;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public int getDefaultEventIndex()
/*  130:     */   {
/*  131: 203 */     return this.defaultEventIndex;
/*  132:     */   }
/*  133:     */   
/*  134:     */   public int getDefaultPropertyIndex()
/*  135:     */   {
/*  136: 208 */     return this.defaultPropertyIndex;
/*  137:     */   }
/*  138:     */   
/*  139:     */   public Image getIcon(int iconKind)
/*  140:     */   {
/*  141: 213 */     return (Image)this.icon[(iconKind - 1)];
/*  142:     */   }
/*  143:     */   
/*  144:     */   void mergeBeanInfo(BeanInfo beanInfo, boolean force)
/*  145:     */     throws IntrospectionException
/*  146:     */   {
/*  147: 218 */     if ((force) || (!this.explicitProperties))
/*  148:     */     {
/*  149: 219 */       PropertyDescriptor[] superDescs = beanInfo.getPropertyDescriptors();
/*  150: 220 */       if (superDescs != null) {
/*  151: 221 */         if (getPropertyDescriptors() != null)
/*  152:     */         {
/*  153: 222 */           this.properties = mergeProps(superDescs, beanInfo
/*  154: 223 */             .getDefaultPropertyIndex());
/*  155:     */         }
/*  156:     */         else
/*  157:     */         {
/*  158: 225 */           this.properties = superDescs;
/*  159: 226 */           this.defaultPropertyIndex = beanInfo.getDefaultPropertyIndex();
/*  160:     */         }
/*  161:     */       }
/*  162:     */     }
/*  163: 231 */     if ((force) || (!this.explicitMethods))
/*  164:     */     {
/*  165: 232 */       MethodDescriptor[] superMethods = beanInfo.getMethodDescriptors();
/*  166: 233 */       if (superMethods != null) {
/*  167: 234 */         if (this.methods != null) {
/*  168: 235 */           this.methods = mergeMethods(superMethods);
/*  169:     */         } else {
/*  170: 237 */           this.methods = superMethods;
/*  171:     */         }
/*  172:     */       }
/*  173:     */     }
/*  174: 242 */     if ((force) || (!this.explicitEvents))
/*  175:     */     {
/*  176: 243 */       EventSetDescriptor[] superEvents = beanInfo
/*  177: 244 */         .getEventSetDescriptors();
/*  178: 245 */       if (superEvents != null) {
/*  179: 246 */         if (this.events != null)
/*  180:     */         {
/*  181: 247 */           this.events = mergeEvents(superEvents, beanInfo
/*  182: 248 */             .getDefaultEventIndex());
/*  183:     */         }
/*  184:     */         else
/*  185:     */         {
/*  186: 250 */           this.events = superEvents;
/*  187: 251 */           this.defaultEventIndex = beanInfo.getDefaultEventIndex();
/*  188:     */         }
/*  189:     */       }
/*  190:     */     }
/*  191:     */   }
/*  192:     */   
/*  193:     */   private PropertyDescriptor[] mergeProps(PropertyDescriptor[] superDescs, int superDefaultIndex)
/*  194:     */     throws IntrospectionException
/*  195:     */   {
/*  196: 263 */     HashMap<String, PropertyDescriptor> subMap = internalAsMap(this.properties);
/*  197: 264 */     String defaultPropertyName = null;
/*  198: 265 */     if ((this.defaultPropertyIndex >= 0) && 
/*  199: 266 */       (this.defaultPropertyIndex < this.properties.length)) {
/*  200: 267 */       defaultPropertyName = this.properties[this.defaultPropertyIndex].getName();
/*  201: 268 */     } else if ((superDefaultIndex >= 0) && 
/*  202: 269 */       (superDefaultIndex < superDescs.length)) {
/*  203: 270 */       defaultPropertyName = superDescs[superDefaultIndex].getName();
/*  204:     */     }
/*  205: 273 */     for (int i = 0; i < superDescs.length; i++)
/*  206:     */     {
/*  207: 274 */       PropertyDescriptor superDesc = superDescs[i];
/*  208: 275 */       String propertyName = superDesc.getName();
/*  209: 276 */       if (!subMap.containsKey(propertyName))
/*  210:     */       {
/*  211: 277 */         subMap.put(propertyName, superDesc);
/*  212:     */       }
/*  213:     */       else
/*  214:     */       {
/*  215: 281 */         Object value = subMap.get(propertyName);
/*  216:     */         
/*  217: 283 */         Method subGet = ((PropertyDescriptor)value).getReadMethod();
/*  218: 284 */         Method subSet = ((PropertyDescriptor)value).getWriteMethod();
/*  219: 285 */         Method superGet = superDesc.getReadMethod();
/*  220: 286 */         Method superSet = superDesc.getWriteMethod();
/*  221:     */         
/*  222: 288 */         Class<?> superType = superDesc.getPropertyType();
/*  223: 289 */         Class<?> superIndexedType = null;
/*  224: 290 */         Class<?> subType = ((PropertyDescriptor)value).getPropertyType();
/*  225: 291 */         Class<?> subIndexedType = null;
/*  226: 293 */         if ((value instanceof IndexedPropertyDescriptor)) {
/*  227: 294 */           subIndexedType = 
/*  228: 295 */             ((IndexedPropertyDescriptor)value).getIndexedPropertyType();
/*  229:     */         }
/*  230: 297 */         if ((superDesc instanceof IndexedPropertyDescriptor)) {
/*  231: 298 */           superIndexedType = 
/*  232: 299 */             ((IndexedPropertyDescriptor)superDesc).getIndexedPropertyType();
/*  233:     */         }
/*  234: 303 */         if (superIndexedType == null)
/*  235:     */         {
/*  236: 304 */           PropertyDescriptor subDesc = (PropertyDescriptor)value;
/*  237: 306 */           if (subIndexedType == null)
/*  238:     */           {
/*  239: 308 */             if ((subType != null) && (superType != null) && 
/*  240: 309 */               (subType.getName() != null) && 
/*  241: 310 */               (subType.getName().equals(superType.getName())))
/*  242:     */             {
/*  243: 311 */               if ((superGet != null) && (
/*  244: 312 */                 (subGet == null) || (superGet.equals(subGet)))) {
/*  245: 313 */                 subDesc.setReadMethod(superGet);
/*  246:     */               }
/*  247: 315 */               if ((superSet != null) && (
/*  248: 316 */                 (subSet == null) || (superSet.equals(subSet)))) {
/*  249: 317 */                 subDesc.setWriteMethod(superSet);
/*  250:     */               }
/*  251: 319 */               if ((subType == Boolean.TYPE) && (subGet != null) && 
/*  252: 320 */                 (superGet != null) && 
/*  253: 321 */                 (superGet.getName().startsWith("is"))) {
/*  254: 322 */                 subDesc.setReadMethod(superGet);
/*  255:     */               }
/*  256:     */             }
/*  257: 326 */             else if (((subGet == null) || (subSet == null)) && 
/*  258: 327 */               (superGet != null))
/*  259:     */             {
/*  260: 328 */               subDesc = new PropertyDescriptor(propertyName, 
/*  261: 329 */                 superGet, superSet);
/*  262: 330 */               if (subGet != null)
/*  263:     */               {
/*  264: 331 */                 String subGetName = subGet.getName();
/*  265: 332 */                 Method method = null;
/*  266: 333 */                 MethodDescriptor[] introspectMethods = introspectMethods();
/*  267: 334 */                 for (MethodDescriptor methodDesc : introspectMethods)
/*  268:     */                 {
/*  269: 335 */                   method = methodDesc.getMethod();
/*  270: 336 */                   if ((method != subGet) && 
/*  271: 337 */                     (subGetName.equals(method
/*  272: 338 */                     .getName()))) {
/*  273: 339 */                     if ((method.getParameterTypes().length == 0) && 
/*  274: 340 */                       (method.getReturnType() == superType))
/*  275:     */                     {
/*  276: 341 */                       subDesc.setReadMethod(method);
/*  277: 342 */                       break;
/*  278:     */                     }
/*  279:     */                   }
/*  280:     */                 }
/*  281:     */               }
/*  282:     */             }
/*  283:     */           }
/*  284:     */           else
/*  285:     */           {
/*  286: 349 */             if ((superType != null) && 
/*  287: 350 */               (superType.isArray())) {
/*  288: 352 */               if (superType.getComponentType().getName().equals(subIndexedType.getName()))
/*  289:     */               {
/*  290: 353 */                 if ((subGet == null) && (superGet != null)) {
/*  291: 354 */                   subDesc.setReadMethod(superGet);
/*  292:     */                 }
/*  293: 356 */                 if ((subSet == null) && (superSet != null)) {
/*  294: 357 */                   subDesc.setWriteMethod(superSet);
/*  295:     */                 }
/*  296:     */               }
/*  297:     */             }
/*  298: 361 */             if ((subIndexedType == Boolean.TYPE) && 
/*  299: 362 */               (superType == Boolean.TYPE))
/*  300:     */             {
/*  301: 363 */               Method subIndexedSet = ((IndexedPropertyDescriptor)subDesc)
/*  302: 364 */                 .getIndexedWriteMethod();
/*  303: 365 */               if ((subGet == null) && (subSet == null) && 
/*  304: 366 */                 (subIndexedSet != null) && (superGet != null))
/*  305:     */               {
/*  306:     */                 try
/*  307:     */                 {
/*  308: 368 */                   subSet = this.beanClass.getDeclaredMethod(
/*  309: 369 */                     subIndexedSet.getName(), new Class[] { Boolean.TYPE });
/*  310:     */                 }
/*  311:     */                 catch (Exception localException) {}
/*  312: 373 */                 if (subSet != null) {
/*  313: 375 */                   subDesc = new PropertyDescriptor(propertyName, 
/*  314: 376 */                     superGet, subSet);
/*  315:     */                 }
/*  316:     */               }
/*  317:     */             }
/*  318:     */           }
/*  319: 381 */           subMap.put(propertyName, subDesc);
/*  320:     */         }
/*  321: 383 */         else if (subIndexedType == null)
/*  322:     */         {
/*  323: 384 */           if ((subType != null) && 
/*  324: 385 */             (subType.isArray())) {
/*  325: 387 */             if (subType.getComponentType().getName().equals(superIndexedType.getName()))
/*  326:     */             {
/*  327: 389 */               if (subGet != null) {
/*  328: 390 */                 superDesc.setReadMethod(subGet);
/*  329:     */               }
/*  330: 392 */               if (subSet != null) {
/*  331: 393 */                 superDesc.setWriteMethod(subSet);
/*  332:     */               }
/*  333: 395 */               subMap.put(propertyName, superDesc);
/*  335:     */             }
/*  336:     */           }
/*  337: 401 */           if ((subGet == null) || (subSet == null))
/*  338:     */           {
/*  339: 402 */             Class<?> beanSuperClass = this.beanClass.getSuperclass();
/*  340: 403 */             String methodSuffix = capitalize(propertyName);
/*  341: 404 */             Method method = null;
/*  342: 405 */             if (subGet == null)
/*  343:     */             {
/*  344: 407 */               if (subType == Boolean.TYPE) {
/*  345:     */                 try
/*  346:     */                 {
/*  347: 409 */                   method = 
/*  348: 410 */                     beanSuperClass.getDeclaredMethod("is" + 
/*  349: 411 */                     methodSuffix, new Class[0]);
/*  350:     */                 }
/*  351:     */                 catch (Exception localException1) {}
/*  352:     */               } else {
/*  353:     */                 try
/*  354:     */                 {
/*  355: 417 */                   method = 
/*  356: 418 */                     beanSuperClass.getDeclaredMethod("get" + 
/*  357: 419 */                     methodSuffix, new Class[0]);
/*  358:     */                 }
/*  359:     */                 catch (Exception localException2) {}
/*  360:     */               }
/*  361: 424 */               if ((method != null) && 
/*  362: 425 */                 (!Modifier.isStatic(method
/*  363: 426 */                 .getModifiers()))) {
/*  364: 427 */                 if (method.getReturnType() == subType) {
/*  365: 429 */                   ((PropertyDescriptor)value).setReadMethod(method);
/*  366:     */                 }
/*  367:     */               }
/*  368:     */             }
/*  369:     */             else
/*  370:     */             {
/*  371:     */               try
/*  372:     */               {
/*  373: 434 */                 method = beanSuperClass.getDeclaredMethod(
/*  374: 435 */                   "set" + methodSuffix, new Class[] { subType });
/*  375:     */               }
/*  376:     */               catch (Exception localException3) {}
/*  377: 439 */               if ((method != null) && 
/*  378: 440 */                 (!Modifier.isStatic(method
/*  379: 441 */                 .getModifiers()))) {
/*  380: 442 */                 if (method.getReturnType() == Void.TYPE) {
/*  381: 444 */                   ((PropertyDescriptor)value).setWriteMethod(method);
/*  382:     */                 }
/*  383:     */               }
/*  384:     */             }
/*  385:     */           }
/*  386: 448 */           subMap.put(propertyName, (PropertyDescriptor)value);
/*  387:     */         }
/*  388: 450 */         else if (subIndexedType.getName().equals(
/*  389: 451 */           superIndexedType.getName()))
/*  390:     */         {
/*  391: 453 */           IndexedPropertyDescriptor subDesc = (IndexedPropertyDescriptor)value;
/*  392: 454 */           if ((subGet == null) && (superGet != null)) {
/*  393: 455 */             subDesc.setReadMethod(superGet);
/*  394:     */           }
/*  395: 457 */           if ((subSet == null) && (superSet != null)) {
/*  396: 458 */             subDesc.setWriteMethod(superSet);
/*  397:     */           }
/*  398: 460 */           IndexedPropertyDescriptor superIndexedDesc = (IndexedPropertyDescriptor)superDesc;
/*  399: 462 */           if ((subDesc.getIndexedReadMethod() == null) && 
/*  400: 463 */             (superIndexedDesc.getIndexedReadMethod() != null)) {
/*  401: 464 */             subDesc.setIndexedReadMethod(superIndexedDesc
/*  402: 465 */               .getIndexedReadMethod());
/*  403:     */           }
/*  404: 468 */           if ((subDesc.getIndexedWriteMethod() == null) && 
/*  405: 469 */             (superIndexedDesc.getIndexedWriteMethod() != null)) {
/*  406: 470 */             subDesc.setIndexedWriteMethod(superIndexedDesc
/*  407: 471 */               .getIndexedWriteMethod());
/*  408:     */           }
/*  409: 474 */           subMap.put(propertyName, subDesc);
/*  410:     */         }
/*  411:     */         label1105:
/*  412: 477 */         mergeAttributes((PropertyDescriptor)value, superDesc);
/*  413:     */       }
/*  414:     */     }
/*  415: 480 */     PropertyDescriptor[] theDescs = new PropertyDescriptor[subMap.size()];
/*  416: 481 */     subMap.values().toArray(theDescs);
/*  417: 483 */     if ((defaultPropertyName != null) && (!this.explicitProperties)) {
/*  418: 484 */       for (int i = 0; i < theDescs.length; i++) {
/*  419: 485 */         if (defaultPropertyName.equals(theDescs[i].getName()))
/*  420:     */         {
/*  421: 486 */           this.defaultPropertyIndex = i;
/*  422: 487 */           break;
/*  423:     */         }
/*  424:     */       }
/*  425:     */     }
/*  426: 491 */     return theDescs;
/*  427:     */   }
/*  428:     */   
/*  429:     */   private String capitalize(String name)
/*  430:     */   {
/*  431: 495 */     if (name == null) {
/*  432: 496 */       return null;
/*  433:     */     }
/*  434: 502 */     if ((name.length() == 0) || ((name.length() > 1) && (Character.isUpperCase(name.charAt(1))))) {
/*  435: 503 */       return name;
/*  436:     */     }
/*  437: 506 */     char[] chars = name.toCharArray();
/*  438: 507 */     chars[0] = Character.toUpperCase(chars[0]);
/*  439: 508 */     return new String(chars);
/*  440:     */   }
/*  441:     */   
/*  442:     */   private static void mergeAttributes(PropertyDescriptor subDesc, PropertyDescriptor superDesc)
/*  443:     */   {
/*  444: 515 */     subDesc.hidden |= superDesc.hidden;
/*  445: 516 */     subDesc.expert |= superDesc.expert;
/*  446: 517 */     subDesc.preferred |= superDesc.preferred;
/*  447: 518 */     subDesc.bound |= superDesc.bound;
/*  448: 519 */     subDesc.constrained |= superDesc.constrained;
/*  449: 520 */     subDesc.name = superDesc.name;
/*  450: 521 */     if ((subDesc.shortDescription == null) && 
/*  451: 522 */       (superDesc.shortDescription != null)) {
/*  452: 523 */       subDesc.shortDescription = superDesc.shortDescription;
/*  453:     */     }
/*  454: 525 */     if ((subDesc.displayName == null) && (superDesc.displayName != null)) {
/*  455: 526 */       subDesc.displayName = superDesc.displayName;
/*  456:     */     }
/*  457:     */   }
/*  458:     */   
/*  459:     */   private MethodDescriptor[] mergeMethods(MethodDescriptor[] superDescs)
/*  460:     */   {
/*  461: 534 */     HashMap<String, MethodDescriptor> subMap = internalAsMap(this.methods);
/*  462: 536 */     for (MethodDescriptor superMethod : superDescs)
/*  463:     */     {
/*  464: 537 */       String methodName = getQualifiedName(superMethod.getMethod());
/*  465: 538 */       MethodDescriptor method = (MethodDescriptor)subMap.get(methodName);
/*  466: 539 */       if (method == null) {
/*  467: 540 */         subMap.put(methodName, superMethod);
/*  468:     */       } else {
/*  469: 542 */         method.merge(superMethod);
/*  470:     */       }
/*  471:     */     }
/*  472: 545 */     MethodDescriptor[] theMethods = new MethodDescriptor[subMap.size()];
/*  473: 546 */     subMap.values().toArray(theMethods);
/*  474: 547 */     return theMethods;
/*  475:     */   }
/*  476:     */   
/*  477:     */   private EventSetDescriptor[] mergeEvents(EventSetDescriptor[] otherEvents, int otherDefaultIndex)
/*  478:     */   {
/*  479: 552 */     HashMap<String, EventSetDescriptor> subMap = internalAsMap(this.events);
/*  480: 553 */     String defaultEventName = null;
/*  481: 554 */     if ((this.defaultEventIndex >= 0) && (this.defaultEventIndex < this.events.length)) {
/*  482: 555 */       defaultEventName = this.events[this.defaultEventIndex].getName();
/*  483: 556 */     } else if ((otherDefaultIndex >= 0) && 
/*  484: 557 */       (otherDefaultIndex < otherEvents.length)) {
/*  485: 558 */       defaultEventName = otherEvents[otherDefaultIndex].getName();
/*  486:     */     }
/*  487: 561 */     for (EventSetDescriptor event : otherEvents)
/*  488:     */     {
/*  489: 562 */       String eventName = event.getName();
/*  490: 563 */       EventSetDescriptor subEvent = (EventSetDescriptor)subMap.get(eventName);
/*  491: 564 */       if (subEvent == null) {
/*  492: 565 */         subMap.put(eventName, event);
/*  493:     */       } else {
/*  494: 567 */         subEvent.merge(event);
/*  495:     */       }
/*  496:     */     }
/*  497: 571 */     EventSetDescriptor[] theEvents = new EventSetDescriptor[subMap.size()];
/*  498: 572 */     subMap.values().toArray(theEvents);
/*  499: 574 */     if ((defaultEventName != null) && (!this.explicitEvents)) {
/*  500: 575 */       for (int i = 0; i < theEvents.length; i++) {
/*  501: 576 */         if (defaultEventName.equals(theEvents[i].getName()))
/*  502:     */         {
/*  503: 577 */           this.defaultEventIndex = i;
/*  504: 578 */           break;
/*  505:     */         }
/*  506:     */       }
/*  507:     */     }
/*  508: 582 */     return theEvents;
/*  509:     */   }
/*  510:     */   
/*  511:     */   private static HashMap<String, PropertyDescriptor> internalAsMap(PropertyDescriptor[] propertyDescs)
/*  512:     */   {
/*  513: 587 */     HashMap<String, PropertyDescriptor> map = new HashMap();
/*  514: 588 */     for (int i = 0; i < propertyDescs.length; i++) {
/*  515: 589 */       map.put(propertyDescs[i].getName(), propertyDescs[i]);
/*  516:     */     }
/*  517: 591 */     return map;
/*  518:     */   }
/*  519:     */   
/*  520:     */   private static HashMap<String, MethodDescriptor> internalAsMap(MethodDescriptor[] theDescs)
/*  521:     */   {
/*  522: 596 */     HashMap<String, MethodDescriptor> map = new HashMap();
/*  523: 597 */     for (int i = 0; i < theDescs.length; i++)
/*  524:     */     {
/*  525: 598 */       String qualifiedName = getQualifiedName(theDescs[i].getMethod());
/*  526: 599 */       map.put(qualifiedName, theDescs[i]);
/*  527:     */     }
/*  528: 601 */     return map;
/*  529:     */   }
/*  530:     */   
/*  531:     */   private static HashMap<String, EventSetDescriptor> internalAsMap(EventSetDescriptor[] theDescs)
/*  532:     */   {
/*  533: 606 */     HashMap<String, EventSetDescriptor> map = new HashMap();
/*  534: 607 */     for (int i = 0; i < theDescs.length; i++) {
/*  535: 608 */       map.put(theDescs[i].getName(), theDescs[i]);
/*  536:     */     }
/*  537: 610 */     return map;
/*  538:     */   }
/*  539:     */   
/*  540:     */   private static String getQualifiedName(Method method)
/*  541:     */   {
/*  542: 614 */     String qualifiedName = method.getName();
/*  543: 615 */     Class[] paramTypes = method.getParameterTypes();
/*  544: 616 */     if (paramTypes != null) {
/*  545: 617 */       for (int i = 0; i < paramTypes.length; i++) {
/*  546: 618 */         qualifiedName = qualifiedName + "_" + paramTypes[i].getName();
/*  547:     */       }
/*  548:     */     }
/*  549: 621 */     return qualifiedName;
/*  550:     */   }
/*  551:     */   
/*  552:     */   private MethodDescriptor[] introspectMethods()
/*  553:     */   {
/*  554: 632 */     return introspectMethods(false, this.beanClass);
/*  555:     */   }
/*  556:     */   
/*  557:     */   private MethodDescriptor[] introspectMethods(boolean includeSuper)
/*  558:     */   {
/*  559: 636 */     return introspectMethods(includeSuper, this.beanClass);
/*  560:     */   }
/*  561:     */   
/*  562:     */   private MethodDescriptor[] introspectMethods(boolean includeSuper, Class<?> introspectorClass)
/*  563:     */   {
/*  564: 643 */     Method[] basicMethods = includeSuper ? introspectorClass.getMethods() : 
/*  565: 644 */       introspectorClass.getDeclaredMethods();
/*  566: 646 */     if ((basicMethods == null) || (basicMethods.length == 0)) {
/*  567: 647 */       return null;
/*  568:     */     }
/*  569: 649 */     ArrayList<MethodDescriptor> methodList = new ArrayList(
/*  570: 650 */       basicMethods.length);
/*  571: 653 */     for (int i = 0; i < basicMethods.length; i++)
/*  572:     */     {
/*  573: 654 */       int modifiers = basicMethods[i].getModifiers();
/*  574: 655 */       if (Modifier.isPublic(modifiers))
/*  575:     */       {
/*  576: 657 */         MethodDescriptor theDescriptor = new MethodDescriptor(
/*  577: 658 */           basicMethods[i]);
/*  578: 659 */         methodList.add(theDescriptor);
/*  579:     */       }
/*  580:     */     }
/*  581: 664 */     int methodCount = methodList.size();
/*  582: 665 */     MethodDescriptor[] theMethods = (MethodDescriptor[])null;
/*  583: 666 */     if (methodCount > 0)
/*  584:     */     {
/*  585: 667 */       theMethods = new MethodDescriptor[methodCount];
/*  586: 668 */       theMethods = (MethodDescriptor[])methodList.toArray(theMethods);
/*  587:     */     }
/*  588: 671 */     return theMethods;
/*  589:     */   }
/*  590:     */   
/*  591:     */   private PropertyDescriptor[] introspectProperties(Class<?> stopClass)
/*  592:     */     throws IntrospectionException
/*  593:     */   {
/*  594: 688 */     MethodDescriptor[] methodDescriptors = introspectMethods();
/*  595: 690 */     if (methodDescriptors == null) {
/*  596: 691 */       return null;
/*  597:     */     }
/*  598: 694 */     ArrayList<MethodDescriptor> methodList = new ArrayList();
/*  599: 696 */     for (int index = 0; index < methodDescriptors.length; index++)
/*  600:     */     {
/*  601: 697 */       int modifiers = methodDescriptors[index].getMethod().getModifiers();
/*  602: 698 */       if (!Modifier.isStatic(modifiers)) {
/*  603: 699 */         methodList.add(methodDescriptors[index]);
/*  604:     */       }
/*  605:     */     }
/*  606: 704 */     int methodCount = methodList.size();
/*  607: 705 */     MethodDescriptor[] theMethods = (MethodDescriptor[])null;
/*  608: 706 */     if (methodCount > 0)
/*  609:     */     {
/*  610: 707 */       theMethods = new MethodDescriptor[methodCount];
/*  611: 708 */       theMethods = (MethodDescriptor[])methodList.toArray(theMethods);
/*  612:     */     }
/*  613: 711 */     if (theMethods == null) {
/*  614: 712 */       return null;
/*  615:     */     }
/*  616: 715 */     HashMap<String, HashMap> propertyTable = new HashMap(
/*  617: 716 */       theMethods.length);
/*  618: 719 */     for (int i = 0; i < theMethods.length; i++)
/*  619:     */     {
/*  620: 720 */       introspectGet(theMethods[i].getMethod(), propertyTable);
/*  621: 721 */       introspectSet(theMethods[i].getMethod(), propertyTable);
/*  622:     */     }
/*  623: 725 */     fixGetSet(propertyTable);
/*  624:     */     
/*  625:     */ 
/*  626: 728 */     MethodDescriptor[] allMethods = introspectMethods(true);
/*  627:     */     MethodDescriptor method;
/*  628: 729 */     if (stopClass != null)
/*  629:     */     {
/*  630: 730 */       MethodDescriptor[] excludeMethods = introspectMethods(true, 
/*  631: 731 */         stopClass);
/*  632: 732 */       if (excludeMethods != null)
/*  633:     */       {
/*  642:     */       }
/*  643:     */     }
/*  644: 743 */     for (int i = 0; i < allMethods.length; i++) {
/*  645: 744 */       introspectPropertyListener(allMethods[i].getMethod());
/*  646:     */     }
/*  647: 747 */     ArrayList<PropertyDescriptor> propertyList = new ArrayList();
/*  648: 749 */     for (Map.Entry<String, HashMap> entry : propertyTable.entrySet())
/*  649:     */     {
/*  650: 750 */       String propertyName = (String)entry.getKey();
/*  651: 751 */       HashMap table = (HashMap)entry.getValue();
/*  652: 752 */       if (table != null)
/*  653:     */       {
/*  654: 755 */         String normalTag = (String)table.get("normal");
/*  655: 756 */         String indexedTag = (String)table.get("indexed");
/*  656: 758 */         if ((normalTag != null) || (indexedTag != null))
/*  657:     */         {
/*  658: 762 */           Method get = (Method)table.get("normalget");
/*  659: 763 */           Method set = (Method)table.get("normalset");
/*  660: 764 */           Method indexedGet = (Method)table.get("indexedget");
/*  661: 765 */           Method indexedSet = (Method)table.get("indexedset");
/*  662:     */           
/*  663: 767 */           PropertyDescriptor propertyDesc = null;
/*  664: 768 */           if (indexedTag == null) {
/*  665: 769 */             propertyDesc = new PropertyDescriptor(propertyName, get, set);
/*  666:     */           } else {
/*  667:     */             try
/*  668:     */             {
/*  669: 772 */               propertyDesc = new IndexedPropertyDescriptor(propertyName, 
/*  670: 773 */                 get, set, indexedGet, indexedSet);
/*  671:     */             }
/*  672:     */             catch (IntrospectionException e)
/*  673:     */             {
/*  674: 777 */               propertyDesc = new IndexedPropertyDescriptor(propertyName, 
/*  675: 778 */                 null, null, indexedGet, indexedSet);
/*  676:     */             }
/*  677:     */           }
/*  678: 783 */           if ((this.canAddPropertyChangeListener) && (this.canRemovePropertyChangeListener)) {
/*  679: 784 */             propertyDesc.setBound(true);
/*  680:     */           } else {
/*  681: 786 */             propertyDesc.setBound(false);
/*  682:     */           }
/*  683: 788 */           if (table.get("isConstrained") == Boolean.TRUE) {
/*  684: 789 */             propertyDesc.setConstrained(true);
/*  685:     */           }
/*  686: 791 */           propertyList.add(propertyDesc);
/*  687:     */         }
/*  688:     */       }
/*  689:     */     }
/*  690: 794 */     PropertyDescriptor[] theProperties = new PropertyDescriptor[propertyList
/*  691: 795 */       .size()];
/*  692: 796 */     propertyList.toArray(theProperties);
/*  693: 797 */     return theProperties;
/*  694:     */   }
/*  695:     */   
/*  696:     */   private boolean isInSuper(MethodDescriptor method, MethodDescriptor[] excludeMethods)
/*  697:     */   {
/*  698: 802 */     for (MethodDescriptor m : excludeMethods) {
/*  699: 803 */       if (method.getMethod().equals(m.getMethod())) {
/*  700: 804 */         return true;
/*  701:     */       }
/*  702:     */     }
/*  703: 807 */     return false;
/*  704:     */   }
/*  705:     */   
/*  706:     */   private void introspectPropertyListener(Method theMethod)
/*  707:     */   {
/*  708: 812 */     String methodName = theMethod.getName();
/*  709: 813 */     Class[] param = theMethod.getParameterTypes();
/*  710: 814 */     if (param.length != 1) {
/*  711: 815 */       return;
/*  712:     */     }
/*  713: 817 */     if ((methodName.equals("addPropertyChangeListener")) && 
/*  714: 818 */       (param[0].equals(PropertyChangeListener.class))) {
/*  715: 819 */       this.canAddPropertyChangeListener = true;
/*  716:     */     }
/*  717: 820 */     if ((methodName.equals("removePropertyChangeListener")) && 
/*  718: 821 */       (param[0].equals(PropertyChangeListener.class))) {
/*  719: 822 */       this.canRemovePropertyChangeListener = true;
/*  720:     */     }
/*  721:     */   }
/*  722:     */   
/*  723:     */   private static void introspectGet(Method theMethod, HashMap<String, HashMap> propertyTable)
/*  724:     */   {
/*  725: 829 */     String methodName = theMethod.getName();
/*  726: 830 */     int prefixLength = 0;
/*  727: 837 */     if (methodName == null) {
/*  728: 838 */       return;
/*  729:     */     }
/*  730: 841 */     if (methodName.startsWith("get")) {
/*  731: 842 */       prefixLength = "get".length();
/*  732:     */     }
/*  733: 845 */     if (methodName.startsWith("is")) {
/*  734: 846 */       prefixLength = "is".length();
/*  735:     */     }
/*  736: 849 */     if (prefixLength == 0) {
/*  737: 850 */       return;
/*  738:     */     }
/*  739: 853 */     String propertyName = Introspector.decapitalize(methodName.substring(prefixLength));
/*  740: 856 */     if (!isValidProperty(propertyName)) {
/*  741: 857 */       return;
/*  742:     */     }
/*  743: 861 */     Class propertyType = theMethod.getReturnType();
/*  744: 863 */     if ((propertyType == null) || (propertyType == Void.TYPE)) {
/*  745: 864 */       return;
/*  746:     */     }
/*  747: 868 */     if ((prefixLength == 2) && 
/*  748: 869 */       (propertyType != Boolean.TYPE)) {
/*  749: 870 */       return;
/*  750:     */     }
/*  751: 875 */     Class[] paramTypes = theMethod.getParameterTypes();
/*  752: 876 */     if ((paramTypes.length > 1) || (
/*  753: 877 */       (paramTypes.length == 1) && (paramTypes[0] != Integer.TYPE))) {
/*  754: 878 */       return;
/*  755:     */     }
/*  756: 881 */     HashMap table = (HashMap)propertyTable.get(propertyName);
/*  757: 882 */     if (table == null)
/*  758:     */     {
/*  759: 883 */       table = new HashMap();
/*  760: 884 */       propertyTable.put(propertyName, table);
/*  761:     */     }
/*  762: 887 */     ArrayList<Method> getters = (ArrayList)table.get("getters");
/*  763: 888 */     if (getters == null)
/*  764:     */     {
/*  765: 889 */       getters = new ArrayList();
/*  766: 890 */       table.put("getters", getters);
/*  767:     */     }
/*  768: 894 */     getters.add(theMethod);
/*  769:     */   }
/*  770:     */   
/*  771:     */   private static void introspectSet(Method theMethod, HashMap<String, HashMap> propertyTable)
/*  772:     */   {
/*  773: 901 */     String methodName = theMethod.getName();
/*  774: 902 */     if (methodName == null) {
/*  775: 903 */       return;
/*  776:     */     }
/*  777: 910 */     Class returnType = theMethod.getReturnType();
/*  778: 911 */     if (returnType != Void.TYPE) {
/*  779: 912 */       return;
/*  780:     */     }
/*  781: 915 */     if ((methodName == null) || (!methodName.startsWith("set"))) {
/*  782: 916 */       return;
/*  783:     */     }
/*  784: 919 */     String propertyName = Introspector.decapitalize(methodName.substring("set".length()));
/*  785: 922 */     if (!isValidProperty(propertyName)) {
/*  786: 923 */       return;
/*  787:     */     }
/*  788: 929 */     Class[] paramTypes = theMethod.getParameterTypes();
/*  789: 931 */     if ((paramTypes.length == 0) || (paramTypes.length > 2) || (
/*  790: 932 */       (paramTypes.length == 2) && (paramTypes[0] != Integer.TYPE))) {
/*  791: 933 */       return;
/*  792:     */     }
/*  793: 936 */     HashMap table = (HashMap)propertyTable.get(propertyName);
/*  794: 937 */     if (table == null)
/*  795:     */     {
/*  796: 938 */       table = new HashMap();
/*  797: 939 */       propertyTable.put(propertyName, table);
/*  798:     */     }
/*  799: 942 */     ArrayList<Method> setters = (ArrayList)table.get("setters");
/*  800: 943 */     if (setters == null)
/*  801:     */     {
/*  802: 944 */       setters = new ArrayList();
/*  803: 945 */       table.put("setters", setters);
/*  804:     */     }
/*  805: 949 */     Class[] exceptions = theMethod.getExceptionTypes();
/*  811: 957 */     setters.add(theMethod);
/*  812:     */   }
/*  813:     */   
/*  814:     */   private void fixGetSet(HashMap<String, HashMap> propertyTable)
/*  815:     */     throws IntrospectionException{

}
/* 1120:     */   
/* 1121:     */   private EventSetDescriptor[] introspectEvents()
/* 1122:     */     throws IntrospectionException
/* 1123:     */   {
/* 1124:1313 */     MethodDescriptor[] theMethods = introspectMethods();
/* 1125:1315 */     if (theMethods == null) {
/* 1126:1316 */       return null;
/* 1127:     */     }
/* 1128:1318 */     HashMap<String, HashMap> eventTable = new HashMap(
/* 1129:1319 */       theMethods.length);
/* 1130:1322 */     for (int i = 0; i < theMethods.length; i++)
/* 1131:     */     {
/* 1132:1323 */       introspectListenerMethods("add", theMethods[i].getMethod(), 
/* 1133:1324 */         eventTable);
/* 1134:1325 */       introspectListenerMethods("remove", theMethods[i].getMethod(), 
/* 1135:1326 */         eventTable);
/* 1136:1327 */       introspectGetListenerMethods(theMethods[i].getMethod(), eventTable);
/* 1137:     */     }
/* 1138:1330 */     ArrayList<EventSetDescriptor> eventList = new ArrayList();
/* 1139:1331 */     for (Map.Entry<String, HashMap> entry : eventTable.entrySet())
/* 1140:     */     {
/* 1141:1332 */       HashMap table = (HashMap)entry.getValue();
/* 1142:1333 */       Method add = (Method)table.get("add");
/* 1143:1334 */       Method remove = (Method)table.get("remove");
/* 1144:1336 */       if ((add != null) && (remove != null))
/* 1145:     */       {
/* 1146:1340 */         Method get = (Method)table.get("get");
/* 1147:1341 */         Class<?> listenerType = (Class)table.get("listenerType");
/* 1148:1342 */         Method[] listenerMethods = (Method[])table.get("listenerMethods");
/* 1149:1343 */         EventSetDescriptor eventSetDescriptor = new EventSetDescriptor(
/* 1150:1344 */           Introspector.decapitalize((String)entry.getKey()), listenerType, listenerMethods, add, 
/* 1151:1345 */           remove, get);
/* 1152:     */         
/* 1153:1347 */         eventSetDescriptor.setUnicast(table.get("isUnicast") != null);
/* 1154:1348 */         eventList.add(eventSetDescriptor);
/* 1155:     */       }
/* 1156:     */     }
/* 1157:1351 */     EventSetDescriptor[] theEvents = new EventSetDescriptor[eventList
/* 1158:1352 */       .size()];
/* 1159:1353 */     eventList.toArray(theEvents);
/* 1160:     */     
/* 1161:1355 */     return theEvents;
/* 1162:     */   }
/* 1163:     */   
/* 1164:     */   private static void introspectListenerMethods(String type, Method theMethod, HashMap<String, HashMap> methodsTable)
/* 1165:     */   {
/* 1166:1364 */     String methodName = theMethod.getName();
/* 1167:1365 */     if (methodName == null) {
/* 1168:1366 */       return;
/* 1169:     */     }
/* 1170:1369 */     if ((!methodName.startsWith(type)) || 
/* 1171:1370 */       (!methodName.endsWith("Listener"))) {
/* 1172:1371 */       return;
/* 1173:     */     }
/* 1174:1374 */     String listenerName = methodName.substring(type.length());
/* 1175:1375 */     String eventName = listenerName.substring(0, listenerName
/* 1176:1376 */       .lastIndexOf("Listener"));
/* 1177:1377 */     if ((eventName == null) || (eventName.length() == 0)) {
/* 1178:1378 */       return;
/* 1179:     */     }
/* 1180:1381 */     Class[] paramTypes = theMethod.getParameterTypes();
/* 1181:1382 */     if ((paramTypes == null) || (paramTypes.length != 1)) {
/* 1182:1383 */       return;
/* 1183:     */     }
/* 1184:1386 */     Class<?> listenerType = paramTypes[0];
/* 1185:1388 */     if (!EventListener.class.isAssignableFrom(listenerType)) {
/* 1186:1389 */       return;
/* 1187:     */     }
/* 1188:1392 */     if (!listenerType.getName().endsWith(listenerName)) {
/* 1189:1393 */       return;
/* 1190:     */     }
/* 1191:1396 */     HashMap table = (HashMap)methodsTable.get(eventName);
/* 1192:1397 */     if (table == null) {
/* 1193:1398 */       table = new HashMap();
/* 1194:     */     }
/* 1195:1401 */     if (table.get("listenerType") == null)
/* 1196:     */     {
/* 1197:1402 */       table.put("listenerType", listenerType);
/* 1198:1403 */       table.put("listenerMethods", 
/* 1199:1404 */         introspectListenerMethods(listenerType));
/* 1200:     */     }
/* 1201:1407 */     table.put(type, theMethod);
/* 1202:1410 */     if (type.equals("add"))
/* 1203:     */     {
/* 1204:1411 */       Class[] exceptionTypes = theMethod.getExceptionTypes();
/* 1205:1412 */       if (exceptionTypes != null) {
/* 1206:1413 */         for (int i = 0; i < exceptionTypes.length; i++) {
/* 1207:1414 */           if (exceptionTypes[i].getName().equals(
/* 1208:1415 */             TooManyListenersException.class.getName()))
/* 1209:     */           {
/* 1210:1416 */             table.put("isUnicast", "true");
/* 1211:1417 */             break;
/* 1212:     */           }
/* 1213:     */         }
/* 1214:     */       }
/* 1215:     */     }
/* 1216:1423 */     methodsTable.put(eventName, table);
/* 1217:     */   }
/* 1218:     */   
/* 1219:     */   private static Method[] introspectListenerMethods(Class<?> listenerType)
/* 1220:     */   {
/* 1221:1427 */     Method[] methods = listenerType.getDeclaredMethods();
/* 1222:1428 */     ArrayList<Method> list = new ArrayList();
/* 1223:1429 */     for (int i = 0; i < methods.length; i++)
/* 1224:     */     {
/* 1225:1430 */       Class[] paramTypes = methods[i].getParameterTypes();
/* 1226:1431 */       if (paramTypes.length == 1) {
/* 1227:1435 */         if (EventObject.class.isAssignableFrom(paramTypes[0])) {
/* 1228:1436 */           list.add(methods[i]);
/* 1229:     */         }
/* 1230:     */       }
/* 1231:     */     }
/* 1232:1439 */     Method[] matchedMethods = new Method[list.size()];
/* 1233:1440 */     list.toArray(matchedMethods);
/* 1234:1441 */     return matchedMethods;
/* 1235:     */   }
/* 1236:     */   
/* 1237:     */   private static void introspectGetListenerMethods(Method theMethod, HashMap<String, HashMap> methodsTable)
/* 1238:     */   {
/* 1239:1447 */     String type = "get";
/* 1240:     */     
/* 1241:1449 */     String methodName = theMethod.getName();
/* 1242:1450 */     if (methodName == null) {
/* 1243:1451 */       return;
/* 1244:     */     }
/* 1245:1454 */     if ((!methodName.startsWith(type)) || 
/* 1246:1455 */       (!methodName.endsWith("Listeners"))) {
/* 1247:1456 */       return;
/* 1248:     */     }
/* 1249:1459 */     String listenerName = methodName.substring(type.length(), methodName
/* 1250:1460 */       .length() - 1);
/* 1251:1461 */     String eventName = listenerName.substring(0, listenerName
/* 1252:1462 */       .lastIndexOf("Listener"));
/* 1253:1463 */     if ((eventName == null) || (eventName.length() == 0)) {
/* 1254:1464 */       return;
/* 1255:     */     }
/* 1256:1467 */     Class[] paramTypes = theMethod.getParameterTypes();
/* 1257:1468 */     if ((paramTypes == null) || (paramTypes.length != 0)) {
/* 1258:1469 */       return;
/* 1259:     */     }
/* 1260:1472 */     Class returnType = theMethod.getReturnType();
/* 1261:1473 */     if ((returnType.getComponentType() == null) || 
/* 1262:1474 */       (!returnType.getComponentType().getName().endsWith(
/* 1263:1475 */       listenerName))) {
/* 1264:1476 */       return;
/* 1265:     */     }
/* 1266:1479 */     HashMap table = (HashMap)methodsTable.get(eventName);
/* 1267:1480 */     if (table == null) {
/* 1268:1481 */       table = new HashMap();
/* 1269:     */     }
/* 1270:1484 */     table.put(type, theMethod);
/* 1271:1485 */     methodsTable.put(eventName, table);
/* 1272:     */   }
/* 1273:     */   
/* 1274:     */   private static boolean isValidProperty(String propertyName)
/* 1275:     */   {
/* 1276:1489 */     return (propertyName != null) && (propertyName.length() != 0);
/* 1277:     */   }
/* 1278:     */   
/* 1279:     */   private static class PropertyComparator
/* 1280:     */     implements Comparator<PropertyDescriptor>
/* 1281:     */   {
/* 1282:     */     public int compare(PropertyDescriptor object1, PropertyDescriptor object2)
/* 1283:     */     {
/* 1284:1496 */       return object1.getName().compareTo(object2.getName());
/* 1285:     */     }
/* 1286:     */   }
/* 1287:     */   
/* 1288:     */   void init()
/* 1289:     */   {
/* 1290:1503 */     if (this.events == null) {
/* 1291:1504 */       this.events = new EventSetDescriptor[0];
/* 1292:     */     }
/* 1293:1506 */     if (this.properties == null) {
/* 1294:1507 */       this.properties = new PropertyDescriptor[0];
/* 1295:     */     }
/* 1296:1510 */     if (this.properties != null)
/* 1297:     */     {
/* 1298:1511 */       String defaultPropertyName = this.defaultPropertyIndex != -1 ? this.properties[this.defaultPropertyIndex]
/* 1299:1512 */         .getName() : 
/* 1300:1513 */         null;
/* 1301:1514 */       Arrays.sort(this.properties, comparator);
/* 1302:1515 */       if (defaultPropertyName != null) {
/* 1303:1516 */         for (int i = 0; i < this.properties.length; i++) {
/* 1304:1517 */           if (defaultPropertyName.equals(this.properties[i].getName()))
/* 1305:     */           {
/* 1306:1518 */             this.defaultPropertyIndex = i;
/* 1307:1519 */             break;
/* 1308:     */           }
/* 1309:     */         }
/* 1310:     */       }
/* 1311:     */     }
/* 1312:     */   }
/* 1313:     */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.StandardBeanInfo

 * JD-Core Version:    0.7.0.1

 */