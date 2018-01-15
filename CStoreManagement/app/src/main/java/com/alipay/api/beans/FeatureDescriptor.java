/*   1:    */ package com.alipay.api.beans;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.HashMap;
/*   6:    */ import java.util.LinkedList;
/*   7:    */ import java.util.Map;
/*   8:    */ 
/*   9:    */ public class FeatureDescriptor
/*  10:    */ {
/*  11:    */   private Map<String, Object> values;
/*  12:    */   boolean preferred;
/*  13:    */   boolean hidden;
/*  14:    */   boolean expert;
/*  15:    */   String shortDescription;
/*  16:    */   String name;
/*  17:    */   String displayName;
/*  18:    */   
/*  19:    */   public FeatureDescriptor()
/*  20:    */   {
/*  21: 48 */     this.values = new HashMap();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void setValue(String attributeName, Object value)
/*  25:    */   {
/*  26: 62 */     if ((attributeName == null) || (value == null)) {
/*  27: 63 */       throw new NullPointerException();
/*  28:    */     }
/*  29: 65 */     this.values.put(attributeName, value);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Object getValue(String attributeName)
/*  33:    */   {
/*  34: 78 */     if (attributeName != null) {
/*  35: 79 */       return this.values.get(attributeName);
/*  36:    */     }
/*  37: 81 */     return null;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Enumeration<String> attributeNames()
/*  41:    */   {
/*  42: 93 */     return Collections.enumeration(new LinkedList(this.values.keySet()));
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setShortDescription(String text)
/*  46:    */   {
/*  47:105 */     this.shortDescription = text;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setName(String name)
/*  51:    */   {
/*  52:117 */     this.name = name;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void setDisplayName(String displayName)
/*  56:    */   {
/*  57:129 */     this.displayName = displayName;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getShortDescription()
/*  61:    */   {
/*  62:140 */     return this.shortDescription == null ? getDisplayName() : this.shortDescription;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String getName()
/*  66:    */   {
/*  67:151 */     return this.name;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String getDisplayName()
/*  71:    */   {
/*  72:162 */     return this.displayName == null ? getName() : this.displayName;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setPreferred(boolean preferred)
/*  76:    */   {
/*  77:175 */     this.preferred = preferred;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setHidden(boolean hidden)
/*  81:    */   {
/*  82:187 */     this.hidden = hidden;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setExpert(boolean expert)
/*  86:    */   {
/*  87:199 */     this.expert = expert;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public boolean isPreferred()
/*  91:    */   {
/*  92:210 */     return this.preferred;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public boolean isHidden()
/*  96:    */   {
/*  97:221 */     return this.hidden;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public boolean isExpert()
/* 101:    */   {
/* 102:232 */     return this.expert;
/* 103:    */   }
/* 104:    */   
/* 105:    */   void merge(FeatureDescriptor feature)
/* 106:    */   {
/* 107:236 */     assert (this.name.equals(feature.name));
/* 108:237 */     this.expert |= feature.expert;
/* 109:238 */     this.hidden |= feature.hidden;
/* 110:239 */     this.preferred |= feature.preferred;
/* 111:240 */     if (this.shortDescription == null) {
/* 112:241 */       this.shortDescription = feature.shortDescription;
/* 113:    */     }
/* 114:243 */     if (this.name == null) {
/* 115:244 */       this.name = feature.name;
/* 116:    */     }
/* 117:246 */     if (this.displayName == null) {
/* 118:247 */       this.displayName = feature.displayName;
/* 119:    */     }
/* 120:    */   }
/* 121:    */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\openbeans-1.0.jar

 * Qualified Name:     com.googlecode.openbeans.FeatureDescriptor

 * JD-Core Version:    0.7.0.1

 */