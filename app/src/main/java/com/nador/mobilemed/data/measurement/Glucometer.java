package com.nador.mobilemed.data.measurement;

import android.content.Context;

import com.nador.mobilemed.R;
import com.nador.mobilemed.data.entity.measurement.BloodGlucoseLevel;
import com.nador.mobilemed.data.measurement.bluetooth.BluetoothConnection;
import com.nador.mobilemed.data.utils.ByteUtils;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by nador on 14/07/16.
 */
public class Glucometer extends HealthDevice {

    @Inject
    public Glucometer() {
        super(new GlucometerAddressProvider());

        // FIXME: only with Taidoc glucometer right now
        mManufacturer = new Taidoc();
    }

    public Observable<BloodGlucoseLevel> measureGlucoseLevel() {
        return (Observable<BloodGlucoseLevel>) mManufacturer.createMeasurementObservable(mBluetooth);
    }

    public static final class Taidoc extends HealthDeviceManufacturer {

        private static final byte[] GLUCOMETER_MEASURE_CMD_GET_DATA = new byte[] {0x51, 0x26, 0x00, 0x00, 0x01, 0x00, (byte) 0xA3, 0x1B};
        private static final byte[] GLUCOMETER_MEASURE_CMD_SLEEP = new byte[] {0x51, 0x50, 0x00, 0x00, 0x00, 0x00, (byte) 0xA3, 0x44};
        private static final byte[] GLUCOMETER_GLUCOSE_VALUE_BITMASK_HIGH = new byte[] {0x00, 0x00, 0x00, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00};
        private static final byte[] GLUCOMETER_GLUCOSE_VALUE_BITMASK_LOW = new byte[] {0x00, 0x00, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00};

        @Override
        public Observable<BloodGlucoseLevel> createMeasurementObservable(BluetoothConnection bluetoothConnection) {
            return bluetoothConnection.sendCommand(GLUCOMETER_MEASURE_CMD_GET_DATA)
                    .flatMap(dataBytes -> {
                        Timber.d(ByteUtils.byteArrayToHexString(dataBytes));

                        long glucoseHighIndex = ByteUtils.byteArrayToInt(ByteUtils.cropToBitmask(dataBytes, GLUCOMETER_GLUCOSE_VALUE_BITMASK_HIGH)) << 8;
                        long glucoseLowIndex = ByteUtils.byteArrayToInt(ByteUtils.cropToBitmask(dataBytes, GLUCOMETER_GLUCOSE_VALUE_BITMASK_LOW));

                        bluetoothConnection.sendCommand(GLUCOMETER_MEASURE_CMD_SLEEP).subscribe();
                        BloodGlucoseLevel level = new BloodGlucoseLevel(glucoseHighIndex + glucoseLowIndex, BloodGlucoseLevel.GlucoseUnit.MG);
                        level.convertTo(BloodGlucoseLevel.GlucoseUnit.MMOL);
                        return Observable.just(level);
                    });
        }
    }

    public static final class GlucometerAddressProvider extends HealthDeviceAddressProvider {
        @Override
        public String getHealthDeviceAddress(Context context) {
            // TODO: hardcoded for testing purposes, get it through webservice (not implemented yet)
            return context.getString(R.string.BT_MAC_GLUCOMETER).toUpperCase();
        }
    }
}
