package com.nador.mobilemed.data.dagger.measurement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.nador.mobilemed.data.dagger.scopes.BluetoothScope;
import com.nador.mobilemed.data.measurement.bluetooth.BluetoothConnection;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nador on 06/06/16.
 */
@Module
public class BluetoothHealthModule {

    // Non-application-wide context
    private Context mContext;

    public BluetoothHealthModule(Context context) {
        mContext = context;
    }

    @Provides
    @BluetoothScope
    Context provideContext() {
        return mContext;
    }

    // Provide system Bluetooth adapter
    // Availability is guaranteed by <uses-feature> in the manifest
    @Provides
    @BluetoothScope
    BluetoothAdapter provideBluetoothAdapter(Context context) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager.getAdapter();
    }

    @Provides
    @BluetoothScope
    BluetoothConnection provideBluetoothConnection(Context context, BluetoothAdapter bluetoothAdapter) {
        return new BluetoothConnection(context, bluetoothAdapter);
    }
}
