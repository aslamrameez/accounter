/**
 * 
 */
package com.vimukti.accounter.web.client.ui.company.options;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author vimukti36
 * 
 */
public class QuantityOnHandOption extends AbstractPreferenceOption {

	private static QuantityOnHandOptionUiBinder uiBinder = GWT
			.create(QuantityOnHandOptionUiBinder.class);
	@UiField
	CheckBox quantityOnHAndOptionCheckBox;

	interface QuantityOnHandOptionUiBinder extends
			UiBinder<Widget, QuantityOnHandOption> {
	}

	/**
	 * Because this class has a default constructor, it can be used as a binder
	 * template. In other words, it can be used in other *.ui.xml files as
	 * follows: <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 * xmlns:g="urn:import:**user's package**">
	 * <g:**UserClassName**>Hello!</g:**UserClassName> </ui:UiBinder> Note that
	 * depending on the widget that is used, it may be necessary to implement
	 * HasHTML instead of HasText.
	 */
	public QuantityOnHandOption() {
		initWidget(uiBinder.createAndBindUi(this));
		createControls();
	}

	private void createControls() {
		quantityOnHAndOptionCheckBox.setText(constants.quantityonhand());
	}

	public QuantityOnHandOption(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAnchor() {
		// TODO Auto-generated method stub
		return null;
	}

}
