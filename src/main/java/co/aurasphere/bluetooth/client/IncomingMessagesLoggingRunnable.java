/*
 * MIT License
 *
 * Copyright (c) 2018 Donato Rimenti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package co.aurasphere.bluetooth.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

/**
 * Runnable which logs the incoming Bluetooth messages from the remote device.
 * 
 * @author Donato Rimenti
 *
 */
public class IncomingMessagesLoggingRunnable implements Runnable {

	/**
	 * The Bluetooth connection between this device and the remote one.
	 */
	private StreamConnection connection;

	/**
	 * Instantiates a new IncomingMessagesListenerRunnable.
	 *
	 * @param connection
	 *            the {@link #connection}
	 */
	public IncomingMessagesLoggingRunnable(StreamConnection connection) {
		this.connection = connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// Opens the connection. If this fails, the whole listening service
		// fails.
		InputStream input = null;
		RemoteDevice device = null;
		try {
			input = new BufferedInputStream(connection.openInputStream());
			device = RemoteDevice.getRemoteDevice(connection);
		} catch (IOException e) {
			System.err.println("Listening service failed. Incoming messages won't be displayed.");
			e.printStackTrace();
			return;
		}

		// Main loop of the program: reads a string incoming from the
		// Bluetooth connection and prints it.
		while (true) {
			byte buffer[] = new byte[1024];
			int bytesRead;
			try {
				bytesRead = input.read(buffer);
				String incomingMessage = new String(buffer, 0, bytesRead);
				System.out.println("[" + device.getFriendlyName(false) + " - " + device.getBluetoothAddress() + "]: "
						+ incomingMessage);
			} catch (IOException e) {
				// Don't rethrow this exception so if one message is lost, the
				// service continues listening.
				System.err.println("Error while reading the incoming message.");
				e.printStackTrace();
			}
		}

	}

}
