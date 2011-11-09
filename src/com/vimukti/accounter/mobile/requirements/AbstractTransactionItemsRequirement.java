package com.vimukti.accounter.mobile.requirements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.TAXCode;
import com.vimukti.accounter.mobile.ActionNames;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.RequirementType;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;
import com.vimukti.accounter.web.client.core.ClientAccount;
import com.vimukti.accounter.web.client.core.ClientQuantity;
import com.vimukti.accounter.web.client.core.ClientTAXCode;
import com.vimukti.accounter.web.client.core.ClientTransactionItem;
import com.vimukti.accounter.web.client.core.ListFilter;
import com.vimukti.accounter.web.client.core.Utility;
import com.vimukti.accounter.web.client.ui.core.DecimalUtil;

public abstract class AbstractTransactionItemsRequirement<T> extends
		ListRequirement<T> {

	private static final String TRANSACTION_ITEMS = "transactionItems";
	protected static final String OLD_TRANSACTION_ITEM_ATTR = "oldTransactionItemAttr";

	protected static final String PROCESS_ATTR = "processAttr";
	protected static final String TAXCODES = "taxcodes";

	public AbstractTransactionItemsRequirement(String requirementName,
			String displayString, String recordName, boolean isOptional,
			boolean isAllowFromContext) {
		super(requirementName, displayString, recordName, isOptional,
				isAllowFromContext, null);

		setValue(new ArrayList<ClientTransactionItem>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isDone() {
		if (!isOptional()) {
			return ((List<ClientTransactionItem>) getValue()).size() != 0;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDefaultValue(Object defaultValue) {
		((List<ClientTransactionItem>) getValue())
				.addAll((List<ClientTransactionItem>) defaultValue);
	}

	@Override
	public Result run(Context context, Result makeResult, ResultList list,
			ResultList actions) {
		String process = (String) context.getAttribute(PROCESS_ATTR);
		if (process != null) {
			if (process.equals(getName())) {
				Result result = transactionItemProcess(context);
				if (result != null) {
					return result;
				}
			}
		}
		List<T> items = context.getSelections(getName());
		List<ClientTransactionItem> transactionItems = getValue();
		if (items != null && items.size() > 0) {
			for (T item : items) {
				context.setString(null);
				ClientTransactionItem transactionItem = new ClientTransactionItem();
				transactionItem.setTaxable(true);

				if (item instanceof ClientAccount) {
					transactionItem.setType(ClientTransactionItem.TYPE_ACCOUNT);
				} else {
					transactionItem.setType(ClientTransactionItem.TYPE_ITEM);
				}

				setItem(transactionItem, item);
				setPrice(transactionItem, item);

				ClientQuantity quantity = new ClientQuantity();
				quantity.setValue(1);
				transactionItem.setQuantity(quantity);

				double lt = transactionItem.getQuantity().getValue()
						* transactionItem.getUnitPrice();
				double disc = transactionItem.getDiscount();
				transactionItem
						.setLineTotal(DecimalUtil.isGreaterThan(disc, 0) ? (lt - (lt
								* disc / 100))
								: lt);
				transactionItems.add(transactionItem);
				addFirstMessage(
						context,
						getMessages()
								.selectedAs(
										getDisplayValue(getTransactionItem(transactionItem)),
										getConstants().transactionItem()));
				Result transactionItemResult = checkItemToEdit(context,
						transactionItem);
				if (transactionItemResult != null) {
					return transactionItemResult;
				}
			}
			context.setAttribute(INPUT_ATTR, "");
		}

		boolean show = false;
		if (!isDone()) {
			show = true;
		}

		ClientTransactionItem editTransactionItem = context
				.getSelection(TRANSACTION_ITEMS + getName());
		if (editTransactionItem != null) {
			Result result = transactionItem(context, editTransactionItem);
			if (result != null) {
				return result;
			}
		}

		Object selection = context.getSelection(ACTIONS);
		if (selection == getName()) {
			show = true;
		}

		String attribute = (String) context.getAttribute(INPUT_ATTR);
		if (attribute.equals(getName())) {
			show = true;
		}

		if (show) {
			List<T> oldValues = new ArrayList<T>();
			for (ClientTransactionItem item : transactionItems) {
				oldValues.add(getTransactionItem(item));
			}
			return showList(makeResult, context, oldValues);
		}

		makeResult.add(getRecordName());
		ResultList itemsList = new ResultList(TRANSACTION_ITEMS + getName());
		for (ClientTransactionItem item : transactionItems) {
			Record itemRec = new Record(item);
			itemRec.add("", getItemDisplayValue(item));
			itemRec.add("", item.getLineTotal());
			itemRec.add("", item.getVATfraction());
			itemsList.add(itemRec);
		}
		makeResult.add(itemsList);

		Record moreItems = new Record(getName());
		moreItems.add("", getAddMoreString());
		actions.add(moreItems);
		return null;
	}

	private Result transactionItemProcess(Context context) {
		ClientTransactionItem transactionItem = (ClientTransactionItem) context
				.getAttribute(OLD_TRANSACTION_ITEM_ATTR);
		Result result = transactionItem(context, transactionItem);
		if (result == null) {
			ActionNames actionName = context.getSelection(ACTIONS);
			if (actionName == ActionNames.DELETE_ITEM) {
				List<ClientTransactionItem> transItems = getValue();
				transItems.remove(transactionItem);
				context.removeAttribute(OLD_TRANSACTION_ITEM_ATTR);
			}

			result = checkItemToEdit(context, transactionItem);
			if (result != null) {
				return result;
			}
			context.setAttribute(INPUT_ATTR, "");
			addFirstMessage(context,
					getDisplayValue(getTransactionItem(transactionItem))
							+ getSetMessage());
		}
		return result;
	}

	protected Result taxCode(Context context, String displayName,
			ClientTAXCode oldCode, String itemName) {
		Result result = context.makeResult();
		String attribute = (String) context.getAttribute(INPUT_ATTR);
		context.setAttribute(INPUT_ATTR, getName());
		String name = null;
		if (attribute.equals(getName())) {
			name = context.getString();
		}
		if (name == null) {
			List<ClientTAXCode> lists = getTaxCodeLists(context);
			if (lists.size() > 5) {
				context.setAttribute("oldValue", "");
				result.add(displayName);
				ResultList actions = new ResultList(ACTIONS);
				Record record = new Record(ActionNames.ALL);
				record.add("", getConstants().showAll());
				actions.add(record);
				result.add(actions);
				return result;
			}
			List<ClientTAXCode> oldRecords = new ArrayList<ClientTAXCode>();
			if (oldCode != null) {
				oldRecords.add(oldCode);
			}
			return displayRecords2(context, lists, result, 5, oldRecords,
					itemName);
		}

		Object selection = context.getSelection(ACTIONS);
		List<ClientTAXCode> lists = new ArrayList<ClientTAXCode>();
		if (selection == ActionNames.ALL) {
			lists = getTaxCodeLists(context);
			if (lists.size() != 0) {
				result.add(getConstants().allRecords());
			}
			name = null;
		} else if (selection == null) {
			lists = getTaxCodeLists(context, name);
			context.setAttribute("oldValue", name);
			if (lists.size() != 0) {
				result.add(getMessages().foundRecords(lists.size(), name));
			} else {
				result.add(getMessages().didNotGetRecords(name));
				context.setAttribute("oldValue", "");
				lists = getTaxCodeLists(context);
			}
		} else {
			String oldValue = (String) context.getAttribute("oldValue");
			if (oldValue != null && !oldValue.equals("")) {
				lists = getTaxCodeLists(context, oldValue);
			} else {
				lists = getTaxCodeLists(context);
			}
		}

		List<ClientTAXCode> oldRecords = new ArrayList<ClientTAXCode>();
		if (oldCode != null) {
			oldRecords.add(oldCode);
		}
		return displayRecords2(context, lists, result, 5, oldRecords, itemName);
	}

	private Result displayRecords2(Context context, List<ClientTAXCode> lists,
			Result result, int recordsToShow, List<ClientTAXCode> oldRecords,
			String itemName) {
		ResultList customerList = new ResultList(TAXCODES);
		Object last = context.getLast(RequirementType.CUSTOMER);
		List<ClientTAXCode> skipCustomers = new ArrayList<ClientTAXCode>();
		if (last != null) {
			ClientTAXCode lastRec = (ClientTAXCode) last;
			customerList.add(createClientTAXCodeRecord(lastRec));
			skipCustomers.add(lastRec);
		}

		if (oldRecords != null) {
			for (ClientTAXCode t : oldRecords) {
				customerList.add(createClientTAXCodeRecord(t));
				skipCustomers.add(t);
			}
		}

		ResultList actions = new ResultList(ACTIONS);

		ActionNames selection = context.getSelection(ACTIONS);

		List<ClientTAXCode> pagination = pagination2(context, selection,
				actions, lists, skipCustomers, recordsToShow);

		for (ClientTAXCode rec : pagination) {
			customerList.add(createClientTAXCodeRecord(rec));
		}

		int size = customerList.size();
		StringBuilder message = new StringBuilder();
		if (size > 0) {
			message.append(getMessages().selectFor(getConstants().taxCode(),
					itemName));
		} else {
			message.append(getMessages().youDontHaveAny(
					getConstants().taxCode()));
		}

		result.add(message.toString());
		result.add(customerList);
		result.add(actions);
		CommandList commandList = new CommandList();
		commandList.add(getMessages().create(getConstants().taxCode()));
		result.add(commandList);
		return result;
	}

	public List<ClientTAXCode> pagination2(Context context,
			ActionNames selection, ResultList actions,
			List<ClientTAXCode> records, List<ClientTAXCode> skipRecords,
			int recordsToShow) {
		if (selection != null && selection == ActionNames.PREV_PAGE) {
			Integer index = (Integer) context.getAttribute(RECORDS_START_INDEX);
			Integer lastPageSize = (Integer) context
					.getAttribute("LAST_PAGE_SIZE");
			context.setAttribute(RECORDS_START_INDEX,
					index
							- (recordsToShow + (lastPageSize == null ? 0
									: lastPageSize)));
		} else if (selection == null || selection != ActionNames.NEXT_PAGE) {
			context.setAttribute(RECORDS_START_INDEX, 0);
		}

		int num = skipRecords.size();
		Integer index = (Integer) context.getAttribute(RECORDS_START_INDEX);
		if (index == null || index < 0) {
			index = 0;
		}
		List<ClientTAXCode> result = new ArrayList<ClientTAXCode>();
		for (int i = index; i < records.size(); i++) {
			if (num == recordsToShow) {
				break;
			}
			ClientTAXCode r = records.get(i);
			if (skipRecords.contains(r)) {
				continue;
			}
			num++;
			result.add(r);
		}
		context.setAttribute("LAST_PAGE_SIZE",
				skipRecords.size() + result.size());
		index += (skipRecords.size() + result.size());
		context.setAttribute(RECORDS_START_INDEX, index);

		if (records.size() > index) {
			Record inActiveRec = new Record(ActionNames.NEXT_PAGE);
			inActiveRec.add("", getConstants().nextPage());
			actions.add(inActiveRec);
		}

		if (index > recordsToShow) {
			Record inActiveRec = new Record(ActionNames.PREV_PAGE);
			inActiveRec.add("", getConstants().prevPage());
			actions.add(inActiveRec);
		}
		return result;
	}

	private Record createClientTAXCodeRecord(ClientTAXCode taxCode) {
		Record record = new Record(taxCode);
		record.add("", taxCode.getName() + "-" + taxCode.getSalesTaxRate());
		return record;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	private List<ClientTAXCode> getTaxCodeLists(Context context) {
		List<ClientTAXCode> clientTAXCodes = new ArrayList<ClientTAXCode>();
		Set<TAXCode> taxCodes = context.getCompany().getTaxCodes();
		for (TAXCode taxCode : taxCodes) {
			if (taxCode.isActive())
				clientTAXCodes.add(taxCode);
		}
		return clientTAXCodes;
	}

	private List<ClientTAXCode> getTaxCodeLists(Context context,
			final String name) {

		return Utility.filteredList(new ListFilter<ClientTAXCode>() {

			@Override
			public boolean filter(ClientTAXCode e) {
				return e.getDisplayName().contains(name);
			}
		}, getTaxCodeLists(context));
	}

	protected Result number(Context context, String displayString,
			String oldValu) {
		return show(context, displayString, oldValu, oldValu);
	}

	protected Result amount(Context context, String displayString,
			double oldValu) {
		return show(context, displayString, String.valueOf(oldValu), oldValu);
	}

	protected abstract T getTransactionItem(ClientTransactionItem item);

	protected abstract void setPrice(ClientTransactionItem transactionItem,
			T item);

	protected abstract void setItem(ClientTransactionItem transactionItem,
			T item);

	protected abstract Result transactionItem(Context context,
			ClientTransactionItem editTransactionItem);

	protected abstract Result checkItemToEdit(Context context,
			ClientTransactionItem transactionItem);

	protected abstract String getAddMoreString();

	protected abstract String getItemDisplayValue(ClientTransactionItem item);

	protected String getItemName(ClientTransactionItem transactionItem) {
		return getDisplayValue(getTransactionItem(transactionItem));
	}
}
