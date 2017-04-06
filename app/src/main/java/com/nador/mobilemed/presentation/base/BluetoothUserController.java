package com.nador.mobilemed.presentation.base;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import timber.log.Timber;

/**
 * Created by nador on 13/06/16.
 */
public abstract class BluetoothUserController<V extends MvpView, P extends MvpPresenter<V>> extends BaseController<V, P> {

    private final static int REQUEST_ENABLE_BT = 666;

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        checkBluetoothAccess();
    }

    private void checkBluetoothAccess() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter adapter = bluetoothManager.getAdapter();

        if (adapter != null && !adapter.isEnabled()) {
            enableBluetooth();
        }
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // If Bluetooth access is denied, pop current controller
            if (resultCode == Activity.RESULT_CANCELED) {
                Timber.d("Bluetooth access not enabled!");
                getRouter().popController(this);
            }
        }
    }
}
