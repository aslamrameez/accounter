package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.ShippingMethod;
import com.vimukti.accounter.mobile.CommandList;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Record;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.requirements.ShowListRequirement;

public class ShippingMethodListCommand extends NewAbstractCommand {
	private static final String SHIPPING_METHODS = "ShiipingMethods";

	@Override
	public String getId() {
		return null;
	}

	protected void addRequirements(List<Requirement> list) {
		list.add(new ShowListRequirement<ShippingMethod>(SHIPPING_METHODS,
				null, 10) {
			@Override
			protected Record createRecord(ShippingMethod value) {
				Record record = new Record(value);
				record.add("", value.getName());
				record.add("", value.getDescription());
				return record;
			}

			@Override
			protected void setCreateCommand(CommandList list) {
				list.add(getMessages().create(getConstants().shippingMethod()));
			}

			@Override
			protected boolean filter(ShippingMethod e, String name) {
				return e.getName().toLowerCase().startsWith(name.toLowerCase());

			}

			@Override
			protected List<ShippingMethod> getLists(Context context) {
				List<ShippingMethod> list = new ArrayList<ShippingMethod>();
				Set<ShippingMethod> methods = context.getCompany()
						.getShippingMethods();
				for (ShippingMethod a : methods) {
					list.add(a);
				}
				return list;
			}

			@Override
			protected String getShowMessage() {
				return getConstants().shippingMethodList();
			}

			@Override
			protected String getEmptyString() {
				return getMessages().noRecordsToShow();
			}

			@Override
			protected String onSelection(ShippingMethod value) {
				return getConstants().update() + " " + value.getName();
			}
		});
	}

	@Override
	protected String getDetailsMessage() {
		return null;
	}

	@Override
	public String getSuccessMessage() {
		return null;
	}

	@Override
	protected String getWelcomeMessage() {
		return null;
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {
		return null;
	}

	@Override
	protected void setDefaultValues(Context context) {

	}

}
