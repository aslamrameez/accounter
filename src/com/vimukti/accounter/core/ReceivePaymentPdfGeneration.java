package com.vimukti.accounter.core;

import java.util.ArrayList;
import java.util.List;

import com.vimukti.accounter.web.client.ui.DataUtils;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

public class ReceivePaymentPdfGeneration {

	private ReceivePayment receive;
	private Company company;

	public ReceivePaymentPdfGeneration(ReceivePayment receive, Company company) {
		this.receive = receive;
		this.company = company;
	}

	public IContext assignValues(IContext context, IXDocReport report) {

		try {
			ReceivePaymentTemplate receivePaymentTmplt = new ReceivePaymentTemplate();

			receivePaymentTmplt.setTitle("Receive Payment");
			receivePaymentTmplt.setChequeOrRefNo(receive.getCheckNumber());
			receivePaymentTmplt.setDateReceived(receive.getDate().toString());
			String currencySymbol = receive.getCurrency().getSymbol();
			receivePaymentTmplt.setPayAmount(DataUtils
					.getAmountAsStringInCurrency(receive.getTotal(),
							currencySymbol));
			receivePaymentTmplt.setPaymentMethod(receive.getPaymentMethod());
			receivePaymentTmplt
					.setReceivedFrom(receive.getCustomer().getName());
			receivePaymentTmplt.setRegisteredAddress(company
					.getRegisteredAddress().toString());

			FieldsMetadata headersMetaData = new FieldsMetadata();
			headersMetaData.addFieldAsList("invoice.invoiceDate");
			headersMetaData.addFieldAsList("invoice.invoiceNumber");
			headersMetaData.addFieldAsList("invoice.amountApplied");
			report.setFieldsMetadata(headersMetaData);
			List<UsedInvoiceTemplate> invoices = new ArrayList<UsedInvoiceTemplate>();
			List<TransactionReceivePayment> transactionReceivePayments = receive
					.getTransactionReceivePayment();
			for (TransactionReceivePayment payment : transactionReceivePayments) {
				UsedInvoiceTemplate invoiceTemplate = new UsedInvoiceTemplate();
				if (payment.getInvoice() != null) {
					invoiceTemplate.setAmountApplied(DataUtils
							.getAmountAsStringInCurrency(payment.getInvoice()
									.getPayments(), currencySymbol));
					invoiceTemplate.setInvoiceDate(payment.getInvoice()
							.getDate().toString());
					invoiceTemplate.setInvoiceNumber(payment.getInvoice()
							.getNumber());
				}
				if (payment.getJournalEntry() != null) {
					JournalEntry journalEntry = payment.getJournalEntry();
					invoiceTemplate.setAmountApplied(DataUtils
							.getAmountAsStringInCurrency(
									journalEntry.getTotal()
											- journalEntry.getBalanceDue(),
									currencySymbol));
					invoiceTemplate.setInvoiceDate(journalEntry.getDate()
							.toString());
					invoiceTemplate.setInvoiceNumber(journalEntry.getNumber());
				}

				if (payment.getCustomerRefund() != null) {
					CustomerRefund customerRefund = payment.getCustomerRefund();
					invoiceTemplate.setAmountApplied(DataUtils
							.getAmountAsStringInCurrency(
									customerRefund.getPayments(),
									currencySymbol));
					invoiceTemplate.setInvoiceDate(customerRefund.getDate()
							.toString());
					invoiceTemplate
							.setInvoiceNumber(customerRefund.getNumber());
				}
				invoices.add(invoiceTemplate);
			}

			context.put("receive", receivePaymentTmplt);
			context.put("invoice", invoices);
			return context;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public class ReceivePaymentTemplate {
		private String title;

		private String registeredAddress;

		private String receivedFrom;

		private String dateReceived;

		private String paymentMethod;

		private String payAmount;

		private String chequeOrRefNo;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getRegisteredAddress() {
			return registeredAddress;
		}

		public void setRegisteredAddress(String registeredAddress) {
			this.registeredAddress = registeredAddress;
		}

		public String getReceivedFrom() {
			return receivedFrom;
		}

		public void setReceivedFrom(String receivedFrom) {
			this.receivedFrom = receivedFrom;
		}

		public String getDateReceived() {
			return dateReceived;
		}

		public void setDateReceived(String dateReceived) {
			this.dateReceived = dateReceived;
		}

		public String getPaymentMethod() {
			return paymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			this.paymentMethod = paymentMethod;
		}

		public String getPayAmount() {
			return payAmount;
		}

		public void setPayAmount(String payAmount) {
			this.payAmount = payAmount;
		}

		public String getChequeOrRefNo() {
			return chequeOrRefNo;
		}

		public void setChequeOrRefNo(String chequeOrRefNo) {
			this.chequeOrRefNo = chequeOrRefNo;
		}
	}

	public class UsedInvoiceTemplate {
		private String invoiceDate;

		private String invoiceNumber;

		private String amountApplied;

		public String getInvoiceDate() {
			return invoiceDate;
		}

		public void setInvoiceDate(String invoiceDate) {
			this.invoiceDate = invoiceDate;
		}

		public String getInvoiceNumber() {
			return invoiceNumber;
		}

		public void setInvoiceNumber(String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
		}

		public String getAmountApplied() {
			return amountApplied;
		}

		public void setAmountApplied(String amountApplied) {
			this.amountApplied = amountApplied;
		}
	}
}