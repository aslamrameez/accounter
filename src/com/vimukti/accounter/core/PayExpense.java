package com.vimukti.accounter.core;

import java.util.List;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.json.JSONException;

import com.vimukti.accounter.web.client.Global;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.client.externalization.AccounterMessages;

public class PayExpense extends Transaction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8470751684678534464L;

	/**
	 * Paid From {@link Account}
	 */
	Account paidFrom;

	/**
	 * Reference or Cheque Number for which, the PayExpense Transaction has
	 * been, associated
	 */
	String refernceOrChequeNumber;

	/**
	 * List of {@link TransactionPayExpense}'s
	 */
	List<TransactionPayExpense> transactionPayExpenses;

	//

	public Account getPaidFrom() {
		return paidFrom;
	}

	public void setPaidFrom(Account paidFrom) {
		this.paidFrom = paidFrom;
	}

	public String getRefernceOrChequeNumber() {
		return refernceOrChequeNumber;
	}

	public void setRefernceOrChequeNumber(String refernceOrChequeNumber) {
		this.refernceOrChequeNumber = refernceOrChequeNumber;
	}

	/**
	 * @return the transactionPayExpenses
	 */
	public List<TransactionPayExpense> getTransactionPayExpenses() {
		return transactionPayExpenses;
	}

	/**
	 * @param transactionPayExpenses
	 *            the transactionPayExpenses to set
	 */
	public void setTransactionPayExpenses(
			List<TransactionPayExpense> transactionPayExpenses) {
		this.transactionPayExpenses = transactionPayExpenses;
	}

	@Override
	public Account getEffectingAccount() {
		return this.paidFrom;
	}

	@Override
	public Payee getPayee() {
		return null;
	}

	@Override
	public boolean isDebitTransaction() {
		return true;
	}

	@Override
	public boolean isPositiveTransaction() {
		return false;
	}

	@Override
	public boolean onSave(Session session) throws CallbackException {

		if (this.isOnSaveProccessed)
			return true;
		this.isOnSaveProccessed = true;
		super.onSave(session);
		Account accountPayable = getCompany().getAccountsPayableAccount();
		accountPayable.updateCurrentBalance(this, -this.total, currencyFactor);
		session.update(accountPayable);
		accountPayable.onUpdate(session);

		return false;
	}

	@Override
	public int getTransactionCategory() {
		return 0;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public Payee getInvolvedPayee() {

		return null;
	}

	@Override
	public void onEdit(Transaction clonedObject) {

	}

	@Override
	public boolean canEdit(IAccounterServerCore clientObject)
			throws AccounterException {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Map<Account, Double> getEffectingAccountsWithAmounts() {
		Map<Account, Double> map = super.getEffectingAccountsWithAmounts();
		map.put(getCompany().getAccountsPayableAccount(), total);
		return map;
	}

	@Override
	public void writeAudit(AuditWriter w) throws JSONException {
		if (getSaveStatus() == STATUS_DRAFT) {
			return;
		}
		
		AccounterMessages messages = Global.get().messages();

		w.put(messages.type(), messages.payExpenses()).gap();
		w.put(messages.no(), this.number);
		w.put(messages.date(), this.transactionDate.toString()).gap();
		w.put(messages.currency(), this.currencyFactor);
		w.put(messages.amount(), this.total).gap();
		w.put(messages.paymentMethod(), this.paymentMethod).gap();
		w.put(messages.memo(), this.memo);

	}

	@Override
	protected void updatePayee(boolean onCreate) {
		// TODO Auto-generated method stub

	}

}
