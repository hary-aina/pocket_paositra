package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.nfc.NfcAdapter;
import android.widget.Toast;

public class NfcPayementActivity extends AppCompatActivity {

    //Declare NfcAdapter and PendingIntent
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_payement);

        //verification de l'existence d'appareil NFC sur le mobile
        //Get default NfcAdapter and PendingIntent instances
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // check NFC feature:
        if (nfcAdapter == null) {
            // process error device not NFC-capable…
            Toast.makeText(this, "NFC NON RECONU", Toast.LENGTH_LONG).show();
            finish();
        }else{
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, "NFC N'EST PAS ACTIVE EN MODE STANDARDS", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // single top flag avoids activity multiple instances launching


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

        //Enable NFC foreground detection
        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(this, "NFC N'EST PAS ACTIVE EN MODE STANDARDS", Toast.LENGTH_LONG).show();
                finish();
            }
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        } else {
            Toast.makeText(this, "NFC NON RECONU", Toast.LENGTH_LONG).show();
            finish();
        }

    }
}