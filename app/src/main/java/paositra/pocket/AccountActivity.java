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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import paositra.pocket.clientApi.RetrofitClient;
import paositra.pocket.service.ApiService;
import paositra.pocket.utils.NetworkChangeReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        int statut_compte = preferences.getInt("statut_compte", 0);
        Switch switch1 = (Switch) findViewById(R.id.switch1);
        TextView statut = (TextView) findViewById(R.id.statut);
        if(statut_compte == 1){
            switch1.setChecked(true);
            statut.setText("active");
            statut.setTextColor(getResources().getColor(R.color.success));
        }else{
            switch1.setChecked(false);
            statut.setText("desactive");
            statut.setTextColor(getResources().getColor(R.color.danger));
        }

        switch1.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                       if(isChecked){
                           activerCompte();
                       }else{
                           desactiverCompte();
                       }
                   }
               }
       );
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


    //activer compte
    private void activerCompte(){

        //initialisation de la connexion vers le serveur
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        // Create the JSON string you want to send
        String jsonString = "{}";
        // Convert the JSON string to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);
        Call<JsonObject> call = apiService.activateAccount("Bearer "+preferences.getString("token", ""));
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
                        //erreu de service
                        String message = responsebody.get("data").getAsString();
                        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                    }else{
                        //success
                        preferences = getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("statut_compte", 1);
                        editor.commit();
                        Switch switch1 = (Switch) findViewById(R.id.switch1);
                        TextView statut = (TextView) findViewById(R.id.statut);
                        switch1.setChecked(true);
                        statut.setText("active");
                        statut.setTextColor(getResources().getColor(R.color.success));
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

    //activer compte
    private void desactiverCompte(){

        //initialisation de la connexion vers le serveur
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        // Create the JSON string you want to send
        String jsonString = "{}";
        // Convert the JSON string to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);
        Call<JsonObject> call = apiService.unactivateAccount("Bearer "+preferences.getString("token", ""));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()){

                    JsonObject responsebody = response.body();
                    boolean error = responsebody.get("error").getAsBoolean();
                    int code = responsebody.get("code").getAsInt();

                    System.out.println(response.body());

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
                        //erreu de service
                        String message = responsebody.get("data").getAsString();
                        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();

                    }else{
                        //success
                        preferences = getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("statut_compte", 0);
                        editor.commit();
                        Switch switch1 = (Switch) findViewById(R.id.switch1);
                        TextView statut = (TextView) findViewById(R.id.statut);
                        switch1.setChecked(false);
                        statut.setText("desactive");
                        statut.setTextColor(getResources().getColor(R.color.danger));
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