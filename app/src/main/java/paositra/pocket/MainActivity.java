package paositra.pocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final static String confPref = "conf_client";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //verification si le client c'est dejà authentifier
        preferences = getSharedPreferences(confPref, Context.MODE_PRIVATE);
        String type_compte = preferences.getString("type_compte", "");
        if(type_compte.equals("")){
            loadLogin();
        }else{
            loadHome();
        }
    }

    public void loadLogin(){
        loginFragment fragment_login = new loginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment_login);
        transaction.commit();
    }
    public void loadHome(){
        homeFragment fragment_home = new homeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main, fragment_home);
        transaction.commit();
    }
}