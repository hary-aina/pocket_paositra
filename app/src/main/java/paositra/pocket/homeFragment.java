package paositra.pocket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import paositra.pocket.adapter.TransactionListAdapter;
import paositra.pocket.clientApi.RetrofitClient;
import paositra.pocket.service.ApiService;
import paositra.pocket.utils.NetworkChangeReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Path;

public class homeFragment extends Fragment implements NetworkChangeReceiver.OnNetworkChangeListener {

    private final static String confPref = "conf_client";
    SharedPreferences preferences;
    private NetworkChangeReceiver networkChangeReceiver;
    private DecimalFormat df;

    @Override
    public void onResume() {
        super.onResume();
        networkChangeReceiver = new NetworkChangeReceiver(this);
        requireActivity().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(networkChangeReceiver);
        networkChangeReceiver = null;
    }
    @Override
    public void onNetworkChanged(boolean isConnected) {
        LinearLayout lost_connexion = getActivity().findViewById(R.id.lost_connexion);
        Button actualiserBtn = (Button) getActivity().findViewById(R.id.actualiser_solde);
        ImageButton transfertActivity = (ImageButton) getActivity().findViewById(R.id.transfertActivity);
        if(isConnected){
            //Toast.makeText(getContext(), "Connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            lost_connexion.setVisibility(View.GONE);
            actualiserBtn.setEnabled(true);
            actualiserBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.primary));
            preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
            transfertActivity.setEnabled(true);
            transfertActivity.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.black));

        }else{
            Toast.makeText(getContext(), "Non connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            lost_connexion.setVisibility(View.VISIBLE);
            actualiserBtn.setEnabled(false);
            actualiserBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.neutral));
            transfertActivity.setEnabled(false);
            transfertActivity.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.neutral));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        df = new DecimalFormat("#,###.00");

        //chargement des informations
        preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
        TextView type_compte = (TextView) view.findViewById(R.id.type_compte);
        type_compte.setText(preferences.getString("type_compte", ""));
        TextView solde = (TextView) view.findViewById(R.id.solde);
        solde.setText("AR " + df.format(Double.parseDouble(preferences.getString("solde", ""))));
        TextView tv_last_update_time = (TextView) view.findViewById(R.id.last_update_time);
        tv_last_update_time.setText(preferences.getString("last_update", ""));

        TextView txt_no_transac = (TextView) view.findViewById(R.id.txt_nonTrans);
        ListView lvTransac = (ListView) view.findViewById(R.id.listTransaction);
        txt_no_transac.setVisibility(View.VISIBLE);
        lvTransac.setVisibility(View.GONE);

        String jsonArrayStringTransaction = preferences.getString( "transactions",null);
        if (jsonArrayStringTransaction != null) {
            Gson gson = new Gson();
            JsonArray data = gson.fromJson(jsonArrayStringTransaction, JsonArray.class);
            if(data.size() > 0){
                txt_no_transac.setVisibility(View.GONE);
                lvTransac.setVisibility(View.VISIBLE);
                ArrayList<JsonObject> transactions = new ArrayList<JsonObject>();
                for (JsonElement item : data) {
                    JsonObject object = item.getAsJsonObject();
                    transactions.add(object);
                }
                TransactionListAdapter adapter = new TransactionListAdapter(view.getContext(), R.layout.adater_view_transaction, transactions);
                lvTransac.setAdapter(adapter);
            }
        }

        //hide some view
        if(preferences.getString("type_compte", "").equalsIgnoreCase("Paositra Money")){
            LinearLayout visa_info = (LinearLayout) view.findViewById(R.id.visa_info);
            visa_info.setVisibility(View.GONE);
        }

        //start Info Perso Activity
        ImageButton profilBtn = view.findViewById(R.id.profil);
        profilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startInfo_Perso(v);
            }
        });

        //logout
        ImageButton logoutBtn = view.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deconnexion();
            }
        });

        //start account activity
        ImageButton accountBtn = view.findViewById(R.id.compteActivity);
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountActivity(v);
            }
        });

        //start payement activity
        ImageButton payBtn = view.findViewById(R.id.paiementActivity);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaiementActivity(v);;
            }
        });

        //actualiser sont solde
        Button actubtn = (Button) view.findViewById(R.id.actualiser_solde);
        actubtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSolde(view);
                        loadTransaction(view);
                    }
                }
        );

        return view;
    }

    //start info_perso
    public void startInfo_Perso(View v){
        Intent InfoPersoActivity = new Intent(v.getContext().getApplicationContext(), infoPerso.class);
        startActivity(InfoPersoActivity);
    }

    //start account
    public void startAccountActivity(View v){
        Intent accountActivity = new Intent(v.getContext().getApplicationContext(), AccountActivity.class);
        startActivity(accountActivity);
    }

    //start historical
    /*public void startHistoricalActivity(View v){
        Intent historicalActivity = new Intent(v.getContext().getApplicationContext(), HistoricalActivity.class);
        startActivity(historicalActivity);
    }*/

    //start paiement Activity
    public void startPaiementActivity(View v){
        Intent paiementActivity = new Intent(v.getContext().getApplicationContext(), PayementActivity.class);
        startActivity(paiementActivity);
    }

    //get solde
    public void getSolde(View v){
        preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
        String type_compte = preferences.getString("type_compte", "");
        String wallet_carte = "0";
        if(!type_compte.equalsIgnoreCase("PAOSITRA Money")){
            wallet_carte = "1";
        }
        String token = preferences.getString("token", "");

        //initialisation de la connexion vers le serveur
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<JsonObject> call = apiService.getSolde(wallet_carte, "Bearer "+token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject responsebody = response.body();
                    boolean error = responsebody.get("error").getAsBoolean();
                    int code = responsebody.get("code").getAsInt();

                    if(code == 401 || code == 403){
                        //erreur token refaire l'authentification
                        Toast.makeText(v.getContext(), "RECONNEXION REQUIS", Toast.LENGTH_LONG).show();
                        preferences = v.getContext().getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent MainActivity = new Intent(v.getContext(), MainActivity.class);
                        startActivity(MainActivity);

                    } else if (error == true) {
                        //erreu de service
                        String message = responsebody.get("data").getAsString();
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();
                    }else{
                        SharedPreferences.Editor editor = preferences.edit();
                        JsonObject data = responsebody.get("data").getAsJsonObject();
                        TextView tvsolde = (TextView) v.findViewById(R.id.solde);
                        String solde = data.get("solde").getAsString();
                        editor.putString("solde", solde);
                        tvsolde.setText("AR "+  df.format(Double.parseDouble(solde)));

                        TextView tv_last_update_time = (TextView) v.findViewById(R.id.last_update_time);
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        editor.putString("last_update", formatter.format(date));
                        tv_last_update_time.setText(formatter.format(date));
                        editor.commit();
                    }

                }else{
                    Toast.makeText(v.getContext(), "ERREUR SERVICE", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(v.getContext(), "ERREUR SERVEUR", Toast.LENGTH_LONG).show();
            }
        });

    }

    //get transaction
    public void loadTransaction(View v){

        //initialisation de la connexion vers le serveur
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String token = preferences.getString("token", "");
        Call<JsonObject> call = apiService.getLastTenTransactions("Bearer "+ token);

        TextView txt_no_transac = (TextView) v.findViewById(R.id.txt_nonTrans);
        ListView lvTransac = (ListView) v.findViewById(R.id.listTransaction);
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

                        Toast.makeText(v.getContext(), "RECONNEXION REQUIS", Toast.LENGTH_LONG).show();
                        preferences = v.getContext().getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent MainActivity = new Intent(v.getContext(), MainActivity.class);
                        startActivity(MainActivity);

                    } else if (error == true) {
                        //werror de service
                        String message = responsebody.get("data").getAsString();
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_LONG).show();

                    }else{
                        //success
                        JsonArray data = responsebody.get("data").getAsJsonArray();
                        Gson gson = new Gson();
                        String jsonArrayStringTransaction = gson.toJson(data);

                        preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("transactions", jsonArrayStringTransaction);
                        editor.apply();
                        editor.commit();

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
                            TransactionListAdapter adapter = new TransactionListAdapter(v.getContext(), R.layout.adater_view_transaction, transactions);
                            lvTransac.setAdapter(adapter);
                        }
                    }

                }else{
                    Toast.makeText(v.getContext(), "ERREUR DE SERVICE", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(v.getContext(), "ERREUR SERVEUR", Toast.LENGTH_LONG).show();
            }
        });

    }

    //deconnexion
    public void deconnexion(){

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Deconnection");
        alert.setMessage("Etes-vous sur de quitter la session ?")
                .setPositiveButton("Rester", null)
                .setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        ((MainActivity)getActivity()).loadLogin();
                    }
                });
        AlertDialog alertLogout = alert.create();
        alertLogout.show();
    }

}