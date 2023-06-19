package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import paositra.pocket.utils.NetworkChangeReceiver;

public class AccountActivity extends AppCompatActivity implements NetworkChangeReceiver.OnNetworkChangeListener{

    private final static String confPref = "conf_client";
    SharedPreferences preferences;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        networkChangeReceiver = new NetworkChangeReceiver(this);

        //chargement des informations
        preferences = getSharedPreferences(confPref, Context.MODE_PRIVATE);
        TextView type_compte = (TextView) findViewById(R.id.type_compte);
        type_compte.setText(preferences.getString("type_compte", ""));
        TextView num_compte = (TextView) findViewById(R.id.num_compte);
        num_compte.setText(preferences.getString("numero_compte", ""));
        TextView telephone = (TextView) findViewById(R.id.telephone);
        telephone.setText(preferences.getString("telephone", ""));

        //hide some parameters
        if(preferences.getString("type_compte", "").equalsIgnoreCase("Paositra Money")){
            TableRow lost_carte = (TableRow) findViewById(R.id.lost_carte);
            lost_carte.setVisibility(View.GONE);
            TableRow delete_carte = (TableRow) findViewById(R.id.delete_carte);
            delete_carte.setVisibility(View.GONE);
        }

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
        if(isConnected){
            //Toast.makeText(getContext(), "Connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            LinearLayout lost_connexion = findViewById(R.id.lost_connexion);
            lost_connexion.setVisibility(View.GONE);

            LinearLayout offline_layout = findViewById(R.id.offline_layout);
            offline_layout.setVisibility(View.INVISIBLE);

            LinearLayout action_layout = findViewById(R.id.action_layout);
            action_layout.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Non connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            LinearLayout lost_connexion = findViewById(R.id.lost_connexion);
            lost_connexion.setVisibility(View.VISIBLE);

            LinearLayout offline_layout = findViewById(R.id.offline_layout);
            offline_layout.setVisibility(View.VISIBLE);

            LinearLayout action_layout = findViewById(R.id.action_layout);
            action_layout.setVisibility(View.GONE);
        }
    }

}