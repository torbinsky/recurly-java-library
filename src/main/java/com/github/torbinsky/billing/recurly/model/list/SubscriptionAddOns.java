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

package com.github.torbinsky.billing.recurly.model.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.torbinsky.billing.recurly.model.SubscriptionAddOn;

@XmlRootElement(name = "subscription_add_on")
public class SubscriptionAddOns extends RecurlyObjects<SubscriptionAddOn> {

    @XmlElement(name = "subscription_add_on")
    private List<SubscriptionAddOn> addOns = new ArrayList<SubscriptionAddOn>();

	@Override
	public List<SubscriptionAddOn> getObjects() {
		return getAddOns();
	}

	public List<SubscriptionAddOn> getAddOns() {
		return addOns;
	}

	public void setAddOns(List<SubscriptionAddOn> addOns) {
		this.addOns = addOns;
	}

}