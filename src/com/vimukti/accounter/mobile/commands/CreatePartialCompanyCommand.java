package com.vimukti.accounter.mobile.commands;

import java.util.List;

import com.vimukti.accounter.mobile.Requirement;

public class CreatePartialCompanyCommand extends AbstractCompanyCommad {

	@Override
	protected void addRequirements(List<Requirement> list) {
		// First page
		super.addRequirements(list);

		addCurrencyRequirements(list);

		list.add(getFiscalYearRequirement());
	}

	@Override
	protected String getDetailsMessage() {
		return getMessages().readyToCreate(getMessages().company());
	}

	@Override
	public String getSuccessMessage() {
		return getMessages().createSuccessfully(getMessages().company());
	}

	@Override
	protected String getWelcomeMessage() {
		return getMessages().creating(getMessages().partialCompanySetup());
	}
}
