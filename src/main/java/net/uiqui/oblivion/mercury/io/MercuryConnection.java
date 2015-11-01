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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.uiqui.oblivion.mercury.api.MercuryRequest;
import net.uiqui.oblivion.mercury.api.MercuryResponse;
import net.uiqui.oblivion.mercury.error.CommunicationError;
import net.uiqui.oblivion.mercury.error.ConnectionError;
import net.uiqui.oblivion.mercury.error.InvalidResponseException;

import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpInputStream;

public class MercuryConnection {
	private Socket socket = null;
	private final byte[] buffer = new byte[65507];

	public MercuryConnection(final String server, final int port) throws ConnectionError {
		try {
			socket = new Socket(server, port);
			socket.setSoTimeout(5000);
		} catch (Exception e) {
			throw new ConnectionError("Error connecting to server", e);
		}
	}

	public MercuryResponse call(final MercuryRequest request) throws CommunicationError, InvalidResponseException {
		try {
			final OutputStream out = socket.getOutputStream();
			final InputStream in = socket.getInputStream();

			request.write(out);
			out.flush();

			final int bytesReaded = in.read(buffer);

			if (bytesReaded > 0) {
				final byte[] payload = new byte[bytesReaded];
				System.arraycopy(buffer, 0, payload, 0, bytesReaded);

				final OtpInputStream buf = new OtpInputStream(payload);
				final OtpErlangObject response = OtpErlangObject.decode(buf);

				return MercuryResponse.parse(response);
			}

			return null;
		} catch (Exception e) {
			throw new CommunicationError("Error sending request to server", e);
		}
	}

	public boolean isOpen() {
		if (socket == null) {
			return false;
		}

		return !socket.isClosed() && socket.isConnected() && !socket.isOutputShutdown() && !socket.isInputShutdown();
	}

	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

}
