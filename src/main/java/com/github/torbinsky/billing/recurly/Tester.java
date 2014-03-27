package com.github.torbinsky.billing.recurly;

import com.github.torbinsky.billing.recurly.model.Invoice;
import com.github.torbinsky.billing.recurly.model.list.Invoices;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeyAgnosticRecurlyClient client = new KeyAgnosticRecurlyClientImpl(); 
		client.open(); 
		Invoices accountInvoices = client.getAccountInvoices("ACCOUNT_2", "collected", "31f24d704b334e6cad86746ee7f7ef3a"); 
		for(Invoice i : accountInvoices.getObjects()){
			System.out.println(i.getInvoiceNumber() + " " + i.getState()); 
		}
	
	}
}
