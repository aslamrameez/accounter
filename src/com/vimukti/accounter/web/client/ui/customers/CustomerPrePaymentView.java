package com.vimukti.accounter.web.client.ui.customers;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientAddress;
import com.vimukti.accounter.web.client.core.ClientCompany;
import com.vimukti.accounter.web.client.core.ClientCurrency;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientCustomerPrePayment;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientPriceLevel;
import com.vimukti.accounter.web.client.core.ClientSalesPerson;
import com.vimukti.accounter.web.client.core.ClientTAXCode;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientTransactionItem;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.ValidationResult;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.exception.AccounterExceptions;
import com.vimukti.accounter.web.client.externalization.AccounterConstants;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.DataUtils;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.combo.AddressCombo;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.core.AccounterValidator;
import com.vimukti.accounter.web.client.ui.core.AmountField;
import com.vimukti.accounter.web.client.ui.core.DecimalUtil;
import com.vimukti.accounter.web.client.ui.core.EditMode;
import com.vimukti.accounter.web.client.ui.core.InvalidEntryException;
import com.vimukti.accounter.web.client.ui.forms.CheckboxItem;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.TextItem;

public class CustomerPrePaymentView extends
		AbstractCustomerTransactionView<ClientCustomerPrePayment> {
	AccounterConstants accounterConstants = GWT
			.create(AccounterConstants.class);
	AccounterMessages accounterMessages = GWT.create(AccounterMessages.class);

	// private CheckboxItem printCheck;
	private AmountField amountText, bankBalText, customerBalText;
	protected double enteredBalance;
	private DynamicForm payForm;
	Double toBeSetEndingBalance;
	Double toBeSetCustomerBalance;
	protected boolean isClose;
	protected String paymentMethod = UIUtils
			.getpaymentMethodCheckBy_CompanyType(Accounter.constants().check());

	private ArrayList<DynamicForm> listforms;
	protected String checkNumber = null;
	protected TextItem checkNo;
	boolean isChecked = false;
	private boolean locationTrackingEnabled;

	public CustomerPrePaymentView() {
		super(ClientTransaction.TYPE_CUSTOMER_PREPAYMENT);
		locationTrackingEnabled = getCompany().getPreferences()
				.isLocationTrackingEnabled();
	}

	@Override
	protected void initMemoAndReference() {
		if (isInViewMode()) {

			ClientCustomerPrePayment customerPrePayment = (ClientCustomerPrePayment) transaction;

			if (customerPrePayment != null) {
				memoTextAreaItem.setDisabled(true);
				setMemoTextAreaItem(customerPrePayment.getMemo());
				// setRefText(customerPrePayment.getReference());

			}
		}

	}

	@Override
	public ValidationResult validate() {
		ValidationResult result = new ValidationResult();

		if (AccounterValidator
				.isInPreventPostingBeforeDate(this.transactionDate)) {
			result.addError(transactionDateItem,
					accounterConstants.invalidateDate());
		}

		result.add(payForm.validate());

		if (!AccounterValidator.isPositiveAmount(amountText.getAmount())) {
			amountText.textBox.addStyleName("highlightedFormItem");
			result.addError(amountText, accounterMessages
					.valueCannotBe0orlessthan0(accounterConstants.amount()));
		}
		ClientAccount bankAccount = depositInCombo.getSelectedValue();
		// check if the currency of accounts is valid or not
		if (bankAccount != null) {
			ClientCurrency bankCurrency = getCurrency(bankAccount.getCurrency());
			ClientCurrency customerCurrency = getCurrency(customer
					.getCurrency());
			if (bankCurrency != getBaseCurrency()
					&& bankCurrency != customerCurrency) {
				result.addError(depositInCombo,
						accounterConstants.selectProperBankAccount());
			}
		}
		return result;
	}

	public static CustomerPrePaymentView getInstance() {

		return new CustomerPrePaymentView();

	}

	public void resetElements() {
		this.setCustomer(null);
		this.addressListOfCustomer = null;
		this.depositInAccount = null;
		this.paymentMethod = UIUtils
				.getpaymentMethodCheckBy_CompanyType(Accounter.constants()
						.check());
		amountText.setAmount(getAmountInTransactionCurrency(0D));
		// endBalText.setAmount(getAmountInTransactionCurrency(0D));
		// customerBalText.setAmount(getAmountInTransactionCurrency(0D));
		memoTextAreaItem.setValue("");
	}

	protected void updateTransaction() {
		super.updateTransaction();

		transaction.setNumber(transactionNumber.getValue().toString());
		if (customer != null)

			transaction.setCustomer(getCustomer().getID());

		if (billingAddress != null)
			transaction.setAddress(billingAddress);
		if (depositInAccount != null)
			transaction.setDepositIn(depositInAccount.getID());
		if (!DecimalUtil.isEquals(enteredBalance, 0.00))
			transaction.setTotal(enteredBalance);
		if (paymentMethod != null)
			transaction.setPaymentMethod(paymentMethodCombo.getSelectedValue());

		if (checkNo.getValue() != null && !checkNo.getValue().equals("")) {
			String value = String.valueOf(checkNo.getValue());
			transaction.setCheckNumber(value);
		} else {
			transaction.setCheckNumber("");

		}
		// if (transaction.getID() != 0)
		//
		// printCheck.setValue(transaction.isToBePrinted());
		// else
		// printCheck.setValue(true);

		if (transactionDate != null)
			transaction.setDate(transactionDateItem.getEnteredDate().getDate());
		transaction.setMemo(getMemoTextAreaItem());

		// if (toBeSetEndingBalance != null)
		// transaction.setEndingBalance(toBeSetEndingBalance);
		if (toBeSetCustomerBalance != null)
			transaction.setCustomerBalance(toBeSetCustomerBalance);

		transaction.setType(ClientTransaction.TYPE_CUSTOMER_PREPAYMENT);

		if (currency != null)
			transaction.setCurrency(currency.getID());
		transaction.setCurrencyFactor(currencyWidget.getCurrencyFactor());

	}

	@Override
	protected void initTransactionViewData() {
		if (transaction == null) {
			setData(new ClientCustomerPrePayment());
			initDepositInAccounts();
		} else {

			if (currencyWidget != null) {
				this.currency = getCompany().getCurrency(
						transaction.getCurrency());
				this.currencyFactor = transaction.getCurrencyFactor();
				currencyWidget.setSelectedCurrency(this.currency);
				// currencyWidget.currencyChanged(this.currency);
				currencyWidget.setCurrencyFactor(transaction
						.getCurrencyFactor());
				currencyWidget.setDisabled(isInViewMode());
			}
			ClientCompany comapny = getCompany();

			ClientCustomer customer = comapny.getCustomer(transaction
					.getCustomer());
			customerSelected(comapny.getCustomer(transaction.getCustomer()));
			this.billingAddress = transaction.getAddress();
			if (billingAddress != null)
				billToaddressSelected(billingAddress);
			amountText.setDisabled(true);
			amountText.setAmount(getAmountInTransactionCurrency(transaction
					.getTotal()));
			customerBalText.setAmount(getAmountInTransactionCurrency(customer
					.getBalance()));
			// bankBalText.setAmount(getAmountInTransactionCurrency(transaction.g));
			paymentMethodSelected(transaction.getPaymentMethod());
			this.depositInAccount = comapny.getAccount(transaction
					.getDepositIn());
			if (depositInAccount != null) {
				depositInCombo.setComboItem(depositInAccount);
				bankBalText.setAmount(depositInAccount
						.getTotalBalanceInAccountCurrency());

			}

			paymentMethodCombo.setComboItem(transaction.getPaymentMethod());
			checkNo.setValue(transaction.getCheckNumber());
			// if (transaction.getPaymentMethod().equals(constants.check())) {
			// printCheck.setDisabled(isInViewMode());
			// checkNo.setDisabled(isInViewMode());
			// } else {
			// printCheck.setDisabled(true);
			// checkNo.setDisabled(true);
			// }

			// if (transaction.getCheckNumber() != null) {
			// if (transaction.getCheckNumber().equals(
			// Accounter.constants().toBePrinted())) {
			// checkNo.setValue(Accounter.constants().toBePrinted());
			// printCheck.setValue(true);
			// } else {
			// checkNo.setValue(transaction.getCheckNumber());
			// printCheck.setValue(false);
			// }
			// }
		}
		if (locationTrackingEnabled)
			locationSelected(getCompany()
					.getLocation(transaction.getLocation()));
		initMemoAndReference();
		initTransactionNumber();
		initCustomers();
		initAccounterClass();
		if (isMultiCurrencyEnabled()) {
			updateAmountsFromGUI();
		}
	}

	private void initCustomers() {
		// TODO Auto-generated method stub

	}

	protected void accountSelected(ClientAccount account) {
		if (account == null)
			return;
		this.depositInAccount = account;
		depositInCombo.setValue(depositInAccount);
		bankBalText.setAmount(depositInAccount.getTotalBalance());
		// if (account != null && !(Boolean) printCheck.getValue()) {
		// setCheckNumber();
		// } else if (account == null)
		// checkNo.setValue("");
		adjustBalance(getAmountInBaseCurrency(amountText.getAmount()));
	}

	private void adjustBalance(double amount) {
		ClientCustomerPrePayment customerPrePayment = (ClientCustomerPrePayment) transaction;
		enteredBalance = amount;

		if (DecimalUtil.isLessThan(enteredBalance, 0)
				|| DecimalUtil.isGreaterThan(enteredBalance, 1000000000000.00)) {
			amountText.setAmount(0D);
			enteredBalance = 0D;
		}
		if (getCustomer() != null) {
			if (isInViewMode()
					&& getCustomer().getID() == (customerPrePayment
							.getCustomer())
					&& !DecimalUtil.isEquals(enteredBalance, 0)) {
				double cusBal = DecimalUtil.isLessThan(getCustomer()
						.getBalance(), 0) ? -1 * getCustomer().getBalance()
						: getCustomer().getBalance();
				toBeSetCustomerBalance = (cusBal - transaction.getTotal())
						+ enteredBalance;
			} else {
				toBeSetCustomerBalance = getCustomer().getBalance()
						- enteredBalance;
			}
			// customerBalText.setAmount(toBeSetCustomerBalance);

		}
		if (depositInAccount != null) {

			if (depositInAccount.isIncrease()) {
				toBeSetEndingBalance = depositInAccount.getTotalBalance()
						- enteredBalance;
			} else {
				toBeSetEndingBalance = depositInAccount.getTotalBalance()
						+ enteredBalance;
			}
			if (isInViewMode()
					&& depositInAccount.getID() == (customerPrePayment
							.getDepositIn())
					&& !DecimalUtil.isEquals(enteredBalance, 0)) {
				toBeSetEndingBalance = toBeSetEndingBalance
						- transaction.getTotal();
			}
			// endBalText.setAmount(toBeSetEndingBalance);

		}
	}

	private void setCheckNumber() {
		rpcUtilService.getNextCheckNumber(depositInAccount.getID(),
				new AccounterAsyncCallback<Long>() {

					public void onException(AccounterException t) {
						checkNo.setValue(Accounter.constants().toBePrinted());
						return;
					}

					public void onResultSuccess(Long result) {
						if (result == null)
							onFailure(null);

						checkNumber = String.valueOf(result);
						checkNo.setValue(result.toString());
					}

				});

	}

	@Override
	protected void priceLevelSelected(ClientPriceLevel priceLevel) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void salesPersonSelected(ClientSalesPerson person) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createControls() {
		Label lab1 = new Label(Accounter.messages().payeePrePayment(
				Global.get().Customer()));
		lab1.setStyleName(Accounter.constants().labelTitle());
		// lab1.setHeight("35px");
		transactionDateItem = createTransactionDateItem();

		transactionNumber = createTransactionNumberItem();

		listforms = new ArrayList<DynamicForm>();
		locationCombo = createLocationCombo();
		DynamicForm dateNoForm = new DynamicForm();
		dateNoForm.setNumCols(6);
		dateNoForm.setStyleName("datenumber-panel");
		dateNoForm.setFields(transactionDateItem, transactionNumber);

		HorizontalPanel datepanel = new HorizontalPanel();
		datepanel.setWidth("100%");
		datepanel.add(dateNoForm);
		datepanel.setCellHorizontalAlignment(dateNoForm,
				HasHorizontalAlignment.ALIGN_RIGHT);
		datepanel.getElement().getStyle().setPaddingRight(15, Unit.PX);

		HorizontalPanel labeldateNoLayout = new HorizontalPanel();
		labeldateNoLayout.setWidth("100%");
		// labeldateNoLayout.add(lab1);
		labeldateNoLayout.add(datepanel);
		labeldateNoLayout.setCellHorizontalAlignment(datepanel, ALIGN_RIGHT);
		// customer and address
		customerCombo = createCustomerComboItem(messages.payeeName(Global.get()
				.Customer()));

		billToCombo = createBillToComboItem(customerConstants.address());
		billToCombo.setDisabled(true);

		// Ending and Vendor Balance
		bankBalText = new AmountField(customerConstants.bankBalance(), this,
				getBaseCurrency());
		bankBalText.setHelpInformation(true);
		bankBalText.setWidth(100);
		bankBalText.setDisabled(true);

		customerBalText = new AmountField(Accounter.messages().payeeBalance(
				Global.get().Customer()), this, getBaseCurrency());
		customerBalText.setHelpInformation(true);
		customerBalText.setDisabled(true);
		customerBalText.setWidth(100);

		DynamicForm balForm = new DynamicForm();
		if (locationTrackingEnabled)
			balForm.setFields(locationCombo);
		balForm.setFields(bankBalText, customerBalText);
		// balForm.getCellFormatter().setWidth(0, 0, "205px");

		// payment
		depositInCombo = createDepositInComboItem(bankBalText);
		depositInCombo.setPopupWidth("500px");

		amountText = new AmountField(customerConstants.amount(), this,
				getBaseCurrency());
		amountText.setHelpInformation(true);
		amountText.setWidth(100);
		amountText.setRequired(true);
		amountText.addBlurHandler(getBlurHandler());

		paymentMethodCombo = createPaymentMethodSelectItem();
		paymentMethodCombo.setComboItem(UIUtils
				.getpaymentMethodCheckBy_CompanyType(Accounter.constants()
						.check()));
		// printCheck = new CheckboxItem(customerConstants.toBePrinted());
		// printCheck.setValue(true);
		// printCheck.addChangeHandler(new ValueChangeHandler<Boolean>() {
		//
		// @Override
		// public void onValueChange(ValueChangeEvent<Boolean> event) {
		// isChecked = (Boolean) event.getValue();
		// if (isChecked) {
		// if (printCheck.getValue().toString()
		// .equalsIgnoreCase("true")) {
		// checkNo.setValue(Accounter.constants().toBePrinted());
		// checkNo.setDisabled(true);
		// } else {
		// if (depositInAccount == null)
		// checkNo.setValue(Accounter.constants()
		// .toBePrinted());
		// else if (isInViewMode()) {
		// checkNo.setValue(((ClientCustomerPrePayment) transaction)
		// .getCheckNumber());
		// }
		// }
		// } else
		// // setCheckNumber();
		// checkNo.setValue("");
		// checkNo.setDisabled(false);
		//
		// }
		// });
		checkNo = createCheckNumberItm();
		// checkNo.setValue(Accounter.constants().toBePrinted());
		checkNo.setWidth(100);
		// checkNo.setDisabled(true);
		checkNo.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				checkNumber = checkNo.getValue().toString();
			}
		});
		checkNo.setDisabled(isInViewMode());
		currencyWidget = createCurrencyFactorWidget();
		payForm = UIUtils.form(customerConstants.payment());
		payForm.getCellFormatter().addStyleName(7, 0, "memoFormAlign");
		memoTextAreaItem = createMemoTextAreaItem();
		memoTextAreaItem.setWidth(100);
		// refText = createRefereceText();
		// refText.setWidth(100);
		payForm.setFields(customerCombo, billToCombo, depositInCombo,
				amountText, paymentMethodCombo, checkNo, memoTextAreaItem);
		// memo and Reference
		double amount = depositInCombo.getSelectedValue() != null ? depositInCombo
				.getSelectedValue().getCurrentBalance() : 0.00;
		bankBalText.setAmount(getAmountInTransactionCurrency(amount));

		payForm.setCellSpacing(5);
		payForm.setWidth("100%");
		payForm.getCellFormatter().setWidth(0, 0, "160px");

		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.setWidth("100%");
		leftPanel.setSpacing(5);
		leftPanel.add(payForm);
		// leftPanel.add(payForm);
		// leftPanel.add(memoForm);

		VerticalPanel rightPanel = new VerticalPanel();
		rightPanel.setWidth("100%");
		rightPanel.add(balForm);
		rightPanel.setCellHorizontalAlignment(balForm, ALIGN_RIGHT);
		if (isMultiCurrencyEnabled()) {
			rightPanel.add(currencyWidget);
			rightPanel.setCellHorizontalAlignment(currencyWidget, ALIGN_RIGHT);
		}
		HorizontalPanel hLay = new HorizontalPanel();
		hLay.addStyleName("fields-panel");
		hLay.setWidth("100%");
		hLay.setSpacing(10);
		hLay.add(leftPanel);
		hLay.add(rightPanel);
		hLay.setCellWidth(leftPanel, "50%");
		hLay.setCellWidth(rightPanel, "50%");
		hLay.setCellHorizontalAlignment(rightPanel, ALIGN_RIGHT);

		VerticalPanel mainVLay = new VerticalPanel();
		mainVLay.setSize("100%", "100%");
		mainVLay.add(lab1);
		mainVLay.add(labeldateNoLayout);
		mainVLay.add(hLay);

		this.add(mainVLay);

		setSize("100%", "100%");

		/* Adding dynamic forms in list */
		listforms.add(dateNoForm);
		listforms.add(balForm);
		listforms.add(payForm);
		settabIndexes();
	}

	private AddressCombo createBillToComboItem(String address) {
		AddressCombo addressCombo = new AddressCombo(Accounter.constants()
				.address(), false);
		addressCombo.setHelpInformation(true);
		addressCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientAddress>() {

					public void selectedComboBoxItem(ClientAddress selectItem) {

						billToaddressSelected(selectItem);

					}

				});

		addressCombo.setDisabled(isInViewMode());

		// formItems.add(addressCombo);

		return addressCombo;

	}

	private TextItem createCheckNumberItm() {
		TextItem checkNoTextItem = new TextItem(
				UIUtils.getpaymentMethodCheckBy_CompanyType(Accounter
						.constants().checkNo()));
		checkNoTextItem.setHelpInformation(true);
		return checkNoTextItem;
	}

	@Override
	public void saveAndUpdateView() {

		updateTransaction();
		saveOrUpdate(transaction);
	}

	private String getCheckNoValue() {
		return checkNumber;
	}

	private BlurHandler getBlurHandler() {
		BlurHandler blurHandler = new BlurHandler() {

			Object value = null;

			public void onBlur(BlurEvent event) {
				try {

					value = amountText.getValue();

					if (value == null)
						return;

					Double amount = DataUtils.getAmountStringAsDouble(value
							.toString());
					if (DecimalUtil.isLessThan(amount, 0)) {
						Accounter.showError(Accounter.constants()
								.noNegativeAmounts());
						amountText
								.setAmount(getAmountInTransactionCurrency(0.00D));

					}

					amountText
							.setAmount(DataUtils.isValidAmount(amount + "") ? amount
									: 0.0);

					adjustBalance(getAmountInBaseCurrency(amountText
							.getAmount()));

				} catch (Exception e) {
					if (e instanceof InvalidEntryException) {
						Accounter.showError(e.getMessage());
					}
					amountText.setAmount(0.0);
				}

			}
		};

		return blurHandler;
	}

	@Override
	protected void paymentMethodSelected(String paymentMethod) {
		if (paymentMethod == null)
			return;

		if (paymentMethod != null) {
			this.paymentMethod = paymentMethod;
			// if
			// (paymentMethod.equalsIgnoreCase(Accounter.constants().cheque()))
			// {
			// //printCheck.setDisabled(false);
			// //checkNo.setDisabled(false);
			// } else {
			// //printCheck.setDisabled(true);
			// //checkNo.setDisabled(true);
			// }
		}

	}

	@Override
	protected void customerSelected(ClientCustomer customer) {
		if (customer == null)
			return;
		ClientCurrency clientCurrency = getCurrency(customer.getCurrency());
		amountText.setCurrency(clientCurrency);
		bankBalText.setCurrency(clientCurrency);
		customerBalText.setCurrency(clientCurrency);

		this.setCustomer(customer);
		if (customerCombo != null) {
			customerCombo.setComboItem(customer);
		}
		this.addressListOfCustomer = customer.getAddress();
		initBillToCombo();
		customerBalText.setAmount(customer.getBalance());
		adjustBalance(getAmountInBaseCurrency(amountText.getAmount()));
		currencyWidget.setSelectedCurrency(clientCurrency);

	}

	@Override
	public List<DynamicForm> getForms() {
		return listforms;
	}

	/**
	 * call this method to set focus in View
	 */
	@Override
	public void setFocus() {
		this.customerCombo.setFocus();
	}

	public void onEdit() {
		AccounterAsyncCallback<Boolean> editCallBack = new AccounterAsyncCallback<Boolean>() {

			@Override
			public void onException(AccounterException caught) {

				int errorCode = ((AccounterException) caught).getErrorCode();

				Accounter.showError(AccounterExceptions
						.getErrorString(errorCode));

			}

			@Override
			public void onResultSuccess(Boolean result) {
				if (result)
					enableFormItems();
			}

		};

		AccounterCoreType type = UIUtils.getAccounterCoreType(transaction
				.getType());
		this.rpcDoSerivce.canEdit(type, transaction.id, editCallBack);

	}

	protected void enableFormItems() {
		setMode(EditMode.EDIT);
		customerCombo.setDisabled(isInViewMode());
		transactionDateItem.setDisabled(isInViewMode());
		transactionNumber.setDisabled(isInViewMode());
		// printCheck.setDisabled(isInViewMode());
		amountText.setDisabled(isInViewMode());
		paymentMethodCombo.setDisabled(isInViewMode());
		paymentMethodSelected(paymentMethodCombo.getSelectedValue());
		// if (printCheck.getValue().toString().equalsIgnoreCase("true")) {
		// checkNo.setValue(Accounter.constants().toBePrinted());
		// checkNo.setDisabled(true);
		// }
		// if (paymentMethodCombo.getSelectedValue().equalsIgnoreCase(
		// Accounter.constants().cheque())
		// && printCheck.getValue().toString().equalsIgnoreCase("true")) {
		// checkNo.setValue(Accounter.constants().toBePrinted());
		checkNo.setDisabled(isInViewMode());
		// }
		memoTextAreaItem.setDisabled(false);
		if (locationTrackingEnabled)
			locationCombo.setDisabled(isInViewMode());
		if (currencyWidget != null) {
			currencyWidget.setDisabled(isInViewMode());
		}
		super.onEdit();
	}

	public void setTransactionDate(ClientFinanceDate transactionDate) {
		super.setTransactionDate(transactionDate);
		if (this.transactionDateItem != null
				&& this.transactionDateItem.getValue() != null) {
			// updateNonEditableItems();
		}
	}

	@Override
	public void updateNonEditableItems() {
		if (bankBalText != null)
			this.bankBalText
					.setAmount(getAmountInTransactionCurrency(toBeSetEndingBalance));
		if (customerBalText != null)
			this.customerBalText
					.setAmount(getAmountInTransactionCurrency(toBeSetCustomerBalance));
	}

	@Override
	protected void initSalesTaxNonEditableItem() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initTransactionTotalNonEditableItem() {
		// TODO Auto-generated method stub

	}

	@Override
	public void print() {
		// TODO Auto-generated method stub

	}

	@Override
	public void printPreview() {
		// NOTHING TO DO.
	}

	@Override
	public void deleteFailed(AccounterException caught) {

	}

	@Override
	public void deleteSuccess(IAccounterCore result) {

	}

	@Override
	protected void depositInAccountSelected(ClientAccount depositInAccount2) {
		super.depositInAccountSelected(depositInAccount2);
		adjustBalance(getAmountInBaseCurrency(amountText.getAmount()));
	}

	@Override
	protected void taxCodeSelected(ClientTAXCode taxCode) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getViewTitle() {
		return Accounter.messages().payeePayment(Global.get().Customer());
	}

	@Override
	protected void initTransactionsItems() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isBlankTransactionGrid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void addNewData(ClientTransactionItem transactionItem) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void refreshTransactionGrid() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ClientTransactionItem> getAllTransactionItems() {
		return new ArrayList<ClientTransactionItem>();
	}

	private void settabIndexes() {
		customerCombo.setTabIndex(1);
		billToCombo.setTabIndex(2);
		depositInCombo.setTabIndex(3);
		amountText.setTabIndex(4);
		paymentMethodCombo.setTabIndex(5);
		// printCheck.setTabIndex(6);
		checkNo.setTabIndex(7);
		memoTextAreaItem.setTabIndex(8);
		transactionDateItem.setTabIndex(9);
		transactionNumber.setTabIndex(10);
		bankBalText.setTabIndex(11);
		customerBalText.setTabIndex(12);
		saveAndCloseButton.setTabIndex(13);
		saveAndNewButton.setTabIndex(14);
		cancelButton.setTabIndex(15);
	}

	@Override
	protected void addAccountTransactionItem(ClientTransactionItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addItemTransactionItem(ClientTransactionItem item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAmountsFromGUI() {
		adjustBalance(getAmountInBaseCurrency(amountText.getAmount()));
	}

}
