package com.vimukti.accounter.web.client.ui.reports;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.combo.CustomerCombo;
import com.vimukti.accounter.web.client.ui.combo.IAccounterComboSelectionChangeHandler;
import com.vimukti.accounter.web.client.ui.combo.SelectCombo;
import com.vimukti.accounter.web.client.ui.core.AccounterButton;
import com.vimukti.accounter.web.client.ui.forms.DateItem;

public class CreateStatementToolBar extends ReportToolbar {
	private DateItem fromItem;
	private DateItem toItem;
	private CustomerCombo customerCombo;
	ClientCustomer selectedCusotmer = null;
	private StatementReport statementReport;
	private SelectCombo dateRangeItemCombo;
	private List<String> dateRangeItemList;
	private com.vimukti.accounter.web.client.ui.core.AccounterButton updateButton;

	@SuppressWarnings("unchecked")
	public CreateStatementToolBar(AbstractReportView reportView) {
		this.reportview = reportView;
		createControls();
	}

	public void createControls() {

		String[] dateRangeArray = { Accounter.getReportsMessages().all(),
				Accounter.getReportsMessages().thisWeek(),
				Accounter.getReportsMessages().thisMonth(),
				Accounter.getReportsMessages().lastWeek(),
				Accounter.getReportsMessages().lastMonth(),
				Accounter.getReportsMessages().thisFinancialYear(),
				Accounter.getReportsMessages().lastFinancialYear(),
				Accounter.getReportsMessages().thisFinancialQuarter(),
				Accounter.getReportsMessages().lastFinancialQuarter(),
				Accounter.getReportsMessages().financialYearToDate(),
				Accounter.getReportsMessages().custom() };

		customerCombo = new CustomerCombo("Choose Customer", false);
		statementReport = new StatementReport();
		customerCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<ClientCustomer>() {

					@Override
					public void selectedComboBoxItem(ClientCustomer selectItem) {
						if (selectItem != null) {
							selectedCusotmer = selectItem;
							ClientFinanceDate startDate = fromItem.getDate();
							ClientFinanceDate endDate = toItem.getDate();
							reportview.makeReportRequest(
									selectedCusotmer.getName(), startDate,
									endDate);

						}

					}
				});

		if (UIUtils.isMSIEBrowser()) {
			customerCombo.setWidth("200px");
		}
		// customerCombo.setSelectedItem(1);
		selectedCusotmer = customerCombo.getSelectedValue();
		customerCombo.setComboItem(selectedCusotmer);
		dateRangeItemCombo = new SelectCombo(Accounter.getReportsMessages()
				.dateRange());
		dateRangeItemCombo.setHelpInformation(true);
		dateRangeItemList = new ArrayList<String>();
		for (int i = 0; i < dateRangeArray.length; i++) {
			dateRangeItemList.add(dateRangeArray[i]);
		}
		dateRangeItemCombo.initCombo(dateRangeItemList);
		dateRangeItemCombo.setComboItem(Accounter.getReportsMessages().all());
		dateRangeItemCombo
				.addSelectionChangeHandler(new IAccounterComboSelectionChangeHandler<String>() {

					@Override
					public void selectedComboBoxItem(String selectItem) {
						dateRangeChanged(dateRangeItemCombo.getSelectedValue());

					}
				});

		fromItem = new DateItem();
		fromItem.setHelpInformation(true);
		fromItem.setDatethanFireEvent(Accounter.getStartDate());
		fromItem.setTitle(Accounter.getReportsMessages().from());

		toItem = new DateItem();
		toItem.setHelpInformation(true);
		ClientFinanceDate date = Accounter.getCompany()
				.getLastandOpenedFiscalYearEndDate();

		if (date != null)
			toItem.setDatethanFireEvent(date);
		else
			toItem.setDatethanFireEvent(new ClientFinanceDate());

		toItem.setTitle(Accounter.getReportsMessages().to());
		toItem.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				startDate = (ClientFinanceDate) fromItem.getValue();
				endDate = (ClientFinanceDate) toItem.getValue();
			}
		});
		updateButton = new AccounterButton(Accounter.getReportsMessages()
				.update());
		updateButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				setStartDate(fromItem.getDate());
				setEndDate(toItem.getDate());
				changeDates(fromItem.getDate(), toItem.getDate());
				dateRangeItemCombo.setDefaultValue(Accounter
						.getReportsMessages().custom());
				setSelectedDateRange(Accounter.getReportsMessages().custom());

			}
		});

		AccounterButton printButton = new AccounterButton(Accounter
				.getReportsMessages().print());
		printButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

			}

		});

		if (UIUtils.isMSIEBrowser()) {
			dateRangeItemCombo.setWidth("200px");
		}

		addItems(customerCombo, dateRangeItemCombo, fromItem, toItem);
		add(updateButton);
		updateButton.enabledButton();
		this.setCellVerticalAlignment(updateButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		// reportRequest();
	}

	/*
	 * @see
	 * com.vimukti.accounter.web.client.ui.reports.ReportToolbar#changeDates
	 * (java.util.Date, java.util.Date)
	 */
	@Override
	public void changeDates(ClientFinanceDate startDate,
			ClientFinanceDate endDate) {
		fromItem.setValue(startDate);
		toItem.setValue(endDate);
		if (selectedCusotmer != null)
			reportview.makeReportRequest(selectedCusotmer.getName(), startDate,
					endDate);
		else
			reportview.addEmptyMessage("No records to show");
	}

	@Override
	public void setDefaultDateRange(String defaultDateRange) {
		dateRangeItemCombo.setDefaultValue(defaultDateRange);
		dateRangeChanged(defaultDateRange);
	}

	@Override
	public void setDateRanageOptions(String... dateRanages) {
		dateRangeItemCombo.setValueMap(dateRanages);
	}

	@Override
	public void setStartAndEndDates(ClientFinanceDate startDate,
			ClientFinanceDate endDate) {
		if (startDate != null && endDate != null) {
			fromItem.setEnteredDate(startDate);
			toItem.setEnteredDate(endDate);
			setStartDate(startDate);
			setEndDate(endDate);
		}

	}

	public void reportRequest() {
		reportview.makeReportRequest(selectedCusotmer.getName(), startDate,
				endDate);
	}

}
