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
package net.uiqui.oblivion.mercury.io;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ConnectionPool {
	private GenericObjectPool<MercuryConnection> pool = null;
	
	public ConnectionPool(final GenericObjectPoolConfig config, final MercuryConnectionFactory factory) {
		pool = new GenericObjectPool<MercuryConnection>(factory, config);
	}
	
	public ConnectionPool(final MercuryConnectionFactory factory) {
		this(getDefaultConfig(), factory);
	}
	
	public MercuryConnection getConnection() throws Exception {
		return pool.borrowObject();
	}
	
	public void returnObject(final MercuryConnection connection) {
		try {
			pool.returnObject(connection);
		} catch (Exception e) {
			// Who cares?
		}
	}

	private static GenericObjectPoolConfig getDefaultConfig() {
		final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(1);
		config.setTestOnBorrow(true);
		
		return config;
	}
}
