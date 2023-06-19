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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import paositra.pocket.utils.NetworkChangeReceiver;

public class homeFragment extends Fragment implements NetworkChangeReceiver.OnNetworkChangeListener {

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
            LinearLayout lost_connexion = getActivity().findViewById(R.id.lost_connexion);
            lost_connexion.setVisibility(View.GONE);

            preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);
            TextView solde = (TextView) getActivity().findViewById(R.id.solde);
            solde.setText("AR "+preferences.getString("solde", ""));

            ImageButton historiqueActivity = getActivity().findViewById(R.id.historiqueActivity);
            historiqueActivity.setEnabled(true);
            historiqueActivity.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.black));

            ImageButton transfertActivity = getActivity().findViewById(R.id.transfertActivity);
            transfertActivity.setEnabled(true);
            transfertActivity.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.black));

        }else{
            Toast.makeText(getContext(), "Non connecter au reseau wi-fi", Toast.LENGTH_SHORT).show();
            LinearLayout lost_connexion = getActivity().findViewById(R.id.lost_connexion);
            lost_connexion.setVisibility(View.VISIBLE);

            TextView solde = (TextView) getActivity().findViewById(R.id.solde);
            solde.setText("solde inconnu");

            ImageButton historiqueActivity = getActivity().findViewById(R.id.historiqueActivity);
            historiqueActivity.setEnabled(false);
            historiqueActivity.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.neutral));

            ImageButton transfertActivity = getActivity().findViewById(R.id.transfertActivity);
            transfertActivity.setEnabled(false);
            transfertActivity.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.neutral));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //chargement des informations
        preferences = getActivity().getSharedPreferences(confPref, Context.MODE_PRIVATE);

        TextView type_compte = (TextView) view.findViewById(R.id.type_compte);
        type_compte.setText(preferences.getString("type_compte", ""));
        TextView solde = (TextView) view.findViewById(R.id.solde);
        solde.setText("AR "+preferences.getString("solde", ""));

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

        //start historical activity
        ImageButton histoBtn = view.findViewById(R.id.historiqueActivity);
        histoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHistoricalActivity(v);
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
    public void startHistoricalActivity(View v){
        Intent historicalActivity = new Intent(v.getContext().getApplicationContext(), HistoricalActivity.class);
        startActivity(historicalActivity);
    }

    //start paiement Activity
    public void startPaiementActivity(View v){
        Intent paiementActivity = new Intent(v.getContext().getApplicationContext(), PayementActivity.class);
        startActivity(paiementActivity);
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