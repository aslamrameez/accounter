package com.vimukti.accounter.web.client.ui.combo;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.vimukti.accounter.web.client.core.ClientUser;
import com.vimukti.accounter.web.client.core.ClientUserInfo;
import com.vimukti.accounter.web.client.ui.Accounter;

public class EmployeeCombo extends CustomCombo<ClientUserInfo> {

	public List<ClientUserInfo> users = new ArrayList<ClientUserInfo>();
	private boolean isAdmin;

	public EmployeeCombo(String title) {
		super(title, false, 1);
		Accounter.createHomeService().getAllUsers(
				new AsyncCallback<List<ClientUserInfo>>() {

					@Override
					public void onSuccess(List<ClientUserInfo> result) {
						users = result;
						if (isAdmin) {
							initCombo(users);
						} else {
							for (ClientUserInfo user : users) {
								if (user.getId() == Accounter.getUser().getID()) {
									List<ClientUserInfo> tempUsers = new ArrayList<ClientUserInfo>();
									tempUsers.add(user);
									initCombo(tempUsers);
									break;
								}
							}
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof InvocationException) {
							Accounter
									.showMessage("Your session expired, Please login again to continue");
						} else {
							Accounter.showError("Failed to load users list");
						}
					}
				});
	}

	@Override
	public SelectItemType getSelectItemType() {
		return null;
	}

	@Override
	protected String getDisplayName(ClientUserInfo object) {
		if (object != null)
			return object.getDisplayName() != null ? object.getDisplayName(): "";
		else
			return "";
	}

	@Override
	protected String getColumnData(ClientUserInfo object, int row, int col) {
		switch (col) {
		case 0:
			return object.getDisplayName();
		}
		return null;
	}

	@Override
	public String getDefaultAddNewCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAddNew() {
		// TODO Auto-generated method stub

	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
		if (isAdmin) {
			initCombo(users);
		} else {
			for (ClientUserInfo user : users) {
				if (user.getId() == Accounter.getUser().getID()) {
					List<ClientUserInfo> tempUsers = new ArrayList<ClientUserInfo>();
					tempUsers.add(user);
					initCombo(tempUsers);
					break;
				}
			}
		}
	}

}
