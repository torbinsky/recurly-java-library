/*
 * Copyright 2010-2012 Ning, Inc.
 * Copyright 2013 Torben Werner - Modifications to abstract basic features to superclass
 *
 * Ning licenses this file to you under the Apache License, version 2.0
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.torbinsky.billing.recurly.exception.RecurlyAPIException;
import com.github.torbinsky.billing.recurly.model.Account;
import com.github.torbinsky.billing.recurly.model.AddOn;
import com.github.torbinsky.billing.recurly.model.Adjustment;
import com.github.torbinsky.billing.recurly.model.BillingInfo;
import com.github.torbinsky.billing.recurly.model.Coupon;
import com.github.torbinsky.billing.recurly.model.CouponRedeem;
import com.github.torbinsky.billing.recurly.model.Invoice;
import com.github.torbinsky.billing.recurly.model.Plan;
import com.github.torbinsky.billing.recurly.model.RecurlyObject;
import com.github.torbinsky.billing.recurly.model.Redemption;
import com.github.torbinsky.billing.recurly.model.Subscription;
import com.github.torbinsky.billing.recurly.model.Transaction;
import com.github.torbinsky.billing.recurly.model.list.Accounts;
import com.github.torbinsky.billing.recurly.model.list.Adjustments;
import com.github.torbinsky.billing.recurly.model.list.Invoices;
import com.github.torbinsky.billing.recurly.model.list.Plans;
import com.github.torbinsky.billing.recurly.model.list.RecurlyObjects;
import com.github.torbinsky.billing.recurly.model.list.Subscriptions;
import com.github.torbinsky.billing.recurly.model.list.Transactions;
import com.github.torbinsky.billing.recurly.serialize.XmlPayloadMap;

public class RecurlyClient extends RecurlyClientBase {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(RecurlyClient.class);

    public RecurlyClient(final String apiKey) {
        super(apiKey);
    }

    public RecurlyClient(final String apiKey, final String host, final int port, final String version) {
        super(apiKey, host, port, version);
    }
    
    /* **************************************
     * Generic CREATE/UPDATE 
     * **************************************/
    
    public <T> T create(String path, XmlPayloadMap<?, ?> payload, Class<T> clazz){
    	return doPOST(path, payload, clazz);
    }
    
    public <T> T update(String path, XmlPayloadMap<?, ?> payload, Class<T> clazz){
    	return doPUT(path, payload, clazz);
    }
    
    /* **************************************
     * 
     * **************************************/

    /**
     * Create Account
     * <p/>
     * Creates a new account. You may optionally include billing information.
     *
     * @param account accountMap object
     * @return the newly created account object on success, null otherwise
     */
    public Account createAccount(final XmlPayloadMap<?, ?> account) {
        return doPOST(Account.ACCOUNT_RESOURCE, account, Account.class);
    }

    /**
     * Get Accounts
     * <p/>
     * Returns information about all accounts.
     *
     * @return account object on success, null otherwise
     */
    public Accounts getAccounts() {
        List<Accounts> paginatedAccounts = doGETs(Accounts.ACCOUNTS_RESOURCE, Accounts.class);
        return depaginateResults(paginatedAccounts);
    }

    /**
     * Get Account
     * <p/>
     * Returns information about a single account.
     *
     * @param accountCode recurly account id
     * @return account object on success, null otherwise
     */
    public Account getAccount(final String accountCode) {
        try {
			return doGET(Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8"), Account.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    

    /**
     * Update Account
     * <p/>
     * Updates an existing account.
     *
     * @param accountCode recurly account id
     * @param account     account object
     * @return the updated account object on success, null otherwise
     */
    public Account updateAccount(final String accountCode, final XmlPayloadMap<?, ?> account) {
    	try {
    		return doPUT(Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8"), account, Account.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Close Account
     * <p/>
     * Marks an account as closed and cancels any active subscriptions. Any saved billing information will also be
     * permanently removed from the account.
     *
     * @param accountCode recurly account id
     */
    public void closeAccount(final String accountCode) {
    	try {
    		doDELETE(Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Create a subscription
     * <p/>
     * Creates a subscription for an account.
     *
     * @param subscription Subscription object
     * @return the newly created Subscription object on success, null otherwise
     */
    public Subscription createSubscription(final XmlPayloadMap<?, ?> subscription) {
        return doPOST(Subscription.SUBSCRIPTION_RESOURCE,
                      subscription, Subscription.class);
    }

    /**
     * Get a particular {@link Subscription} by it's UUID
     * <p/>
     * Returns information about a single account.
     *
     * @param uuid UUID of the subscription to lookup
     * @return Subscriptions for the specified user
     */
    public Subscription getSubscription(final String uuid) {
    	try {
	        return doGET(Subscriptions.SUBSCRIPTIONS_RESOURCE
	                     + "/" + URLEncoder.encode(uuid, "UTF-8"),
	                     Subscription.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    

    /**
     * Cancel a subscription
     * <p/>
     * Cancel a subscription so it remains active and then expires at the end of the current bill cycle.
     *
     * @param subscription Subscription object
     * @return -?-
     */
    public Subscription cancelSubscription(final Subscription subscription) {
    	try {
	        return doPUT(Subscription.SUBSCRIPTION_RESOURCE + "/" + URLEncoder.encode(subscription.getUuid(), "UTF-8") + "/cancel",
	                     subscription, Subscription.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Reactivating a canceled subscription
     * <p/>
     * Reactivate a canceled subscription so it renews at the end of the current bill cycle.
     *
     * @param subscription Subscription object
     * @return -?-
     */
    public Subscription reactivateSubscription(final Subscription subscription) {
    	try {
	        return doPUT(Subscription.SUBSCRIPTION_RESOURCE + "/" + URLEncoder.encode(subscription.getUuid(), "UTF-8") + "/reactivate",
	                     subscription, Subscription.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Update a particular {@link Subscription} by it's UUID
     * <p/>
     * Returns information about a single account.
     *
     * @param uuid UUID of the subscription to update
     * @return Subscription the updated subscription
     */
    public Subscription updateSubscription(final String uuid, final XmlPayloadMap<?, ?> subscriptionUpdate) {
    	try {
	        return doPUT(Subscriptions.SUBSCRIPTIONS_RESOURCE
	                     + "/" + URLEncoder.encode(uuid, "UTF-8"),
	                     subscriptionUpdate,
	                     Subscription.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Get the subscriptions for an {@link Account}.
     * <p/>
     * Returns information about a single {@link Account}.
     *
     * @param accountCode recurly account id
     * @return Subscriptions for the specified user
     */
    public Subscriptions getAccountSubscriptions(final String accountCode) {
    	try {
	        return depaginateResults(doGETs(Account.ACCOUNT_RESOURCE
	                     + "/" + URLEncoder.encode(accountCode, "UTF-8")
	                     + Subscriptions.SUBSCRIPTIONS_RESOURCE, Subscriptions.class));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Get the subscriptions for an account.
     * <p/>
     * Returns information about a single account.
     *
     * @param accountCode recurly account id
     * @param status      Only accounts in this status will be returned
     * @return Subscriptions for the specified user
     */
    public Subscriptions getAccountSubscriptions(final String accountCode, final String status) {
    	try {
	        return depaginateResults(doGETs(Account.ACCOUNT_RESOURCE
	                     + "/" + URLEncoder.encode(accountCode, "UTF-8")
	                     + Subscriptions.SUBSCRIPTIONS_RESOURCE
	                     + "?state="
	                     + URLEncoder.encode(status, "UTF-8"),
	                     Subscriptions.class));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Update an account's billing info
     * <p/>
     * When new or updated credit card information is updated, the billing information is only saved if the credit card
     * is valid. If the account has a past due invoice, the outstanding balance will be collected to validate the
     * billing information.
     * <p/>
     * If the account does not exist before the API request, the account will be created if the billing information
     * is valid.
     * <p/>
     * Please note: this API end-point may be used to import billing information without security codes (CVV).
     * Recurly recommends requiring CVV from your customers when collecting new or updated billing information.
     *
     * @param billingInfo billing info object to create or update
     * @return the newly created or update billing info object on success, null otherwise
     */
    public BillingInfo createOrUpdateBillingInfo(final XmlPayloadMap<?, ?> billingInfo, String accountCode) {
    	try {
	        return doPUT(Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + BillingInfo.BILLING_INFO_RESOURCE,
	                     billingInfo, BillingInfo.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Lookup an account's billing info
     * <p/>
     * Returns only the account's current billing information.
     *
     * @param accountCode recurly account id
     * @return the current billing info object associated with this account on success, null otherwise
     */
    public BillingInfo getBillingInfo(final String accountCode) {
    	try{
            return doGET(Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + BillingInfo.BILLING_INFO_RESOURCE,
                    BillingInfo.class);
    	}catch(RecurlyAPIException e){
    		if(e.getMessage().contains("Couldn't find BillingInfo with account_code")){
    			return null;
    		}
    		// Some other problem occurred, re-throw the exception
    		throw e;
    	}
        
    }

    /**
     * Clear an account's billing info
     * <p/>
     * You may remove any stored billing information for an account. If the account has a subscription, the renewal will
     * go into past due unless you update the billing info before the renewal occurs
     *
     * @param accountCode recurly account id
     */
    public void clearBillingInfo(final String accountCode) {
    	try {
    		doDELETE(Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + BillingInfo.BILLING_INFO_RESOURCE);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    ///////////////////////////////////////////////////////////////////////////
    // User transactions

    /**
     * Lookup an account's transactions history
     * <p/>
     * Returns the account's transaction history
     *
     * @param accountCode recurly account id
     * @return the transaction history associated with this account on success, null otherwise
     */
    public Transactions getAccountTransactions(final String accountCode) {
    	try {
	        return depaginateResults(doGETs(Accounts.ACCOUNTS_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + Transactions.TRANSACTIONS_RESOURCE,
	                     Transactions.class));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    public Transaction getTransaction(final String uuid){
    	try {
    		return doGET(Transactions.TRANSACTIONS_RESOURCE + "/" + URLEncoder.encode(uuid, "UTF-8"), Transaction.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    
    public void partialRefundTransaction(final String transactionId, int refundInCents){
    	try {
	    	Map<String, String> param = new HashMap<>(); 
	    	param.put("amount_in_cents", String.valueOf(refundInCents)); 
	    	doDELETE(Transactions.TRANSACTIONS_RESOURCE + "/" + URLEncoder.encode(transactionId, "UTF-8"), param);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Creates a {@link Transaction} throgh the Recurly API.
     *
     * @param trans The {@link Transaction} to create
     * @return The created {@link Transaction} object
     */
    public Transaction createTransaction(final XmlPayloadMap<?, ?> trans) {
   		return doPOST(Transactions.TRANSACTIONS_RESOURCE, trans, Transaction.class);
    }
    
	///////////////////////////////////////////////////////////////////////////
	// Redemptions
    /**
     * @param accountCode Recurly account id
     * @return the redemption associated with this account on success, null otherwise
     */
    public Redemption getAccountRedemption(final String accountCode){
    	try{
	    	return doGET(Accounts.ACCOUNTS_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + Redemption.REDEMPTIONS_RESOURCE,
	    			Redemption.class);
    	}catch(RecurlyAPIException e){
    		if(e.getMessage().contains("Couldn't find Redemption for Account")){
    			return null;
    		}
    		// Some other problem occurred, re-throw the exception
    		throw e;
    	} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    /**
     * Redeem a coupon
     * <p/>
     * Redeems a coupon with the matching code
     */
    public CouponRedeem redeemCoupon(final String couponCode, final XmlPayloadMap<?, ?> couponRedeem) {
    	try {
    		return doPOST(Coupon.COUPON_RESOURCE + "/" + URLEncoder.encode(couponCode, "UTF-8") + CouponRedeem.COUPON_REDEEM_RESOURCE, couponRedeem, CouponRedeem.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    /**
     * Deactivate a coupon
     * <p/>
     * Deactivates a coupon with the matching code
     */
    public void deactivateCoupon(final String couponCode) {
    	try {
    		doDELETE(Coupon.COUPON_RESOURCE + "/" + URLEncoder.encode(couponCode, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
	///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // User invoices

    /**
     * Lookup an account's invoices
     * <p/>
     * Returns the account's invoices
     *
     * @param accountCode recurly account id
     * @param stateQuery the invoice state (default 'all')
     * @return the invoices associated with this account on success, null otherwise
     */
    public Invoices getAccountInvoices(final String accountCode, String stateQuery) {
    	try {
	        return depaginateResults(doGETs(Accounts.ACCOUNTS_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + Invoices.INVOICES_RESOURCE,
	        			 "&state="+stateQuery,
	                     Invoices.class));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    public Invoice getInvoice(final String invoiceNumber){
    	try {
    		return doGET(Invoices.INVOICES_RESOURCE + "/" + URLEncoder.encode(invoiceNumber, "UTF-8"), Invoice.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    
    /**
     * Lookup an account's collected invoices
     * <p/>
     * Returns the account's collected invoices
     *
     * @param accountCode recurly account id
     * @return the invoices associated with this account on success, null otherwise
     */
    public Invoices getAccountCollectedInvoices(final String accountCode) {
    	try {
    		return getAccountInvoices(URLEncoder.encode(accountCode, "UTF-8"), "collected");
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    /**
     * Lookup an account's invoices
     * <p/>
     * Returns the account's invoices
     *
     * @param accountCode recurly account id
     * @return the invoices associated with this account on success, null otherwise
     */
    public Invoices getAccountInvoices(final String accountCode) {
    	try {
    		return getAccountInvoices(URLEncoder.encode(accountCode, "UTF-8"), "all");
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

	///////////////////////////////////////////////////////////////////////////
	// Account Adjustments
    
    public Adjustment getAdjustment(final String uuid){
    	try {
    		return doGET(Adjustments.ADJUSTMENTS_RESOURCE + "/" + URLEncoder.encode(uuid, "UTF-8"), Adjustment.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    public Adjustments getAccountAdjustments(final String accountCode){
    	try {
	    	return depaginateResults(doGETs(Account.ACCOUNT_RESOURCE
	                + "/" + URLEncoder.encode(accountCode, "UTF-8")
	                + Adjustments.ADJUSTMENTS_RESOURCE, Adjustments.class));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    public Adjustments getAccountAdjustments(final String accountCode, final String state){
    	try {
	    	return depaginateResults(doGETs(Account.ACCOUNT_RESOURCE
	                + "/" + URLEncoder.encode(accountCode, "UTF-8")
	                + Adjustments.ADJUSTMENTS_RESOURCE, "&state=" + state, Adjustments.class));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    public Adjustment createAdjustment(final String accountCode, final XmlPayloadMap<?, ?> adjustmentData){
    	try {
	    	return doPOST(
	    			Account.ACCOUNT_RESOURCE + "/" + URLEncoder.encode(accountCode, "UTF-8") + Adjustments.ADJUSTMENTS_RESOURCE, 
	    			adjustmentData,
	    			Adjustment.class
	    		);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    public void deleteAdjustment(final String adjustmentUUID){
    	try {
    		doDELETE(Adjustments.ADJUSTMENTS_RESOURCE + "/" + URLEncoder.encode(adjustmentUUID, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create a Plan's info
     * <p/>
     *
     * @param plan The plan to create on recurly
     * @return the plan object as identified by the passed in ID
     */
    public Plan createPlan(final XmlPayloadMap<?, ?> plan) {
   		return doPOST(Plan.PLANS_RESOURCE, plan, Plan.class);
    }

    /**
     * Get a Plan's details
     * <p/>
     *
     * @param planCode recurly id of plan
     * @return the plan object as identified by the passed in ID
     */
    public Plan getPlan(final String planCode) {
    	try {
    		return doGET(Plan.PLANS_RESOURCE + "/" + URLEncoder.encode(planCode, "UTF-8"), Plan.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Return all the plans
     * <p/>
     *
     * @return the plan object as identified by the passed in ID
     */
    public Plans getPlans() {
   		return depaginateResults(doGETs(Plans.PLANS_RESOURCE, Plans.class));
    }

    /**
     * Deletes a {@link Plan}
     * <p/>
     *
     * @param planCode The {@link Plan} object to delete.
     */
    public void deletePlan(final String planCode) {
    	try {
	        doDELETE(Plan.PLANS_RESOURCE +
	                 "/" +
	                 URLEncoder.encode(planCode, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create an AddOn to a Plan
     * <p/>
     *
     * @param planCode The planCode of the {@link Plan } to create within recurly
     * @param addOn    The {@link AddOn} to create within recurly
     * @return the {@link AddOn} object as identified by the passed in object
     */
    public AddOn createPlanAddOn(final String planCode, final XmlPayloadMap<?, ?> addOn) {
    	try {
	        return doPOST(Plan.PLANS_RESOURCE +
	                      "/" +
	                      URLEncoder.encode(planCode, "UTF-8") +
	                      AddOn.ADDONS_RESOURCE,
	                      addOn, AddOn.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Get an AddOn's details
     * <p/>
     *
     * @param addOnCode recurly id of {@link AddOn}
     * @param planCode  recurly id of {@link Plan}
     * @return the {@link AddOn} object as identified by the passed in plan and add-on IDs
     */
    public AddOn getAddOn(final String planCode, final String addOnCode) {
    	try {
	        return doGET(Plan.PLANS_RESOURCE +
	                     "/" +
	                     URLEncoder.encode(planCode, "UTF-8") +
	                     AddOn.ADDONS_RESOURCE +
	                     "/" +
	                     URLEncoder.encode(addOnCode, "UTF-8"), AddOn.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Return all the {@link AddOn} for a {@link Plan}
     * <p/>
     *
     * @return the {@link AddOn} objects as identified by the passed plan ID
     */
    public AddOn getAddOns(final String planCode) {
    	try {
	        return doGET(Plan.PLANS_RESOURCE +
	                     "/" +
	                     URLEncoder.encode(planCode, "UTF-8") +
	                     AddOn.ADDONS_RESOURCE, AddOn.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    /**
     * Deletes a {@link AddOn} for a Plan
     * <p/>
     *
     * @param planCode  The {@link Plan} object.
     * @param addOnCode The {@link AddOn} object to delete.
     */
    public void deleteAddOn(final String planCode, final String addOnCode) {
    	try {
	        doDELETE(Plan.PLANS_RESOURCE +
	                 "/" +
	                 URLEncoder.encode(planCode, "UTF-8") +
	                 AddOn.ADDONS_RESOURCE +
	                 "/" +
	                 URLEncoder.encode(addOnCode, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Create a {@link Coupon}
     * <p/>
     *
     * @param coupon The coupon to create on recurly
     * @return the {@link Coupon} object
     */
    public Coupon createCoupon(final XmlPayloadMap<?, ?> coupon) {
   		return doPOST(Coupon.COUPON_RESOURCE, coupon, Coupon.class);
    }

    /**
     * Get a Coupon
     * <p/>
     *
     * @param couponCode The code for the {@link Coupon}
     * @return The {@link Coupon} object as identified by the passed in code
     */
    public Coupon getCoupon(final String couponCode) {
    	try {
    		return doGET(Coupon.COUPON_RESOURCE + "/" + URLEncoder.encode(couponCode, "UTF-8"), Coupon.class);
		} catch (UnsupportedEncodingException e) {
			throw new RecurlyAPIException("Invalid Request", e);
		}
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    //
    // Recurly.js API
    //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Fetch Subscription
     * <p/>
     * Returns subscription from a recurly.js token.
     *
     * @param recurlyToken token given by recurly.js
     * @return subscription object on success, null otherwise
     */
    public Subscription fetchSubscription(final String recurlyToken) {
   		return fetch(recurlyToken, Subscription.class);
    }

    /**
     * Fetch BillingInfo
     * <p/>
     * Returns billing info from a recurly.js token.
     *
     * @param recurlyToken token given by recurly.js
     * @return billing info object on success, null otherwise
     */
    public BillingInfo fetchBillingInfo(final String recurlyToken) {
   		return fetch(recurlyToken, BillingInfo.class);
    }

    /**
     * Fetch Invoice
     * <p/>
     * Returns invoice from a recurly.js token.
     *
     * @param recurlyToken token given by recurly.js
     * @return invoice object on success, null otherwise
     */
    public Invoice fetchInvoice(final String recurlyToken) {
   		return fetch(recurlyToken, Invoice.class);
    }
    
    private <R extends RecurlyObject, T extends RecurlyObjects<R>> T depaginateResults(List<T> results){
    	Iterator<T> ai = results.iterator();
        T depaginatedType = null;
        while(ai.hasNext()){
        	// First element
        	if(depaginatedType == null){
        		depaginatedType = ai.next();
        		continue;
        	}else{
        		// Add subsequent elements to the first element
        		List<R> objects = ai.next().getObjects();
				depaginatedType.getObjects().addAll(objects);
        	}
        }
        
		return depaginatedType;
    }
}
