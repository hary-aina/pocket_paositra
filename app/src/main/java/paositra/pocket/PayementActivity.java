package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PayementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payement);

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

    //start NFC payement
    public void startPaiementNFCActivity(View v){
        Intent nfcActivity = new Intent(v.getContext().getApplicationContext(), NfcPayementActivity.class);
        startActivity(nfcActivity);
    }
}