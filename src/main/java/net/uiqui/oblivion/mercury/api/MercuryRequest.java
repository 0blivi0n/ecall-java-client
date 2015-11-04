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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import net.uiqui.oblivion.mercury.util.Converter;
import net.uiqui.oblivion.mercury.util.MercuryConstants;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpExternal;
import com.ericsson.otp.erlang.OtpOutputStream;

public class MercuryRequest implements Serializable {
	private static final long serialVersionUID = 850764874192765477L;

	private OtpErlangTuple tuple = null;

	private MercuryRequest(final OtpErlangTuple tuple) {
		this.tuple = tuple;
	}

	public void write(final OutputStream os) throws IOException {
		final OtpOutputStream stream = new OtpOutputStream();
		stream.write(OtpExternal.versionTag);
		stream.write_any(tuple);
		stream.writeTo(os);
		stream.close();
	}

	public static MercuryRequest build(final String opName, final String[] resource) {
		final Builder builder = new Builder();
		return builder.operation(opName).resource(resource).build();
	}

	public static MercuryRequest build(final String opName, final String[] resource, final Map<String, Object> params) {
		final Builder builder = new Builder();
		return builder.operation(opName).resource(resource).params(params).build();
	}

	public static MercuryRequest build(final String opName, final String[] resource, final Map<String, Object> params, final Object payload) {
		final Builder builder = new Builder();
		return builder.operation(opName).resource(resource).params(params).payload(payload).build();
	}

	public static MercuryRequest build(final String opName, final String[] resource, final Object payload) {
		final Builder builder = new Builder();
		return builder.operation(opName).resource(resource).payload(payload).build();
	}

	private static class Builder {
		private final OtpErlangObject[] elems = new OtpErlangObject[5];

		private Builder() {
			elems[0] = new OtpErlangAtom(MercuryConstants.REQUEST);
			elems[3] = new OtpErlangList();
			elems[4] = new OtpErlangAtom(MercuryConstants.EMPTY);
		}

		public Builder operation(final String opName) {
			elems[1] = Converter.encode(opName);
			return this;
		}

		public Builder resource(final String[] resource) {
			elems[2] = Converter.encode(resource);
			return this;
		}

		public Builder params(final Map<String, Object> params) {
			elems[3] = Converter.encode(params);
			return this;
		}

		public Builder payload(final Object payload) {
			elems[4] = Converter.encode(payload);
			return this;
		}

		public MercuryRequest build() {
			final OtpErlangTuple tuple = new OtpErlangTuple(elems);
			return new MercuryRequest(tuple);
		}
	}
}
