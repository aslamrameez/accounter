package com.vimukti.accounter.web.client.ui.customers;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.core.ClientInvoice;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.MainFinanceWindow;
import com.vimukti.accounter.web.client.ui.core.AccounterAsync;
import com.vimukti.accounter.web.client.ui.core.Action;
import com.vimukti.accounter.web.client.ui.core.CreateViewAsyncCallBack;
import com.vimukti.accounter.web.client.ui.core.ParentCanvas;

/**
 * 
 * @author Raj Vimal
 */

public class NewInvoiceAction extends Action {

	protected InvoiceView view;

	public NewInvoiceAction(String text, String iconString) {
		super(text, iconString);
		this.catagory = Accounter.getCustomersMessages().customer();
	}

	public NewInvoiceAction(String text, String iconString,
			ClientInvoice invoice, AsyncCallback<Object> callback) {
		super(text, iconString, invoice, callback);
		this.catagory = Accounter.getCustomersMessages().customer();
	}

	@Override
	public void run(Object data, Boolean isDependent) {
		runAsync(data, isDependent);
	}

	public void runAsync(final Object data, final Boolean isDependent) {
		AccounterAsync.createAsync(new CreateViewAsyncCallBack() {

			public void onCreated() {

				try {

					view = InvoiceView.getInstance();
					MainFinanceWindow.getViewManager().showView(view, data,
							isDependent, NewInvoiceAction.this);

				} catch (Throwable e) {
					onCreateFailed(e);
				}

			}

			public void onCreateFailed(Throwable t) {
				// //UIUtils.logError("Failed to Load Invoice...", t);
			}
		});
	}

	@Override
	public ParentCanvas<?> getView() {
		// NOTHING TO DO.
		return null;
	}

	public ImageResource getBigImage() {
		// NOTHING TO DO.
		return null;
	}

	public ImageResource getSmallImage() {
		return Accounter.getFinanceMenuImages().newInvoice();
	}

	@Override
	public String getImageUrl() {
		return "/images/new_invoice.png";
	}

	@Override
	public String getHistoryToken() {
		return "newInvoice";
	}
}
