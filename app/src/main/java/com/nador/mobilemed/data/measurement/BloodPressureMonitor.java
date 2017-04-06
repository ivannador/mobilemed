package com.nador.mobilemed.data.measurement;

import android.content.Context;

import com.nador.mobilemed.data.utils.ByteUtils;
import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.BloodPressure;
import com.nador.mobilemed.data.measurement.bluetooth.BluetoothConnection;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 07/06/16.
 */
public class BloodPressureMonitor extends HealthDevice {

    @Inject
    public BloodPressureMonitor() {
        super(new BloodPressureMonitorAddressProvider());

        // FIXME: only with Taidoc blood pressure monitor right now
        mManufacturer = new Taidoc();
    }

    public Observable<BloodPressure> measureBloodPressure() {
        return (Observable<BloodPressure>) mManufacturer.createMeasurementObservable(mBluetooth);
    }

    public static final class Taidoc extends HealthDeviceManufacturer {

        private static final byte[] BLOODPRESSURE_MEASURE_CMD_DEVICE_INFO = new byte[] {0x51, 0x24, 0x00, 0x00, 0x00, 0x00, (byte) 0xA3, 0x18};
        private static final byte[] BLOODPRESSURE_MEASURE_CMD_DEVICE_INFO_ANSWER = new byte[] {0x51, 0x24, 0x28, 0x31, 0x02, 0x04, (byte) 0xA5, 0x79};
        private static final byte[] BLOODPRESSURE_MEASURE_CMD_GET_DATA = new byte[] {0x51, 0x26, 0x00, 0x00, 0x01, 0x00, (byte) 0xA3, 0x1B};
        private static final byte[] BLOODPRESSURE_MEASURE_CMD_SLEEP = new byte[] {0x51, 0x50, 0x00, 0x00, 0x00, 0x00, (byte) 0xA3, 0x44};
        private static final byte[] BLOODPRESSURE_BP_SYS_VALUE_BITMASK = new byte[] {0x00, 0x00, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00};
        private static final byte[] BLOODPRESSURE_BP_DIA_VALUE_BITMASK = new byte[] {0x00, 0x00, 0x00, 0x00, (byte) 0xFF, 0x00, 0x00, 0x00};
        private static final byte[] BLOODPRESSURE_BP_PUL_VALUE_BITMASK = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xFF, 0x00, 0x00};

        @Override
        public Observable<BloodPressure> createMeasurementObservable(BluetoothConnection bluetoothConnection) {
//            mBluetooth.sendCommand(BLOODPRESSURE_MEASURE_CMD_DEVICE_INFO)
//                    .flatMap(bytes -> {
//                        Timber.d(ByteUtils.byteArrayToHexString(bytes));
//
//                        String result = ByteUtils.byteArrayToHexString(bytes);
//                        String validator = ByteUtils.byteArrayToHexString(BLOODPRESSURE_MEASURE_CMD_DEVICE_INFO_ANSWER);
//                        if (result.equals(validator)) {
//                            return mBluetooth.sendCommand(BLOODPRESSURE_MEASURE_CMD_GET_DATA);
//                        } else {
//                            return Observable.error(new Throwable("Blood pressure monitor error!"));
//                        }
//                    })
            return bluetoothConnection.sendCommand(BLOODPRESSURE_MEASURE_CMD_GET_DATA)
                    .flatMap(dataBytes -> {
                        Timber.d(ByteUtils.byteArrayToHexString(dataBytes));

                        long systolic = ByteUtils.byteArrayToInt(ByteUtils.cropToBitmask(dataBytes, BLOODPRESSURE_BP_SYS_VALUE_BITMASK));
                        long diastolic = ByteUtils.byteArrayToInt(ByteUtils.cropToBitmask(dataBytes, BLOODPRESSURE_BP_DIA_VALUE_BITMASK));
                        long pulse = ByteUtils.byteArrayToInt(ByteUtils.cropToBitmask(dataBytes, BLOODPRESSURE_BP_PUL_VALUE_BITMASK));

                        bluetoothConnection.sendCommand(BLOODPRESSURE_MEASURE_CMD_SLEEP).subscribe();
                        return Observable.just(new BloodPressure(systolic, diastolic, pulse));
                    });
        }
    }

    public static final class BloodPressureMonitorAddressProvider extends HealthDeviceAddressProvider {
        @Override
        public String getHealthDeviceAddress(Context context) {
            // TODO: hardcoded for testing purposes, get it through webservice (not implemented yet)
            return context.getString(R.string.BT_MAC_BPMONITOR).toUpperCase();
        }
    }
}
