package com.refrii.client;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class UnitActivity extends AppCompatActivity {

    private Unit mUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mUnit = (Unit) intent.getSerializableExtra("unit");

        TextView labelTextView = (TextView) findViewById(R.id.labelTextView);
        TextView stepTextView = (TextView) findViewById(R.id.stepTextView);
        TextView createdTextView = (TextView) findViewById(R.id.createdTextView);
        TextView updatedTextView = (TextView) findViewById(R.id.updatedTextView);
        ImageView labelImageView = (ImageView) findViewById(R.id.labelImageView);
        ImageView stepImageView = (ImageView) findViewById(R.id.stepImageView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        setUnit(mUnit);

        labelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditLabelDialogFragment fragment = new EditLabelDialogFragment();
                fragment.setUnit(mUnit);
                fragment.show(getFragmentManager(), "edit_label");
            }
        });

        stepImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditStepDialogFragment fragment = new EditStepDialogFragment();
                fragment.setUnit(mUnit);
                fragment.show(getFragmentManager(), "edit_step");
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUnit(mUnit, view);
            }
        });
    }

    public void setUnit(Unit unit) {
        TextView labelTextView = (TextView) findViewById(R.id.labelTextView);
        TextView stepTextView = (TextView) findViewById(R.id.stepTextView);
        TextView createdTextView = (TextView) findViewById(R.id.createdTextView);
        TextView updatedTextView = (TextView) findViewById(R.id.updatedTextView);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH/mm");

        labelTextView.setText(unit.getLabel());
        stepTextView.setText(String.valueOf(unit.getStep()));
        createdTextView.setText(formatter.format(unit.getCreatedAt()));
        updatedTextView.setText(formatter.format(unit.getUpdatedAt()));
    }

    public void updateUnit(Unit unit, final View view) {
        UnitService service = RetrofitFactory.getClient(UnitService.class, UnitActivity.this);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", unit.getLabel())
                .addFormDataPart("step", String.valueOf(unit.getStep()))
                .build();
        Call<Unit> call = service.updateUnit(unit.getId(), body);
        call.enqueue(new BasicCallback<Unit>(UnitActivity.this) {
            @Override
            public void onResponse(Call<Unit> call, Response<Unit> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    public static class EditLabelDialogFragment extends DialogFragment {
        private EditText mEditText;
        private Unit mUnit;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setText(mUnit.getLabel());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Label")
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String label = mEditText.getText().toString();
                            mUnit.setLabel(label);
                            ((UnitActivity) getActivity()).setUnit(mUnit);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });
            return builder.create();
        }

        public void setUnit(Unit unit) {
            mUnit = unit;
        }
    }

    public static class EditStepDialogFragment extends DialogFragment {
        private EditText mEditText;
        private Unit mUnit;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEditText.setText(String.valueOf(mUnit.getStep()));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Step size")
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            double step = Double.valueOf(mEditText.getText().toString());
                            mUnit.setStep(step);
                            ((UnitActivity) getActivity()).setUnit(mUnit);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });
            return builder.create();
        }

        public void setUnit(Unit unit) {
            mUnit = unit;
        }
    }
}
