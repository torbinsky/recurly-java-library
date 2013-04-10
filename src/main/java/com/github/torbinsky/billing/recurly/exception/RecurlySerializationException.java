/*
 * Copyright 2013 Torben
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
package com.github.torbinsky.billing.recurly.exception;

/**
 * @author torben
 *
 * A {@link RecurlyAPIException} that occurs when we are unable to parse a response from the Recurly API
 *
 */
public class RecurlySerializationException extends RecurlyAPIException {

	public RecurlySerializationException() {
		super();
	}

	public RecurlySerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RecurlySerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecurlySerializationException(String message) {
		super(message);
	}

	public RecurlySerializationException(Throwable cause) {
		super(cause);
	}

}
