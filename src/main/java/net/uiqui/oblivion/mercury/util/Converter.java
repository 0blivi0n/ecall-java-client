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
package net.uiqui.oblivion.mercury.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.uiqui.oblivion.mercury.api.JSON;
import net.uiqui.oblivion.mercury.error.DataTypeNotSupported;
import net.uiqui.oblivion.mercury.error.UnexpectedError;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangBinary;
import com.ericsson.otp.erlang.OtpErlangBoolean;
import com.ericsson.otp.erlang.OtpErlangByte;
import com.ericsson.otp.erlang.OtpErlangDouble;
import com.ericsson.otp.erlang.OtpErlangFloat;
import com.ericsson.otp.erlang.OtpErlangInt;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangRangeException;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;

public class Converter {
	private static final Charset utf8 = Charset.forName("UTF-8");

	public static OtpErlangObject encode(final Object input) throws DataTypeNotSupported {
		if (input == null) {
			return null;
		}

		if (input instanceof String) {
			final String value = (String) input;
			return new OtpErlangBinary(value.getBytes(utf8));
		}

		if (input instanceof JSON) {
			final JSON json = (JSON) input;
			final OtpErlangObject[] list = new OtpErlangObject[json.fields().size()];

			int index = 0;

			for (JSON.Field field : json.fields()) {
				OtpErlangObject[] tuple = { Converter.encode(field.name()), Converter.encode(field.value()) };
				list[index++] = new OtpErlangTuple(tuple);
			}

			final OtpErlangList proplist = new OtpErlangList(list);
			return new OtpErlangTuple(proplist);
		}

		if (input instanceof Byte) {
			final Byte value = (Byte) input;
			return new OtpErlangByte(value);
		}

		if (input instanceof Integer) {
			final Integer value = (Integer) input;
			return new OtpErlangInt(value);
		}

		if (input instanceof Long) {
			final Long value = (Long) input;
			return new OtpErlangLong(value);
		}

		if (input instanceof BigInteger) {
			final BigInteger value = (BigInteger) input;
			return new OtpErlangLong(value);
		}

		if (input instanceof Boolean) {
			final Boolean value = (Boolean) input;
			return new OtpErlangBoolean(value);
		}

		if (input instanceof Float) {
			final Float value = (Float) input;
			return new OtpErlangFloat(value);
		}

		if (input instanceof Double) {
			final Double value = (Double) input;
			return new OtpErlangDouble(value);
		}

		if (input instanceof byte[]) {
			final byte[] value = (byte[]) input;
			return new OtpErlangBinary(value);
		}

		if (input instanceof List<?>) {
			final List<?> values = (List<?>) input;
			final OtpErlangObject[] list = new OtpErlangObject[values.size()];

			int index = 0;

			for (Object obj : values) {
				list[index++] = encode(obj);
			}

			return new OtpErlangList(list);
		}

		if (input instanceof Object[]) {
			final Object[] values = (Object[]) input;
			final OtpErlangObject[] list = new OtpErlangObject[values.length];

			int index = 0;

			for (Object obj : values) {
				list[index++] = encode(obj);
			}

			return new OtpErlangList(list);
		}

		if (input instanceof Map<?, ?>) {
			final Map<?, ?> values = (Map<?, ?>) input;
			final OtpErlangObject[] list = new OtpErlangObject[values.size()];

			int index = 0;

			for (Map.Entry<?, ?> entry : values.entrySet()) {
				OtpErlangObject[] tuple = { Converter.encode(entry.getKey()), Converter.encode(entry.getValue()) };
				list[index++] = new OtpErlangTuple(tuple);
			}

			return new OtpErlangList(list);
		}

		throw new DataTypeNotSupported(input.getClass().getName() + " data type is not supported");
	}

