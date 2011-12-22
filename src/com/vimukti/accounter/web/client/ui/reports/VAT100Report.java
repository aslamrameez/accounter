package com.vimukti.accounter.web.client.ui.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientTAXAgency;
import com.vimukti.accounter.web.client.core.reports.VATSummary;
import com.vimukti.accounter.web.client.ui.Accounter;
import com.vimukti.accounter.web.client.ui.UIUtils;
import com.vimukti.accounter.web.client.ui.serverreports.VAT100ServerReport;

public class VAT100Report extends AbstractReportView<VATSummary> {

	private String sectionName = "";

	private int row = -1;
	private static long vatAgency;

	protected Double box3amount = 0.0D;
	protected Double box4amount = 0.0D;

	public VAT100Report() {
		super(false, Accounter.messages().noRecordsToShow());
		this.serverReport = new VAT100ServerReport(this);
	}

	@Override
	public void init() {
		super.init();
		toolbar.setDateRanageOptions(Accounter.messages().all(), Accounter
				.messages().thisWeek(), Accounter.messages().thisMonth(),
				Accounter.messages().lastWeek(), Accounter.messages()
						.lastMonth(),
				Accounter.messages().thisFinancialYear(), Accounter
						.messages().lastFinancialYear(), Accounter.messages()
						.thisFinancialQuarter(), Accounter.messages()
						.lastFinancialQuarter(), Accounter.messages()
						.financialYearToDate(), Accounter.messages()
						.lastVATQuarter(), Accounter.messages()
						.lastVATQuarterToDate(), Accounter.messages().custom());

		// Make rpc request for default VAT Agency and default DateRange
		List<ClientTAXAgency> vatAgencies = Accounter.getCompany()
				.getTaxAgencies();
		for (ClientTAXAgency vatAgency : vatAgencies) {
			if (vatAgency.getName().equalsIgnoreCase(
					Accounter.messages().hmCustomsExciseVAT())) {
				ClientFinanceDate date = new ClientFinanceDate();
				int month = (date.getMonth()) % 3;
				int startMonth = date.getMonth() - month;
				ClientFinanceDate startDate = new ClientFinanceDate(
						date.getYear(), startMonth, 1);
				ClientFinanceDate start = startDate;
				ClientFinanceDate end = date;
				makeReportRequest(vatAgency.getID(), start, end);
				break;
			}
		}
	}

	@Override
	public void OnRecordClick(VATSummary record) {
	}

	@Override
	public String[] getDynamicHeaders() {
		return new String[] {
				"",
				UIUtils.getDateByCompanyType(toolbar.getStartDate()) + "-"
						+ UIUtils.getDateByCompanyType(toolbar.getEndDate()) };
	}

	@Override
	public int getToolbarType() {
		return TOOLBAR_TYPE_DATE_RANGE_VATAGENCY;
	}

	@Override
	public void makeReportRequest(long vatAgency, ClientFinanceDate startDate,
			ClientFinanceDate endDate) {
		// row = -1;
		// this.sectionName = "";
		Accounter.createReportService().getVAT100Report(vatAgency, startDate,
				endDate, this);
		this.setVatAgency(vatAgency);
	}

	@Override
	public void makeReportRequest(ClientFinanceDate start, ClientFinanceDate end) {

	}

	@Override
	public void onEdit() {

	}

	@Override
	public void print() {

		UIUtils.generateReportPDF(
				Integer.parseInt(String.valueOf(startDate.getDate())),
				Integer.parseInt(String.valueOf(endDate.getDate())), 137, "",
				"", getVatAgency());
	}

	@Override
	public void printPreview() {

	}

	@Override
	public int sort(VATSummary obj1, VATSummary obj2, int col) {

		// switch (col) {
		// case 0:
		// return obj1.getName().compareTo(obj2.getName());
		// case 1:
		// return UIUtils.compareDouble(obj1.getValue(), obj2.getValue());
		// }
		return 0;
	}

	public void exportToCsv() {

		UIUtils.exportReport(
				Integer.parseInt(String.valueOf(startDate.getDate())),
				Integer.parseInt(String.valueOf(endDate.getDate())), 137, "",
				"", getVatAgency());
	}

	public long getVatAgency() {
		return vatAgency;
	}

	public void setVatAgency(long vatAgency) {
		this.vatAgency = vatAgency;
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
		long status1 = ((Long) map.get("vat100"));
		VAT100Report.vatAgency = status1;
		isDatesArranged = true;
	}

	@Override
	public Map<String, Object> saveView() {
		Map<String, Object> map = new HashMap<String, Object>();
		String selectedDateRange = toolbar.getSelectedDateRange();
		ClientFinanceDate startDate = toolbar.getStartDate();
		ClientFinanceDate endDate = toolbar.getEndDate();
		long status = VAT100Report.vatAgency;
		map.put("selectedDateRange", selectedDateRange);
		map.put("vat100", status);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		return map;
	}

}
