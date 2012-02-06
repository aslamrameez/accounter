package com.vimukti.accounter.web.client.core.reports;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InventoryStockStatusDetail extends BaseReport implements
		IsSerializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String itemDesc;

	private long reorderPts;

	private long onHand;

	private long onSalesOrder;

	private long assemblies;

	private long avilability;

	private long orderOnPo;

	private long nextDeliveryDate;

	private long salesPerWeek;

	private Long preferVendor;

	private long order;

	private String itemName;

	private long itemId;

	public String getItemDesc() {
		return itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public long getReorderPts() {
		return reorderPts;
	}

	public void setReorderPts(long reorderPts) {
		this.reorderPts = reorderPts;
	}

	public long getOnHand() {
		return onHand;
	}

	public void setOnHand(long onHand) {
		this.onHand = onHand;
	}

	public long getOnSalesOrder() {
		return onSalesOrder;
	}

	public void setOnSalesOrder(long onSalesOrder) {
		this.onSalesOrder = onSalesOrder;
	}

	public long getAssemblies() {
		return assemblies;
	}

	public void setAssemblies(long assemblies) {
		this.assemblies = assemblies;
	}

	public long getAvilability() {
		return avilability;
	}

	public void setAvilability(long avilability) {
		this.avilability = avilability;
	}

	public long getOrderOnPo() {
		return orderOnPo;
	}

	public void setOrderOnPo(long orderOnPo) {
		this.orderOnPo = orderOnPo;
	}

	public long getNextDeliveryDate() {
		return nextDeliveryDate;
	}

	public void setNextDeliveryDate(long nextDeliveryDate) {
		this.nextDeliveryDate = nextDeliveryDate;
	}

	public long getSalesPerWeek() {
		return salesPerWeek;
	}

	public void setSalesPerWeek(long salesPerWeek) {
		this.salesPerWeek = salesPerWeek;
	}

	public Long getPreferVendor() {
		return preferVendor;
	}

	public void setPreferVendor(Long next) {
		this.preferVendor = next;
	}

	public long getOredr() {
		return getOrder();
	}

	public long getOrder() {
		return order;
	}

	public void setOrder(long order) {
		this.order = order;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

}
