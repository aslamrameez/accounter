package com.vimukti.accounter.mobile.commands;

import java.util.List;

import org.hibernate.Session;

import com.vimukti.accounter.core.TAXAgency;
import com.vimukti.accounter.core.TAXItem;
import com.vimukti.accounter.core.VATReturnBox;
import com.vimukti.accounter.mobile.ActionNames;
import com.vimukti.accounter.mobile.Command;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.RequirementType;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;

public class NewVATItemCommand extends Command {

	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String IS_PERCENTAGE = "isPercentage";
	private static final String TAX_RATE = "taxRate";
	private static final String IS_ACTIVE = "isActive";
	private static final String TAX_AGENCY = "taxAgency";
	private static final String VAT_RETURN_BOX = "vatReturnBox";

	private static final int TAXAGENCIES_TO_SHOW = 0;
	private static final int VATRETURN_BOXES_TO_SHOW = 0;

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addRequirements(List<Requirement> list) {
		list.add(new Requirement(NAME, false, true));
		list.add(new Requirement(DESCRIPTION, true, true));
		list.add(new Requirement(IS_PERCENTAGE, true, true));
		list.add(new Requirement(TAX_RATE, false, true));
		list.add(new Requirement(IS_ACTIVE, true, true));
		list.add(new Requirement(TAX_AGENCY, false, true));
		list.add(new Requirement(VAT_RETURN_BOX, false, true));
	}

	@Override
	public Result run(Context context) {
		Result result = null;

		result = nameRequirement(context);

		result = taxRateRequirement(context);

		result = taxAgencyRequirement(context);

		result = createOptionalResult(context);

		return createVATItem(context);
	}

	private Result taxAgencyRequirement(Context context) {
		Requirement taxAgency = get(TAX_AGENCY);
		if (!taxAgency.isDone()) {
			return getTaxAgencyResult(context);
		}
		Requirement vatReturnBox = get(VAT_RETURN_BOX);
		if (!vatReturnBox.isDone()) {
			return getVatReturnBoxResult(context);
		}
		return null;
	}

	private Result taxRateRequirement(Context context) {
		Requirement taxRate = get(TAX_RATE);
		double selTaxRate = context.getSelection(TAX_RATE);
		if (selTaxRate != 0) {
			taxRate.setValue(selTaxRate);
		}
		if (!taxRate.isDone()) {
			return getResultToAsk(context, "Please Enter the TaxRate.");
		}
		return null;
	}

	private Result nameRequirement(Context context) {
		Requirement name = get(NAME);
		String selName = context.getSelection(NAME);
		if (selName != null) {
			name.setValue(selName);
		}
		if (!name.isDone()) {
			return getResultToAsk(context, "Please Enter the VAT Item Name.");
		}
		return null;
	}

	private Result createOptionalResult(Context context) {
		Object selection = context.getSelection("actions");
		if (selection != null) {
			ActionNames actionName = (ActionNames) selection;
			switch (actionName) {
			case FINISH:
				return null;
			default:
				break;
			}
		}

		Result result = context.makeResult();

		result.add("VAT Item is ready to create with following values.");
		ResultList list = new ResultList("");

		String name = (String) get(NAME).getValue();
		Record nameRecord = new Record(name);
		nameRecord.add("Name", "Name");
		nameRecord.add("Value", name);
		list.add(nameRecord);

		String description = (String) get(DESCRIPTION).getDefaultValue();
		Record descriptionRecord = new Record(description);
		descriptionRecord.add("Name", "Description");
		descriptionRecord.add("Value", description);
		list.add(descriptionRecord);

		boolean isPercentage = (Boolean) get(IS_PERCENTAGE).getDefaultValue();
		Record isPercentageRecord = new Record(isPercentage);
		isPercentageRecord.add("Name", "IsPercentage");
		isPercentageRecord.add("Value", isPercentage);
		list.add(isPercentageRecord);

		double taxRate = (Double) get(TAX_RATE).getValue();
		Record taxRateRecord = new Record(taxRate);
		taxRateRecord.add("Name", "Tax Rate");
		taxRateRecord.add("Value", taxRate);
		list.add(taxRateRecord);

		boolean isActive = (Boolean) get(IS_ACTIVE).getDefaultValue();
		Record isActiveRecord = new Record(isActive);
		isActiveRecord.add("Name", "Is Active");
		isActiveRecord.add("Value", isActive);
		list.add(isActiveRecord);

		TAXAgency taxAgency = (TAXAgency) get(TAX_AGENCY).getValue();
		Record taxAgencyRecord = new Record(taxAgency);
		taxAgencyRecord.add("Name", "Tax Agency");
		taxAgencyRecord.add("Value", taxAgency);
		list.add(taxAgencyRecord);

		VATReturnBox vatReturnBox = (VATReturnBox) get(VAT_RETURN_BOX)
				.getValue();
		Record vatReturnBoxRecord = new Record(vatReturnBox);
		vatReturnBoxRecord.add("Name", "VAT Return Box");
		vatReturnBoxRecord.add("Value", vatReturnBox);
		list.add(vatReturnBoxRecord);

		result.add(list);
		result.add("Finish to create VAT Item");

		return result;
	}

