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
import javax.xml.bind.annotation.XmlTransient;

import com.github.torbinsky.billing.recurly.model.Redemption;

@XmlRootElement(name = "redemptions")
public class Redemptions extends RecurlyObjects<Redemption> {

    @XmlTransient
    public static final String REDEMPTIONS_RESOURCE = "/redemptions";
    
    @XmlElement(name = "redemption")
    private List<Redemption> redemptions = new ArrayList<Redemption>();

	@Override
	public List<Redemption> getObjects() {
		return redemptions;
	}
	
	public void setInvoices(List<Redemption> redemptionList) {
        this.redemptions = redemptionList;
    }
}
