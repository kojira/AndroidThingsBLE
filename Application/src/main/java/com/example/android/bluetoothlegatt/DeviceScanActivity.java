/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.e("keyCode:" + keyCode + " KeyEvent:" + event.getAction());
        switch (keyCode) {
            case 19:
                scanLeDevice(true);
                break;
            case 20:
                scanLeDevice(false);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        if (mLeDeviceListAdapter == null) {
            // Initializes list view adapter.
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            setListAdapter(mLeDeviceListAdapter);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanLeDevice(false);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        final android.bluetooth.le.BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) {
            return;
        }
        if (enable) {
            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
            mLeDeviceListAdapter.notifyDataSetChanged();
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<ScannedDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<ScannedDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            synchronized (mLeDevices) {
                boolean detected = false;
                for (int i = 0; i < mLeDevices.size(); i++) {
                    if (mLeDevices.get(i).address.equals(device.getAddress())) {
                        mLeDevices.get(i).name = device.getName();
                        detected = true;
                        break;
                    }
                }
                if (!detected) {
                    ScannedDevice newDevice = new ScannedDevice(device);
                    mLeDevices.add(newDevice);
                }
            }
        }

        public void addDevice(BluetoothDevice device, byte[] record) {
            synchronized (mLeDevices) {
                boolean detected = false;
                for (int i = 0; i < mLeDevices.size(); i++) {
                    if (mLeDevices.get(i).address.equals(device.getAddress())) {
                        mLeDevices.get(i).record = record;
                        mLeDevices.get(i).name = device.getName();
                        detected = true;
                        break;
                    }
                }
                if (!detected) {
                    ScannedDevice newDevice = new ScannedDevice(device, record);
                    mLeDevices.add(newDevice);
                }
            }
        }

        public void addMetaDevice(String name, String address) {
            synchronized (mLeDevices) {
                boolean detected = false;
                for (int i = 0; i < mLeDevices.size(); i++) {
                    if (mLeDevices.get(i).address.equals(address)) {
                        detected = true;
                        break;
                    }
                }
                if (!detected) {
                    ScannedDevice newDevice = new ScannedDevice(null, null);
                    newDevice.address = address;
                    newDevice.name = name;
                    mLeDevices.add(newDevice);
                }
            }
        }

        public void updateMetaDevice(String address, String extra) {
            synchronized (mLeDevices) {
                for (int i = 0; i < mLeDevices.size(); i++) {
                    if (mLeDevices.get(i).address.equals(address)) {
                        mLeDevices.get(i).extra = extra;
                        break;
                    }
                }
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position).device;
        }

        public ScannedDevice getMetaDevice(int position) {
            return mLeDevices.get(position);
        }

        public ScannedDevice getMetaDevice(String address) {
            int i;
            boolean detected = false;
            synchronized (mLeDevices) {
                for (i = 0; i < mLeDevices.size(); i++) {
                    if (mLeDevices.get(i).address.equals(address)) {
                        detected = true;
                        break;
                    }
                }
            }
            if (detected) {
                return mLeDevices.get(i);
            } else {
                return null;
            }
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i).device;
            L.e("address:"+device.getAddress()+" name:"+device.getName());
            if (device != null) {
                final String deviceName = device.getName();
                if (deviceName != null && deviceName.length() > 0) {
                    viewHolder.deviceName.setText(deviceName);
                } else {
                    viewHolder.deviceName.setText(R.string.unknown_device);
                }
                viewHolder.deviceAddress.setText(device.getAddress());
                if (mLeDevices.get(i).record != null) {
                    for (int k=0; k<mLeDevices.get(i).record.length; k++) {
                        L.e("record["+k+"]"+String.format("0x%x",mLeDevices.get(i).record[k]));
                    }
                }
            } else {
                ScannedDevice scannedDevice = mLeDevices.get(i);
                viewHolder.deviceName.setText(scannedDevice.name);
                viewHolder.deviceAddress.setText(scannedDevice.address);
            }

            return view;
        }
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, final ScanResult result) {
                    super.onScanResult(callbackType, result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(result.getDevice());
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}