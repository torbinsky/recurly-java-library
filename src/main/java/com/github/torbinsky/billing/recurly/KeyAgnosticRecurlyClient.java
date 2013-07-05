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
 * A Recurly client which allows the API key to be specified for each API call.
 * 
 * @author twerner
 *
 */
public interface KeyAgnosticRecurlyClient {
	
	/* **************************************
     * Generic CREATE/UPDATE 
     * **************************************/
	
	public <T> T create(String path, XmlPayloadMap<?, ?> payload, Class<T> clazz, String apiKey);
    
    public <T> T update(String path, XmlPayloadMap<?, ?> payload, Class<T> clazz, String apiKey);
    
    /* **************************************
     * 
     * **************************************/

	/**
	 * Open the underlying http client
	 */
	public void open();

	/**
	 * Close the underlying http client
	 */
	public void close();

	/**
	 * Create Account
	 * <p/>
	 * Creates a new account. You may optionally include billing information.
	 * 
	 * @param account
	 *            account object
	 * @return the newly created account object on success, null otherwise
	 */
	public Account createAccount(final XmlPayloadMap<?, ?> account, final String apiKey);

	/**
	 * Get Accounts
	 * <p/>
	 * Returns information about all accounts.
	 * 
	 * @return account object on success, null otherwise
	 */
	public Accounts getAccounts(String apiKey);

	/**
	 * Get Account
	 * <p/>
	 * Returns information about a single account.
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @return account object on success, null otherwise
	 */
	public Account getAccount(final String accountCode, final String apiKey);

	/**
	 * Update Account
	 * <p/>
	 * Updates an existing account.
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @param account
	 *            account object
	 * @return the updated account object on success, null otherwise
	 */
	public Account updateAccount(final String accountCode, final XmlPayloadMap<?, ?> account, final String apiKey);

	/**
	 * Close Account
	 * <p/>
	 * Marks an account as closed and cancels any active subscriptions. Any
	 * saved billing information will also be permanently removed from the
	 * account.
	 * 
	 * @param accountCode
	 *            recurly account id
	 */
	public void closeAccount(final String accountCode, final String apiKey);

	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Create a subscription
	 * <p/>
	 * Creates a subscription for an account.
	 * 
	 * @param subscription
	 *            Subscription object
	 * @return the newly created Subscription object on success, null otherwise
	 */
	public Subscription createSubscription(final XmlPayloadMap<?, ?> subscription, final String apiKey);

	/**
	 * Get a particular {@link Subscription} by it's UUID
	 * <p/>
	 * Returns information about a single account.
	 * 
	 * @param uuid
	 *            UUID of the subscription to lookup
	 * @return Subscriptions for the specified user
	 */
	public Subscription getSubscription(final String uuid, final String apiKey);

	/**
	 * Cancel a subscription
	 * <p/>
	 * Cancel a subscription so it remains active and then expires at the end of
	 * the current bill cycle.
	 * 
	 * @param subscription
	 *            Subscription object
	 * @return -?-
	 */
	public Subscription cancelSubscription(final Subscription subscription, final String apiKey);

	/**
	 * Reactivating a canceled subscription
	 * <p/>
	 * Reactivate a canceled subscription so it renews at the end of the current
	 * bill cycle.
	 * 
	 * @param subscription
	 *            Subscription object
	 * @return -?-
	 */
	public Subscription reactivateSubscription(final Subscription subscription, final String apiKey);

	/**
	 * Update a particular {@link Subscription} by it's UUID
	 * <p/>
	 * Returns information about a single account.
	 * 
	 * @param uuid
	 *            UUID of the subscription to update
	 * @return Subscription the updated subscription
	 */
	public Subscription updateSubscription(final String uuid, final XmlPayloadMap<?, ?> subscriptionUpdate, final String apiKey);

	/**
	 * Get the subscriptions for an {@link Account}.
	 * <p/>
	 * Returns information about a single {@link Account}.
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @return Subscriptions for the specified user
	 */
	public Subscriptions getAccountSubscriptions(final String accountCode, final String apiKey);

