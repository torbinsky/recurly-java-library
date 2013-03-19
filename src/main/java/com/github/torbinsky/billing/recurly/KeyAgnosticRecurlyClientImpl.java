/*
 * Copyright 2013 Torben Werner
 *
 * Torben Werner licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.torbinsky.billing.recurly;

import com.github.torbinsky.billing.recurly.model.Account;
import com.github.torbinsky.billing.recurly.model.Accounts;
import com.github.torbinsky.billing.recurly.model.AddOn;
import com.github.torbinsky.billing.recurly.model.BillingInfo;
import com.github.torbinsky.billing.recurly.model.Coupon;
import com.github.torbinsky.billing.recurly.model.Invoice;
import com.github.torbinsky.billing.recurly.model.Invoices;
import com.github.torbinsky.billing.recurly.model.Plan;
import com.github.torbinsky.billing.recurly.model.Plans;
import com.github.torbinsky.billing.recurly.model.Subscription;
import com.github.torbinsky.billing.recurly.model.SubscriptionUpdate;
import com.github.torbinsky.billing.recurly.model.Subscriptions;
import com.github.torbinsky.billing.recurly.model.Transaction;
import com.github.torbinsky.billing.recurly.model.Transactions;

/**
 * A concrete implementation of the {@link KeyAgnosticRecurlyClient} interface
 * 
 * @author twerner
 *
 */
public class KeyAgnosticRecurlyClientImpl implements KeyAgnosticRecurlyClient {

	/**
	 * We wrap this so we can use different keys during runtime.
	 */
	private final RecurlyClient keyClient;

	public KeyAgnosticRecurlyClientImpl() {
		keyClient = new RecurlyClient("");
	}

	@Override
	public Account createAccount(Account account, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createAccount(account);
	}

	@Override
	public Accounts getAccounts(String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAccounts();
	}

	@Override
	public Account getAccount(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAccount(accountCode);
	}

	@Override
	public Account updateAccount(String accountCode, Account account, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.updateAccount(accountCode, account);
	}

	@Override
	public void closeAccount(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		
	}

	@Override
	public Subscription createSubscription(Subscription subscription, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createSubscription(subscription);
	}

	@Override
	public Subscription getSubscription(String uuid, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getSubscription(uuid);
	}

	@Override
	public Subscription cancelSubscription(Subscription subscription, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.cancelSubscription(subscription);
	}

	@Override
	public Subscription reactivateSubscription(Subscription subscription, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.reactivateSubscription(subscription);
	}

	@Override
	public Subscription updateSubscription(String uuid, SubscriptionUpdate subscriptionUpdate, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.updateSubscription(uuid, subscriptionUpdate);
	}

	@Override
	public Subscriptions getAccountSubscriptions(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAccountSubscriptions(accountCode);
	}

	@Override
	public Subscriptions getAccountSubscriptions(String accountCode, String status, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAccountSubscriptions(accountCode);
	}

	@Override
	public BillingInfo createOrUpdateBillingInfo(BillingInfo billingInfo, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createOrUpdateBillingInfo(billingInfo);
	}

	@Override
	public BillingInfo getBillingInfo(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getBillingInfo(accountCode);
	}

	@Override
	public void clearBillingInfo(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		
	}

	@Override
	public Transactions getAccountTransactions(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAccountTransactions(accountCode);
	}

	@Override
	public Transaction createTransaction(Transaction trans, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createTransaction(trans);
	}

	@Override
	public Invoices getAccountInvoices(String accountCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAccountInvoices(accountCode);
	}

	@Override
	public Plan createPlan(Plan plan, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createPlan(plan);
	}

	@Override
	public Plan getPlan(String planCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getPlan(planCode);
	}

	@Override
	public Plans getPlans(String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getPlans();
	}

	@Override
	public void deletePlan(String planCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		keyClient.deletePlan(planCode);
	}

	@Override
	public AddOn createPlanAddOn(String planCode, AddOn addOn, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createPlanAddOn(planCode, addOn);
	}

	@Override
	public AddOn getAddOn(String planCode, String addOnCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAddOn(planCode, addOnCode);
	}

	@Override
	public AddOn getAddOns(String planCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getAddOns(planCode);
	}

	@Override
	public void deleteAddOn(String planCode, String addOnCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		keyClient.deleteAddOn(planCode, addOnCode);
	}

	@Override
	public Coupon createCoupon(Coupon coupon, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.createCoupon(coupon);
	}

	@Override
	public Coupon getCoupon(String couponCode, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.getCoupon(couponCode);
	}

	@Override
	public Subscription fetchSubscription(String recurlyToken, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.fetchSubscription(recurlyToken);
	}

	@Override
	public BillingInfo fetchBillingInfo(String recurlyToken, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.fetchBillingInfo(recurlyToken);
	}

	@Override
	public Invoice fetchInvoice(String recurlyToken, String apiKey) {
		keyClient.setApiKey(apiKey);
		return keyClient.fetchInvoice(recurlyToken);
	}

	@Override
	public synchronized void open() {
		keyClient.open();
	}

	@Override
	public synchronized void close() {
		keyClient.close();
	}


}
