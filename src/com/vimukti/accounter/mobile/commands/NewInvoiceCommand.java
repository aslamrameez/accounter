package com.vimukti.accounter.mobile.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.vimukti.accounter.core.Contact;
import com.vimukti.accounter.core.Currency;
import com.vimukti.accounter.core.Customer;
import com.vimukti.accounter.core.Invoice;
import com.vimukti.accounter.core.Item;
import com.vimukti.accounter.core.NumberUtils;
import com.vimukti.accounter.core.PaymentTerms;
import com.vimukti.accounter.core.TAXCode;
import com.vimukti.accounter.core.Transaction;
import com.vimukti.accounter.mobile.Context;
import com.vimukti.accounter.mobile.Requirement;
import com.vimukti.accounter.mobile.Result;
import com.vimukti.accounter.mobile.ResultList;
import com.vimukti.accounter.mobile.requirements.AddressRequirement;
import com.vimukti.accounter.mobile.requirements.AmountRequirement;
import com.vimukti.accounter.mobile.requirements.BooleanRequirement;
import com.vimukti.accounter.mobile.requirements.ChangeListner;
import com.vimukti.accounter.mobile.requirements.ContactRequirement;
import com.vimukti.accounter.mobile.requirements.CurrencyRequirement;
import com.vimukti.accounter.mobile.requirements.CustomerRequirement;
import com.vimukti.accounter.mobile.requirements.DateRequirement;
import com.vimukti.accounter.mobile.requirements.EstimatesAndSalesOrderTableRequirement;
import com.vimukti.accounter.mobile.requirements.NameRequirement;
import com.vimukti.accounter.mobile.requirements.NumberRequirement;
import com.vimukti.accounter.mobile.requirements.PaymentTermRequirement;
import com.vimukti.accounter.mobile.requirements.TaxCodeRequirement;
import com.vimukti.accounter.mobile.requirements.TransactionItemTableRequirement;
import com.vimukti.accounter.mobile.utils.CommandUtils;
import com.vimukti.accounter.services.DAOException;
import com.vimukti.accounter.web.client.core.AccounterCoreType;
import com.vimukti.accounter.web.client.core.ClientAddress;
import com.vimukti.accounter.web.client.core.ClientCompanyPreferences;
import com.vimukti.accounter.web.client.core.ClientCurrency;
import com.vimukti.accounter.web.client.core.ClientCustomer;
import com.vimukti.accounter.web.client.core.ClientEstimate;
import com.vimukti.accounter.web.client.core.ClientFinanceDate;
import com.vimukti.accounter.web.client.core.ClientInvoice;
import com.vimukti.accounter.web.client.core.ClientSalesOrder;
import com.vimukti.accounter.web.client.core.ClientTransaction;
import com.vimukti.accounter.web.client.core.ClientTransactionItem;
import com.vimukti.accounter.web.client.core.Lists.EstimatesAndSalesOrdersList;
import com.vimukti.accounter.web.client.exception.AccounterException;
import com.vimukti.accounter.web.server.FinanceTool;

/**
 * 
 * @author Sai Prasad N
 * 
 */
public class NewInvoiceCommand extends NewAbstractTransactionCommand {

	private static final String ESTIMATEANDSALESORDER = "estimateAndSalesOrder";
	private static final String PAYMENT_TERMS = "paymentTerms";
	private static final String DUE_DATE = "duedate";
	private static final String ORDER_NO = "orderNo";

	private ClientInvoice invoice;

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getWelcomeMessage() {
		return invoice.getID() == 0 ? "Creating new invoice... "
				: "Updating invoice";
	}

	@Override
	protected void addRequirements(List<Requirement> list) {

		list.add(new CustomerRequirement(CUSTOMER,
				"Please Enter Customer name or number to set InvoiceCustomer",
				"Customer", false, true, new ChangeListner<Customer>() {

					@Override
					public void onSelection(Customer value) {
						NewInvoiceCommand.this.get(CONTACT).setValue(null);
						for (Contact contact : value.getContacts()) {
							if (contact.isPrimary()) {
								NewInvoiceCommand.this.get(CONTACT).setValue(
										contact);
								break;
							}
						}
					}
				}) {

			@Override
			protected List<Customer> getLists(Context context) {
				return new ArrayList<Customer>(context.getCompany()
						.getCustomers());
			}
		});

		list.add(new CurrencyRequirement(CURRENCY, getMessages().pleaseSelect(
				getConstants().currency()), getConstants().currency(), true,
				true, null) {
			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				if (context.getPreferences().isEnableMultiCurrency()) {
					return super.run(context, makeResult, list, actions);
				} else {
					return null;
				}
			}

			@Override
			protected List<Currency> getLists(Context context) {
				return new ArrayList<Currency>(context.getCompany()
						.getCurrencies());
			}
		});

