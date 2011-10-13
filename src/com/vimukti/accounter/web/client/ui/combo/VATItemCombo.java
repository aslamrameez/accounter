package com.vimukti.accounter.web.client.ui.combo;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.web.client.core.ClientTAXAgency;
import com.vimukti.accounter.web.client.core.ClientTAXItem;
import com.vimukti.accounter.web.client.core.ClientTAXItemGroup;
import com.vimukti.accounter.web.client.ui.core.ActionCallback;
import com.vimukti.accounter.web.client.ui.customers.TaxDialog;

/**
 * @author Murali.A
 * 
 */
public class VATItemCombo extends CustomCombo<ClientTAXItemGroup> {

	/**
	 * @param title
	 */
	public VATItemCombo(String title) {
		super(title);
		initCombo(getVATItmes());
	}

	public VATItemCombo(String title, boolean isAddNewRequired) {
		super(title, isAddNewRequired, 1);
		initCombo(getVATItmes());
	}

	public VATItemCombo(String title, ClientTAXAgency taxAgency) {
		super(title);
		initCombo(getVATItmes());

	}

	/* This method returns the VATItems for a VATAgency */
	public List<ClientTAXItemGroup> getVATItmesByVATAgncy(
			ClientTAXAgency taxAgency) {
		List<ClientTAXItemGroup> vatItmsList = new ArrayList<ClientTAXItemGroup>();
		if (taxAgency != null) {
			for (ClientTAXItem vatItem : getCompany().getTaxItems()) {
				if (vatItem.getTaxAgency() == (taxAgency.getID())) {
					vatItmsList.add(vatItem);
				}
			}
		}
		return vatItmsList;
	}

	/* VATItmes whose 'isPercentage' is false, only allowed into the list */
	List<ClientTAXItemGroup> getVATItmes() {
		List<ClientTAXItemGroup> vatItmsList = new ArrayList<ClientTAXItemGroup>();
		ArrayList<ClientTAXItemGroup> taxItemGroups = getCompany()
				.getTaxItemGroups();
		taxItemGroups.addAll(getCompany().getTaxItems());
		for (ClientTAXItemGroup vatItem : taxItemGroups) {
			if (!vatItem.isPercentage()) {
				vatItmsList.add(vatItem);
			}
		}
		return vatItmsList;
	}

	/* VATItmes whose 'isPercentage' is true, only allowed into the list */
	public List<ClientTAXItemGroup> getFilteredVATItems() {
		List<ClientTAXItemGroup> vatItmsList = new ArrayList<ClientTAXItemGroup>();
		ArrayList<ClientTAXItemGroup> taxItemGroups = getCompany()
				.getTaxItemGroups();
		taxItemGroups.addAll(getCompany().getTaxItems());
		for (ClientTAXItemGroup vatItem : getCompany().getTaxItems()) {
			if (vatItem.isPercentage()) {
				vatItmsList.add(vatItem);
			}
		}
		return vatItmsList;
	}

	/*
	 * VATItmes whose 'isPercentage' is true,and "Sales" type VATItems only
	 * allowed into the list
	 */
	public List<ClientTAXItemGroup> getSalesWithPrcntVATItems() {
		List<ClientTAXItemGroup> vatItmsList = new ArrayList<ClientTAXItemGroup>();
		for (ClientTAXItemGroup vatItem : getFilteredVATItems()) {
			if (vatItem.isSalesType()) {
				vatItmsList.add(vatItem);
			}
		}
		return vatItmsList;
	}

	/*
	 * VATItmes whose 'isPercentage' is true,and "Purchase" type VATItems only
	 * allowed into the list
	 */
	public List<ClientTAXItemGroup> getPurchaseWithPrcntVATItems() {
		List<ClientTAXItemGroup> vatItmsList = new ArrayList<ClientTAXItemGroup>();
		for (ClientTAXItemGroup vatItem : getFilteredVATItems()) {
			if (!vatItem.isSalesType()) {
				vatItmsList.add(vatItem);
			}
		}
		return vatItmsList;
	}

	/*
	 * @see
	 * com.vimukti.accounter.web.client.ui.combo.CustomCombo#getDefaultAddNewCaption
	 * ()
	 */
	@Override
	public String getDefaultAddNewCaption() {
		return comboMessages.newVATItem();
	}

	/*
	 * @see
	 * com.vimukti.accounter.web.client.ui.combo.CustomCombo#getDisplayName(
	 * java.lang.Object)
	 */
	@Override
	protected String getDisplayName(ClientTAXItemGroup object) {
		if (object != null)
			return object.getName() != null ? object.getName() : "";
		else
			return "";
	}

	/*
	 * @see
	 * com.vimukti.accounter.web.client.ui.combo.CustomCombo#getSelectItemType()
	 */

	/*
	 * @see com.vimukti.accounter.web.client.ui.combo.CustomCombo#onAddNew()
	 */
	@Override
	public void onAddNew() {
		TaxDialog dialog = new TaxDialog();
		dialog.setCallback(new ActionCallback<ClientTAXItemGroup>() {

			@Override
			public void actionResult(ClientTAXItemGroup result) {
				addItemThenfireEvent(result);
			}
		});
		dialog.show();
		// NewVatItemAction action = ActionFactory.getNewVatItemAction();
		// action.setCallback(new ActionCallback<ClientTAXItem>() {
		//
		// @Override
		// public void actionResult(ClientTAXItem result) {
		// if (result.getName() != null || result.getDisplayName() != null)
		// addItemThenfireEvent(result);
		//
		// }
		// });
		//
		// action.run(null, true);
	}

	@Override
	protected String getColumnData(ClientTAXItemGroup object, int col) {
		switch (col) {
		case 0:
			return object.getName();
		}
		return null;
	}

}
