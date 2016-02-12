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

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.torbinsky.billing.recurly.model.list.Redemptions;

public class TestRedemptions extends TestModelBase {

    @Test(groups = "fast")
    public void testDeserialization() throws Exception {
        // See http://docs.recurly.com/api/adjustments
        final String redemptionsData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
        		"<redemptions type=\"array\">"+
        		"  <redemption href=\"https://example.recurly.com/v2/accounts/AHH94028HHSH@/redemption\">"+
        		"    <coupon href=\"https://example.recurly.com/v2/coupons/test\"/>"+
        		"    <account href=\"https://example.recurly.com/v2/accounts/AHH94028HHSH@\"/>"+
        		"    <uuid>a88a9f898aha983h9ah9823ha9ha</uuid>"+
        		"    <single_use type=\"boolean\">true</single_use>"+
        		"    <total_discounted_in_cents type=\"integer\">1000</total_discounted_in_cents>"+
        		"    <currency>USD</currency>"+
        		"    <state>inactive</state>"+
        		"    <coupon_code>test</coupon_code>"+
        		"    <created_at type=\"datetime\">2015-11-25T00:35:16Z</created_at>"+
        		"  </redemption>"+
        		"</redemptions>";

        final Redemptions redemptions = xmlMapper.readValue(redemptionsData, Redemptions.class);
        Assert.assertEquals(redemptions.getObjects().size(), 1);

        final Redemption redemption = redemptions.getObjects().get(0);

        Assert.assertEquals(redemption.getAccount().getHref(), "https://example.recurly.com/v2/accounts/AHH94028HHSH@");
        Assert.assertEquals(redemption.getCurrency(), "USD");
        Assert.assertEquals(redemption.getCreatedAt(), new DateTime("2015-11-25T00:35:16Z"));
        Assert.assertEquals(redemption.getCoupon().getCouponCode(), "test");
    }
}
