# BLE-HID-Peripheral-for-Android
[![Build Status](https://travis-ci.org/kshoji/BLE-HID-Peripheral-for-Android.svg?branch=master)](https://travis-ci.org/kshoji/BLE-HID-Peripheral-for-Android)

## BLE HID over GATT Profile for Android

This library provides BLE HID Peripheral feature to Android devices. <br/>
Android device will behave as:

- BLE Mouse (relative position / absolute position)
- BLE Keyboard
- BLE Joystick

Tested connection:

- Android(Peripheral) <--> Android(Central)
    - Relative Position Mouse, Keyboard
- Android(Peripheral) <--> OS X(Central)
    - Absolute Position Mouse, Relative Position Mouse, Keyboard

Currently, connection with iOS central is not tested yet.

Requirements
------------

- **API Level 21 or later** and **Bluetooth LE Peripheral feature** will be needed.

Repository Overview
-------------------

- Library Project: `lib`
- Sample Project: `app`

LICENSE
=======
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

D/BluetoothGattServer: onServerConnectionState() - status=0 serverIf=5 device=64:49:02:BC:95:37
D/HidPeripheral: onConnectionStateChange status: 0, newState: 2
D/HidPeripheral: BluetoothProfile.STATE_CONNECTED bondState: 12
D/BluetoothGattServer: connect() - device: 64:49:02:BC:95:37, auto: true
D/BluetoothGattServer: onMtuChanged() - device=64:49:02:BC:95:37, mtu=185
I/Timeline: Timeline: Activity_idle id: android.os.BinderProxy@183d3ed time:11784984
D/HidPeripheral: onCharacteristicReadRequest characteristic: 00002a29-0000-1000-8000-00805f9b34fb, offset: 0
D/HidPeripheral: onCharacteristicReadRequest characteristic: 00002a24-0000-1000-8000-00805f9b34fb, offset: 0
D/HidPeripheral: onCharacteristicReadRequest characteristic: 00002a25-0000-1000-8000-00805f9b34fb, offset: 0
D/HidPeripheral: onCharacteristicReadRequest characteristic: 00002a4a-0000-1000-8000-00805f9b34fb, offset: 0
D/HidPeripheral: onCharacteristicReadRequest characteristic: 00002a4b-0000-1000-8000-00805f9b34fb, offset: 0
E/HidPeripheral: onCharacteristicReadRequest offset:0
D/HidPeripheral: onDescriptorReadRequest requestId: 7, offset: 0, descriptor: 00002908-0000-1000-8000-00805f9b34fb
D/HidPeripheral: onDescriptorReadRequest requestId: 8, offset: 0, descriptor: 00002908-0000-1000-8000-00805f9b34fb
D/HidPeripheral: onDescriptorWriteRequest descriptor: 00002902-0000-1000-8000-00805f9b34fb, value: [1, 0], responseNeeded: true, preparedWrite: false
D/HidPeripheral: onDescriptorWriteRequest descriptor: 00002902-0000-1000-8000-00805f9b34fb, value: [1, 0], responseNeeded: true, preparedWrite: false
D/HidPeripheral: onCharacteristicReadRequest characteristic: 00002a19-0000-1000-8000-00805f9b34fb, offset: 0


