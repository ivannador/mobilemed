package com.nador.mobilemed.data.dagger.measurement;

import com.nador.mobilemed.data.measurement.Glucometer;
import com.nador.mobilemed.data.dagger.scopes.BluetoothScope;
import com.nador.mobilemed.data.measurement.BloodPressureMonitor;
import com.nador.mobilemed.data.measurement.WeightScale;

import dagger.Component;

/**
 * Created by nador on 07/06/16.
 */
@BluetoothScope
@Component(modules = {BluetoothHealthModule.class})
public interface BluetoothHealthComponent {
    WeightScale weightScale();
    BloodPressureMonitor bloodPressureMonitor();
    Glucometer glucometer();
}
