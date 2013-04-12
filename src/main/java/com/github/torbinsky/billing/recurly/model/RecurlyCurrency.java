/*
 * Copyright 2010-2012 Ning, Inc.
 * Copyright 2013 Torben Werner - Modification to fix setter bug for line items
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
package com.github.torbinsky.billing.recurly.model;

/**
 * Describes the various currency types supported by Recurly
 * 
 * @author Torben
 *
 */
public enum RecurlyCurrency {
	/** United States Dollars */
	USD("USD"),
	/** Australian Dollars */
	AUD("AUD"),
	/** Canadian Dollars */
	CAD("CAD"),
	/** Euros */
	EUR("EUR"),
	/** British Pounds */
	GBP("GBP"),
	/** Czech Korunas */
	CZK("CZK"),
	/** Danish Krones */
	DKK("DKK"),
	/** Hungarian Forints */
	HUF("HUF"),
	/** Norwegian Krones */
	NOK("NOK"),
	/** New Zealand Dollars */
	NZD("NZD"),
	/** Polish Zloty */
	PLN("PLN"),
	/** Singapore Dollars */
	SGD("SGD"),
	/** Swedish Kronas */
	SEK("SEK"),
	/** Swiss Francs */
	CHF("CHF"),
	/** South African Rand */
	ZAR("ZAR");

	String currencyCode;

	RecurlyCurrency(String code) {
		this.currencyCode = code;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	public static RecurlyCurrency getCurrency(String currencyCode){
		for(RecurlyCurrency rc : RecurlyCurrency.values()){
			if(rc.getCurrencyCode().equals(currencyCode)){
				return rc;
			}
		}
		
		return null;
	}
}
