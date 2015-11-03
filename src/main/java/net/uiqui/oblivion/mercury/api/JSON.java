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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class JSON.
 */
public class JSON {
	private final List<Field> fields = new ArrayList<Field>();
	
	/**
	 * Instantiates a new json.
	 *
	 * @param fields the fields
	 */
	public JSON(final Field...fields) {
		for (Field field : fields) {
			this.fields.add(field);
		}
	}
	
	/**
	 * Field.
	 *
	 * @param name the name
	 * @param value the value
	 * @return the json
	 */
	public JSON field(final String name, final Object value) {
		fields.add(new Field(name, value));
		return this;
	}
	
	/**
	 * Fields.
	 *
	 * @return the list
	 */
	public List<Field> fields()  {
		return fields;
	}
	
	/**
	 * To map.
	 *
	 * @return the map
	 */
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<String, Object>();
		
		for (Field field : fields) {
			map.put(field.name(), field.value());
		}
		
		return map;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("{");
		
		boolean first = true;
		
		for (Field field : fields) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}
			
			builder.append(field);
		}
		
		builder.append("}");
		
		return builder.toString();
	}	
	
	/**
	 * From map.
	 *
	 * @param fieldMap the field map
	 * @return the json
	 */
	public static JSON fromMap(final Map<String, Object> fieldMap) {
		final JSON json = new JSON();
		
		for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
			json.field(entry.getKey(), entry.getValue());
		}
		
		return json;
	}
	
	/**
	 * The Class Field.
	 */
	public static class Field extends Param {
		private static final long serialVersionUID = -1349404110784531443L;

		/**
		 * Instantiates a new field.
		 *
		 * @param name the name
		 * @param value the value
		 */
		public Field(final String name, final Object value) {
			super(name, value);
		}

		/* (non-Javadoc)
		 * @see net.uiqui.oblivion.mercury.api.Param#toString()
		 */
		@Override
		public String toString() {
			return "'" + name() + "' : " + value();
		}
	}
}
