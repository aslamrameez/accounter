package com.vimukti.accounter.web.client.ui.reports;

import java.util.HashMap;
import java.util.Map;

import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.reports.TransactionDetailByAccount;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.serverreports.MissingChecksServerReport;

public class MissingChecksReport extends
		AbstractReportView<TransactionDetailByAccount> {

	private long accounId = 0;

	public MissingChecksReport() {
		this.serverReport = new MissingChecksServerReport(this);
	}

	@Override
	public void init() {
		super.init();
		this.toolbar.setAccId(this.accounId);
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {
		this.makeReportRequest(getAccountId(), start, end);
		
	}

	@Override
	public void makeReportRequest(long account, ClientFinanceDate startDate,
			ClientFinanceDate endDate) {
		grid.clear();
		if (account != 0) {
			setAccountId(account);
		}
		Accounter.createReportService().getMissingCheckDetils(account,
				startDate, endDate, this);
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_ACCOUNT;
	}

	@Override
	public void OnRecordClick(TransactionDetailByAccount record) {
		record.setStartDate(toolbar.getStartDate());
		record.setEndDate(toolbar.getEndDate());
		record.setDateRange(toolbar.getSelectedDateRange());
		ReportsRPC.openTransactionView(record.getTransactionType(),
				record.getTransactionId());
	}

	@Override
	public void restoreView(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			isDatesArranged = false;
			return;
		}
		ClientFinanceDate startDate = (ClientFinanceDate) map.get("startDate");
		ClientFinanceDate endDate = (ClientFinanceDate) map.get("endDate");
		this.serverReport.setStartAndEndDates(startDate, endDate);
		toolbar.setEndDate(endDate);
		toolbar.setStartDate(startDate);
		toolbar.setDefaultDateRange((String) map.get("selectedDateRange"));
		isDatesArranged = true;
	}

	@Override
	public Map<String, Object> saveView() {
		Map<String, Object> map = new HashMap<String, Object>();
		String selectedDateRange = toolbar.getSelectedDateRange();
		ClientFinanceDate startDate = toolbar.getStartDate();
		ClientFinanceDate endDate = toolbar.getEndDate();
		map.put("selectedDateRange", selectedDateRange);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		return map;
	}

	public long getAccountId() {
		return accounId;
	}

	public void setAccountId(long accounId) {
		this.accounId = accounId;
	}

	@Override
	public void print() {

		if (getAccountId() == 0) {
			Accounter.showError(messages.pleaseSelect(messages.account()));
		} else {
			UIUtils.generateReportPDF(
					Integer.parseInt(String.valueOf(startDate.getDate())),
					Integer.parseInt(String.valueOf(endDate.getDate())), 181,
					"", "", getAccountId());
		}
	}

	@Override
	public void exportToCsv() {
		if (getAccountId() == 0) {
			Accounter.showError(messages.pleaseSelect(Global.get().Vendor()));
		} else {
			UIUtils.exportReport(
					Integer.parseInt(String.valueOf(startDate.getDate())),
					Integer.parseInt(String.valueOf(endDate.getDate())), 181,
					"", "", getAccountId());
		}
	}
}