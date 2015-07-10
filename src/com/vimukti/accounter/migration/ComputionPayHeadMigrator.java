package com.vimukti.accounter.migration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vimukti.accounter.core.Account;
import com.vimukti.accounter.core.ComputaionFormulaFunction;
import com.vimukti.accounter.core.ComputationSlab;
import com.vimukti.accounter.core.ComputionPayHead;

public class ComputionPayHeadMigrator implements IMigrator<ComputionPayHead> {

	@Override
	public JSONObject migrate(ComputionPayHead obj, MigratorContext context)
			throws JSONException {
		JSONObject payHead = new JSONObject();
		CommonFieldsMigrator.migrateCommonFields(obj, payHead, context);
		payHead.put("name", obj.getName());
		payHead.put("payHeadType",
				PicklistUtilMigrator.getPayHeadType(obj.getType()));
		payHead.put("isAffectNetSalary", obj.isAffectNetSalary());

		Account account = obj.getAccount();
		if (account != null) {
			JSONObject accountJson = new JSONObject();
			accountJson.put("name", account.getName());
			payHead.put("expenseAccount", accountJson);
		}
		payHead.put("calculationType", PicklistUtilMigrator
				.getCalculationType(obj.getCalculationType()));
		payHead.put("paySlipName", obj.getNameToAppearInPaySlip());
		payHead.put("isDeduction", obj.isDeduction());
		payHead.put("isEarning", obj.isEarning());

		// assert Account
		Account assetAccount = obj.getAssetAccount();
		if (assetAccount != null) {
			JSONObject accountJson = new JSONObject();
			accountJson.put("name", assetAccount.getName());
			payHead.put("assetAccount", accountJson);
		}
		// laibility Account
		Account liabilityAccount = obj.getLiabilityAccount();
		if (liabilityAccount != null) {
			JSONObject accountJson = new JSONObject();
			accountJson.put("name", liabilityAccount.getName());
			payHead.put("statutoryLiabilityAccount", accountJson);
		}

		payHead.put("calculationPeriod", PicklistUtilMigrator
				.getCalculationPeriod(obj.getCalculationPeriod()));

		payHead.put("perDayCalculationBasis", PicklistUtilMigrator
				.getPerdayCalculationBasis(obj.getCalculationPeriod()));

		payHead.put("computeOn", PicklistUtilMigrator.getComputationType(obj
				.getComputationType()));

		JSONArray jsonSlabs = new JSONArray();
		for (ComputationSlab slab : obj.getSlabs()) {
			JSONObject jsonSlab = new JSONObject();
			jsonSlab.put("effectiveFrom", slab.getEffectiveFrom()
					.getAsDateObject());
			jsonSlab.put("fromAmount", slab.getFromAmount());
			jsonSlab.put("amountUpto", slab.getToAmount());
			jsonSlab.put("slabType", PicklistUtilMigrator
					.getComputationSlabType(slab.getSlabType()));
			jsonSlab.put("value", slab.getValue());
			jsonSlabs.put(jsonSlab);
		}
		payHead.put("computationSlabs", jsonSlabs);
		JSONArray jsonformulaItems = new JSONArray();
		for (ComputaionFormulaFunction formulaItem : obj.getFormulaFunctions()) {
			JSONObject jsonFormula = new JSONObject();
			jsonFormula.put("functionType", PicklistUtilMigrator
					.getPayHeadFormulaFunctionType(formulaItem
							.getFunctionType()));
			jsonformulaItems.put(jsonFormula);
		}
		payHead.put("formulaItems", jsonformulaItems);
		return payHead;
	}
}
