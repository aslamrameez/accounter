package com.vimukti.accounter.web.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vimukti.accounter.web.client.AccounterAsyncCallback;
import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.IGlobal;
import com.vimukti.accounter.web.client.core.AccounterCommand;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.reports.AccountRegister;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.ui.combo.DepositInAccountCombo;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.PayeeCombo;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;
import com.vimukti.accounter.web.client.ui.grids.AccountRegisterListGrid;

public class AccountRegisterView extends AbstractBaseView<AccountRegister> {

	AccountRegisterListGrid grid;

	private DepositInAccountCombo bankAccSelect;
	PayeeCombo paytoSelect;
	private ClientAccount takenaccount;
	private ClientAccount account;
	protected List<AccountRegister> accountRegister;
	private AccountRegister accRegister;
	private AccountRegisterListGrid grid2;
	private VerticalPanel mainVLay;
	private HorizontalPanel hlayTop, gridLayout;

	private Label lab1;

	private List<ClientAccount> listOfAccounts;
	private double total = 0.0;
	private boolean isBankActGrid = true;

	public AccountRegisterView(ClientAccount account2) {
		super();
		this.takenaccount = account2;

	}

	@Override
	public void init() {
		createControls();
	}

	@Override
	public void initData() {
		// initAccountsToList();
		getDepositInAccounts();
		// initPayToCombo();
		super.initData();
		accountSelected(takenaccount);
	}

	public void initAccountsToList() {
		bankAccSelect.initCombo(getCompany().getActiveAccounts());

	}

	public void getDepositInAccounts() {

		List<ClientAccount> listOfAccounts = new ArrayList<ClientAccount>();
		for (ClientAccount account : getCompany().getActiveAccounts()) {
			if (account.getType() == ClientAccount.TYPE_BANK
					|| account.getType() == ClientAccount.TYPE_CREDIT_CARD) {
				listOfAccounts.add(account);
			}
		}
		bankAccSelect.initCombo(listOfAccounts);
		bankAccSelect.setComboItem(takenaccount);

	}

	protected void createControls() {

		bankAccSelect = new DepositInAccountCombo(Accounter.messages()
				.bankAccount(Global.get().account()));
		bankAccSelect.setRequired(true);

		bankAccSelect
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientAccount>() {
					public void selectedComboBoxItem(ClientAccount selectItem) {
						ClientAccount selectBankAccMethod = selectItem;

						if (selectBankAccMethod.getType() == ClientAccount.TYPE_BANK) {
							if (!isBankActGrid()) {
								grid2.removeAllRecords();
								gridLayout.remove(grid2);

								gridLayout.add(grid);
								mainVLay.add(hlayTop);
								mainVLay.add(gridLayout);
								setBankActGrid(true);
							}

							accountSelected(selectBankAccMethod);

						} else if (selectBankAccMethod.getType() == ClientAccount.TYPE_CREDIT_CARD) {
							if (isBankActGrid()) {
								grid.removeAllRecords();
								gridLayout.remove(grid);

								gridLayout.add(grid2);
								mainVLay.add(hlayTop);
								mainVLay.add(gridLayout);
								setBankActGrid(false);
							}

							accountSelected(selectBankAccMethod);

						}

					}

				});

		HorizontalPanel hlay = new HorizontalPanel();
		DynamicForm form = new DynamicForm();

		form.setIsGroup(true);
		form.setGroupTitle(Accounter.messages().account(Global.get().account()));
		form.setFields(bankAccSelect);
		form.setWidth("100%");

		hlay.add(form);

		hlayTop = new HorizontalPanel();

		hlayTop.add(hlay);

		lab1 = new Label(Accounter.messages().accountRegister(
				Global.get().Account())
				+ " - " + takenaccount.getName());

		grid = new AccountRegisterListGrid(false, ClientAccount.TYPE_BANK);
		grid.addStyleName("listgrid-tl");
		grid.init();

		grid2 = new AccountRegisterListGrid(false,
				ClientAccount.TYPE_CREDIT_CARD);
		// grid2.addStyleName("listgrid-tl");
		grid2.init();

		gridLayout = new HorizontalPanel() {
			@Override
			protected void onAttach() {
				grid.setHeight(this.getOffsetHeight() - 43 + "px");
				grid2.setHeight(this.getOffsetHeight() - 43 + "px");
				super.onAttach();
			}
		};
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");

