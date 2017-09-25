package com.refrii.client;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class BoxInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Box box = (Box) getIntent().getSerializableExtra("box");

        setContentView(R.layout.activity_box_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(box.getName());
        setSupportActionBar(toolbar);

        TextView noticeTextView = (TextView) findViewById(R.id.noticeBoxInfoTextView);
        TextView createdUserTextView = (TextView) findViewById(R.id.ownerBoxInfoTextView);
        TextView createdAtTextView = (TextView) findViewById(R.id.createdAtBoxInfoTextView);
        TextView updatedAtTextView = (TextView) findViewById(R.id.updatedAtBoxInfoTextView);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        noticeTextView.setText(box.getNotice());
        createdUserTextView.setText(box.getOwner().getName());
        createdAtTextView.setText(formatter.format(box.getCreatedAt()));
        updatedAtTextView.setText(formatter.format(box.getUpdatedAt()));
    }

}
