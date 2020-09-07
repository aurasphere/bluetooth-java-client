[![Donate](https://img.shields.io/badge/Donate-PayPal-orange.svg)](https://www.paypal.com/donate/?cmd=_donations&business=8UK2BZP2K8NSS)

# Bluetooth Java Client
Simple Bluetooth Java client based on the Bluecove library.

This program can be used to perform the following operations:

 - perform Bluetooth device discovery by passing no arguments
 - connect to a Bluetooth device and open an IRC-style chat by passing a Bluetooth RFCOMM address

A Bluetooth RFCOMM address has the following format:

    btspp://<device_address>:<CN>
   
For reference, here's my Arduino Bluetooth address: 

    btspp://98D3318041DE:1.

The program needs that the device to which connect is paired before-hand (through your PC settings). If working with an Arduino, remember that the passcode is something like 1234.
