package com.example.nativeandroidremote

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import com.arduinoremote.Bluetooth
import com.example.nativeandroidremote.composables.BluetoothList
import com.example.nativeandroidremote.composables.Controller
import com.example.nativeandroidremote.ui.theme.NativeAndroidRemoteTheme
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface
import java.util.Timer
import java.util.TimerTask

class MainActivity : ComponentActivity() {
    private var ty = 0f
    private var rx = 0f
    private var ry = 0f
    private var lastTime = 0L
    private val bluetooth: Bluetooth = Bluetooth()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                1
            )
        }

        val devices = bluetooth.listBluetoothDevices()

//        t = Timer()
//        t.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                Log.d("test", "throttle ${ty}, Pitch - ${ry}, Roll ${rx}")
//            }
//        }, 0, 50)
        setContent {
            var connectedDevice by rememberSaveable {
                mutableStateOf<Boolean>(false)
            }

            if (!connectedDevice) {
                Surface {
                    BluetoothList(devices = devices) { device ->
                        bluetooth.connectDevice(device.address) { _ ->
                            connectedDevice = true
                        }
                    }
                }
            } else {
                Surface {
                    Controller(
                        { throttle ->
                            ty = throttle
                            sendValuesBt()
                        },
                        { pitch, roll ->
                            ry = pitch
                            rx = roll
                            sendValuesBt()
                        },
                        {
                            ry = 0f
                            rx = 0f
                            sendValuesBt(true)
                        }
                    )
                }
            }
        }
    }

    private fun sendValuesBt(force: Boolean = false) {
        val elapsed = System.currentTimeMillis() - lastTime
        if (elapsed > 40 || force) {
            Log.d("message", "${ty.toInt()} ${ry.toInt()} ${rx.toInt()} \n")
            bluetooth.sendMessage("${ty.toInt()} ${ry.toInt()} ${rx.toInt()} \n")
            lastTime = System.currentTimeMillis()
        }
    }
}