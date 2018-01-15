package com.alipay.api.response;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.domain.VoucherDetail;
import com.alipay.api.internal.mapping.ApiListField;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * ALIPAY API: alipay.trade.query response.
 * 
 * @author auto create
 * @since 1.0, 2017-10-19 16:09:23
 */
public class AlipayTradeQueryResponse extends AlipayResponse {

	private static final long serialVersionUID = 5421169124818691522L;

	/** 
	 * 支付宝店铺编号
	 */
	@SerializedName("alipay_store_id")
	private String alipayStoreId;

	/** 
	 * 买家支付宝账号
	 */
	@SerializedName("buyer_logon_id")
	private String buyerLogonId;

	/** 
	 * 买家实付金额，单位为元，两位小数。该金额代表该笔交易买家实际支付的金额，不包含商户折扣等金额
	 */
	@SerializedName("buyer_pay_amount")
	private String buyerPayAmount;

	/** 
	 * 买家在支付宝的用户id
	 */
	@SerializedName("buyer_user_id")
	private String buyerUserId;

	/** 
	 * 本次交易支付所使用的单品券优惠的商品优惠信息
	 */
	@SerializedName("discount_goods_detail")
	private String discountGoodsDetail;

	/** 
	 * 交易支付使用的资金渠道
	 */
	@ApiListField("fund_bill_list")
	@SerializedName("trade_fund_bill")
	private List<TradeFundBill> fundBillList;

	/** 
	 * 行业特殊信息（例如在医保卡支付业务中，向用户返回医疗信息）。
	 */
	@SerializedName("industry_sepc_detail")
	private String industrySepcDetail;

	/** 
	 * 交易中用户支付的可开具发票的金额，单位为元，两位小数。该金额代表该笔交易中可以给用户开具发票的金额
	 */
	@SerializedName("invoice_amount")
	private String invoiceAmount;

	/** 
	 * 买家支付宝用户号，该字段将废弃，不要使用
	 */
	@SerializedName("open_id")
	private String openId;

	/** 
	 * 商家订单号
	 */
	@SerializedName("out_trade_no")
	private String outTradeNo;

	/** 
	 * 积分支付的金额，单位为元，两位小数。该金额代表该笔交易中用户使用积分支付的金额，比如集分宝或者支付宝实时优惠等
	 */
	@SerializedName("point_amount")
	private String pointAmount;

	/** 
	 * 实收金额，单位为元，两位小数。该金额为本笔交易，商户账户能够实际收到的金额
	 */
	@SerializedName("receipt_amount")
	private String receiptAmount;

	/** 
	 * 本次交易打款给卖家的时间
	 */
	@SerializedName("send_pay_date")
	private String sendPayDate;

	/** 
	 * 商户门店编号
	 */
	@SerializedName("store_id")
	private String storeId;

	/** 
	 * 请求交易支付中的商户店铺的名称
	 */
	@SerializedName("store_name")
	private String storeName;

	/** 
	 * 商户机具终端编号
	 */
	@SerializedName("terminal_id")
	private String terminalId;

	/** 
	 * 交易的订单金额，单位为元，两位小数。该参数的值为支付时传入的total_amount
	 */
	@SerializedName("total_amount")
	private String totalAmount;

	/** 
	 * 支付宝交易号
	 */
	@SerializedName("trade_no")
	private String tradeNo;

	/** 
	 * 交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
	 */
	@SerializedName("trade_status")
	private String tradeStatus;

	/** 
	 * 本交易支付时使用的所有优惠券信息
	 */
	@ApiListField("voucher_detail_list")
	@SerializedName("voucher_detail")
	private List<VoucherDetail> voucherDetailList;

	public void setAlipayStoreId(String alipayStoreId) {
		this.alipayStoreId = alipayStoreId;
	}
	public String getAlipayStoreId( ) {
		return this.alipayStoreId;
	}

	public void setBuyerLogonId(String buyerLogonId) {
		this.buyerLogonId = buyerLogonId;
	}
	public String getBuyerLogonId( ) {
		return this.buyerLogonId;
	}

	public void setBuyerPayAmount(String buyerPayAmount) {
		this.buyerPayAmount = buyerPayAmount;
	}
	public String getBuyerPayAmount( ) {
		return this.buyerPayAmount;
	}

	public void setBuyerUserId(String buyerUserId) {
		this.buyerUserId = buyerUserId;
	}
	public String getBuyerUserId( ) {
		return this.buyerUserId;
	}

	public void setDiscountGoodsDetail(String discountGoodsDetail) {
		this.discountGoodsDetail = discountGoodsDetail;
	}
	public String getDiscountGoodsDetail( ) {
		return this.discountGoodsDetail;
	}

	public void setFundBillList(List<TradeFundBill> fundBillList) {
		this.fundBillList = fundBillList;
	}
	public List<TradeFundBill> getFundBillList( ) {
		return this.fundBillList;
	}

	public void setIndustrySepcDetail(String industrySepcDetail) {
		this.industrySepcDetail = industrySepcDetail;
	}
	public String getIndustrySepcDetail( ) {
		return this.industrySepcDetail;
	}

	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public String getInvoiceAmount( ) {
		return this.invoiceAmount;
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

	public void setPointAmount(String pointAmount) {
		this.pointAmount = pointAmount;
	}
	public String getPointAmount( ) {
		return this.pointAmount;
	}

	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	public String getReceiptAmount( ) {
		return this.receiptAmount;
	}

	public void setSendPayDate(String sendPayDate) {
		this.sendPayDate = sendPayDate;
	}
	public String getSendPayDate( ) {
		return this.sendPayDate;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getStoreId( ) {
		return this.storeId;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStoreName( ) {
		return this.storeName;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getTerminalId( ) {
		return this.terminalId;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTotalAmount( ) {
		return this.totalAmount;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTradeNo( ) {
		return this.tradeNo;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}
	public String getTradeStatus( ) {
		return this.tradeStatus;
	}

	public void setVoucherDetailList(List<VoucherDetail> voucherDetailList) {
		this.voucherDetailList = voucherDetailList;
	}
	public List<VoucherDetail> getVoucherDetailList( ) {
		return this.voucherDetailList;
	}

}
