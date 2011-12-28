package com.vimukti.accounter.web.client.ui.edittable;

import com.vimukti.accounter.web.client.core.ClientTransactionItem;

public class DescriptionEditColumn extends
		TextAreaEditColumn<ClientTransactionItem> {

	@Override
	protected String getValue(ClientTransactionItem row) {
		return row.getDescription();
	}

	@Override
	protected void setValue(ClientTransactionItem row, String value) {
		row.setDescription(value);
	}

	@Override
	protected String getColumnName() {
		return messages.description();
	}

	@Override
	public int getWidth() {
		return -1;
	}
}
