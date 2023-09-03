package paositra.pocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import paositra.pocket.R;

public class TransactionListAdapter extends ArrayAdapter<JsonObject> {
    private static final String TAG = "TransactionListAdapter";

    private Context mContext;
    int mRessource;
    private DecimalFormat df;

    public TransactionListAdapter(Context context, int ressource, ArrayList<JsonObject> objects){
        super(context, ressource, objects);
        mContext = context;
        mRessource = ressource;
        df = new DecimalFormat("#,###.00");;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String numtransaction = getItem(position).get("num_transac").getAsString();
        String date_transaction = getItem(position).get("date_transaction").getAsString();
        String operation = getItem(position).get("operation").getAsString();
        String montant = getItem(position).get("montant").getAsString();
        String commentaire = getItem(position).get("commentaire").getAsString();
        String service = getItem(position).get("service").getAsString();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mRessource, parent, false);

        TextView tvcomtransaction = (TextView) convertView.findViewById(R.id.comtransaction);
        TextView tvservicetransaction = (TextView) convertView.findViewById(R.id.servicetransaction);
        TextView tvdateTarasaction = (TextView) convertView.findViewById(R.id.date_transaction);
        TextView tvmontant = (TextView) convertView.findViewById(R.id.montant);

        if(operation.equalsIgnoreCase("DEBIT")){
            montant = "-" + df.format(Double.parseDouble(montant));
            tvmontant.setText(montant);
            tvmontant.setTextColor(mContext.getResources().getColor(R.color.danger));
        }

        if(operation.equalsIgnoreCase("CREDIT")){
            montant = "+" + df.format(Double.parseDouble(montant));
            tvmontant.setText(montant);
            tvmontant.setTextColor(mContext.getResources().getColor(R.color.success));
        }

        String outputDateFormat = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);
        try{
            Date inputDate = inputFormat.parse(date_transaction);
            String outputDateString = outputFormat.format(inputDate);
            tvdateTarasaction.setText(outputDateString);
        }catch (ParseException e){
            tvdateTarasaction.setText("inconnue");
        }
        tvcomtransaction.setText(commentaire);
        tvservicetransaction.setText(service);
        tvmontant.setText(montant);

        return convertView;
    }
}
