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
package net.uiqui.oblivion.mercury;

import net.uiqui.oblivion.mercury.api.MercuryRequest;
import net.uiqui.oblivion.mercury.api.MercuryReply;
import net.uiqui.oblivion.mercury.io.ConnectionPool;
import net.uiqui.oblivion.mercury.io.MercuryConnection;
import net.uiqui.oblivion.mercury.io.MercuryConnectionFactory;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class Mercury {
	private ConnectionPool connectionPool = null;

	public Mercury(final GenericObjectPoolConfig config, final MercuryConnectionFactory factory) {
		this.connectionPool = new ConnectionPool(config, factory);
	}

	public Mercury(final MercuryConnectionFactory factory) {
		this.connectionPool = new ConnectionPool(factory);
	}

	public Mercury(final String server, final int port) {
		this.connectionPool = new ConnectionPool(new MercuryConnectionFactory(server, port));
	}

	public MercuryReply call(final MercuryRequest request) throws Exception {
		final MercuryConnection conn = connectionPool.getConnection();

		try {
			return conn.call(request);
		} finally {
			connectionPool.returnObject(conn);
		}
	}
}
