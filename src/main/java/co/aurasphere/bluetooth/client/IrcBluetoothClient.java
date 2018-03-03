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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 * Simple Bluetooth Java client based on the Bluecove library. It implements two
 * methods to perform device discovery and Bluetooth connection in IRC
 * style.<br>
 * <br>
 * This script assumes that the Bluetooth device is already paired using your PC
 * settings (if you are using Arduino default passcodes should be something like
 * 1234).
 * 
 * @author Donato Rimenti
 *
 */
public class IrcBluetoothClient {

	/**
	 * Starts the Bluetooth devices discovery. Close-by devices are printed in
	 * console.
	 * 
	 * @throws BluetoothStateException
	 * @throws InterruptedException 
	 */
	private static void startDiscovery() throws BluetoothStateException, InterruptedException {
		DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		System.out.println("Starting device discovery...");
		agent.startInquiry(DiscoveryAgent.GIAC, new DeviceDiscoveredLoggingCallback());
		
		// Very basic synchronization mechanism.
		synchronized (IrcBluetoothClient.class) {
			IrcBluetoothClient.class.wait();
		}
	}

	/**
	 * Opens up a connection to the specified address. After the connection is
	 * open, it's possible to send and receive messages like in an IRC.
	 * 
	 * @param address
	 *            the address to which connect
	 * @throws IOException
	 */
	private static void openConnection(String address) throws IOException {
		// Tries to open the connection.
		StreamConnection connection = (StreamConnection) Connector.open(address);
		if (connection == null) {
			System.err.println("Could not open connection to address: " + address);
			System.exit(1);
		}

		// Initializes the streams.
		OutputStream output = connection.openOutputStream();
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(isr);

		// Starts the listening service for incoming messages.
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(new IncomingMessagesLoggingRunnable(connection));

		// Main loop of the program: reads a string and sends to the Bluetooth
		// device.
		System.out.println("Connection opened, type in console and press enter to send a message to: " + address);
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		while (true) {
			String toSend = reader.readLine();
			byte[] toSendBytes = toSend.getBytes(StandardCharsets.US_ASCII);
			output.write(toSendBytes);
			System.out.println(
					"[" + localDevice.getFriendlyName() + " - " + localDevice.getBluetoothAddress() + "]: " + toSend);
		}
	}

	/**
	 * Main method of this program. It's behavior depends on the number of
	 * arguments passed:
	 * <ul>
	 * <li>if no arguments are passed, starts Bluetooth discovery</li>
	 * <li>if one argument is passed, it is interepreted as a Bluetooth address
	 * to which connect. If the connection is succesfull, an IRC chat is
	 * started</li>
	 * </ul>
	 * 
	 * An RFCOMM Bluetooth URL follows the structure:
	 * <ul>
	 * <li>btspp://</li>
	 * <li>bluetooth address</li>
	 * <li>CN (equivalent of a TCP/IP port for the service you want to use)</li>
	 * </ul>
	 * 
	 * For reference, here's an example address from my Arduino:
	 * btspp://98D3318041DE:1.
	 * 
	 * @param args
	 *            an optional Bluetooth address to which connect
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// Prints some informations at startup about this device.
		LocalDevice local = LocalDevice.getLocalDevice();
		System.out.println("----------- LOCAL DEVICE INFORMATION -----------");
		System.out.println("Address: " + local.getBluetoothAddress());
		System.out.println("Name: " + local.getFriendlyName());

		// Checks the number of arguments passed.
		switch (args.length) {
		case 0:
			// If no arguments are passed, performs device discovery.
			startDiscovery();
			break;
		case 1:
			// If 1 argument is passed, it is the Bluetooth address to connect
			// with.
			openConnection(args[0]);
			break;
		default:
			// If more arguments are passed, the help is printed.
			System.err.println("Usage:");
			System.err.println("<no-args> -> starts device discovery");
			System.err.println("<bluetooth-address> -> opens IRC chat with that device through Bluetooth");
			break;
		}
	}

}
