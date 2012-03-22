package com.vimukti.accounter.web.client.languages;

import java.util.Locale;

import com.ibm.icu.text.RuleBasedNumberFormat;

public class Portuguese implements Ilanguage {

	@Override
	public String getAmountAsString(double amount) {
		Locale l = new Locale("pt");
		RuleBasedNumberFormat rbf = new RuleBasedNumberFormat(l,
				RuleBasedNumberFormat.SPELLOUT);
		String[] ruleSetNames = rbf.getRuleSetNames();
		String format = rbf.format(amount, ruleSetNames[4]);
		return format;
	}

}