/*
 * Copyright 2013 Torbinsky
 *
 * Torbinsky licenses this file to you under the Apache License, version 2.0
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author tw
 *
 */
@XmlRootElement(name = "redemption")
public class CouponRedeem extends RecurlyObject {

	public static final String COUPON_REDEEM_RESOURCE = "/redeem";

	@XmlElement(name = "account_code")
	private String accountCode;

	@XmlElement(name = "currency")
	private String currency;

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final Object currency) {
		this.currency = stringOrNull(currency);
	}
	
	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(final Object currency) {
		this.accountCode = stringOrNull(currency);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Invoice");
		sb.append(", account_code='").append(accountCode).append('\'');
		sb.append(", currency='").append(currency).append('\'');
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final CouponRedeem redemption = (CouponRedeem) o;

		if (accountCode != null ? !accountCode.equals(redemption.accountCode) : redemption.accountCode != null) {
            return false;
        }
        
		
        if (currency != null ? !currency.equals(redemption.currency) : redemption.currency != null) {
            return false;
        }
        

		return true;
	}

	@Override
	public int hashCode() {
		int result = (accountCode != null ? accountCode.hashCode() : 0);
		result = 31 * result + (currency != null ? currency.hashCode() : 0);		
		return result;
	}

}