	/**
	 * Get the subscriptions for an account.
	 * <p/>
	 * Returns information about a single account.
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @param status
	 *            Only accounts in this status will be returned
	 * @return Subscriptions for the specified user
	 */
	public Subscriptions getAccountSubscriptions(final String accountCode, final String status, final String apiKey);

	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Update an account's billing info
	 * <p/>
	 * When new or updated credit card information is updated, the billing
	 * information is only saved if the credit card is valid. If the account has
	 * a past due invoice, the outstanding balance will be collected to validate
	 * the billing information.
	 * <p/>
	 * If the account does not exist before the API request, the account will be
	 * created if the billing information is valid.
	 * <p/>
	 * Please note: this API end-point may be used to import billing information
	 * without security codes (CVV). Recurly recommends requiring CVV from your
	 * customers when collecting new or updated billing information.
	 * 
	 * @param billingInfo
	 *            billing info object to create or update
	 * @return the newly created or update billing info object on success, null
	 *         otherwise
	 */
	public BillingInfo createOrUpdateBillingInfo(final XmlPayloadMap<?, ?> billingInfo, final String accountCode, final String apiKey);

	/**
	 * Lookup an account's billing info
	 * <p/>
	 * Returns only the account's current billing information.
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @return the current billing info object associated with this account on
	 *         success, null otherwise
	 */
	public BillingInfo getBillingInfo(final String accountCode, final String apiKey);

	/**
	 * Clear an account's billing info
	 * <p/>
	 * You may remove any stored billing information for an account. If the
	 * account has a subscription, the renewal will go into past due unless you
	 * update the billing info before the renewal occurs
	 * 
	 * @param accountCode
	 *            recurly account id
	 */
	public void clearBillingInfo(final String accountCode, final String apiKey);

	// /////////////////////////////////////////////////////////////////////////
	// User transactions

	/**
	 * Lookup an account's transactions history
	 * <p/>
	 * Returns the account's transaction history
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @return the transaction history associated with this account on success,
	 *         null otherwise
	 */
	public Transactions getAccountTransactions(final String accountCode, final String apiKey);

	/**
	 * Creates a {@link Transaction} throgh the Recurly API.
	 * 
	 * @param trans
	 *            The {@link Transaction} to create
	 * @return The created {@link Transaction} object
	 */
	public Transaction createTransaction(final XmlPayloadMap<?, ?> trans, final String apiKey);
	
	/**
	 * @param accountCode Recurly account code
	 * @param apiKey Recurly api key
	 * @return an account's "active" {@link Redemption} or null 
	 */
	public Redemption getAccountRedemption(final String accountCode, final String apiKey);
	
	public CouponRedeem redeemCoupon(final String couponCode, final XmlPayloadMap<?, ?> couponRedeem, final String apiKey);
	
	/**
     * Deactivate a coupon
     * <p/>
     * Deactivates a coupon with the matching code
     */
    public void deactivateCoupon(final String couponCode, final String apiKey);

	// /////////////////////////////////////////////////////////////////////////
	// User invoices

	/**
	 * Lookup an account's invoices
	 * <p/>
	 * Returns the account's invoices
	 * 
	 * @param accountCode
	 *            recurly account id
	 * @return the invoices associated with this account on success, null
	 *         otherwise
	 */
	public Invoices getAccountInvoices(final String accountCode, final String apiKey);
	
	/**
     * Lookup an account's invoices
     * <p/>
     * Returns the account's invoices
     *
     * @param accountCode recurly account id
     * @param stateQuery the invoice state (default 'all')
     * @return the invoices associated with this account on success, null otherwise
     */
    public Invoices getAccountInvoices(final String accountCode, String stateQuery, String apiKey);
    
    /**
     * Lookup an account's collected invoices
     * <p/>
     * Returns the account's collected invoices
     *
     * @param accountCode recurly account id
     * @return the invoices associated with this account on success, null otherwise
     */
    public Invoices getAccountCollectedInvoices(final String accountCode, String apiKey);

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create a Plan's info
	 * <p/>
	 * 
	 * @param plan
	 *            The plan to create on recurly
	 * @return the plan object as identified by the passed in ID
	 */
	public Plan createPlan(final XmlPayloadMap<?, ?> plan, final String apiKey);

	/**
	 * Get a Plan's details
	 * <p/>
	 * 
	 * @param planCode
	 *            recurly id of plan
	 * @return the plan object as identified by the passed in ID
	 */
	public Plan getPlan(final String planCode, final String apiKey);

	/**
	 * Return all the plans
	 * <p/>
	 * 
	 * @return the plan object as identified by the passed in ID
	 */
	public Plans getPlans(final String apiKey);

