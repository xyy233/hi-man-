/*   1:    */ package com.alipay.api.request;
/*   2:    */ 
/*   3:    */ import com.alipay.api.AlipayObject;
/*   4:    */ import com.alipay.api.AlipayRequest;
/*   5:    */ import com.alipay.api.internal.util.AlipayHashMap;
/*   6:    */ import com.alipay.api.response.AlipayTradeCancelResponse;
/*   7:    */ import java.util.Map;
/*   8:    */ 
/*   9:    */ public class AlipayTradeCancelRequest
/*  10:    */   implements AlipayRequest<AlipayTradeCancelResponse>
/*  11:    */ {
/*  12:    */   private AlipayHashMap udfParams;
/*  13: 20 */   private String apiVersion = "1.0";
/*  14:    */   private String bizContent;
/*  15:    */   private String terminalType;
/*  16:    */   private String terminalInfo;
/*  17:    */   private String prodCode;
/*  18:    */   private String notifyUrl;
/*  19:    */   private String returnUrl;
/*  20:    */   
/*  21:    */   public void setBizContent(String bizContent)
/*  22:    */   {
/*  23: 28 */     this.bizContent = bizContent;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public String getBizContent()
/*  27:    */   {
/*  28: 31 */     return this.bizContent;
/*  29:    */   }
/*  30:    */   
/*  31: 38 */   private boolean needEncrypt = false;
/*  32: 39 */   private AlipayObject bizModel = null;
/*  33:    */   
/*  34:    */   public String getNotifyUrl()
/*  35:    */   {
/*  36: 42 */     return this.notifyUrl;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void setNotifyUrl(String notifyUrl)
/*  40:    */   {
/*  41: 46 */     this.notifyUrl = notifyUrl;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getReturnUrl()
/*  45:    */   {
/*  46: 50 */     return this.returnUrl;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setReturnUrl(String returnUrl)
/*  50:    */   {
/*  51: 54 */     this.returnUrl = returnUrl;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getApiVersion()
/*  55:    */   {
/*  56: 58 */     return this.apiVersion;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setApiVersion(String apiVersion)
/*  60:    */   {
/*  61: 62 */     this.apiVersion = apiVersion;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setTerminalType(String terminalType)
/*  65:    */   {
/*  66: 66 */     this.terminalType = terminalType;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String getTerminalType()
/*  70:    */   {
/*  71: 70 */     return this.terminalType;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setTerminalInfo(String terminalInfo)
/*  75:    */   {
/*  76: 74 */     this.terminalInfo = terminalInfo;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String getTerminalInfo()
/*  80:    */   {
/*  81: 78 */     return this.terminalInfo;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setProdCode(String prodCode)
/*  85:    */   {
/*  86: 82 */     this.prodCode = prodCode;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String getProdCode()
/*  90:    */   {
/*  91: 86 */     return this.prodCode;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getApiMethodName()
/*  95:    */   {
/*  96: 90 */     return "alipay.trade.cancel";
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Map<String, String> getTextParams()
/* 100:    */   {
/* 101: 94 */     AlipayHashMap txtParams = new AlipayHashMap();
/* 102: 95 */     txtParams.put("biz_content", this.bizContent);
/* 103: 96 */     if (this.udfParams != null) {
/* 104: 97 */       txtParams.putAll(this.udfParams);
/* 105:    */     }
/* 106: 99 */     return txtParams;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void putOtherTextParam(String key, String value)
/* 110:    */   {
/* 111:103 */     if (this.udfParams == null) {
/* 112:104 */       this.udfParams = new AlipayHashMap();
/* 113:    */     }
/* 114:106 */     this.udfParams.put(key, value);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public Class<AlipayTradeCancelResponse> getResponseClass()
/* 118:    */   {
/* 119:110 */     return AlipayTradeCancelResponse.class;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public boolean isNeedEncrypt()
/* 123:    */   {
/* 124:116 */     return this.needEncrypt;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public void setNeedEncrypt(boolean needEncrypt)
/* 128:    */   {
/* 129:122 */     this.needEncrypt = needEncrypt;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public AlipayObject getBizModel()
/* 133:    */   {
/* 134:127 */     return this.bizModel;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void setBizModel(AlipayObject bizModel)
/* 138:    */   {
/* 139:133 */     this.bizModel = bizModel;
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\alipay-sdk-java.jar
 * Qualified Name:     com.alipay.api.request.AlipayTradeCancelRequest
 * JD-Core Version:    0.7.0.1
 */