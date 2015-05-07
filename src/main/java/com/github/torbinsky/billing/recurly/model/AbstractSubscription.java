/*
 * Copyright 2010-2012 Ning, Inc.
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.torbinsky.billing.recurly.model.list.SubscriptionAddOns;

@XmlRootElement(name = "subscription")
public class AbstractSubscription extends RecurlyObject {

    public static final String SUBSCRIPTION_RESOURCE = "/subscriptions";

    @XmlElement(name = "unit_amount_in_cents")
    protected Integer unitAmountInCents;

    @XmlElement(name = "quantity")
    protected Integer quantity;

    @XmlElementWrapper(name = "subscription_add_ons")
    @XmlElement(name = "subscription_add_ons")
    protected SubscriptionAddOns subscriptionAddOns;

    @XmlElement(name = "plan_code")
    private String planCode;

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(final String planCode) {
        this.planCode = stringOrNull(planCode);
    }

    public Integer getUnitAmountInCents() {
        return unitAmountInCents;
    }

    public void setUnitAmountInCents(final Object unitAmountInCents) {
        this.unitAmountInCents = integerOrNull(unitAmountInCents);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Object quantity) {
        this.quantity = integerOrNull(quantity);
    }

    public SubscriptionAddOns getSubscriptionAddOns() {
        return subscriptionAddOns;
    }

    public void setSubscriptionAddOns(final SubscriptionAddOns subscriptionAddOns) {
        this.subscriptionAddOns = subscriptionAddOns;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractSubscription)) {
            return false;
        }

        final AbstractSubscription that = (AbstractSubscription) o;

        if (subscriptionAddOns != null ? !subscriptionAddOns.equals(that.subscriptionAddOns) : that.subscriptionAddOns != null) {
            return false;
        }
        if (planCode != null ? !planCode.equals(that.planCode) : that.planCode != null) {
            return false;
        }
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) {
            return false;
        }
        if (unitAmountInCents != null ? !unitAmountInCents.equals(that.unitAmountInCents) : that.unitAmountInCents != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = unitAmountInCents != null ? unitAmountInCents.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (subscriptionAddOns != null ? subscriptionAddOns.hashCode() : 0);
        result = 31 * result + (planCode != null ? planCode.hashCode() : 0);
        return result;
    }
}
