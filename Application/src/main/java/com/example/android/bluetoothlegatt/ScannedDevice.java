package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothDevice;

/**
 * Created by kojira on 16/07/21.
 */
public class ScannedDevice {
    public BluetoothDevice device;
    public byte[] record;
    public String name;
    public String address;
    public float temperature;
    public float humidity;
    public String extra;

    public ScannedDevice(BluetoothDevice device) {
        if (device == null) return;
        this.device = device;
        this.name = device.getName();
        this.address = device.getAddress();
    }

    public ScannedDevice(BluetoothDevice device, byte[] record) {
        if (device == null) return;
        this.device = device;
        this.name = device.getName();
        this.address = device.getAddress();
        this.record = record;
    }
}