	@SuppressWarnings("unchecked")
	public static Object decode(final OtpErlangObject input) throws DataTypeNotSupported, UnexpectedError {
		if (input == null) {
			return null;
		}

		if (input instanceof OtpErlangBinary) {
			final OtpErlangBinary value = (OtpErlangBinary) input;
			return new String(value.binaryValue(), utf8);
		}

		if (input instanceof OtpErlangString) {
			final OtpErlangString value = (OtpErlangString) input;
			return value.stringValue();
		}

		if (input instanceof OtpErlangAtom) {
			final OtpErlangAtom value = (OtpErlangAtom) input;
			return value.atomValue();
		}

		if (input instanceof OtpErlangTuple) {
			final OtpErlangTuple tuple = (OtpErlangTuple) input;

			if (tuple.arity() == 1) {
				final OtpErlangObject obj = tuple.elementAt(0);

				if (obj instanceof OtpErlangList) {
					final OtpErlangList list = (OtpErlangList) obj;
					final JSON json = new JSON();

					for (OtpErlangObject element : list.elements()) {
						if (element instanceof OtpErlangTuple) {
							OtpErlangTuple field = (OtpErlangTuple) element;

							if (field.arity() != 2) {
								throw new DataTypeNotSupported("Expecting tuple/2 inside a list");
							}

							String name = (String) decode(field.elementAt(0));
							Object value = decode(field.elementAt(1));

							json.field(name, value);
						} else {
							throw new DataTypeNotSupported("Expecting tuple inside a list");
						}
					}

					return json;
				}

				return decode(obj);
			}

			final List<Object> list = new ArrayList<Object>();

			for (OtpErlangObject element : tuple.elements()) {
				list.add(decode(element));
			}

			return list;
		}

		if (input instanceof OtpErlangByte) {
			final OtpErlangByte value = (OtpErlangByte) input;

			try {
				return value.byteValue();
			} catch (OtpErlangRangeException e) {
				throw new UnexpectedError("Error retrieving OtpErlangByte", e);
			}
		}

		if (input instanceof OtpErlangInt) {
			final OtpErlangInt value = (OtpErlangInt) input;

			try {
				return value.intValue();
			} catch (OtpErlangRangeException e) {
				throw new UnexpectedError("Error retrieving OtpErlangInt", e);
			}
		}

		if (input instanceof OtpErlangLong) {
			final OtpErlangLong value = (OtpErlangLong) input;

			if (value.isLong()) {
				return value.longValue();
			} else {
				return value.bigIntegerValue();
			}
		}

		if (input instanceof OtpErlangBoolean) {
			final OtpErlangBoolean value = (OtpErlangBoolean) input;
			return value.booleanValue();
		}

		if (input instanceof OtpErlangFloat) {
			final OtpErlangFloat value = (OtpErlangFloat) input;

			try {
				return value.floatValue();
			} catch (OtpErlangRangeException e) {
				throw new UnexpectedError("Error retrieving OtpErlangFloat", e);
			}
		}

		if (input instanceof OtpErlangDouble) {
			final OtpErlangDouble value = (OtpErlangDouble) input;
			return value.doubleValue();
		}

		if (input instanceof OtpErlangList) {
			final OtpErlangList values = (OtpErlangList) input;

			if (values.arity() > 0) {
				final OtpErlangObject first = values.elementAt(0);

				if (first instanceof OtpErlangTuple) {
					final OtpErlangTuple tuple = (OtpErlangTuple) first;

					if (tuple.arity() == 2) {
						@SuppressWarnings("rawtypes")
						final Map map = new HashMap();

						for (OtpErlangObject obj : values.elements()) {
							if (obj instanceof OtpErlangTuple) {
								OtpErlangTuple kv = (OtpErlangTuple) obj;

								if (kv.arity() == 2) {
									map.put(decode(kv.elementAt(0)), decode(kv.elementAt(1)));
								} else {
									throw new UnexpectedError("Expecting an OtpErlangTuple/2 inside the OtpErlangList");
								}
							} else {
								throw new UnexpectedError("Expecting an OtpErlangTuple inside the OtpErlangList");
							}
						}

						return map;
					}
				}
			}
			
			final List<Object> list = new ArrayList<Object>();

			for (OtpErlangObject obj : values.elements()) {
				list.add(decode(obj));
			}

			return list;
		}

		throw new DataTypeNotSupported(input.getClass().getName() + " data type is not supported");
	}
}
