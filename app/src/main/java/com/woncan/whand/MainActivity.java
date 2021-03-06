package com.woncan.whand;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.woncan.whand.device.IDevice;
import com.woncan.whand.listener.OnConnectListener;
import com.woncan.whand.scan.ScanCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WHandManager.getInstance().init(BuildConfig.DEBUG);
        Options.isDebug = BuildConfig.DEBUG;
        Options.isAutoConnect = false;
        Options.scanPeriod = 10 * 1000;
        WHandManager.getInstance().init(BuildConfig.DEBUG);


        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        WHandManager.getInstance().startScan(new ScanCallback() {

            @Override
            public void onError(int errorCode, String message) {
                Log.i("TAG", "onError: " + message);
            }

            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
                WHandManager.getInstance().stopScan();
                IDevice device = WHandManager.getInstance().connect(MainActivity.this, bluetoothDevice);
                device.setOnConnectionStateChangeListener((i, i1) -> {

                });
                device.setOnConnectListener(new OnConnectListener() {
                    @Override
                    public void onDeviceChanged(WHandInfo wHandInfo) {
                        Log.i("TAG", "onDeviceChanged: " + wHandInfo.toString());
                    }

                    @Override
                    public void onNameChanged(String name) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onAccountChanged(String name) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                new String(Base64.decode(name, 0)), Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.i("TAG", "onError: " + e.getMessage());
                    }
                });
            }
        });

    }
}