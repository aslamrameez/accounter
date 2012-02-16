package com.vimukti.accounter.web.client.ui.combo;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.ValueCallBack;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientJob;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.NewJobDialog;

public class JobCombo extends CustomCombo<ClientJob> {
	private ValueCallBack<ClientJob> newJobtHandler;
	private ClientCustomer customer;

	public JobCombo(String title) {
		super(title, true, 1);
	}

	private void getJobsByCustomer() {
		Accounter.createHomeService().getJobsByCustomer(customer.getID(),
				new AsyncCallback<List<ClientJob>>() {
					@Override
					public void onSuccess(List<ClientJob> result) {
						if (!result.isEmpty()) {
							initCombo(result);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	@Override
	protected String getDisplayName(ClientJob object) {
		if (object != null)
			return object.getJobName() != null ? object.getJobName() : "";
		else
			return "";
	}

	@Override
	protected String getColumnData(ClientJob object, int col) {
		switch (col) {
		case 0:
			return object.getJobName();
		}
		return null;
	}

	@Override
	public String getDefaultAddNewCaption() {
		return messages.job();
	}

	/**
	 * @param valueCallBack
	 */

	public void addNewJobHandler(ValueCallBack<ClientJob> newJobtHandler) {
		this.newJobtHandler = newJobtHandler;
	}

	@Override
	public void onAddNew() {
		NewJobDialog jobDialog = new NewJobDialog(null, messages.job(), "",
				customer);
		jobDialog.addSuccessCallback(newJobtHandler);
	}

	public void setCustomer(ClientCustomer customer) {
		this.customer = customer;
		getJobsByCustomer();
	}
}
