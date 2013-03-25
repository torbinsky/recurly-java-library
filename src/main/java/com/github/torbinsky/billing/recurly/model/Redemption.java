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

import org.joda.time.DateTime;

/**
 * @author tw
 *
 */
@XmlRootElement(name = "redemption")
public class Redemption extends RecurlyObject {

	public static final String REDEMPTIONS_RESOURCE = "/redemption";

	@XmlElement(name = "account")
	private Account account;

	@XmlElement(name = "coupon")
	private Coupon coupon;

	@XmlElement(name = "single_use")
	private Boolean singleUse;

	@XmlElement(name = "total_discounted_in_cents")
	private Integer totalDiscountedInCents;

	@XmlElement(name = "state")
	private String state;

	@XmlElement(name = "currency")
	private String currency;

	@XmlElement(name = "created_at")
	private DateTime createdAt;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Coupon getCoupon() {
		return coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}

	public Boolean getSingleUse() {
		return singleUse;
	}

	public void setSingleUse(Boolean singleUse) {
		this.singleUse = singleUse;
	}

	public Integer getTotalDiscountedInCents() {
		return totalDiscountedInCents;
	}

	public void setTotalDiscountedInCents(Integer totalDiscountedInCents) {
		this.totalDiscountedInCents = totalDiscountedInCents;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(DateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Invoice");
		sb.append("{account=").append(account);
		sb.append(", coupon=").append(coupon);
		sb.append(", single_use='").append(singleUse).append('\'');
		sb.append(", total_discounted='").append(totalDiscountedInCents).append('\'');
		sb.append(", currency='").append(currency).append('\'');
		sb.append(", state='").append(state).append('\'');
		sb.append(", created_at='").append(createdAt).append('\'');
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

		final Redemption redemption = (Redemption) o;

		if (account != null ? !account.equals(redemption.account) : redemption.account != null) {
			return false;
		}
		
		if (coupon != null ? !coupon.equals(redemption.coupon) : redemption.coupon != null) {
			return false;
		}
		
		if (singleUse != null ? !singleUse.equals(redemption.singleUse) : redemption.singleUse != null) {
            return false;
        }
		
		if (createdAt != null ? !createdAt.equals(redemption.createdAt) : redemption.createdAt != null) {
            return false;
        }
		
        if (currency != null ? !currency.equals(redemption.currency) : redemption.currency != null) {
            return false;
        }
        
        if (state != null ? !state.equals(redemption.state) : redemption.state != null) {
            return false;
        }
        
        if (totalDiscountedInCents != null ? !totalDiscountedInCents.equals(redemption.totalDiscountedInCents) : redemption.totalDiscountedInCents != null) {
            return false;
        }

		return true;
	}

	@Override
	public int hashCode() {
		int result = account != null ? account.hashCode() : 0;
		result += coupon != null ? coupon.hashCode() : 0;
		result = 31 * result + (singleUse != null ? singleUse.hashCode() : 0);
		result = 31 * result + (totalDiscountedInCents != null ? totalDiscountedInCents.hashCode() : 0);
		result = 31 * result + (currency != null ? currency.hashCode() : 0);
		result = 31 * result + (state != null ? state.hashCode() : 0);
		result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);		
		return result;
	}

}
