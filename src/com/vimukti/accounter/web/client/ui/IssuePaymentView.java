package com.vimukti.accounter.web.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.core.AccounterCommand;
import com.vimukti.accounter.web.client.core.AccounterConstants;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientCompany;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientIssuePayment;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientTransactionIssuePayment;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.Lists.IssuePaymentTransactionsList;
import com.vimukti.accounter.web.client.ui.combo.AccountCombo;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.PayFromAccountsCombo;
import com.vimukti.accounter.web.client.ui.core.AccounterErrorType;
import com.vimukti.accounter.web.client.ui.core.AccounterValidator;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.core.InputDialogHandler;
import com.vimukti.accounter.web.client.ui.core.InvalidEntryException;
import com.vimukti.accounter.web.client.ui.core.InvalidTransactionEntryException;
import com.vimukti.accounter.web.client.ui.core.ViewManager;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.forms.SelectItem;
import com.vimukti.accounter.web.client.ui.forms.TextItem;
import com.vimukti.accounter.web.client.ui.grids.TransactionIssuePaymentGrid;

/**
 * @author Ravi Kiran.G
 * 
 */
public class IssuePaymentView extends BaseDialog<ClientIssuePayment> {

	private PayFromAccountsCombo accountCombo;
	private SelectItem payMethodSelect;

	private TransactionIssuePaymentGrid grid;
	private VerticalPanel gridLayout;
	private Label totalLabel;
	private Label amountLabel;
	protected String selectedpaymentMethod;
	public Double totalAmount = 0D;
	private List<ClientAccount> payFromAccounts;
	private ClientAccount selectedPayFromAccount;
	private TextItem checkNoText;
	private VerticalPanel mainVLay;
	private DynamicForm payForm;
	HorizontalPanel bottomLabelLayOut;
	private String checkNo;
	private String transactionNumber;
	public int validationCount;

	public IssuePaymentView(String text, String description) {
		super(text, description);
		this.validationCount = 1;
		createControls();
		getPayFromAccounts();
		setTransactionNumber();

		fillGrid();
		center();
	}

