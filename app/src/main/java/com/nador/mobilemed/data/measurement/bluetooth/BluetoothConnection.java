package com.nador.mobilemed.data.measurement.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;

import com.nador.mobilemed.data.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import rx.Observable;
import rx.subscriptions.Subscriptions;
import timber.log.Timber;

/**
 * Created by nador on 06/06/16.
 */
public class BluetoothConnection {

    // Status of the running device's Bluetooth adapter
    public enum BluetoothAdapterStatus {
        BT_UNAVAILABLE,
        BT_NOT_ENABLED,
        BT_READY
    }

    // Status of Bluetooth device with provided address
    public enum BluetoothDeviceStatus {
        BT_DEVICE_UNAVAILABLE,
        BT_DEVICE_CONNECTED,
        BT_DEVICE_DISCONNECTED
    }

    public class BluetoothAdapterException extends Exception {
        public BluetoothAdapterException(String message) {
            super(message);
        }
    }

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDeviceConnection mDeviceConnection;

    public BluetoothConnection(Context context, BluetoothAdapter bluetoothAdapter) {
        mContext = context;
        mBluetoothAdapter = bluetoothAdapter;
    }

    private BluetoothAdapterStatus getStatus() {
        if (mBluetoothAdapter == null) {
            return BluetoothAdapterStatus.BT_UNAVAILABLE;
        } else if (!mBluetoothAdapter.isEnabled()) {
            return BluetoothAdapterStatus.BT_NOT_ENABLED;
        } else {
            return BluetoothAdapterStatus.BT_READY;
        }
    }

    /**
     * Attempt to find BT device with the provided address
     * Mainly needed for determining the type of BT connection (Classic or Smart)
     *
     * @param deviceAddress MAC address of the target device
     * @return The device if found, null otherwise
     */
    public Observable<BluetoothDevice> findDevice(String deviceAddress) {
        return Observable.create(subscriber -> {
            Timber.d("Attempt to find device with address: %s", deviceAddress);

            if (getStatus() != BluetoothAdapterStatus.BT_READY) {
                subscriber.onError(new BluetoothAdapterException("Bluetooth not enabled!"));
            }

            // Try to find device in the bonded devices list (beware: it doesn't mean that the device is available)
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (!pairedDevices.isEmpty()) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getAddress().toUpperCase().equals(deviceAddress)) {
                        Timber.d("Found our device in the paired list!");
                        subscriber.onNext(device);
                        subscriber.onCompleted();
                        return;
                    }
                }
            }

            // Cancel discovery first just in case
            mBluetoothAdapter.cancelDiscovery();

