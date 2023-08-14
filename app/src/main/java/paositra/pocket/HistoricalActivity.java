package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import paositra.pocket.adapter.TransactionListAdapter;
import paositra.pocket.clientApi.RetrofitClient;
import paositra.pocket.service.ApiService;
import paositra.pocket.utils.NetworkChangeReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoricalActivity extends AppCompatActivity implements NetworkChangeReceiver.OnNetworkChangeListener{

    private final static String confPref = "conf_client";
    SharedPreferences preferences;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);
        networkChangeReceiver = new NetworkChangeReceiver(this);
        preferences = getSharedPreferences(confPref, Context.MODE_PRIVATE);

        //load historique
        loadTransaction(this);

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

    //get transaction
    public void loadTransaction(Context context){

        //initialisation de la connexion vers le serveur
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String token = preferences.getString("token", "");
        Call<JsonObject> call = apiService.getLastTenTransactions("Bearer "+ token);

        TextView txt_no_transac = (TextView)findViewById(R.id.txt_nonTrans);
        ListView lvTransac = (ListView)findViewById(R.id.listTransaction);
        txt_no_transac.setVisibility(View.VISIBLE);
        lvTransac.setVisibility(View.GONE);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){

                    JsonObject responsebody = response.body();
                    boolean error = responsebody.get("error").getAsBoolean();
                    int code = responsebody.get("code").getAsInt();

                    if(code == 401 || code == 403){
                        //erreur token refaire l'authentification

                        Toast.makeText(getApplication(), "RECONNEXION REQUIS", Toast.LENGTH_LONG).show();
                        preferences = getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent MainActivity = new Intent(getApplication(), MainActivity.class);
                        startActivity(MainActivity);
                        finish();

                    } else if (error == true) {
                        //werror de service
                        String message = responsebody.get("data").getAsString();
                        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                    }else{
                        //success
                        JsonArray data = responsebody.get("data").getAsJsonArray();
                        //static data
                        ArrayList<JsonObject> transactions = new ArrayList<JsonObject>();
                        /*for(int i = 0; i < 10; i++){
                            JsonObject item = new JsonObject();
                            item.addProperty("numtransaction", "36441");
                            item.addProperty("date_transaction", "2023-03-01");
                            item.addProperty("operation", "DEBIT");
                            item.addProperty("montant", "100");
                            transactions.add(item);
                        }*/

                        if(data.size() > 0){
                            txt_no_transac.setVisibility(View.GONE);
                            lvTransac.setVisibility(View.VISIBLE);
                            for (JsonElement item : data) {
                                JsonObject object = item.getAsJsonObject();
                                transactions.add(object);
                            }
                            TransactionListAdapter adapter = new TransactionListAdapter(context, R.layout.adater_view_transaction, transactions);
                            lvTransac.setAdapter(adapter);
                        }
                    }

                }else{
                    Toast.makeText(getApplication(), "ERREUR DE SERVICE", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplication(), "ERREUR SERVEUR", Toast.LENGTH_LONG).show();
            }
        });

    }
}