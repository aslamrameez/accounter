package com.vimukti.accounter.web.client.ui.combo;

import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientVendorGroup;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.vendors.VendorGroupListDialog;

public class VendorGroupCombo extends CustomCombo<ClientVendorGroup> {

	public VendorGroupCombo(String title) {
		super(title);
	}

	@Override
	public String getDefaultAddNewCaption() {
		return Accounter.comboMessages().newVendorGroup(Global.get().vendor());
	}

	@Override
	protected String getDisplayName(ClientVendorGroup object) {
		if (object != null)
			return object.getName() != null ? object.getName() : "";
		else
			return "";
	}

	@Override
	public void onAddNew() {
		VendorGroupListDialog vendorGroup = new VendorGroupListDialog();
		vendorGroup.hide();
		vendorGroup.addCallBack(createAddNewCallBack());
		vendorGroup.showAddEditGroupDialog(null);
	}

	@Override
	protected String getColumnData(ClientVendorGroup object,  int col) {
		switch (col) {
		case 0:
			return object.getName();
		}
		return null;
	}

}
