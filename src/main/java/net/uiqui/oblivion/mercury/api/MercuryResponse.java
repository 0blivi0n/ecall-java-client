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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.uiqui.oblivion.mercury.error.InvalidResponseException;
import net.uiqui.oblivion.mercury.util.Converter;
import net.uiqui.oblivion.mercury.util.MercuryConstants;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangTuple;

/**
 * The Class MercuryResponse.
 */
public class MercuryResponse implements Serializable {
	private static final long serialVersionUID = 6294092702616550011L;

	private Long status = null;
	private List<Param> params = null;
	private Object payload = null;

	private MercuryResponse(final Long status, final List<Param> params, final Object payload) {
		this.status = status;
		this.params = params;
		this.payload = payload;
	}

	/**
	 * Status.
	 *
	 * @return the long
	 */
	public long status() {
		return status;
	}

	/**
	 * Params.
	 *
	 * @return the list
	 */
	public List<Param> params() {
		return params;
	}

	/**
	 * Params2 map.
	 *
	 * @return the map
	 */
	public Map<String, Object> params2Map() {
		if (params == null) {
			return null;
		}

		final Map<String, Object> map = new HashMap<String, Object>();

		for (Param param : params) {
			map.put(param.name(), param.value());
		}

		return map;
	}

	/**
	 * Payload.
	 *
	 * @return the object
	 */
	public Object payload() {
		return payload;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MercuryResponse [status=" + status + ", params=" + params + ", payload=" + payload + "]";
	}

	/**
	 * Parses the.
	 *
	 * @param response the response
	 * @return the mercury response
	 * @throws InvalidResponseException the invalid response exception
	 */
	public static MercuryResponse parse(final OtpErlangObject response) throws InvalidResponseException {
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

				if (tupleType.atomValue().equals(MercuryConstants.RESPONSE)) {
					try {
						final Long status = parseStatus(tuple.elementAt(1));
						final List<Param> params = parseParams(tuple.elementAt(2));
						final Object payload = parsePayload(tuple.elementAt(3));

						return new MercuryResponse(status, params, payload);
					} catch (InvalidResponseException e) {
						throw e;
					} catch (Exception e) {
						throw new InvalidResponseException("Response is invalid - error parsing response tuple", e);
					}
				} else {
					throw new InvalidResponseException("Response is invalid - expecting tuple.elementAt(0) to be 'response', it was "
							+ tupleType.atomValue());
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

	private static List<Param> parseParams(final OtpErlangObject elementAt) throws InvalidResponseException {
		if (elementAt == null) {
			return null;
		}
		
		if (elementAt instanceof OtpErlangList) {
			final OtpErlangList list = (OtpErlangList) elementAt;
			final List<Param> params = new ArrayList<Param>();
			
			for (OtpErlangObject obj : list) {
				if (obj instanceof OtpErlangTuple) {
					OtpErlangTuple tuple = (OtpErlangTuple) obj;
					
					if (tuple.arity() != 2) {
						throw new InvalidResponseException("Response is invalid - params is not a list of tuple/2");
					}
					
					String name = (String) Converter.decode(tuple.elementAt(0));
					Object value = Converter.decode(tuple.elementAt(1));
					
					params.add(new Param(name, value));
				} else {
					throw new InvalidResponseException("Response is invalid - params is not a list of tuple/2");
				}
			}
			
			return params;
		}
		
		throw new InvalidResponseException("Response is invalid - params is not a list of tuple/2");
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