	private Result createVATItem(Context context) {
		String name = (String) get("name").getValue();
		String description = (String) get("description").getValue();
		boolean isPercentage = (Boolean) get("isPercentage").getValue();
		double taxRate = (Double) get("taxRate").getValue();
		boolean isActive = (Boolean) get("isActive").getValue();
		TAXAgency taxAgency = (TAXAgency) get("taxAgency").getValue();
		VATReturnBox vatReturnBox = (VATReturnBox) get("vatReturnBox")
				.getValue();

		TAXItem taxItem = new TAXItem();
		taxItem.setName(name);
		taxItem.setDescription(description);
		taxItem.setPercentage(isPercentage);
		taxItem.setTaxRate(taxRate);
		taxItem.setActive(isActive);
		taxItem.setTaxAgency(taxAgency);
		taxItem.setVatReturnBox(vatReturnBox);

		return null;
	}

	private Result getVatReturnBoxResult(Context context) {
		Result result = context.makeResult();
		ResultList vatReturnBoxesList = new ResultList("");

		Object last = context.getLast(RequirementType.VAT_RETURN_BOX);
		if (last != null) {
			vatReturnBoxesList
					.add(createVATReturnBoxRecord((VATReturnBox) last));
		}

		List<VATReturnBox> vatReturnBoxes = getVATReturnBoxes(context
				.getSession());
		for (int i = 0; i < VATRETURN_BOXES_TO_SHOW
				|| i < vatReturnBoxes.size(); i++) {
			VATReturnBox vatReturnBox = vatReturnBoxes.get(i);
			if (vatReturnBox != last) {
				vatReturnBoxesList
						.add(createVATReturnBoxRecord((VATReturnBox) vatReturnBox));
			}
		}

		int size = vatReturnBoxesList.size();
		StringBuilder message = new StringBuilder();
		if (size > 0) {
			message.append("Please Select the vatReturnBox");
		}

		CommandList commandList = new CommandList();
		commandList.add("create");

		result.add(message.toString());
		result.add(vatReturnBoxesList);
		result.add(commandList);
		result.add("Select the vatReturnBox");

		return result;
	}

	private List<VATReturnBox> getVATReturnBoxes(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	private Record createVATReturnBoxRecord(VATReturnBox last) {
		// TODO Auto-generated method stub
		return null;
	}

	private Result getTaxAgencyResult(Context context) {
		Result result = context.makeResult();
		ResultList taxAgenciesList = new ResultList("");

		Object last = context.getLast(RequirementType.TAXAGENCY);
		if (last != null) {
			taxAgenciesList.add(createTaxAgencyRecord((TAXAgency) last));
		}

		List<TAXAgency> taxAgencies = getTaxAgencies(context.getSession());
		for (int i = 0; i < TAXAGENCIES_TO_SHOW || i < taxAgencies.size(); i++) {
			TAXAgency taxAgency = taxAgencies.get(i);
			if (taxAgency != last) {
				taxAgenciesList
						.add(createTaxAgencyRecord((TAXAgency) taxAgency));
			}
		}

		int size = taxAgenciesList.size();
		StringBuilder message = new StringBuilder();
		if (size > 0) {
			message.append("Please Select the TaxAgency");
		}

		CommandList commandList = new CommandList();
		commandList.add("create");

		result.add(message.toString());
		result.add(taxAgenciesList);
		result.add(commandList);
		result.add("Select the TaxAgency");

		return result;
	}

	private List<TAXAgency> getTaxAgencies(Session session) {
		// TODO Auto-generated method stub
		return null;
	}

	private Record createTaxAgencyRecord(TAXAgency last) {
		// TODO Auto-generated method stub
		return null;
	}

	private Result getResultToAsk(Context context, String message) {
		Result result = context.makeResult();
		result.add(message);
		return result;
	}

}