		mainVLay = new VerticalPanel();
		mainVLay.setHeight("100%");
		mainVLay.setWidth("100%");

		if (takenaccount.getType() == ClientAccount.TYPE_BANK) {
			gridLayout.add(grid);
			mainVLay.add(hlayTop);
			mainVLay.add(gridLayout);
		} else if (takenaccount.getType() == ClientAccount.TYPE_CREDIT_CARD) {
			setBankActGrid(false);
			gridLayout.add(grid2);
			mainVLay.add(hlayTop);
			mainVLay.add(gridLayout);
		}

		add(mainVLay);

		setSize("100%", "100%");

	}

	public void getAccountRegisterGrid(List<AccountRegister> result) {
		grid.removeAllRecords();
		grid.balance = 0.0;
		grid.deposit = 0.0;
		grid.payment = 0.0;
		grid.totalBalance = 0.0;
		grid2.removeAllRecords();
		grid2.balance = 0.0;
		grid2.deposit = 0.0;
		grid2.payment = 0.0;
		grid2.totalBalance = 0.0;
		if (accountRegister != null) {

			for (int i = 0; i < accountRegister.size(); i++) {
				accRegister = this.accountRegister.get(i);
				if (account.getType() == ClientAccount.TYPE_BANK) {
					grid.addData(accRegister);
					// this.total += accRegister.getBalance();
				} else {
					grid2.addData(accRegister);
					// this.total += accRegister.getBalance();
				}
			}
		}
		// grid.updateFooterValues(FinanceApplication.constants()
		// .endingbalance(), 7);
		// grid.addFooterValue(DataUtils.getAmountAsString(this.account
		// .getCurrentBalance()), 8);
		// grid2.updateFooterValues(FinanceApplication.constants()
		// .endingbalance(), 7);
		// grid2.addFooterValue(DataUtils.getAmountAsString(this.account
		// .getCurrentBalance()), 8);
		this.total = 0;

	}

	protected void accountSelected(final ClientAccount takenaccount) {

		if (takenaccount == null) {
			accountRegister = null;
			return;
		}

		this.account = takenaccount;

		ClientFinanceDate endDate = Accounter.getCompany()
				.getLastandOpenedFiscalYearEndDate();

		if (endDate == null)
			endDate = new ClientFinanceDate();

		Accounter.createReportService().getAccountRegister(
				Accounter.getStartDate(), endDate, takenaccount.getID(),
				new AccounterAsyncCallback<ArrayList<AccountRegister>>() {

					public void onException(AccounterException caught) {
						Accounter.showError(Accounter.messages()
								.failedtoGetListofAccounts(
										Global.get().account())

								+ takenaccount.getName());

					}

					public void onResultSuccess(
							ArrayList<AccountRegister> result) {
						accountRegister = result;

						getAccountRegisterGrid(result);

					}

				});

	}

	protected void clearFields() {
	}

	public boolean isBankActGrid() {
		return isBankActGrid;
	}

	public void setBankActGrid(boolean isBankActGrid) {
		this.isBankActGrid = isBankActGrid;
	}

	/**
	 * call this method to set focus in View
	 */
	@Override
	public void setFocus() {

	}

	@Override
	public void deleteFailed(AccounterException caught) {

	}

	@Override
	public void deleteSuccess(IAccounterCore result) {

	}

	@Override
	public void fitToSize(int height, int width) {
		grid.setHeight(height - 140 + "px");
		grid2.setHeight(height - 140 + "px");

	}

	@Override
	public void processupdateView(IAccounterCore core, int command) {
		switch (command) {
		case AccounterCommand.CREATION_SUCCESS:

			if (core.getObjectType() == AccounterCoreType.ACCOUNT)
				this.bankAccSelect.addComboItem((ClientAccount) core);

			break;
		case AccounterCommand.DELETION_SUCCESS:

			if (core.getObjectType() == AccounterCoreType.ACCOUNT)
				this.bankAccSelect.removeComboItem((ClientAccount) core);

			break;
		case AccounterCommand.UPDATION_SUCCESS:
			break;
		}

	}

	@Override
	public void onEdit() {

	}

	@Override
	public void print() {

	}

	@Override
	public void printPreview() {

	}

	@Override
	protected String getViewTitle() {
		return Accounter.messages().accountRegister(Global.get().Account());

	}

	@Override
	public void saveFailed(AccounterException exception) {
	}
}
