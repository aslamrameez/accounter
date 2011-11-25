package com.vimukti.accounter.mobile.commands.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vimukti.accounter.core.FinanceDate;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.web.client.core.Lists.DummyDebitor;
import com.vimukti.accounter.web.client.core.reports.AgedDebtors;
import com.vimukti.accounter.web.server.FinanceTool;

public class APAgingSummaryReportCommand extends
		NewAbstractReportCommand<DummyDebitor> {

	@Override
	protected void addRequirements(List<Requirement> list) {
		addFromToDateRequirements(list);
		super.addRequirements(list);
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	protected Record createReportRecord(DummyDebitor record) {
		Record agingRecord = new Record(record);
		agingRecord.add("Creditor", record.getDebitorName());
		agingRecord.add("0-30 days",
				record.getDebitdays_in30() + record.getDebitdays_incurrent());
		agingRecord.add("31-60 days", record.getDebitdays_in60());
		agingRecord.add("61-90 days", record.getDebitdays_in90());
		agingRecord.add("Older", record.getDebitdays_inolder());
		agingRecord.add(
				"Total balance",
				record.getDebitdays_in30() + record.getDebitdays_in60()
						+ record.getDebitdays_in90()
						+ record.getDebitdays_inolder()
						+ record.getDebitdays_incurrent());
		return agingRecord;
	}

	@Override
	protected List<DummyDebitor> getRecords() {
		FinanceDate start = getStartDate();
		FinanceDate end = getEndDate();
		ArrayList<DummyDebitor> apAgingSummaryReport = new ArrayList<DummyDebitor>();
		ArrayList<AgedDebtors> apAgingDetailsReport = new ArrayList<AgedDebtors>();
		try {
			apAgingDetailsReport = new FinanceTool().getReportManager()
					.getAgedCreditors(start, end, getCompanyId());

			apAgingSummaryReport = getDebtorsWidSameName(apAgingDetailsReport,
					start, end);
			DummyDebitor obj = new DummyDebitor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apAgingSummaryReport;
	}

	private ArrayList<DummyDebitor> getDebtorsWidSameName(
			List<AgedDebtors> agedDebtors, FinanceDate startdate,
			FinanceDate enddate) {
		ArrayList<DummyDebitor> listDebtors = new ArrayList<DummyDebitor>();
		Map<Integer, List<AgedDebtors>> sameDebtorsMap = new HashMap<Integer, List<AgedDebtors>>();
		List<AgedDebtors> sameDebtors;
		List<AgedDebtors> tempDtrs = new ArrayList<AgedDebtors>();
		String name;
		int listSize = agedDebtors.size();

		/*
		 * seperating(made a list and it to a map wid a key) debtors who has
		 * same name
		 */
		for (int i = 0; !agedDebtors.isEmpty(); i++) {

			sameDebtors = new ArrayList<AgedDebtors>();

			/* Initialising list with atleast one obj */
			if (!tempDtrs.contains(agedDebtors.get(0)))
				sameDebtors.add(agedDebtors.get(0));
			name = agedDebtors.get(0).getName();
			agedDebtors.remove(0);

			/* Iterating from next available obj in the list */
			for (AgedDebtors debitorAgedDebtors : agedDebtors) {
				if (name.equals(debitorAgedDebtors.getName())
						&& !tempDtrs.contains(debitorAgedDebtors)) {
					sameDebtors.add(debitorAgedDebtors);
					tempDtrs.add(debitorAgedDebtors);
				}
			}
			/*
			 * changing decision var. with modifed list size.And intialize name
			 * with 1st available obj in the list
			 */
			agedDebtors.removeAll(tempDtrs);
			listSize = agedDebtors.size();
			if (listSize != 0)
				name = agedDebtors.get(0).getName();

			/* add the list to map */
			if (sameDebtors.size() > 0) {
				sameDebtorsMap.put(i, sameDebtors);
			}
		}

		/*
		 * Iterating the map for calculating the totals for the debtors who has
		 * same name
		 */
		for (Map.Entry<Integer, List<AgedDebtors>> sameDtr : sameDebtorsMap
				.entrySet()) {
			List<AgedDebtors> SimilarDtrs = sameDtr.getValue();
			double dayscurrentTotal = 0.0;
			double days30Total = 0.0;
			double days60Total = 0.0;
			double days90Total = 0.0;
			double daysOlderTotal = 0.0;
			long days = 0;
			for (AgedDebtors agdDebtor : SimilarDtrs) {
				// // if (!agdDebtor.getDate().after(enddate)) {
				// days = UIUtils.getDays_between(agdDebtor.getDate()
				// .getDateAsObject(), enddate.getDateAsObject());
				// }

				days = agdDebtor.getAgeing();

				if (days <= 0)
					dayscurrentTotal += agdDebtor.getTotal();
				else if (days > 0 && days <= 30)
					days30Total += agdDebtor.getTotal();
				else if (days > 30 && days <= 60)
					days60Total += agdDebtor.getTotal();
				else if (days > 60 && days <= 90)
					days90Total += agdDebtor.getTotal();
				else if (days > 90)
					daysOlderTotal += agdDebtor.getTotal();
			}
			AgedDebtors dtr = SimilarDtrs.get(0);

			DummyDebitor debitor = new DummyDebitor();
			debitor.setDebitdays_incurrent(dayscurrentTotal);
			debitor.setDebitdays_in30(days30Total);
			debitor.setDebitdays_in60(days60Total);
			debitor.setDebitdays_in90(days90Total);
			debitor.setDebitdays_inolder(daysOlderTotal);
			debitor.setDebitorName(dtr.getName());
			debitor.setDueDate(dtr.getDueDate());
			debitor.setTransactionId(dtr.getTransactionId());
			listDebtors.add(debitor);
		}

		return listDebtors;
	}

	@Override
	protected String addCommandOnRecordClick(DummyDebitor selection) {
		return "A/P Aging Detail ," + selection.getDebitorName();
	}

	@Override
	protected String getEmptyString() {
		return getMessages().youDontHaveAnyReports();
	}

	@Override
	protected String getShowMessage() {
		return null;
	}

	@Override
	protected String getSelectRecordString() {
		return getMessages().reportSelected(getMessages().apAgeingSummary());
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		String string = context.getString();
		if (string != null && !string.isEmpty()) {

		}
		return null;
	}

	@Override
	protected String getWelcomeMessage() {
		return getMessages().reportCommondActivated(
				getMessages().apAgeingSummary());
	}

	@Override
	protected String getDetailsMessage() {
		return getMessages().reportDetails(getMessages().apAgeingSummary());
	}

	@Override
	public String getSuccessMessage() {
		return getMessages().reportCommondClosedSuccessfully(
				getMessages().apAgeingSummary());
	}

}