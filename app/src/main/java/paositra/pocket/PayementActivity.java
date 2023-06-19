package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import paositra.pocket.utils.NetworkChangeReceiver;

public class PayementActivity extends AppCompatActivity implements NetworkChangeReceiver.OnNetworkChangeListener{

    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement);
        networkChangeReceiver = new NetworkChangeReceiver(this);

        //finish
        ImageButton returnBtn = (ImageButton)findViewById(R.id.retour);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //start NFC payement
        ImageButton nfcBtn = (ImageButton)findViewById(R.id.nfcBtn);
        nfcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaiementNFCActivity(v);;
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
        if(isConnected){
            //Toast.makeText(getContext(), "Connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            LinearLayout lost_connexion = findViewById(R.id.lost_connexion);
            lost_connexion.setVisibility(View.GONE);

            TextView qr_text = findViewById(R.id.qr_text);
            qr_text.setTextColor(ContextCompat.getColorStateList(this, R.color.black));
            ImageButton qrcodeBtn = findViewById(R.id.qrcodeBtn);
            qrcodeBtn.setEnabled(true);
            qrcodeBtn.setImageTintList(ContextCompat.getColorStateList(this, R.color.black));
        }else{
            Toast.makeText(this, "Non connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            LinearLayout lost_connexion = findViewById(R.id.lost_connexion);
            lost_connexion.setVisibility(View.VISIBLE);

            TextView qr_text = findViewById(R.id.qr_text);
            qr_text.setTextColor(ContextCompat.getColorStateList(this, R.color.neutral));
            ImageButton qrcodeBtn = findViewById(R.id.qrcodeBtn);
            qrcodeBtn.setEnabled(false);
            qrcodeBtn.setImageTintList(ContextCompat.getColorStateList(this, R.color.neutral));
        }
    }


    //start NFC payement
    public void startPaiementNFCActivity(View v){
        Intent nfcActivity = new Intent(v.getContext().getApplicationContext(), NfcPayementActivity.class);
        startActivity(nfcActivity);
    }
}