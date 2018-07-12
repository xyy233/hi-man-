package com.alipay.api.response;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.internal.mapping.ApiListField;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * ALIPAY API: alipay.trade.refund response.
 * 
 * @author auto create
 * @since 1.0, 2017-04-19 20:31:51
 */
public class AlipayTradeRefundResponse extends AlipayResponse {

	private static final long serialVersionUID = 1693896255179349125L;

	/** 
	 * 用户的登录id
	 */
	@SerializedName("buyer_logon_id")
	private String buyerLogonId;

	/** 
	 * 买家在支付宝的用户id
	 */
	@SerializedName("buyer_user_id")
	private String buyerUserId;

	/** 
	 * 本次退款是否发生了资金变化
	 */
	@SerializedName("fund_change")
	private String fundChange;

	/** 
	 * 退款支付时间
	 */
	@SerializedName("gmt_refund_pay")
	private String gmtRefundPay;

	/** 
	 * 买家支付宝用户号，该参数已废弃，请不要使用
	 */
	@SerializedName("open_id")
	private String openId;

	/** 
	 * 商户订单号
	 */
	@SerializedName("out_trade_no")
	private String outTradeNo;

	/** 
	 * 退款使用的资金渠道
	 */
	@ApiListField("refund_detail_item_list")
	@SerializedName("trade_fund_bill")
	private List<TradeFundBill> refundDetailItemList;

	/** 
	 * 退款总金额
	 */
	@SerializedName("refund_fee")
	private String refundFee;

	/** 
	 * 本次商户实际退回金额
注：在签约收单产品时需勾选“返回资金明细”才会返回
	 */
	@SerializedName("send_back_fee")
	private String sendBackFee;

	/** 
	 * 交易在支付时候的门店名称
	 */
	@SerializedName("store_name")
	private String storeName;

	/** 
	 * 2013112011001004330000121536
	 */
	@SerializedName("trade_no")
	private String tradeNo;

	public void setBuyerLogonId(String buyerLogonId) {
		this.buyerLogonId = buyerLogonId;
	}
	public String getBuyerLogonId( ) {
		return this.buyerLogonId;
	}

	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}
	public String getBuyerUserId( ) {
		return this.buyerUserId;
	}

	public void setFundChange(String fundChange) {
		this.fundChange = fundChange;
	}
	public String getFundChange( ) {
		return this.fundChange;
	}

	public void setGmtRefundPay(String gmtRefundPay) {
		this.gmtRefundPay = gmtRefundPay;
	}
	public String getGmtRefundPay( ) {
		return this.gmtRefundPay;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getOpenId( ) {
		return this.openId;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getOutTradeNo( ) {
		return this.outTradeNo;
	}

	public void setRefundDetailItemList(List<TradeFundBill> refundDetailItemList) {
		this.refundDetailItemList = refundDetailItemList;
	}
	public List<TradeFundBill> getRefundDetailItemList( ) {
		return this.refundDetailItemList;
	}

	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}
	public String getRefundFee( ) {
		return this.refundFee;
	}

	public void setSendBackFee(String sendBackFee) {
		this.sendBackFee = sendBackFee;
	}
	public String getSendBackFee( ) {
		return this.sendBackFee;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStoreName( ) {
		return this.storeName;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTradeNo( ) {
		return this.tradeNo;
	}

}
