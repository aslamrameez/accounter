package com.vimukti.accounter.web.client.ui.company;

import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.core.ClientBank;
import com.vimukti.accounter.web.client.core.ClientTAXItem;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.exception.AccounterExceptions;
import com.vimukti.accounter.web.client.ui.AbstractBaseView;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.TextItem;

public class AddBankDialog extends BaseDialog {

	private TextItem bankNameText;
	private AccounterAsyncCallback<ClientBank> callBack;

	public AddBankDialog(AbstractBaseView<ClientBank> parent) {
		super(messages.addBank(), null);
		createControls();
		center();
	}

	private void createControls() {

		setText(messages.addBank());

		bankNameText = new TextItem(messages.bankName());
		bankNameText.setRequired(true);
		DynamicForm bankForm = new DynamicForm();
		bankForm.setFields(bankNameText);

		setBodyLayout(bankForm);
		setWidth("275px");

	}

	protected void createBank() {
		ClientBank bank = new ClientBank();
		bank.setName(UIUtils.toStr(bankNameText.getValue()));
		saveOrUpdate(bank);
	}

	@Override
	protected ValidationResult validate() {
		ValidationResult result = new ValidationResult();
		if (!bankNameText.validate()) {
			result.addError(bankNameText, bankNameText.getTitle());
		}
		String itemName = bankNameText.getValue();
		ClientTAXItem clientTAXItem = company.getTaxItemByName(itemName);
		if (clientTAXItem != null) {
			result.addError(this, messages.alreadyExist());
		}
		return result;
	}

	@Override
	public void saveSuccess(IAccounterCore object) {
		if (callBack != null) {
			callBack.onResultSuccess((ClientBank) object);
		}
		// Accounter.showInformation(FinanceApplication.constants()
		// .bankCreated());
		super.saveSuccess(object);

	}

	@Override
	public void saveFailed(AccounterException exception) {
		super.saveFailed(exception);
		AccounterException accounterException = (AccounterException) exception;
		int errorCode = accounterException.getErrorCode();
		String errorString = AccounterExceptions.getErrorString(errorCode);
		Accounter.showError(errorString);
	}

	public void addCallBack(AccounterAsyncCallback<ClientBank> callback) {
		this.callBack = callback;
	}

	@Override
	protected boolean onOK() {
		createBank();
		return true;
	}

	@Override
	public void deleteFailed(AccounterException caught) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		bankNameText.setFocus();

	}

}