	/**
	 * Deletes a {@link Plan}
	 * <p/>
	 * 
	 * @param planCode
	 *            The {@link Plan} object to delete.
	 */
	public void deletePlan(final String planCode, final String apiKey);

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create an AddOn to a Plan
	 * <p/>
	 * 
	 * @param planCode
	 *            The planCode of the {@link Plan } to create within recurly
	 * @param addOn
	 *            The {@link AddOn} to create within recurly
	 * @return the {@link AddOn} object as identified by the passed in object
	 */
	public AddOn createPlanAddOn(final String planCode, final XmlPayloadMap<?, ?> addOn, final String apiKey);

	/**
	 * Get an AddOn's details
	 * <p/>
	 * 
	 * @param addOnCode
	 *            recurly id of {@link AddOn}
	 * @param planCode
	 *            recurly id of {@link Plan}
	 * @return the {@link AddOn} object as identified by the passed in plan and
	 *         add-on IDs
	 */
	public AddOn getAddOn(final String planCode, final String addOnCode, final String apiKey);

	/**
	 * Return all the {@link AddOn} for a {@link Plan}
	 * <p/>
	 * 
	 * @return the {@link AddOn} objects as identified by the passed plan ID
	 */
	public AddOn getAddOns(final String planCode, final String apiKey);

	/**
	 * Deletes a {@link AddOn} for a Plan
	 * <p/>
	 * 
	 * @param planCode
	 *            The {@link Plan} object.
	 * @param addOnCode
	 *            The {@link AddOn} object to delete.
	 */
	public void deleteAddOn(final String planCode, final String addOnCode, final String apiKey);

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create a {@link Coupon}
	 * <p/>
	 * 
	 * @param coupon
	 *            The coupon to create on recurly
	 * @return the {@link Coupon} object
	 */
	public Coupon createCoupon(final XmlPayloadMap<?, ?> coupon, final String apiKey);

	/**
	 * Get a Coupon
	 * <p/>
	 * 
	 * @param couponCode
	 *            The code for the {@link Coupon}
	 * @return The {@link Coupon} object as identified by the passed in code
	 */
	public Coupon getCoupon(final String couponCode, final String apiKey);

	// /////////////////////////////////////////////////////////////////////////
	//
	// Recurly.js API
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Fetch Subscription
	 * <p/>
	 * Returns subscription from a recurly.js token.
	 * 
	 * @param recurlyToken
	 *            token given by recurly.js
	 * @return subscription object on success, null otherwise
	 */
	public Subscription fetchSubscription(final String recurlyToken, final String apiKey);

	/**
	 * Fetch BillingInfo
	 * <p/>
	 * Returns billing info from a recurly.js token.
	 * 
	 * @param recurlyToken
	 *            token given by recurly.js
	 * @return billing info object on success, null otherwise
	 */
	public BillingInfo fetchBillingInfo(final String recurlyToken, final String apiKey);

	/**
	 * Fetch Invoice
	 * <p/>
	 * Returns invoice from a recurly.js token.
	 * 
	 * @param recurlyToken
	 *            token given by recurly.js
	 * @return invoice object on success, null otherwise
	 */
	public Invoice fetchInvoice(final String recurlyToken, final String apiKey);
	
	/**
	 * Gets a list of adjustments for an account.
	 * 
	 * @param accountCode the account identifier
	 * @return a list of adjustments corresponding to a particular account
	 */
	public Adjustments getAccountAdjustments(final String accountCode, final String apiKey);
	
	/**
	 * Gets a list of adjustments for an account.
	 * 
	 * @param accountCode the account identifier
	 * @param state limit the adjustments returned by their state (The state of the adjustments to return: "pending" or "invoiced").
	 * @return a list of adjustments corresponding to a particular account
	 */
	public Adjustments getAccountAdjustments(final String accountCode, final String state, final String apiKey);  
	
    /**
     * Creates and adjustment on an account.
     * 
     * @param accountCode the identifier of the account which the adjustment will be applied against
     * @param adjustmentData the adjustment which should be applied to an account
     * @return the adjustment that was created, if any
     */
    public Adjustment createAdjustment(final String accountCode, final XmlPayloadMap<?, ?> adjustmentData, final String apiKey);    
    /**
     * Deletes an adjustment, if possible
     * 
     * @param adjustmentUUID the identifier of the adjustment
     */
    public void deleteAdjustment(final String adjustmentUUID, final String apiKey);
}
