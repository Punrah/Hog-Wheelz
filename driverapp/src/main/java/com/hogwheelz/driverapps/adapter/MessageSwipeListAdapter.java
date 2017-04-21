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
import com.hogwheelz.driverapps.persistence.Message;
import com.hogwheelz.driverapps.persistence.Transaction;

import java.util.List;


/**
 * Created by Ravi on 13/05/15.
 */
public class MessageSwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Message> messageList;

    public MessageSwipeListAdapter(Activity activity, List<Message> messageList) {
        this.activity = activity;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int location) {
        return messageList.get(location);
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
            convertView = inflater.inflate(R.layout.list_message, null);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView body = (TextView) convertView.findViewById(R.id.body);
        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        ImageView status = (ImageView) convertView.findViewById(R.id.status);

        date.setText(messageList.get(position).date);
        body.setText(messageList.get(position).body);
        subject.setText(messageList.get(position).subject);

        if(messageList.get(position).status.equals("read"))
        {
            status.setImageResource(R.drawable.read_message);
        }
        else if(messageList.get(position).status.equals("unread"))
        {
            status.setImageResource(R.drawable.unread_message);
        }
        else
        {
        }

        return convertView;
    }

}