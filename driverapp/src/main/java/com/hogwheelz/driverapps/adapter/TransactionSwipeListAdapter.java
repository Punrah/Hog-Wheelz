package com.hogwheelz.driverapps.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.app.Formater;
import com.hogwheelz.driverapps.persistence.Transaction;

import java.util.List;


/**
 * Created by Ravi on 13/05/15.
 */
public class TransactionSwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Transaction> transactionList;

    public TransactionSwipeListAdapter(Activity activity, List<Transaction> transactionList) {
        this.activity = activity;
        this.transactionList = transactionList;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int location) {
        return transactionList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_transaction, null);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView info = (TextView) convertView.findViewById(R.id.info);
        TextView amount = (TextView) convertView.findViewById(R.id.amount);
        ImageView transactionType = (ImageView) convertView.findViewById(R.id.transaction_type);

        date.setText(transactionList.get(position).getDateTime());
        info.setText(transactionList.get(position).info);
        amount.setText(Formater.getPrice(String.valueOf(transactionList.get(position).amount)));

        if(transactionList.get(position).type.equals("debit"))
        {
            transactionType.setImageResource(R.drawable.debit);
        }
        else if(transactionList.get(position).type.equals("kredit"))
        {
            transactionType.setImageResource(R.drawable.credit);
        }
        else
        {
        }

        return convertView;
    }

}