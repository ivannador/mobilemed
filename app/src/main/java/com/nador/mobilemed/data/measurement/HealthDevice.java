package com.nador.mobilemed.data.measurement;

import android.content.Context;

import com.nador.mobilemed.data.measurement.bluetooth.BluetoothConnection;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

/**
 * Generic health device with Bluetooth connection
 * It has no BT access request functionality, so enabled BT is needed
 *
 * Created by nador on 07/06/16.
 */
public class HealthDevice {

    @Inject Context mContext;
    @Inject
    BluetoothConnection mBluetooth;

    private HealthDeviceAddressProvider mAddressProvider;
    // Health device manufacturer obtained through BT communication
    protected HealthDeviceManufacturer mManufacturer;

    public HealthDevice(HealthDeviceAddressProvider addressProvider) {
        mAddressProvider = addressProvider;
    }

    public Observable<Boolean> connectDevice() {
        return mBluetooth.findDevice(mAddressProvider.getHealthDeviceAddress(mContext))
                .flatMap(bluetoothDevice -> {
                    if (bluetoothDevice == null) {
                        return Observable.just(BluetoothConnection.BluetoothDeviceStatus.BT_DEVICE_UNAVAILABLE);
                    }
                    Timber.d("Bluetooth device found: %s", bluetoothDevice.toString());
                    return mBluetooth.connect(bluetoothDevice);
                })
                .flatMap(bluetoothDeviceStatus -> {
                    Timber.d("Bluetooth device status: %s", bluetoothDeviceStatus.toString());
                    if (bluetoothDeviceStatus == BluetoothConnection.BluetoothDeviceStatus.BT_DEVICE_CONNECTED) {
                        return Observable.just(true);
                    } else {
                        return Observable.just(false);
                    }
                });
    }

    public Observable<Boolean> disconnectDevice() {
        return mBluetooth.disconnect()
                .flatMap(bluetoothDeviceStatus -> Observable.just(bluetoothDeviceStatus == BluetoothConnection.BluetoothDeviceStatus.BT_DEVICE_DISCONNECTED));
    }

    public static abstract class HealthDeviceAddressProvider {
        /**
         * Get MAC address of Bluetooth health device
         * The address is set through web
         *
         * @return
         */
        public String getHealthDeviceAddress(Context context) {
            return null;
        }
    }

    public static abstract class HealthDeviceManufacturer {
        /**
         * Create device manufacturer specific command sequence Observable
         *
         * @return
         */
        public abstract Observable createMeasurementObservable(final BluetoothConnection bluetoothConnection);
    }
}
