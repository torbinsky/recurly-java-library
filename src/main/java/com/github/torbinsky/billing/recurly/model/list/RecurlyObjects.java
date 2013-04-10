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

package com.github.torbinsky.billing.recurly.model.list;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.torbinsky.billing.recurly.model.RecurlyObject;

/**
 * Container for a collection of objects (e.g. accounts, coupons, plans, ...)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RecurlyObjects<T extends RecurlyObject> {
	@JsonIgnore
	public abstract List<T> getObjects();
}
