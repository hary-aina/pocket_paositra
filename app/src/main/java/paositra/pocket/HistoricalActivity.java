package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import paositra.pocket.adapter.TransactionListAdapter;
import paositra.pocket.utils.NetworkChangeReceiver;

public class HistoricalActivity extends AppCompatActivity implements NetworkChangeReceiver.OnNetworkChangeListener{

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);
        networkChangeReceiver = new NetworkChangeReceiver(this);

        //load historique
        ListView lvTransac = (ListView)findViewById(R.id.listTransaction);
        //static data
        ArrayList<JsonObject> transactions = new ArrayList<JsonObject>();
        for(int i = 0; i < 10; i++){
            JsonObject item = new JsonObject();
            item.addProperty("numtransaction", "36441");
            item.addProperty("date_transaction", "2023-03-01");
            item.addProperty("operation", "DEBIT");
            item.addProperty("montant", "100");
            transactions.add(item);
        }
        TransactionListAdapter adapter = new TransactionListAdapter(this, R.layout.adater_view_transaction, transactions);
        lvTransac.setAdapter(adapter);

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