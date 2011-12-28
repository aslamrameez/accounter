package com.vimukti.accounter.web.client.ui.vendors;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.core.ActionFactory;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.RadioGroupItem;

public class SelectExpenseType extends BaseDialog {
	RadioGroupItem typeRadio;
	private final String CHECK = messages.check();
	private final String CREDIT_CARD = messages.creditCard();

	private final String CASH = messages.cash();
	private final String EMPLOYEE = messages.employee();

	// private ViewConfiguration configuration;

	public SelectExpenseType() {
		super(messages.recordExpenses(), "");
		setText(messages.recordExpenses());
		createControls();
		center();
	}

	public SelectExpenseType(AccounterAsyncCallback<IAccounterCore> callBack) {
		super(messages.recordExpenses(), "");
		setText(messages.recordExpenses());
		createControls();
		center();
	}

	private void createControls() {
		mainPanel.setSpacing(3);
		typeRadio = new RadioGroupItem();
		typeRadio.setShowTitle(false);
		if (Global.get().preferences().isHaveEpmloyees()
				&& Global.get().preferences().isTrackEmployeeExpenses()) {
			typeRadio.setValue(EMPLOYEE, CREDIT_CARD, CASH);
		} else {
			typeRadio.setValue(CREDIT_CARD, CASH);
		}

		// setting the default value
		typeRadio.setValue(new String(CREDIT_CARD));

		DynamicForm typeForm = new DynamicForm();
		typeForm.setWidth("100%");
		typeForm.setIsGroup(true);

		typeForm.setGroupTitle(messages
				.selectHowYouPaidForExpense());
		typeForm.setFields(typeRadio);

		VerticalPanel mainVLay = new VerticalPanel();
		mainVLay.setSize("100%", "100%");
		mainVLay.add(typeForm);

		// okbtn.setWidth("60px");
		// cancelBtn.setWidth("60px");

		setBodyLayout(mainVLay);
		setWidth("300px");

	}

	public void setFocus() {
		cancelBtn.setFocus(true);
	}

	@Override
	protected ValidationResult validate() {
		ValidationResult result = new ValidationResult();
		String radio = typeRadio.getValue().toString();
		if (!radio.equals(EMPLOYEE) && !radio.equals(CHECK)
				&& !radio.equals(CREDIT_CARD) && !radio.equals(CASH)
				&& !radio.equals(EMPLOYEE)) {
			result.addError(this, messages
					.pleaseSelectExpenseType());
		}
		return result;
	}

	@Override
	protected boolean onOK() {
		if (typeRadio.getValue() != null) {
			String radio = typeRadio.getValue().toString();
			if (radio.equals(EMPLOYEE)) {
				ActionFactory.EmployeeExpenseAction().run(null, false);
			} else if (radio.equals(CHECK)) {
				ActionFactory.getWriteChecksAction().run(null, false);
			} else if (radio.equals(CREDIT_CARD)) {
				ActionFactory.CreditCardExpenseAction().run(null, false);
			} else if (radio.equals(CASH)) {
				ActionFactory.CashExpenseAction().run(null, false);
			} else {
				Accounter.showError(messages
						.pleaseSelectExpenseType());
			}

		}
		removeFromParent();
		return true;
	}
}