            // If not found, add broadcast receiver for device discovery (discovered device means it's available)
            final BroadcastReceiver receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        Timber.d("Found device with address: %s and type: %s", device.getAddress(), device.getType());
                        if (device.getAddress().toUpperCase().equals(deviceAddress)) {
                            Timber.d("Found our device!");
                            mBluetoothAdapter.cancelDiscovery();
                            subscriber.onNext(device);
                            subscriber.onCompleted();
                        }
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        Timber.d("Discovery ended!");
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }
                }
            };
            // Add unsubscribe handler for unregistering the broadcast receiver
            subscriber.add(Subscriptions.create(() -> {
                Timber.d("Unregistering BT discovery broadcast receiver");
                mContext.unregisterReceiver(receiver);
            }));

            // Filter "found device" and "discovery finished" for termination
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

            HandlerThread handlerThread = new HandlerThread("BroadcastReceiverThread");
            handlerThread.start();
            mContext.registerReceiver(receiver, filter, null, new Handler(handlerThread.getLooper()));
            mBluetoothAdapter.startDiscovery();
        });
    }

    /**
     * Connect to the target Bluetooth device according to it's BT protocol (Classic or Smart)
     *
     * @param device The target device
     * @return The status of the device wrapped in Observable
     */
    public Observable<BluetoothDeviceStatus> connect(BluetoothDevice device) {
        // Cancel discovery first just in case
        mBluetoothAdapter.cancelDiscovery();

        return Observable.create(subscriber -> {
            Timber.d("Try to connect to device: %s", device.toString());

            if (getStatus() != BluetoothAdapterStatus.BT_READY) {
                subscriber.onError(new BluetoothAdapterException("Bluetooth not enabled!"));
            }

            if (device == null) {
                subscriber.onNext(BluetoothDeviceStatus.BT_DEVICE_UNAVAILABLE);
                subscriber.onCompleted();
                return;
            }

            Timber.d("Type of BT device: %d", device.getType());

            switch (device.getType()) {
                case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                    mDeviceConnection = new BluetoothClassicDeviceConnection(device);
                    break;
                case BluetoothDevice.DEVICE_TYPE_DUAL:
                case BluetoothDevice.DEVICE_TYPE_LE:
                    mDeviceConnection = new BluetoothLEDeviceConnection(device);
                    break;
                case BluetoothDevice.DEVICE_TYPE_UNKNOWN:
                    break;
            }

            mDeviceConnection.connect().subscribe(aBoolean -> {
                subscriber.onNext(aBoolean ? BluetoothDeviceStatus.BT_DEVICE_CONNECTED : BluetoothDeviceStatus.BT_DEVICE_UNAVAILABLE);
                if (aBoolean) {
                    Timber.d("Device: %s", device.toString());
                }
                subscriber.onCompleted();
            });
        });
    }

    /**
     * Helper function for connecting with device address
     *
     * @param deviceAddress MAC address of the target device
     * @return The status of the device wrapped in Observable
     */
    public Observable<BluetoothDeviceStatus> connect(String deviceAddress) {
        Timber.d("Try to connect to device with address: %s", deviceAddress);
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        return connect(device);
    }

    /**
     * Disconnect the Bluetooth device
     *
     * @return The status of the device wrapped in Observable
     */
    public Observable<BluetoothDeviceStatus> disconnect() {
        return Observable.create(subscriber -> {
            if (getStatus() != BluetoothAdapterStatus.BT_READY) {
                subscriber.onError(new BluetoothAdapterException("Bluetooth not enabled!"));
            }

            if (mDeviceConnection == null) {
                subscriber.onError(new Throwable("There is no connection which could be disconnected!"));
            }

            mDeviceConnection.disconnect().subscribe(aBoolean -> {
                subscriber.onNext(aBoolean ? BluetoothDeviceStatus.BT_DEVICE_DISCONNECTED : BluetoothDeviceStatus.BT_DEVICE_UNAVAILABLE);
                subscriber.onCompleted();
            });
        });
    }

    /**
     * Send command to Bluetooth device
     *
     * @param command Control command
     * @return Hex answer wrapped in Observable
     */
    public Observable<byte[]> sendCommand(byte[] command) {
        if (getStatus() != BluetoothAdapterStatus.BT_READY) {
            return Observable.error(new BluetoothAdapterException("Bluetooth not enabled!"));
        }

        if (mDeviceConnection == null) {
            return Observable.error(new Throwable("There is no connection on which a command could be sent!"));
        }

        return mDeviceConnection.write(command);
    }

    /**
     * Abstract class representing a connection to a device allowing communication
     */
    private abstract class BluetoothDeviceConnection {

        // UUID for connecting to serial communicating devices
        protected static final String SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb";

        protected BluetoothDevice mDevice;

        public Observable<Boolean> connect() {
            return null;
        }

        public Observable<Boolean> disconnect() {
            return null;
        }

        /**
         *  Write a hex command and wait for the answer
         *
         * @param command Control command
         * @return Hex answer wrapped in Observable
         */
        public Observable<byte[]> write(byte[] command) {
            return null;
        }
    }

    /**
     * Device connection using Bluetooth Classic protocol
     */
    private final class BluetoothClassicDeviceConnection extends BluetoothDeviceConnection {

        private final BluetoothSocket mSocket;
        private InputStream mInput;
        private OutputStream mOutput;

        public BluetoothClassicDeviceConnection(BluetoothDevice device) {
            mDevice = device;

            BluetoothSocket socket = null;
            try {
                socket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
                Timber.i("RFCOMM socket created with UUID: %s", SPP_UUID);
            } catch (IOException e) {
                Timber.e(e, "Could not create RFCOMM socket with UUID: %s", SPP_UUID);
            }
            mSocket = socket;
        }

        @Override
        public Observable<Boolean> connect() {
            return Observable.create(subscriber -> {
                if (mSocket == null) {
                    subscriber.onError(new Throwable("RFCOMM socket does not exist!"));
                }

                Timber.d("Try to connect to RFCOMM socket with UUID: %s", SPP_UUID);
                try {
                    mSocket.connect();
                    Timber.i("Connected to RFCOMM socket with UUID: %s", SPP_UUID);
                    subscriber.onNext(true);
                } catch (IOException e) {
                    Timber.e(e, "RFCOMM socket connect failed, closing...");
                    try {
                        mSocket.close();
                    } catch (IOException e1) {}
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            });
        }

        @Override
        public Observable<Boolean> disconnect() {
            return Observable.create(subscriber -> {
                if (mSocket == null) {
                    subscriber.onError(new Throwable("RFCOMM socket does not exist!"));
                }

                Timber.d("Try to disconnect RFCOMM socket with UUID: %s", SPP_UUID);

                try {
                    mSocket.close();
                    Timber.i("Disconnected RFCOMM socket with UUID: %s", SPP_UUID);
                    subscriber.onNext(true);
                } catch (IOException e) {
                    Timber.e(e, "RFCOMM socket disconnect failed!");
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            });
        }

        @Override
        public Observable<byte[]> write(byte[] command) {
            return Observable.create(subscriber -> {
                if (mSocket == null || !mSocket.isConnected()) {
                    subscriber.onError(new Throwable("RFCOMM socket does not exist or isn't connected!"));
                }

                // Try to get input and output streams from the socket
                try {
                    mInput = mSocket.getInputStream();
                    mOutput = mSocket.getOutputStream();
                } catch (IOException e) {
                    subscriber.onError(new Throwable("Could not get BT input and output stream!"));
                }

                // Try to write the command to the output
                try {
                    mOutput.write(command);
                } catch (IOException e) {
                    subscriber.onError(new Throwable("Could not write to output stream!"));
                }

                // Allocate the buffer for the incoming data (8 bytes should be enough)
                byte[] buffer = new byte[8];
                int bytes = 0;

                // Read until input is empty (throws IOException)
                // Emit bytes truncated to the length of received bytes
                while (true) {
                    try {
                        bytes = mInput.read(buffer);
                        subscriber.onNext(ByteUtils.truncate(buffer, bytes));
                    } catch (IOException e) {
                        break;
                    }
                }

                subscriber.onCompleted();
            });
        }
    }

    /**
     * Device connection using Bluetooth Smart protocol
     */
    private final class BluetoothLEDeviceConnection extends BluetoothDeviceConnection {

        private BluetoothGatt mGatt;

        private LEConnectionListener mConnListener;
        private LEDataListener mDataListener;

        public BluetoothLEDeviceConnection(BluetoothDevice device) {
            mDevice = device;
        }

        @Override
        public Observable<Boolean> connect() {
            return Observable.create(subscriber -> {
                if (mGatt == null) {
                    mConnListener = new LEConnectionListener() {
                        @Override
                        public void leDeviceConnected() {
                            subscriber.onNext(true);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void leDeviceDisconnected() {
                            subscriber.onError(new Throwable("Can't disconnect LE device in connect function!"));
                        }
                    };
                    subscriber.add(Subscriptions.create(() -> mConnListener = null));

                    Timber.d("Connect to LE device.");
                    mDevice.connectGatt(mContext, false, mGattCallback);
                }
            });
        }

        @Override
        public Observable<Boolean> disconnect() {
            return Observable.create(subscriber -> {
                if (mGatt == null) {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                    return;
                }

                mConnListener = new LEConnectionListener() {
                    @Override
                    public void leDeviceConnected() {
                        subscriber.onError(new Throwable("Can't connect LE device in disconnect function!"));
                    }

                    @Override
                    public void leDeviceDisconnected() {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }
                };
                subscriber.add(Subscriptions.create(() -> mConnListener = null));

                Timber.d("Disconnect LE device.");
                mGatt.disconnect();
                mGatt.close();
                mGatt = null;
            });
        }

        private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        Timber.i("Connected to GATT server.");
                        if (mConnListener != null) {
                            mConnListener.leDeviceConnected();
                        }
                        if (mGatt != null) {
                            mGatt.discoverServices();
                        }
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        Timber.i("Disconnected from GATT server.");
                        if (mConnListener != null) {
                            mConnListener.leDeviceDisconnected();
                        }
                        break;
                    default:
                        Timber.i("GATT other state.");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS && mGatt != null) {
                    findGattService(mGatt.getServices());
                } else {
                    Timber.w("onServicesDiscovered received: %s", status);
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);

                String dataValue = characteristic.getStringValue(0);
                Timber.d("Receiving data: %s", dataValue);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);

                // TODO: implement read
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);

                // TODO: implement write
            }
        };

        private void findGattService(List<BluetoothGattService> gattServices) {
            if (gattServices == null) {
                Timber.d("findGattService found no Services");
                return;
            }

            // TODO: implement service discovery
        }
    }

    /**
     * Listener for LE device connection events
     * Needed for operating GATT callback with Rx
     */
    private interface LEConnectionListener {
        void leDeviceConnected();
        void leDeviceDisconnected();
    }

    /**
     * Listener for GATT characteristic events (change, read, write)
     * Needed for operating GATT callback with Rx
     */
    private interface LEDataListener {
        // TODO: add read/write callbacks
    }
}
