package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {

    private final static String confPref = "conf_client";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

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
}