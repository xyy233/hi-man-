/*  1:   */ package com.alipay.api.response;
/*  2:   */ 
/*  3:   */

import com.alipay.api.AlipayResponse;
import com.google.gson.annotations.SerializedName;

/*  4:   */

/*  5:   */ 
/*  6:   */ public class AlipayTradeCancelResponse
/*  7:   */   extends AlipayResponse
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 1167542313361592519L;
/* 10:   */   @SerializedName("action")
/* 11:   */   private String action;
/* 12:   */   @SerializedName("out_trade_no")
/* 13:   */   private String outTradeNo;
/* 14:   */   @SerializedName("retry_flag")
/* 15:   */   private String retryFlag;
/* 16:   */   @SerializedName("trade_no")
/* 17:   */   private String tradeNo;
/* 18:   */   
/* 19:   */   public void setAction(String action)
/* 20:   */   {
/* 21:44 */     this.action = action;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public String getAction()
/* 25:   */   {
/* 26:47 */     return this.action;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public void setOutTradeNo(String outTradeNo)
/* 30:   */   {
/* 31:51 */     this.outTradeNo = outTradeNo;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public String getOutTradeNo()
/* 35:   */   {
/* 36:54 */     return this.outTradeNo;
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void setRetryFlag(String retryFlag)
/* 40:   */   {
/* 41:58 */     this.retryFlag = retryFlag;
/* 42:   */   }
/* 43:   */   
/* 44:   */   public String getRetryFlag()
/* 45:   */   {
/* 46:61 */     return this.retryFlag;
/* 47:   */   }
/* 48:   */   
/* 49:   */   public void setTradeNo(String tradeNo)
/* 50:   */   {
/* 51:65 */     this.tradeNo = tradeNo;
/* 52:   */   }
/* 53:   */   
/* 54:   */   public String getTradeNo()
/* 55:   */   {
/* 56:68 */     return this.tradeNo;
/* 57:   */   }
/* 58:   */ }



/* Location:           C:\Users\zhiya.zhang.C-STORE\Desktop\alipay-sdk-java.jar

 * Qualified Name:     com.alipay.api.response.AlipayTradeCancelResponse

 * JD-Core Version:    0.7.0.1

 */