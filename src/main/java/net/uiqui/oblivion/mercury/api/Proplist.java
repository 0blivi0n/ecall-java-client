/*
 * 0blivi0n-cache
 * ==============
 * Mercury Java Client
 * 
 * Copyright (C) 2015 Joaquim Rocha <jrocha@gmailbox.org>
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.uiqui.oblivion.mercury.api;

import java.util.HashMap;
import java.util.Map;

public class Proplist {
	public static Map<String, Object> build(final String name, final Object value) {
		final Builder builder = new Builder();
		builder.put(name, value);
		return builder.build();
	}
	
	public static class Builder {
		private final Map<String, Object> params = new HashMap<String, Object>();

		public Builder put(final String name, final Object value) {
			params.put(name, value);
			return this;
		}

		public Map<String, Object> build() {
			return params;
		}
	}
}