	private void setTransactionNumber() {

		rpcUtilService.getNextTransactionNumber(
				ClientTransaction.TYPE_ISSUE_PAYMENT,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						// UIUtils.logError(
						// "Failed to get the Transaction Number..",
						// caught);
						// //UIUtils.logError(
						// "Failed to get the Transaction Number..",
						// caught);
						return;
					}

					public void onSuccess(String result) {
						if (result == null)
							onFailure(null);
						IssuePaymentView.this.transactionNumber = result;
					}

				});

	}

	/**
	 * This method fills the grid with records while initializing the dialog See
	 * FinanceTool.getChecks() for the record types
	 * 
	 */
	private void fillGrid() {

		rpcUtilService
				.getChecks(new AsyncCallback<List<IssuePaymentTransactionsList>>() {

					public void onFailure(Throwable caught) {

					}

					public void onSuccess(
							List<IssuePaymentTransactionsList> result) {

						for (IssuePaymentTransactionsList entry : result)
							addRecord(entry);

					}

				});

	}

	protected void addRecord(IssuePaymentTransactionsList entry) {

		ClientTransactionIssuePayment record = new ClientTransactionIssuePayment();

		setValuesToRecord(record, entry);
		grid.addData(record);

	}

	private void setValuesToRecord(ClientTransactionIssuePayment record,
			IssuePaymentTransactionsList entry) {

		if (entry.getDate() != null)
			record.setDate(entry.getDate().getTime());
		if (entry.getNumber() != null)
			record.setNumber(entry.getNumber());
		record.setName(entry.getName() != null ? entry.getName() : "");
		record.setMemo(entry.getMemo() != null ? entry.getMemo() : "");
		if (entry.getAmount() != null)
			record.setAmount(entry.getAmount());
		if (entry.getPaymentMethod() != null)
			record.setPaymentMethod(entry.getPaymentMethod());
		record.setRecordType(entry.getType());
		if (record.getRecordType() == ClientTransaction.TYPE_WRITE_CHECK)
			record.setWriteCheck(entry.getTransactionId());
		else if (record.getRecordType() == ClientTransaction.TYPE_CUSTOMER_REFUNDS)
			record.setCustomerRefund(entry.getTransactionId());
		record.setID(entry.getTransactionId());

	}

	private void getPayFromAccounts() {
		payFromAccounts = new ArrayList<ClientAccount>();
		payFromAccounts = accountCombo.getAccounts();
		accountCombo.initCombo(payFromAccounts);
		accountCombo.setAccountTypes(UIUtils
				.getOptionsByType(AccountCombo.PAY_FROM_COMBO));
	}

	private void createControls() {
		setWidth("80");
		mainPanel.setSpacing(10);

		payMethodSelect = new SelectItem(Accounter.getFinanceUIConstants()
				.paymentMethod());
		payMethodSelect.setRequired(true);
		payMethodSelect.setValueMap(new String[] { "",
				AccounterConstants.PAYMENT_METHOD_CHECK });
		payMethodSelect.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				payMethodSelect.getValue().toString();
				paymentMethodSelected(payMethodSelect.getValue().toString());
			}
		});

		accountCombo = new PayFromAccountsCombo(Accounter
				.getFinanceUIConstants().account());
		accountCombo.setRequired(true);
		accountCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientAccount>() {

					public void selectedComboBoxItem(ClientAccount selectItem) {
						selectedPayFromAccount = selectItem;
						changeGridData(selectedPayFromAccount);
						setStartingCheckNumber(selectedPayFromAccount);
					}

				});

		payForm = new DynamicForm();
		payForm.setWidth("50%");
		payForm.setFields(payMethodSelect, accountCombo);

		Label label = new Label();
		label.setText(Accounter.getFinanceUIConstants().PaymentsToBeIssued());
		initListGrid();

		addInputDialogHandler(new InputDialogHandler() {

			public void onCancelClick() {

			}

			public boolean onOkClick() {

				try {
					if (validate()) {
						createIssuePayment();
						// return true;

					}
				} catch (Exception e) {
					Accounter.showError(e.getMessage() == null ? e.toString()
							: e.getMessage());
					return false;
				}
				return false;
			}

		});

		mainVLay = new VerticalPanel();
		mainVLay.setWidth("800");

		mainVLay.add(payForm);
		mainVLay.add(label);
		mainVLay.add(gridLayout);

		setBodyLayout(mainVLay);
		headerLayout.setWidth("800");
		headerLayout.setHeight("15%");
		// footerLayout.setCellWidth(okbtn, "74%");

	}

	protected void setStartingCheckNumber(ClientAccount account) {

		if (checkNoText != null) {
			rpcUtilService.getNextCheckNumber(account.getID(),
					new AsyncCallback<Long>() {

						public void onFailure(Throwable caught) {

						}

						public void onSuccess(Long result) {

							if (result == null) {
								onFailure(null);
								return;
							}
							// setCheckNo(result);
							// CheckNoText.setValue(getCheckNo());
							checkNoText.setValue(result);
						}

					});

		}

	}

	protected boolean validate() throws InvalidEntryException,
			InvalidTransactionEntryException {
		if (AccounterValidator.validateForm(payForm, true)
				&& grid.validateGrid())
			return true;
		else
			return false;
	}

	@SuppressWarnings("unused")
	private boolean validateCheckNo() {
		boolean valid = true;
		try {
			String number = checkNoText.getValue().toString();
			setCheckNo(number);
		} catch (NumberFormatException e) {
			valid = false;
			Accounter.showError(Accounter.getFinanceUIConstants()
					.invalidCheckNumber());
		}

		return valid;
	}

	protected void createIssuePayment() {
		ClientIssuePayment issuePayment = getIssuePaymentObject();
		ViewManager.getInstance().createObject(issuePayment, this);
	}

	private ClientIssuePayment getIssuePaymentObject() {

		ClientIssuePayment issuePayment = new ClientIssuePayment();

		issuePayment.setType(ClientTransaction.TYPE_ISSUE_PAYMENT);

		issuePayment.setNumber(transactionNumber);

		issuePayment.setDate(new ClientFinanceDate().getTime());

		issuePayment.setPaymentMethod(selectedpaymentMethod);

		issuePayment.setAccount(selectedPayFromAccount.getID());

		issuePayment.setTotal(totalAmount);
		String chkNo;
		if (checkNoText != null) {
			chkNo = (checkNoText.getValue().toString().isEmpty()) ? "0"
					: (checkNoText.getValue().toString());
			issuePayment.setCheckNumber(chkNo);
		}
		issuePayment
				.setTransactionIssuePayment(getTransactionIssuePayments(issuePayment));

		return issuePayment;
	}

	/*
	 * This method fills the grid with the records which has this account
	 * selected while creating them.
	 */
	protected void changeGridData(ClientAccount selectedPayFromAccount2) {

		rpcUtilService.getChecks(selectedPayFromAccount2.getID(),
				new AsyncCallback<List<IssuePaymentTransactionsList>>() {

					public void onFailure(Throwable t) {

						// UIUtils
						// .logError(
						// "Failed to get the IssuePaymentTransactionsList..",
						// t);

					}

					public void onSuccess(
							List<IssuePaymentTransactionsList> result) {

						if (result == null) {
							onFailure(null);
							return;
						}
						removeGridData();
						for (IssuePaymentTransactionsList entry : result) {
							addRecord(entry);
						}

					}

				});

	}

	protected void removeGridData() {
		grid.removeAllRecords();
		// totalAmount = 0D;
	}

	private void initListGrid() {
		gridLayout = new VerticalPanel();
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100");
		grid = new TransactionIssuePaymentGrid();
		grid.isEnable = false;
		grid.init();
		grid.setHeight("200px");
		// grid.setIssuePaymentView(this);
		// grid.addFooterValues("", "", "", "", FinanceApplication
		// .getVendorsMessages().total(), "0.0");

		// bottomLabelLayOut = new HorizontalPanel();
		// bottomLabelLayOut.setWidth("100%");
		// bottomLabelLayOut.setHeight("100px");
		Label emptyLabel = new Label();
		emptyLabel.setWidth("25%");
		totalLabel = new Label();
		totalLabel.setWidth("30%");
		totalLabel.setText(Accounter.getFinanceUIConstants().totalAmount());

		amountLabel = new Label();
		amountLabel.setText("" + UIUtils.getCurrencySymbol() + "0");
		amountLabel.setWidth("20%");

		gridLayout.add(grid);
		// gridLayout.add(bottomLabelLayOut);

	}

	private void paymentMethodSelected(String selectedpaymentMethod1) {
		selectedpaymentMethod = selectedpaymentMethod1;
		if (!selectedpaymentMethod.isEmpty()) {
			checkNoText = new TextItem(
					getCompany().getAccountingType() == ClientCompany.ACCOUNTING_TYPE_UK ? Accounter
							.getFinanceUIConstants().startingCheckNo()
							: Accounter.getFinanceUIConstants()
									.startingChequeNo());
			checkNoText.setWidth(100);
			checkNoText.setRequired(true);
			if (selectedPayFromAccount != null)
				setStartingCheckNumber(selectedPayFromAccount);

			payForm.removeAllRows();
			payForm.setFields(payMethodSelect, accountCombo, checkNoText);
		} else {
			payForm.removeAllRows();
			payForm.setFields(payMethodSelect, accountCombo);
		}
	}

	private List<ClientTransactionIssuePayment> getTransactionIssuePayments(
			ClientIssuePayment issuePayment) {
		List<ClientTransactionIssuePayment> transactionIssuePaymentsList = new ArrayList<ClientTransactionIssuePayment>();

		ClientTransactionIssuePayment entry;

		for (ClientTransactionIssuePayment record : grid.getSelectedRecords()) {
			entry = new ClientTransactionIssuePayment();
			if (record.getDate() != 0)
				entry.setDate(record.getDate());
			if (record.getNumber() != null)
				entry.setNumber(record.getNumber());

			if (record.getName() != null)
				entry.setName(record.getName());

			try {
				entry.setAmount(record.getAmount());
			} catch (Exception e) {
				e.printStackTrace();
			}
			entry.setMemo(record.getMemo());

			if (record.getPaymentMethod() != null) {
				entry.setPaymentMethod(record.getPaymentMethod());
			}

			if (record.getRecordType() == ClientTransaction.TYPE_WRITE_CHECK) {
				entry.setWriteCheck(record.getWriteCheck());
			} else {
				entry.setCustomerRefund(record.getCustomerRefund());
			}

			entry.setTransaction(issuePayment);

			transactionIssuePaymentsList.add(entry);

		}
		return transactionIssuePaymentsList;

	}

	public void setCheckNo(String checkNo) {
		this.checkNo = checkNo;
	}

	public String getCheckNo() {
		return checkNo;
	}

	@Override
	public Object getGridColumnValue(IsSerializable obj, int index) {
		// NOTHING TO DO.
		return null;
	}

	@Override
	public void deleteFailed(Throwable caught) {

	}

	@Override
	public void deleteSuccess(Boolean result) {

	}

	@Override
	public void saveSuccess(IAccounterCore object) {
		//
		// Accounter.showInformation(FinanceApplication.getFinanceUIConstants()
		// .issuePaymentWith()
		// + transactionNumber
		// + FinanceApplication.getFinanceUIConstants()
		// .createdSuccessfully());
		IssuePaymentView.this.removeFromParent();
	}

	@Override
	public void saveFailed(Throwable exception) {
		BaseDialog.errordata.setHTML(AccounterErrorType.FAILEDREQUEST);
		BaseDialog.commentPanel.setVisible(true);
	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {
		if (core.getID() == this.accountCombo.getSelectedValue().getID()) {
			this.accountCombo.addItemThenfireEvent((ClientAccount) core);
		}

		switch (command) {
		case AccounterCommand.CREATION_SUCCESS:

			if (core.getObjectType() == AccounterCoreType.ACCOUNT)
				this.accountCombo.addComboItem((ClientAccount) core);
			break;

		case AccounterCommand.DELETION_SUCCESS:

			if (core.getObjectType() == AccounterCoreType.ACCOUNT)
				this.accountCombo.removeComboItem((ClientAccount) core);

			break;
		case AccounterCommand.UPDATION_SUCCESS:
			break;
		}
	}

	@Override
	protected String getViewTitle() {
		return Accounter.getVendorsMessages().issuePayments();
	}

}
