package tech.xdrd.wifimap;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView console;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // wifi已成功扫描到可用wifi。
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.d("WifiMap", "接收到");

            }
        }
    };

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 100001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Button refresh = (Button) findViewById(R.id.a_main_button_refresh);
        refresh.setOnClickListener(v -> {
            wifiManager.startScan();
            List<ScanResult> results = wifiManager.getScanResults();
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            for (ScanResult scanResult : results) {
                builder.append(String.format("{BSSID: \"%s\", level: \"%d\"},", scanResult.BSSID, scanResult.level));
            }
            builder.append("],");
            console.append(builder.toString());
        });

        console = (TextView) findViewById(R.id.a_main_text_console);

        registerWifiScanBroadcast();
    }

    private void registerWifiScanBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a_main_menu_permission:
                askForPermission();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION) {
            Toast.makeText(this,  (grantResults[0] == PackageManager.PERMISSION_GRANTED) + "", Toast.LENGTH_SHORT).show();
        }
    }
}
