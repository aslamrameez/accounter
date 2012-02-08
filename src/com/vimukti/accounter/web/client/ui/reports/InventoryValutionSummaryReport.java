package com.vimukti.accounter.web.client.ui.reports;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.reports.InventoryValutionSummary;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.core.ActionFactory;
import com.vimukti.accounter.web.client.ui.serverreports.InventoryValutionSummaryServerReport;

public class InventoryValutionSummaryReport extends
		AbstractReportView<InventoryValutionSummary> {
	public InventoryValutionSummaryReport() {
		this.serverReport = new InventoryValutionSummaryServerReport(this);
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {
		Accounter.createReportService().getInventoryValutionSummary(start, end,
				this);
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_DATE_RANGE;
	}

	@Override
	public void OnRecordClick(InventoryValutionSummary record) {
		record.setStartDate(toolbar.getStartDate());
		record.setEndDate(toolbar.getEndDate());
		record.setDateRange(toolbar.getSelectedDateRange());
		UIUtils.runAction(record, ActionFactory
				.getInventoryValuationDetailsAction(record.getId()));
	}

}