		list.add(new AmountRequirement(CURRENCY_FACTOR, getMessages()
				.pleaseSelect(getConstants().currency()), getConstants()
				.currency(), false, true) {
			@Override
			protected String getDisplayValue(Double value) {
				ClientCurrency primaryCurrency = getPreferences()
						.getPrimaryCurrency();
				Currency selc = get(CURRENCY).getValue();
				return "1 " + selc.getFormalName() + " = " + value + " "
						+ primaryCurrency.getFormalName();
			}

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				if (get(CURRENCY).getValue() != null) {
					if (context.getPreferences().isEnableMultiCurrency()
							&& !((Currency) get(CURRENCY).getValue())
									.equals(context.getCompany()
											.getPrimaryCurrency())) {
						return super.run(context, makeResult, list, actions);
					}
				}
				return null;
			}
		});

		list.add(new TransactionItemTableRequirement(ITEMS,
				"Please Enter Item Name or number", getConstants().items(),
				false, true, true) {

			@Override
			public List<Item> getItems(Context context) {
				Set<Item> items2 = context.getCompany().getItems();
				ArrayList<Item> items = new ArrayList<Item>();
				for (Item item : items2) {
					if (item.getType() == Item.TYPE_SERVICE) {
						items.add(item);
					}
				}
				return items;
			}
		});

		list.add(new DateRequirement(DATE, getMessages().pleaseEnter(
				getConstants().transactionDate()), getConstants()
				.transactionDate(), true, true));

		list.add(new NumberRequirement(NUMBER, getMessages().pleaseEnter(
				getConstants().number()), getConstants().number(), true, true));

		list.add(new PaymentTermRequirement(PAYMENT_TERMS, getMessages()
				.pleaseSelect(getConstants().paymentTerm()), getConstants()
				.paymentTerm(), true, true, null) {

			@Override
			public List<PaymentTerms> getLists(Context context) {
				return new ArrayList<PaymentTerms>(context.getCompany()
						.getPaymentTerms());
			}
		});

		list.add(new ContactRequirement(CONTACT, "Enter contact name",
				"Contact", true, true, null) {

			@Override
			protected List<Contact> getLists(Context context) {
				return new ArrayList<Contact>(
						((Customer) NewInvoiceCommand.this.get(CUSTOMER)
								.getValue()).getContacts());
			}

			@Override
			protected String getContactHolderName() {
				return ((Customer) get(CUSTOMER).getValue()).getName();
			}
		});

		list.add(new AddressRequirement(BILL_TO, getMessages().pleaseEnter(
				getConstants().billTo()), getConstants().billTo(), true, true));

		list.add(new DateRequirement(DUE_DATE, getMessages().pleaseEnter(
				getConstants().dueDate()), getConstants().dueDate(), true, true));

		list.add(new NumberRequirement(ORDER_NO, getMessages().pleaseEnter(
				getConstants().orderNo()), getConstants().orderNo(), true, true));

		list.add(new NameRequirement(MEMO, getMessages().pleaseEnter(
				getConstants().memo()), getConstants().memo(), true, true));

		list.add(new EstimatesAndSalesOrderTableRequirement(
				ESTIMATEANDSALESORDER, getMessages().selectTypeOfThis(
						getConstants().quote()), getConstants()
						.quoteAndSalesOrderList(), true, true) {

			@Override
			protected Customer getCustomer() {
				return (Customer) NewInvoiceCommand.this.get(CUSTOMER)
						.getValue();
			}
		});

		// list.add(new EstimatesAndSalesOrderListRequirement(
		// ESTIMATEANDSALESORDER, getMessages().selectTypeOfThis(
		// getConstants().quote()), getConstants()
		// .quoteAndSalesOrderList(), true, true, null) {
		//
		// @Override
		// protected List<EstimatesAndSalesOrdersList> getLists(Context context)
		// {
		// try {
		// return new FinanceTool().getCustomerManager()
		// .getEstimatesAndSalesOrdersList(
		// ((ClientCustomer) get(CUSTOMER).getValue())
		// .getID(),
		// context.getCompany().getID());
		// } catch (DAOException e) {
		// e.printStackTrace();
		// }
		// return null;
		// }
		//
		// @Override
		// protected boolean filter(EstimatesAndSalesOrdersList e, String name)
		// {
		// return e.getName().contains(name);
		// }
		// });

		list.add(new TaxCodeRequirement(TAXCODE, getMessages().pleaseSelect(
				getConstants().taxCode()), getConstants().taxCode(), false,
				true, null) {

			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				if (context.getPreferences().isTrackTax()
						&& !context.getPreferences().isTaxPerDetailLine()) {
					return super.run(context, makeResult, list, actions);
				}
				return null;
			}

			@Override
			protected List<TAXCode> getLists(Context context) {
				return new ArrayList<TAXCode>(context.getCompany()
						.getTaxCodes());
			}

			@Override
			protected boolean filter(TAXCode e, String name) {
				return e.getName().contains(name);
			}
		});

		list.add(new BooleanRequirement(IS_VAT_INCLUSIVE, true) {
			@Override
			public Result run(Context context, Result makeResult,
					ResultList list, ResultList actions) {
				ClientCompanyPreferences preferences = context.getPreferences();
				if (preferences.isTrackTax()
						&& !preferences.isTaxPerDetailLine()) {
					return super.run(context, makeResult, list, actions);
				}
				return null;
			}

			@Override
			protected String getTrueString() {
				return "Include VAT with Amount enabled";
			}

			@Override
			protected String getFalseString() {
				return "Include VAT with Amount disabled";
			}
		});
	}

	private ClientEstimate getEstimate(long transactionId, Context context) {
		ClientEstimate cEstimate = null;
		try {
			cEstimate = new FinanceTool().getManager().getObjectById(
					AccounterCoreType.ESTIMATE, transactionId,
					context.getCompany().getID());
		} catch (DAOException e1) {
			e1.printStackTrace();
		} catch (AccounterException e1) {
			e1.printStackTrace();
		}
		return cEstimate;
	}

	private ClientSalesOrder getSalesOrder(long transactionId, Context context) {
		ClientSalesOrder cSalesOrder = null;
		try {
			cSalesOrder = new FinanceTool().getManager().getObjectById(
					AccounterCoreType.SALESORDER, transactionId,
					context.getCompany().getID());
		} catch (DAOException e1) {
			e1.printStackTrace();
		} catch (AccounterException e1) {
			e1.printStackTrace();
		}
		return cSalesOrder;
	}

	@Override
	protected Result onCompleteProcess(Context context) {
		ClientFinanceDate date = get(DATE).getValue();
		invoice.setDate(date.getDate());

		invoice.setType(Transaction.TYPE_INVOICE);

		String number = get(NUMBER).getValue();
		invoice.setNumber(number);

		List<ClientTransactionItem> items = get(ITEMS).getValue();

		Customer customer = get(CUSTOMER).getValue();
		invoice.setCustomer(customer.getID());

		ClientFinanceDate dueDate = get(DUE_DATE).getValue();
		invoice.setDueDate(dueDate.getDate());

		Contact contact = get(CONTACT).getValue();
		invoice.setContact(toClientContact(contact));

		ClientAddress billTo = get(BILL_TO).getValue();
		invoice.setBillingAddress(billTo);

		PaymentTerms paymentTerm = get(PAYMENT_TERMS).getValue();
		invoice.setPaymentTerm(paymentTerm.getID());

		String orderNo = get(ORDER_NO).getValue();
		invoice.setOrderNum(orderNo);

		String memo = get(MEMO).getValue();
		invoice.setMemo(memo);
		invoice.setStatus(Invoice.STATUS_OPEN);
		// Adding selecting estimate or salesOrder to Invoice
		invoice.setCurrencyFactor(1);
		ClientCompanyPreferences preferences = context.getPreferences();
		if (preferences.isEnableMultiCurrency()) {
			Currency currency = get(CURRENCY).getValue();
			if (currency != null) {
				invoice.setCurrency(currency.getID());
			}

			double factor = get(CURRENCY_FACTOR).getValue();
			invoice.setCurrencyFactor(factor);
		}

		List<EstimatesAndSalesOrdersList> e = get(ESTIMATEANDSALESORDER)
				.getValue();
		List<ClientEstimate> estimates = new ArrayList<ClientEstimate>();
		List<ClientSalesOrder> salesOrders = new ArrayList<ClientSalesOrder>();
		for (EstimatesAndSalesOrdersList estimatesAndSalesOrdersList : e) {
			if (e != null) {
				if (estimatesAndSalesOrdersList.getType() == ClientTransaction.TYPE_ESTIMATE) {
					ClientEstimate cct = getEstimate(
							estimatesAndSalesOrdersList.getTransactionId(),
							context);
					estimates.add(cct);
				} else {
					ClientSalesOrder cSalesOrder = getSalesOrder(
							estimatesAndSalesOrdersList.getTransactionId(),
							context);
					salesOrders.add(cSalesOrder);
				}
			}
		}
		invoice.setEstimates(estimates);
		invoice.setSalesOrders(salesOrders);
		Boolean isVatInclusive = get(IS_VAT_INCLUSIVE).getValue();
		if (preferences.isTrackTax() && !preferences.isTaxPerDetailLine()) {
			invoice.setAmountsIncludeVAT(isVatInclusive);
			TAXCode taxCode = get(TAXCODE).getValue();
			for (ClientTransactionItem item : items) {
				item.setTaxCode(taxCode.getID());
			}
		}

		invoice.setTransactionItems(items);
		double taxTotal = updateTotals(context, invoice, true);
		double totalAmount = 0.0;
		double totalNetAmount = 0.0;
		for (ClientSalesOrder clientSalesOrder : salesOrders) {
			totalAmount += clientSalesOrder.getTotal();
			totalNetAmount += clientSalesOrder.getNetAmount();
			taxTotal += clientSalesOrder.getTaxTotal();
		}
		for (ClientEstimate clientEstimate : estimates) {
			if (clientEstimate.getEstimateType() == ClientEstimate.CREDITS) {
				totalAmount -= clientEstimate.getTotal();
				totalNetAmount -= clientEstimate.getNetAmount();
				taxTotal -= clientEstimate.getTaxTotal();
			} else {
				totalAmount += clientEstimate.getTotal();
				totalNetAmount += clientEstimate.getNetAmount();
				taxTotal += clientEstimate.getTaxTotal();
			}
		}
		invoice.setNetAmount(invoice.getNetAmount() + totalNetAmount);
		invoice.setTotal(invoice.getTotal() + totalAmount);
		invoice.setTaxTotal(taxTotal);
		create(invoice, context);
		return null;
	}

	@Override
	protected String getDetailsMessage() {
		return invoice.getID() == 0 ? getMessages().readyToCreate(
				getConstants().invoice())
				: "Invoice ready to update with follwoing details";
	}

	@Override
	protected void setDefaultValues(Context context) {
		get(DATE).setDefaultValue(new ClientFinanceDate());
		get(NUMBER).setDefaultValue(
				NumberUtils.getNextTransactionNumber(
						ClientTransaction.TYPE_INVOICE, context.getCompany()));
		get(CONTACT).setDefaultValue(null);
		Set<PaymentTerms> paymentTerms = context.getCompany().getPaymentTerms();
		for (PaymentTerms p : paymentTerms) {
			if (p.getName().equals("Due on Receipt")) {
				get(PAYMENT_TERMS).setDefaultValue(p);
			}
		}
		get(DUE_DATE).setDefaultValue(new ClientFinanceDate());
		get(IS_VAT_INCLUSIVE).setDefaultValue(false);
		get(CURRENCY).setDefaultValue(null);
		get(CURRENCY_FACTOR).setDefaultValue(1.0);
	}

	@Override
	public String getSuccessMessage() {
		return invoice.getID() == 0 ? getMessages().createSuccessfully(
				getConstants().invoice()) : getMessages().updateSuccessfully(
				getConstants().invoice());
	}

	@Override
	public void beforeFinishing(Context context, Result makeResult) {
		// TODO
		List<ClientTransactionItem> allrecords = get(ITEMS).getValue();
		ClientCompanyPreferences preferences = context.getPreferences();
		if (preferences.isTrackTax() && !preferences.isTaxPerDetailLine()) {
			TAXCode taxCode = get(TAXCODE).getValue();
			for (ClientTransactionItem item : allrecords) {
				if (taxCode != null) {
					item.setTaxCode(taxCode.getID());
				}
			}
		}

		Boolean isVatInclusive = get(IS_VAT_INCLUSIVE).getValue();
		double[] result = getTransactionTotal(context, isVatInclusive,
				allrecords, true);
		List<EstimatesAndSalesOrdersList> e = get(ESTIMATEANDSALESORDER)
				.getValue();
		for (EstimatesAndSalesOrdersList estimatesAndSalesOrdersList : e) {
			if (e != null) {
				if (estimatesAndSalesOrdersList.getType() == ClientTransaction.TYPE_ESTIMATE) {
					ClientEstimate clientEstimate = getEstimate(
							estimatesAndSalesOrdersList.getTransactionId(),
							context);
					if (clientEstimate.getEstimateType() == ClientEstimate.CREDITS) {
						result[0] -= clientEstimate.getNetAmount();
						result[1] -= clientEstimate.getTaxTotal();
					} else {
						result[0] += clientEstimate.getNetAmount();
						result[1] += clientEstimate.getTaxTotal();
					}
				} else {
					ClientSalesOrder cSalesOrder = getSalesOrder(
							estimatesAndSalesOrdersList.getTransactionId(),
							context);
					result[0] += cSalesOrder.getNetAmount();
					result[1] += cSalesOrder.getTaxTotal();
				}
			}
		}
		if (context.getPreferences().isTrackTax()) {
			makeResult.add("Total Tax: " + result[1]);
		}
		makeResult.add("Total: " + (result[0] + result[1]));
	}

	@Override
	protected String initObject(Context context, boolean isUpdate) {

		if (isUpdate) {
			String string = context.getString();
			if (string.isEmpty()) {
				addFirstMessage(context, "Select an Invoice to update.");
				return "Invoices List";
			}
			ClientInvoice invoiceByNum = (ClientInvoice) CommandUtils
					.getClientTransactionByNumber(context.getCompany(),
							getNumberFromString(string));
			if (invoiceByNum == null) {
				addFirstMessage(context, "Select an Invoice to update.");
				return "Invoices List " + string;
			}
			invoice = invoiceByNum;
			setValues();
		} else {
			String string = context.getString();
			if (!string.isEmpty()) {
				get(NUMBER).setValue(string);
			}
			invoice = new ClientInvoice();
		}
		return null;
	}

	private void setValues() {
		get(DATE).setValue(invoice.getDate());
		get(NUMBER).setValue(invoice.getNumber());
		get(ITEMS).setValue(invoice.getTransactionItems());
		get(CUSTOMER).setValue(
				CommandUtils.getServerObjectById(invoice.getCustomer(),
						AccounterCoreType.CUSTOMER));
		get(DUE_DATE).setValue(new ClientFinanceDate(invoice.getDueDate()));
		get(CONTACT).setValue(toServerContact(invoice.getContact()));
		get(BILL_TO).setValue(invoice.getBillingAddress());
		get(PAYMENT_TERMS).setValue(
				CommandUtils.getServerObjectById(invoice.getPaymentTerm(),
						AccounterCoreType.PAYMENT_TERM));
		get(ORDER_NO).setValue(invoice.getOrderNum());
		get(MEMO).setValue(invoice.getMemo());
		get(CURRENCY_FACTOR).setValue(invoice.getCurrencyFactor());

		List<EstimatesAndSalesOrdersList> e = getEstimatesSalesOrderList();
		get(ESTIMATEANDSALESORDER).setValue(e);
		get(IS_VAT_INCLUSIVE).setValue(invoice.isAmountsIncludeVAT());
	}

	private List<EstimatesAndSalesOrdersList> getEstimatesSalesOrderList() {
		List<EstimatesAndSalesOrdersList> list = new ArrayList<EstimatesAndSalesOrdersList>();
		List<ClientEstimate> estimates = invoice.getEstimates();
		if (estimates == null) {
			return list;
		}
		for (ClientEstimate clientEstimate : estimates) {
			EstimatesAndSalesOrdersList el = new EstimatesAndSalesOrdersList();
			el.setTransactionId(clientEstimate.getID());
			el.setType(clientEstimate.getType());
			el.setTransactionNumber(clientEstimate.getNumber());
			el.setTotal(clientEstimate.getTotal());
			el.setDate(clientEstimate.getDate());
			ClientCustomer clientObjectById = (ClientCustomer) CommandUtils
					.getClientObjectById(clientEstimate.getCustomer(),
							AccounterCoreType.CUSTOMER, getCompanyId());
			el.setCustomerName(clientObjectById != null ? clientObjectById
					.getName() : "");
			el.setEstimateType(clientEstimate.getEstimateType());
			list.add(el);
		}

		return list;
	}

}