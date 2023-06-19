package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import paositra.pocket.utils.NetworkChangeReceiver;

public class HistoricalActivity extends AppCompatActivity implements NetworkChangeReceiver.OnNetworkChangeListener{

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);
        networkChangeReceiver = new NetworkChangeReceiver(this);

        //finish
        ImageButton returnBtn = (ImageButton)findViewById(R.id.retour);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }
    @Override
    public void onNetworkChanged(boolean isConnected) {
        if(!isConnected){
            Toast.makeText(this, "Non connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}