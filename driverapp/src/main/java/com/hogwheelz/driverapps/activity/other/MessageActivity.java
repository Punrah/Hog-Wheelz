package com.hogwheelz.driverapps.activity.other;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hogwheelz.driverapps.R;
import com.hogwheelz.driverapps.helper.MessageSQLiteHandler;
import com.hogwheelz.driverapps.persistence.Message;

public class MessageActivity extends AppCompatActivity {

    TextView textViewSubject;
    TextView textViewBody;
    TextView textViewDate;

    String idMessage;
    String subject;
    String body;
    String date;
    ImageView back;
    ImageView delete;
    MessageSQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        db = new MessageSQLiteHandler(getApplicationContext());

        textViewSubject = (TextView) findViewById(R.id.subject);
        textViewBody = (TextView) findViewById(R.id.body);
        textViewDate = (TextView) findViewById(R.id.date);
        idMessage=getIntent().getStringExtra("id_message");
        subject = getIntent().getStringExtra("subject");
        body = getIntent().getStringExtra("body");
        date = getIntent().getStringExtra("date");

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                onBackPressed();
            }
        });

        delete = (ImageView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);

                builder.setTitle("Delete message");
                builder.setMessage("Do you want to delete this message?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteMessage(idMessage);
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });




        textViewSubject.setText(subject);
        textViewBody.setText(body);
        textViewDate.setText(date);





    }
}
