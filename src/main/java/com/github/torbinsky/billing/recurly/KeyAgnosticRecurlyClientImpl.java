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
import com.github.torbinsky.billing.recurly.model.AddOn;
import com.github.torbinsky.billing.recurly.model.Adjustment;
import com.github.torbinsky.billing.recurly.model.BillingInfo;
import com.github.torbinsky.billing.recurly.model.Coupon;
import com.github.torbinsky.billing.recurly.model.CouponRedeem;
import com.github.torbinsky.billing.recurly.model.Invoice;
import com.github.torbinsky.billing.recurly.model.Plan;
import com.github.torbinsky.billing.recurly.model.Redemption;
import com.github.torbinsky.billing.recurly.model.Subscription;
import com.github.torbinsky.billing.recurly.model.Transaction;
import com.github.torbinsky.billing.recurly.model.list.Accounts;
import com.github.torbinsky.billing.recurly.model.list.Adjustments;
import com.github.torbinsky.billing.recurly.model.list.Invoices;
import com.github.torbinsky.billing.recurly.model.list.Plans;
import com.github.torbinsky.billing.recurly.model.list.Subscriptions;
import com.github.torbinsky.billing.recurly.model.list.Transactions;
import com.github.torbinsky.billing.recurly.serialize.XmlPayloadMap;

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
		keyClient = new RecurlyClient(null);
	}

	@Override
	public Account createAccount(final XmlPayloadMap<?, ?> account, String apiKey) {
		return new ThreadScopedAPIClientCall<Account>(apiKey){
			@Override
			Account doCall() {
				return keyClient.createAccount(account);
			}			
		}.call();
	}

	@Override
	public Accounts getAccounts(String apiKey) {
		return new ThreadScopedAPIClientCall<Accounts>(apiKey){
			@Override
			Accounts doCall() {
				return keyClient.getAccounts();
			}			
		}.call();
	}

	@Override
	public Account getAccount(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Account>(apiKey){
			@Override
			Account doCall() {
				return keyClient.getAccount(accountCode);
			}			
		}.call();
	}

	@Override
	public Account updateAccount(final String accountCode, final XmlPayloadMap<?, ?> account, String apiKey) {
		return new ThreadScopedAPIClientCall<Account>(apiKey){
			@Override
			Account doCall() {
				return keyClient.updateAccount(accountCode, account);
			}			
		}.call();
	}

	@Override
	public void closeAccount(final String accountCode, String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.closeAccount(accountCode);
				return null;
			}			
		}.call();
	}

	@Override
	public Subscription createSubscription(final XmlPayloadMap<?, ?> subscription, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscription>(apiKey){
			@Override
			Subscription doCall() {
				return keyClient.createSubscription(subscription);
			}			
		}.call();
	}

	@Override
	public Subscription getSubscription(final String uuid, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscription>(apiKey){
			@Override
			Subscription doCall() {
				return keyClient.getSubscription(uuid);
			}			
		}.call();
	}

	@Override
	public Subscription cancelSubscription(final Subscription subscription, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscription>(apiKey){
			@Override
			Subscription doCall() {
				return keyClient.cancelSubscription(subscription);
			}			
		}.call();
	}

	@Override
	public Subscription reactivateSubscription(final Subscription subscription, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscription>(apiKey){
			@Override
			Subscription doCall() {
				return keyClient.reactivateSubscription(subscription);
			}			
		}.call();
	}

	@Override
	public Subscription updateSubscription(final String uuid, final XmlPayloadMap<?, ?> subscriptionUpdate, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscription>(apiKey){
			@Override
			Subscription doCall() {
				return keyClient.updateSubscription(uuid, subscriptionUpdate);
			}			
		}.call();
	}

	@Override
	public Subscriptions getAccountSubscriptions(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscriptions>(apiKey){
			@Override
			Subscriptions doCall() {
				return keyClient.getAccountSubscriptions(accountCode);
			}			
		}.call();
	}

	@Override
	public Subscriptions getAccountSubscriptions(final String accountCode, String status, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscriptions>(apiKey){
			@Override
			Subscriptions doCall() {
				return keyClient.getAccountSubscriptions(accountCode);
			}			
		}.call();
	}

	@Override
	public BillingInfo createOrUpdateBillingInfo(final XmlPayloadMap<?, ?> billingInfo, final String accountCode, final String apiKey) {
		return new ThreadScopedAPIClientCall<BillingInfo>(apiKey){
			@Override
			BillingInfo doCall() {
				return keyClient.createOrUpdateBillingInfo(billingInfo, accountCode);
			}			
		}.call();
	}

	@Override
	public BillingInfo getBillingInfo(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<BillingInfo>(apiKey){
			@Override
			BillingInfo doCall() {
				return keyClient.getBillingInfo(accountCode);
			}			
		}.call();
	}

	@Override
	public void clearBillingInfo(final String accountCode, String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.clearBillingInfo(accountCode);
				return null;
			}			
		}.call();
		
	}

	@Override
	public Transactions getAccountTransactions(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Transactions>(apiKey){
			@Override
			Transactions doCall() {
				return keyClient.getAccountTransactions(accountCode);
			}			
		}.call();
	}

	@Override
	public Transaction createTransaction(final XmlPayloadMap<?, ?> trans, String apiKey) {
		return new ThreadScopedAPIClientCall<Transaction>(apiKey){
			@Override
			Transaction doCall() {
				return keyClient.createTransaction(trans);
			}			
		}.call();
	}
	
	@Override
	public void partialRefundTransaction(final String transactionId, final int refundInCents, String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.partialRefundTransaction(transactionId, refundInCents);
				return null; 
			}
		}.call(); 
	}

	@Override
	public Invoices getAccountInvoices(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Invoices>(apiKey){
			@Override
			Invoices doCall() {
				return keyClient.getAccountInvoices(accountCode);
			}			
		}.call();
	}

	@Override
	public Plan createPlan(final XmlPayloadMap<?, ?> plan, String apiKey) {
		return new ThreadScopedAPIClientCall<Plan>(apiKey){
			@Override
			Plan doCall() {
				return keyClient.createPlan(plan);
			}			
		}.call();
	}

	@Override
	public Plan getPlan(final String planCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Plan>(apiKey){
			@Override
			Plan doCall() {
				return keyClient.getPlan(planCode);
			}			
		}.call();
	}

	@Override
	public Plans getPlans(String apiKey) {
		return new ThreadScopedAPIClientCall<Plans>(apiKey){
			@Override
			Plans doCall() {
				return keyClient.getPlans();
			}			
		}.call();
	}

	@Override
	public void deletePlan(final String planCode, String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.deletePlan(planCode);
				return null;
			}			
		}.call();
	}

	@Override
	public AddOn createPlanAddOn(final String planCode, final XmlPayloadMap<?, ?> addOn, String apiKey) {
		return new ThreadScopedAPIClientCall<AddOn>(apiKey){
			@Override
			AddOn doCall() {
				return keyClient.createPlanAddOn(planCode, addOn);
			}			
		}.call();
	}

	@Override
	public AddOn getAddOn(final String planCode, final String addOnCode, String apiKey) {
		return new ThreadScopedAPIClientCall<AddOn>(apiKey){
			@Override
			AddOn doCall() {
				return keyClient.getAddOn(planCode, addOnCode);
			}			
		}.call();
	}

	@Override
	public AddOn getAddOns(final String planCode, String apiKey) {
		return new ThreadScopedAPIClientCall<AddOn>(apiKey){
			@Override
			AddOn doCall() {
				return keyClient.getAddOns(planCode);
			}			
		}.call();
	}

	@Override
	public void deleteAddOn(final String planCode, final String addOnCode, String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.deleteAddOn(planCode, addOnCode);
				return null;
			}			
		}.call();
	}

	@Override
	public Coupon createCoupon(final XmlPayloadMap<?, ?> coupon, String apiKey) {
		return new ThreadScopedAPIClientCall<Coupon>(apiKey){
			@Override
			Coupon doCall() {
				return keyClient.createCoupon(coupon);
			}			
		}.call();
	}
	

	@Override
	public void deactivateCoupon(final String couponCode, final String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.deactivateCoupon(couponCode);
				return null;
			}			
		}.call();
	}

	@Override
	public Coupon getCoupon(final String couponCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Coupon>(apiKey){
			@Override
			Coupon doCall() {
				return keyClient.getCoupon(couponCode);
			}			
		}.call();
	}

	@Override
	public Subscription fetchSubscription(final String recurlyToken, String apiKey) {
		return new ThreadScopedAPIClientCall<Subscription>(apiKey){
			@Override
			Subscription doCall() {
				return keyClient.fetchSubscription(recurlyToken);
			}			
		}.call();
	}

	@Override
	public BillingInfo fetchBillingInfo(final String recurlyToken, String apiKey) {
		return new ThreadScopedAPIClientCall<BillingInfo>(apiKey){
			@Override
			BillingInfo doCall() {
				return keyClient.fetchBillingInfo(recurlyToken);
			}			
		}.call();
	}

	@Override
	public Invoice fetchInvoice(final String recurlyToken, String apiKey) {
		return new ThreadScopedAPIClientCall<Invoice>(apiKey){
			@Override
			Invoice doCall() {
				return keyClient.fetchInvoice(recurlyToken);
			}			
		}.call();
	}

	@Override
	public Redemption getAccountRedemption(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Redemption>(apiKey){
			@Override
			Redemption doCall() {
				return keyClient.getAccountRedemption(accountCode);
			}			
		}.call();
	}

	@Override
	public Invoices getAccountInvoices(final String accountCode, final String stateQuery, String apiKey) {
		return new ThreadScopedAPIClientCall<Invoices>(apiKey){
			@Override
			Invoices doCall() {
				return keyClient.getAccountInvoices(accountCode, stateQuery);
			}			
		}.call();
	}

	@Override
	public Invoices getAccountCollectedInvoices(final String accountCode, String apiKey) {
		return new ThreadScopedAPIClientCall<Invoices>(apiKey){
			@Override
			Invoices doCall() {
				return keyClient.getAccountCollectedInvoices(accountCode);
			}			
		}.call();
	}	
	
	@Override
	public CouponRedeem redeemCoupon(final String couponCode, final XmlPayloadMap<?, ?> couponRedeem, String apiKey) {
		return new ThreadScopedAPIClientCall<CouponRedeem>(apiKey){
			@Override
			CouponRedeem doCall() {
				return keyClient.redeemCoupon(couponCode, couponRedeem);
			}			
		}.call();
	}
	
	@Override
	public Adjustments getAccountAdjustments(final String accountCode, final String apiKey) {
		return new ThreadScopedAPIClientCall<Adjustments>(apiKey){
			@Override
			Adjustments doCall() {
				return keyClient.getAccountAdjustments(accountCode);
			}			
		}.call();
	}
	
	@Override
	public Adjustments getAccountAdjustments(final String accountCode, final String state, final String apiKey) {
		return new ThreadScopedAPIClientCall<Adjustments>(apiKey){
			@Override
			Adjustments doCall() {
				return keyClient.getAccountAdjustments(accountCode, state);
			}			
		}.call();
	}
	
	@Override
	public Adjustment createAdjustment(final String accountCode, final XmlPayloadMap<?, ?> adjustmentData, final String apiKey) {
		return new ThreadScopedAPIClientCall<Adjustment>(apiKey){
			@Override
			Adjustment doCall() {
				return keyClient.createAdjustment(accountCode, adjustmentData);
			}			
		}.call();
	}
	
	@Override
	public void deleteAdjustment(final String adjustmentUUID, final String apiKey) {
		new ThreadScopedAPIClientCall<Void>(apiKey){
			@Override
			Void doCall() {
				keyClient.deleteAdjustment(adjustmentUUID);
				return null;
			}			
		}.call();
	}

	@Override
	public <T> T create(final String path, final XmlPayloadMap<?, ?> payload, final Class<T> clazz, String apiKey) {
		return new ThreadScopedAPIClientCall<T>(apiKey){
			@Override
			T doCall() {
				return keyClient.create(path, payload, clazz);
			}			
		}.call();
	}

	@Override
	public <T> T update(final String path, final XmlPayloadMap<?, ?> payload, final Class<T> clazz, String apiKey) {
		return new ThreadScopedAPIClientCall<T>(apiKey){
			@Override
			T doCall() {
				return keyClient.update(path, payload, clazz);
			}			
		}.call();
	}
	
	@Override
	public synchronized void open() {
		keyClient.open();
	}
	
	@Override
	public synchronized void close() {
		keyClient.close();
	}

	/**
	 * Wraps API calls which should be thread scoped in order to avoid API key
	 * race conditions between threads.
	 * 
	 * @author Torben
	 *
	 * @param <T> the type of result returned by this API call
	 */
	protected abstract class ThreadScopedAPIClientCall<T> {
		private String apiKey;
		ThreadScopedAPIClientCall(String apiKey){
			this.apiKey = apiKey;			
		}
		
		/**
		 * This is the method you should call which will wrap the call in api key set/unset operations
		 */
		public T call(){
			keyClient.setThreadLocalApiKey(apiKey);
			T result = doCall();
			keyClient.unsetThreadLocalApiKey();
			
			return result;
		}
		
		/**
		 * This is where you implement the specific api call(s) that should run within the scope of the key
		 */
		abstract T doCall();
	}

	@Override
	public Adjustment getAdjustment(final String uuid, String apiKey) {
		return new ThreadScopedAPIClientCall<Adjustment>(apiKey){
			@Override
			Adjustment doCall() {
				return keyClient.getAdjustment(uuid);
			}			
		}.call();	}



}
