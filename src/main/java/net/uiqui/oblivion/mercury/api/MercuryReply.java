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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangMap;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

import net.uiqui.oblivion.mercury.error.InvalidResponseException;
import net.uiqui.oblivion.mercury.util.Converter;
import net.uiqui.oblivion.mercury.util.MercuryConstants;

public class MercuryReply implements Serializable {
	private static final long serialVersionUID = 6294092702616550011L;

	private Long status = null;
	private Map<String, Object> params = null;
	private Object payload = null;

	private MercuryReply(final Long status, final Map<String, Object> params, final Object payload) {
		this.status = status;
		this.params = params;
		this.payload = payload;
	}

	public long status() {
		return status;
	}

	public Map<String, Object> params() {
		return params;
	}

	@SuppressWarnings("unchecked")
	public <T> T payload() {
		return (T) payload;
	}

	@Override
	public String toString() {
		return "MercuryResponse [status=" + status + ", params=" + params + ", payload=" + payload + "]";
	}

	public static MercuryReply parse(final OtpErlangObject response) throws InvalidResponseException {
		if (response == null) {
			return null;
		}

		if (response instanceof OtpErlangTuple) {
			final OtpErlangTuple tuple = (OtpErlangTuple) response;

			if (tuple.arity() != 4) {
				throw new InvalidResponseException("Response is invalid - expecting tuple/4 received tuple/" + tuple.arity());
			}

			final OtpErlangObject tuple0 = tuple.elementAt(0);

			if (tuple0 instanceof OtpErlangAtom) {
				final OtpErlangAtom tupleType = (OtpErlangAtom) tuple0;

				if (tupleType.atomValue().equals(MercuryConstants.REPLY)) {
					try {
						final Long status = parseStatus(tuple.elementAt(1));
						final Map<String, Object> params = parseParams(tuple.elementAt(2));
						final Object payload = parsePayload(tuple.elementAt(3));

						return new MercuryReply(status, params, payload);
					} catch (InvalidResponseException e) {
						throw e;
					} catch (Exception e) {
						throw new InvalidResponseException("Response is invalid - error parsing response tuple", e);
					}
				} else {
					throw new InvalidResponseException(
							"Response is invalid - expecting tuple.elementAt(0) to be 'response', it was " + tupleType.atomValue());
				}
			} else {
				throw new InvalidResponseException("Response is invalid - expecting tuple.elementAt(0) to be an atom");
			}
		}

		throw new InvalidResponseException("Response is invalid!");
	}

	private static Long parseStatus(final OtpErlangObject elementAt) throws InvalidResponseException {
		if (elementAt == null) {
			throw new InvalidResponseException("Response is invalid - status can't be null");
		}

		if (elementAt instanceof OtpErlangLong) {
			final OtpErlangLong status = (OtpErlangLong) elementAt;

			return status.longValue();
		}

		throw new InvalidResponseException("Response is invalid - status must be an long");
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> parseParams(final OtpErlangObject elementAt) throws InvalidResponseException {
		if (elementAt == null) {
			return null;
		}

		if (elementAt instanceof OtpErlangMap) {
			return (Map<String, Object>) Converter.decode(elementAt);
		}

		throw new InvalidResponseException("Response is invalid - params is not a map");
	}

	private static Object parsePayload(final OtpErlangObject elementAt) {
		if (elementAt == null) {
			return null;
		}

		if (elementAt instanceof OtpErlangAtom) {
			final OtpErlangAtom payload = (OtpErlangAtom) elementAt;

			if (payload.atomValue().equals(MercuryConstants.EMPTY)) {
				return null;
			}
		}

		return Converter.decode(elementAt);
	}

}
