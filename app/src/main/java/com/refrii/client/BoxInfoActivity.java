package com.refrii.client;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class BoxInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Box box = (Box) getIntent().getSerializableExtra("box");

        setContentView(R.layout.activity_box_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(box.getName());
        setSupportActionBar(toolbar);

        final TextView nameTextView = (TextView) findViewById(R.id.shareTextView);
        final TextView noticeTextView = (TextView) findViewById(R.id.noticeTextView);
        TextView createdUserTextView = (TextView) findViewById(R.id.ownerBoxInfoTextView);
        TextView sharedUsersTextView = (TextView) findViewById(R.id.sharedUsersTextView);
        TextView createdAtTextView = (TextView) findViewById(R.id.createdAtBoxInfoTextView);
        TextView updatedAtTextView = (TextView) findViewById(R.id.updatedAtBoxInfoTextView);
        ImageView editNameImageView = (ImageView) findViewById(R.id.editNameImageView);
        ImageView editNoticeImageView = (ImageView) findViewById(R.id.editNoticeImageView);
        ImageView shareImageView = (ImageView) findViewById(R.id.shareImageView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        nameTextView.setText(box.getName());
        noticeTextView.setText(box.getNotice());
        createdUserTextView.setText(box.getOwner().getName());
        createdAtTextView.setText(formatter.format(box.getCreatedAt()));
        updatedAtTextView.setText(formatter.format(box.getUpdatedAt()));

        String sharedUsers = "";
        for (User user : box.getInvitedUsers()) {
            sharedUsers += user.getName();
            if (box.getInvitedUsers().get(box.getInvitedUsers().size() - 1) != user) {
                sharedUsers += System.getProperty("line.separator");
            }
        }
        sharedUsersTextView.setText(sharedUsers);

        editNameImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment fragment = new EditNameDialogFragment();
                fragment.setTextView(nameTextView);
                fragment.setBox(box);
                fragment.show(getFragmentManager(), "edit_name");
            }
        });

        editNoticeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNoticeDialogFragment fragment = new EditNoticeDialogFragment();
                fragment.setTextView(noticeTextView);
                fragment.setBox(box);
                fragment.show(getFragmentManager(), "edit_notice");
            }
        });

        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedUsersDialogFragment newFragment = new SharedUsersDialogFragment();
                newFragment.setUsers(box.getInvitedUsers());
                newFragment.show(getFragmentManager(), "contact_us");
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBox(box);
            }
        });
    }

    private void updateBox(Box box) {
        BoxService service = RetrofitFactory.getClient(BoxService.class, BoxInfoActivity.this);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", box.getName())
                .addFormDataPart("notice", box.getNotice())
                .build();
        Call<Box> call = service.updateBox(box.getId(), body);
        call.enqueue(new BasicCallback<Box>(BoxInfoActivity.this) {
            @Override
            public void onResponse(Call<Box> call, Response<Box> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    View view = findViewById(R.id.floatingActionButton);
                    Snackbar.make(view, "This box is successfully updated", Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", null).show();
                }
            }
        });
    }

    public static class EditNameDialogFragment extends DialogFragment {
        private EditText mEditText;
        private TextView mTextView;
        private Box mBox;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setText(mTextView.getText());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Name")
                .setView(content)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = mEditText.getText().toString();
                        mBox.setName(name);
                        mTextView.setText(name);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { }
                });
            return builder.create();
        }

        public void setTextView(TextView textView) {
            mTextView = textView;
        }

        public void setBox(Box box) {
            mBox = box;
        }
    }

    public static class EditNoticeDialogFragment extends DialogFragment {
        private EditText mEditText;
        private TextView mTextView;
        private Box mBox;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setSingleLine(false);
            mEditText.setMaxLines(5);
            mEditText.setText(mTextView.getText());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Notice")
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String notice = mEditText.getText().toString();
                            mBox.setNotice(notice);
                            mTextView.setText(notice);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });
            return builder.create();
        }

        public void setTextView(TextView textView) {
            mTextView = textView;
        }

        public void setBox(Box box) {
            mBox = box;
        }
    }

    public static class SharedUsersDialogFragment extends DialogFragment {
        private List<User> mUsers;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.shared_users_dialog, null);
            ListView listView = content.findViewById(R.id.listView);
            TextView shareTextView = content.findViewById(R.id.shareTextView);

            UserListAdapter adapter = new UserListAdapter(getActivity(), mUsers);
            listView.setAdapter(adapter);

            shareTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("aaa", "share");
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Shared users")
                    .setView(content);

            return builder.create();
        }

        public void setUsers(List<User> users) {
            mUsers = users;
        }

        private class UserListAdapter extends BaseAdapter {
            private Context mContext;
            private LayoutInflater mLayoutInflater;
            private List<User> mUsers;

            public UserListAdapter(Context context, List<User> users) {
                mContext = context;
                mUsers = users;
                mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return mUsers.size();
            }

            @Override
            public Object getItem(int i) {
                return mUsers.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                User user = mUsers.get(i);
                view = mLayoutInflater.inflate(R.layout.shared_user_list_row, viewGroup, false);

                TextView nameTextView = view.findViewById(R.id.shareTextView);
                TextView mailTextView = view.findViewById(R.id.mailTextView);
                ImageView removeImageView = view.findViewById(R.id.removeImageView);

                nameTextView.setText(user.getName());
                mailTextView.setText(user.getEmail());
                removeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("aaa", "333333333333333333333333333333333333333");
                    }
                });

                return view;
            }
        }
    }
}
