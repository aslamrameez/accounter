/**
 * 
 */
package com.vimukti.accounter.web.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.vimukti.accounter.web.client.core.ClientCompanyPreferences;
import com.vimukti.accounter.web.client.core.TemplateAccount;

/**
 * @author Prasanna Kumar G
 * 
 */
public interface IAccounterCompanyInitializationServiceAsync {

	void initalizeCompany(ClientCompanyPreferences preferences,
			List<TemplateAccount> accountsTemplates,
			AsyncCallback<Boolean> callback);
}
