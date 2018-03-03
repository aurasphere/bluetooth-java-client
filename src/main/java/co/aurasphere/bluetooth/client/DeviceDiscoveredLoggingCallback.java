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

import java.io.IOException;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * Callback to handle device discovery event. It only logs the names and
 * addresses of the device discovered. It ignores the services discovered.
 * 
 * @author Donato Rimenti
 *
 */
public class DeviceDiscoveredLoggingCallback implements DiscoveryListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int,
	 * javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] services) {
		// Not supported.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public void serviceSearchCompleted(int transID, int respCode) {
		// Not supported.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public void inquiryCompleted(int discType) {
		// Logs the end of the device discovery.
		System.out.println("Device discovery completed!");

		// Very basic synchronization mechanism.
		synchronized (IrcBluetoothClient.class) {
			IrcBluetoothClient.class.notify();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.
	 * RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		// Logs the device discovered.
		String address = btDevice.getBluetoothAddress();
		try {
			String name = btDevice.getFriendlyName(false);
			System.out.println("New device discovered: [" + address + " - " + name + "]");
		} catch (IOException e) {
			System.err.println("Error while retrieving name for device [" + address + "]");
			e.printStackTrace();
		}
	}
}
