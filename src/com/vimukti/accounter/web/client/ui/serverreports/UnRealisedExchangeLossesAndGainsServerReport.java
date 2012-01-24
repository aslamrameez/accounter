package com.vimukti.accounter.web.client.ui.serverreports;

import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.reports.UnRealisedLossOrGain;
import com.vimukti.accounter.web.client.ui.DataUtils;
import com.vimukti.accounter.web.client.ui.reports.UnRealisedExchangeLossesAndGainsReport;

public class UnRealisedExchangeLossesAndGainsServerReport extends
		AbstractFinaneReport<UnRealisedLossOrGain> {

	public UnRealisedExchangeLossesAndGainsServerReport(
			UnRealisedExchangeLossesAndGainsReport reportView) {
		this.reportView = reportView;
	}

	@Override
	public String[] getDynamicHeaders() {
		return getColunms();
	}

	@Override
	public String getTitle() {
		return messages.unRealisedExchangeLossesAndGains();
	}

	@Override
	public String[] getColunms() {
		return new String[] { messages.Account(), messages.currency(),
				messages.foreignBalance(), messages.exchangeRate(),
				messages.adjustedBalance(), messages.currentBalance(),
				messages.unrealisedLossOrGain() };
	}

	@Override
	public int[] getColumnTypes() {
		return new int[] { COLUMN_TYPE_TEXT, COLUMN_TYPE_TEXT,
				COLUMN_TYPE_TEXT, COLUMN_TYPE_TEXT, COLUMN_TYPE_TEXT,
				COLUMN_TYPE_TEXT, COLUMN_TYPE_TEXT };
	}

	@Override
	public void processRecord(UnRealisedLossOrGain record) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getColumnData(UnRealisedLossOrGain record, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return record.getAccountName();
		case 1:
			return record.getCurrency();
		case 2:
			return DataUtils.getAmountAsStringInCurrency(
					record.getForeignBalance(), null);
		case 3:
			return DataUtils.getAmountAsStringInCurrency(
					record.getExchangeRate(), null);
		case 4:
			return DataUtils.getAmountAsStringInCurrency(
					record.getAdjustedBalance(), null);
		case 5:
			return DataUtils.getAmountAsStringInCurrency(
					record.getCurrentBalance(), null);
		case 6:
			return DataUtils.getAmountAsStringInCurrency(
					record.getLossOrGain(), null);
		}
		return "";
	}

	@Override
	public ClientFinanceDate getStartDate(UnRealisedLossOrGain obj) {
		return obj.getStartDate();
	}

	@Override
	public ClientFinanceDate getEndDate(UnRealisedLossOrGain obj) {
		return obj.getEndDate();
	}

	@Override
	public void makeReportRequest(long start, long end) {
		// TODO Auto-generated method stub

	}

}