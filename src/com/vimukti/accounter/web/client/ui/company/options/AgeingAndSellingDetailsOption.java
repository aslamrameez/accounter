package com.vimukti.accounter.web.client.ui.company.options;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class AgeingAndSellingDetailsOption extends AbstractPreferenceOption {

	private static AgeingAndSellingDetailsOptionUiBinder uiBinder = GWT
			.create(AgeingAndSellingDetailsOptionUiBinder.class);
	@UiField
	Label ageingLabel;
	@UiField
	RadioButton ageingforduedateRadioButton;
	@UiField
	RadioButton ageingfortransactiondateRadioButton;
	@UiField
	Label sellingsLabel;
	@UiField
	RadioButton servicesRadioButton;
	@UiField
	RadioButton productsRadioButton;
	@UiField
	RadioButton bothRadioButton;

	interface AgeingAndSellingDetailsOptionUiBinder extends
			UiBinder<Widget, AgeingAndSellingDetailsOption> {
	}

	public AgeingAndSellingDetailsOption() {
		initWidget(uiBinder.createAndBindUi(this));
		createControls();
		initData();
	}

	private void initData() {
		if (companyPreferences.getAgeingFromTransactionDateORDueDate() == 1) {
			ageingforduedateRadioButton.setValue(true);
			ageingfortransactiondateRadioButton.setValue(false);
		} else {
			ageingforduedateRadioButton.setValue(false);
			ageingfortransactiondateRadioButton.setValue(true);
		}
		if (companyPreferences.isSellServices()
				&& companyPreferences.isSellProducts()) {
			bothRadioButton.setValue(true);
			servicesRadioButton.setValue(false);
			productsRadioButton.setValue(false);
		} else if (companyPreferences.isSellServices()) {
			bothRadioButton.setValue(false);
			servicesRadioButton.setValue(true);
			productsRadioButton.setValue(false);
		} else {
			bothRadioButton.setValue(false);
			servicesRadioButton.setValue(false);
			productsRadioButton.setValue(true);
		}
	}

	private void createControls() {

		ageingforduedateRadioButton.setText(constants.ageingforduedate());
		ageingfortransactiondateRadioButton.setText(constants
				.ageingfortransactiondate());

		ageingLabel.setText(constants.ageingDetails());
		sellingsLabel.setText(constants.sellingDetails());

		servicesRadioButton.setText(constants.services());
		productsRadioButton.setText(constants.products());
		bothRadioButton.setText(constants.bothServiceProducts());

	}

	public AgeingAndSellingDetailsOption(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public String getTitle() {
		return constants.ageingAndSellingDetails();
	}

	@Override
	public void onSave() {
		if (ageingforduedateRadioButton.getValue())
			companyPreferences.setAgeingFromTransactionDateORDueDate(1);
		else
			companyPreferences.setAgeingFromTransactionDateORDueDate(2);

		if (bothRadioButton.getValue()) {
			companyPreferences.setSellServices(true);
			companyPreferences.setSellProducts(true);
		} else if (servicesRadioButton.getValue()) {
			companyPreferences.setSellServices(true);
			companyPreferences.setSellProducts(false);
		} else {
			companyPreferences.setSellProducts(true);
			companyPreferences.setSellServices(false);
		}
	}

	@Override
	public String getAnchor() {
		return constants.ageingAndSellingDetails();
	}

}
