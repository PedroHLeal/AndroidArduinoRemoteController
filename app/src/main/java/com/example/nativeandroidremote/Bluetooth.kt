package com.arduinoremote

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.harrysoft.androidbluetoothserial.BluetoothManager
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.lang.NullPointerException


class Bluetooth() {
    private val bluetoothManager = BluetoothManager.getInstance();
    private var deviceInterface: SimpleBluetoothDeviceInterface? = null

    fun checkIfBTAvailable(): Boolean {
        return bluetoothManager != null
    }

    fun listBluetoothDevices(): Collection<BluetoothDevice> {
        return bluetoothManager.pairedDevicesList
    }

    @SuppressLint("CheckResult")
    fun connectDevice(address: String, callback: (device: SimpleBluetoothDeviceInterface) -> Unit) {
        bluetoothManager.openSerialDevice(address)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { connected ->
                    deviceInterface = connected.toSimpleDeviceInterface()
                    callback(deviceInterface!!)
                    deviceInterface?.setListeners({},
                        this::mesageSent,
                        { e -> Log.d("bt", e.message.orEmpty()) })
                },
                { error -> error.message?.let { Log.e("error", it) } }
            );
    }

    fun sendMessage(message: String) {
        deviceInterface?.sendMessage(message);
    }

    private fun mesageSent(message: String) {
        Log.d("bt message sent", message)
    }
}