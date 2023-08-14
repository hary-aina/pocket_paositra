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

import java.util.ArrayList;

import paositra.pocket.R;

public class TransactionListAdapter extends ArrayAdapter<JsonObject> {
    private static final String TAG = "TransactionListAdapter";

    private Context mContext;
    int mRessource;

    public TransactionListAdapter(Context context, int ressource, ArrayList<JsonObject> objects){
        super(context, ressource, objects);
        mContext = context;
        mRessource = ressource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String numtransaction = getItem(position).get("num_transac").getAsString();
        String date_transaction = getItem(position).get("date_transaction").getAsString();
        String operation = getItem(position).get("operation").getAsString();
        String montant = getItem(position).get("montant").getAsString();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mRessource, parent, false);

        TextView tvnumtransaction = (TextView) convertView.findViewById(R.id.numtransaction);
        TextView tvdateTarasaction = (TextView) convertView.findViewById(R.id.date_transaction);
        TextView tvoperation = (TextView) convertView.findViewById(R.id.operation);
        TextView tvmontant = (TextView) convertView.findViewById(R.id.montant);

        if(operation.equalsIgnoreCase("DEBIT")){
            tvoperation.setTextColor(mContext.getResources().getColor(R.color.danger));
        }

        if(operation.equalsIgnoreCase("CREDIT")){
            tvoperation.setTextColor(mContext.getResources().getColor(R.color.success));
        }

        tvnumtransaction.setText(numtransaction);
        tvdateTarasaction.setText(date_transaction);
        tvoperation.setText(operation);
        tvmontant.setText(montant);

        return convertView;
    }
}
