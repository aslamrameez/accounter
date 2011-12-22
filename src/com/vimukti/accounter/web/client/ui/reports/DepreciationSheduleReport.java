package com.vimukti.accounter.web.client.ui.reports;

import java.util.HashMap;
import java.util.Map;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientFixedAsset;
import com.vimukti.accounter.web.client.core.IAccounterCore;
import com.vimukti.accounter.web.client.core.reports.DepreciationShedule;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.serverreports.DepreciationSheduleServerReport;

public class DepreciationSheduleReport extends
		AbstractReportView<DepreciationShedule> {
	private final String currentsectionName = "";
	private int reportType = REPORT_TYPE_DEPRECIATIONSHEDULE;

	// private int reportType = REPORT_TYPE_D;
	public DepreciationSheduleReport() {
		super(false, Accounter.messages().noRecordsToShow());
		this.serverReport = new DepreciationSheduleServerReport(this);
		this.serverReport.setIshowGridFooter(false);
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {
		Accounter.createReportService().getDepreciationSheduleReport(start,
				end, ClientFixedAsset.STATUS_REGISTERED,
				Accounter.getCompany().getID(), this);
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_AS_OF;
	}

	@Override
	public void OnRecordClick(DepreciationShedule record) {
		record.setStartDate(toolbar.getStartDate());
		record.setEndDate(toolbar.getEndDate());
		ReportsRPC.openTransactionView(IAccounterCore.FIXED_ASSET,
				record.getFixedAssetId());
	}

	@Override
	public void print() {
		String accountName = data != null ? ((DepreciationShedule) data)
				.getAssetAccountName() : "";
		UIUtils.generateReportPDF(
				Integer.parseInt(String.valueOf(startDate.getDate())),
				Integer.parseInt(String.valueOf(endDate.getDate())),
				getReportType(), "", "", accountName);
	}

	@Override
	public void exportToCsv() {
		UIUtils.exportReport(
				Integer.parseInt(String.valueOf(startDate.getDate())),
				Integer.parseInt(String.valueOf(endDate.getDate())),
				getReportType(), "", "");
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;

	}
	
	@Override
	public void restoreView(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			isDatesArranged = false;
			return;
		}
		ClientFinanceDate endDate = (ClientFinanceDate) map.get("endDate");
		toolbar.setEndDate(endDate);
		toolbar.setDefaultDateRange((String) map.get("selectedDateRange"));
		isDatesArranged = true;
	}

	@Override
	public Map<String, Object> saveView() {
		Map<String, Object> map = new HashMap<String, Object>();
		String selectedDateRange = toolbar.getSelectedDateRange();
		ClientFinanceDate endDate = toolbar.getEndDate();
		map.put("selectedDateRange", selectedDateRange);
		map.put("endDate", endDate);
		return map;
	}
}
