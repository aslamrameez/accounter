/**
 * 
 */
package com.vimukti.accounter.web.client.core;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.vimukti.accounter.web.client.ui.Accounter;

/**
 * @author Prasanna Kumar G
 * 
 */
public class ClientQuantity implements IsSerializable, Serializable, Cloneable,
		Comparable<ClientQuantity> {

	private long unit;
	private double value;

	/**
	 * @return the unit
	 */
	public long getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(long unit) {
		this.unit = unit;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	public ClientQuantity clone() {
		ClientQuantity quantity = (ClientQuantity) this.clone();
		quantity.unit = this.unit;
		return quantity;
	}

	@Override
	public String toString() {
		if (unit != 0) {
			return value + Accounter.getCompany().getUnitById(unit).getType();
		} else {
			return String.valueOf(value);
		}
	}

	@Override
	public int compareTo(ClientQuantity o) {
		// TODO verify this method later
		ClientUnit unitById = Accounter.getCompany().getUnitById(unit);
		ClientQuantity thisQty = convertToDefaultUnit(unitById);
		ClientQuantity otherQty = o.convertToDefaultUnit(unitById);
		return (int) (thisQty.getValue() - otherQty.getValue());
	}

	/**
	 * Converts the present quantity into default measurement.
	 * 
	 * @return
	 */
	private ClientQuantity convertToDefaultUnit(ClientUnit unit) {
		double conversionFactor = unit == null ? 1 : unit.getMeasurement()
				.getConversionFactor(unit.getType());

		ClientQuantity quantity = new ClientQuantity();
		quantity.setValue(value * conversionFactor);
		if (unit != null)
			quantity.setUnit(unit.getMeasurement().getDefaultUnit().getId());

		return quantity;
	}
}
