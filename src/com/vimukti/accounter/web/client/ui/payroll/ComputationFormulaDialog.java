package com.vimukti.accounter.web.client.ui.payroll;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.vimukti.accounter.web.client.ValueCallBack;
import com.vimukti.accounter.web.client.core.AddNewButton;
import com.vimukti.accounter.web.client.core.ClientComputaionFormulaFunction;
import com.vimukti.accounter.web.client.ui.core.BaseDialog;
import com.vimukti.accounter.web.client.ui.forms.DynamicForm;

public class ComputationFormulaDialog extends
		BaseDialog<ClientComputaionFormulaFunction> {

	private DynamicForm mainForm;

	private ValueCallBack<List<ClientComputaionFormulaFunction>> successCallback;
	private ComputationFormulaTable table;
	private AddNewButton itemTableButton;

	public ComputationFormulaDialog(String string) {
		super(string);
		createControls();
	}

	public void createControls() {
		mainForm = new DynamicForm("mainForm");

		table = new ComputationFormulaTable();
		itemTableButton = new AddNewButton();
		itemTableButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ClientComputaionFormulaFunction row = new ClientComputaionFormulaFunction();
				table.add(row);
			}
		});

		mainForm.add(table);
		mainForm.add(itemTableButton);

		bodyLayout.add(mainForm);
	}

	@Override
	protected boolean onOK() {
		if (successCallback != null) {
			successCallback.execute(createFunctions());
		}
		return true;
	}

	public void addCallback(
			ValueCallBack<List<ClientComputaionFormulaFunction>> successCallback) {
		this.successCallback = successCallback;
	}

	private List<ClientComputaionFormulaFunction> createFunctions() {
		List<ClientComputaionFormulaFunction> functions = new ArrayList<ClientComputaionFormulaFunction>();
		functions = table.getAllRows();
		return functions;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setData(List<ClientComputaionFormulaFunction> formulas) {
		if (table == null) {
			return;
		}
		table.setAllRows(formulas);
	}

}
