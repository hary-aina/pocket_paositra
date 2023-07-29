package paositra.pocket;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import paositra.pocket.clientApi.RetrofitClient;
import paositra.pocket.service.ApiService;
import paositra.pocket.utils.NetworkChangeReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class loginFragment extends Fragment implements NetworkChangeReceiver.OnNetworkChangeListener {

    private final static String confPref = "conf_client";
    SharedPreferences preferences;
    private NetworkChangeReceiver networkChangeReceiver;

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
        if(isConnected){
            //Toast.makeText(getContext(), "Connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            Button loginBtn = getActivity().findViewById(R.id.loginBtn);
            loginBtn.setEnabled(true);
            loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.secondary));
        }else{
            Button loginBtn = getActivity().findViewById(R.id.loginBtn);
            loginBtn.setEnabled(false);
            loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.neutral));
            Toast.makeText(getContext(), "Non connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginBtn = view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificationChamp(view)){

                    if(authetification(view)){

                        EditText editLoginText = (EditText) view.findViewById(R.id.editLoginText);
                        EditText editTextPassword = (EditText) view.findViewById(R.id.editTextPassword);

                        //stockage des informations utilisateurs
                        preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("nom", "RAVAOARISOA");
                        editor.putString("prenom", "Marcel");
                        editor.putString("adresse", "Lot II A Bis Ivandry");
                        editor.putString("telephone", "+261 32 48 965 90");
                        editor.putString("agence", "Antaninarenina");

                        editor.putString("login", editLoginText.getText().toString());
                        editor.putString("password", editTextPassword.getText().toString());
                        editor.putString("idBenef", "1");
                        editor.putString("idcarte", "1");
                        //editor.putString("type_compte", "Paositra Money");
                        editor.putString("type_compte", "E-poketra");
                        editor.putString("numero_carte", "20221552555");
                        editor.putString("numero_serie", "5621456");
                        editor.putString("numero_compte", "0099 0007 17896541237895 88");
                        editor.putString("solde", "20000000");
                        editor.putString("token", "ujubeizhevuzjvbezrhbvzoerbveezouvbrvhrvizbr");

                        editor.commit();

                        ((MainActivity)getActivity()).loadHome();

                    }

                }
            }
        });

        return view;
    }

    public boolean verificationChamp(View v){
        boolean result = true;
        //verification du champ login
        TextView loginText = (TextView) v.findViewById(R.id.loginText);
        EditText editLoginText = (EditText) v.findViewById(R.id.editLoginText);
        if(editLoginText.getText().toString().equals("")){
            loginText.setText("Identifiant obligatoire");
            loginText.setTextColor(getResources().getColor(R.color.danger));
            result = false;
        }else{
            loginText.setText("Identifiant");
            loginText.setTextColor(getResources().getColor(R.color.neutral));
        }
        //verification du champ login
        TextView passwordText = (TextView) v.findViewById(R.id.passwordText);
        EditText editTextPassword = (EditText) v.findViewById(R.id.editTextPassword);
        if(editTextPassword.getText().toString().equals("")){
            passwordText.setText("Mots de passe obligatoire");
            passwordText.setTextColor(getResources().getColor(R.color.danger));
            result = false;
        }else{
            passwordText.setText("Mots de passe");
            passwordText.setTextColor(getResources().getColor(R.color.neutral));
        }

        return result;
    }


    //access au serveur a revoir
    private boolean authetification(View v){

        EditText editTextPassword = (EditText) v.findViewById(R.id.editTextPassword);
        EditText editLoginText = (EditText) v.findViewById(R.id.editLoginText);
        String email = editLoginText.getText().toString();
        String mot_de_passe = editTextPassword.getText().toString();

        //initialisation de la connexion vers le serveur
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Create the JSON string you want to send
        String jsonString = "{\"email\":\""+email+"\",\"password\":"+mot_de_passe+"}";
        // Convert the JSON string to RequestBody
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString);

        Call<JsonObject> call = apiService.login(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    System.out.println("sucess");
                    System.out.println(response);

                    JsonObject responsebody = response.body();

                    boolean error = responsebody.get("error").getAsBoolean();
                    if(!error){

                        JsonObject data = responsebody.get("data").getAsJsonObject();
                        //recuperation token
                        String token = data.get("access_token").getAsString();

                        //recuperation info beneficiaire
                        JsonObject beneficiaire = data.get("beneficiaire").getAsJsonObject();
                        int idBenef = beneficiaire.get("rowid").getAsInt();
                        String nom = beneficiaire.get("nom").getAsString();
                        String prenom = beneficiaire.get("prenom").getAsString();
                        String cni = beneficiaire.get("cni").getAsString();
                        String date_delivrance = beneficiaire.get("date_delivrance").getAsString();
                        String adresse = beneficiaire.get("adresse").getAsString();
                        String email = beneficiaire.get("email").getAsString();
                        String date_nais = beneficiaire.get("date_nais").getAsString();
                        String codepostal = beneficiaire.get("codepostal").getAsString();
                        String sexe = beneficiaire.get("sexe").getAsString();
                        int statut = beneficiaire.get("statut").getAsInt();
                        int etat = beneficiaire.get("etat").getAsInt();

                        System.out.println(nom);

                        //recuperation info compte
                        JsonObject compte = data.get("compte").getAsJsonObject();
                        int idCarte = compte.get("rowid").getAsInt();
                        int statut_carte = compte.get("statut").getAsInt();
                        double solde = compte.get("solde").getAsDouble();
                        int solde_carte = compte.get("solde_carte").getAsInt();
                        int wallet_carte = compte.get("wallet_carte").getAsInt();
                        String numcompte = compte.get("numcompte").getAsString();
                        String telephone = compte.get("telephone").getAsString();

                        if(compte.get("numero").isJsonNull()){

                        } else {
                            String numero = compte.get("numero").getAsString();
                        }
                        if(compte.get("numero_serie").isJsonNull()){

                        } else {
                            String numero_serie = compte.get("numero_serie").getAsString();
                        }
                        if(compte.get("date_expiration").isJsonNull()){

                        } else {
                            String date_expiration = compte.get("date_expiration").getAsString();
                        }
                        if(compte.get("date_activation").isJsonNull()){

                        } else {
                            String date_expiration = compte.get("date_activation").getAsString();
                        }

                    }else{
                        System.out.println("error");
                    }

                }else{
                    System.out.println("echec de recuperation");
                    System.out.println(response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println("echec de serveur");
                System.out.println(t);
            }
        });

        return false;
    }

}