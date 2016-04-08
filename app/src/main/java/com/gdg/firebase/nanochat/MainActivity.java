package com.gdg.firebase.nanochat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import codelab.gdg.nanochat.ChatMessage;

public class MainActivity extends FirebaseLoginBaseActivity {

    private Firebase mFirebaseRef;

    FirebaseListAdapter<ChatMessage> mListAdapter;

    private String email = "Android Guest";

    private MenuItem itemLogin;
    private MenuItem itemLogout;

    Boolean isFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase("https://codega.firebaseio.com");

        final EditText textEdit = (EditText) this.findViewById(R.id.text_edit);
        Button sendButton = (Button) this.findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textEdit.getText().toString();
                ChatMessage message = new ChatMessage(email, text);
                mFirebaseRef.push().setValue(message);
                textEdit.setText("");
            }
        });

        final ListView listView = (ListView) this.findViewById(android.R.id.list);
        mListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                android.R.layout.two_line_list_item, mFirebaseRef) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                ((TextView) v.findViewById(android.R.id.text1)).setText(model.getName());
                ((TextView) v.findViewById(android.R.id.text2)).setText(model.getText());
            }
        };
        listView.setAdapter(mListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        itemLogin = menu.findItem(R.id.login);
        itemLogout = menu.findItem(R.id.logout);

        itemLogin.setVisible(isFlag);
        if(isFlag){
            itemLogout.setVisible(false);
        }else{
            itemLogout.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.login) {
            showFirebaseLoginPrompt();

            return true;
        }
        if (id == R.id.logout) {
            Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
            logout();
            return true;
        }

        if (id == R.id.info) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Info developer");
            String info = "Name: Ngô Đức Dương\nEmail: ngoducduong123@gmail.com\nPhone: 01648274498";
            alertDialog.setMessage(info);
            alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Firebase getFirebaseRef() {
        return mFirebaseRef;
    }

    @Override
    protected void onFirebaseLoggedIn(AuthData authData) {
        super.onFirebaseLoggedIn(authData);

        switch (authData.getProvider()) {
            case "facebook":
                Log.d("Firebase", authData.getUid());
                email = authData.getProviderData().get("displayName") + "";
                break;
            case "password":
                if (BuildConfig.DEBUG) {
                    Log.d("Firebase", authData.getProviderData().get("email") + "");
                }
                email = authData.getProviderData().get("email") + "";
                break;
        }
        isFlag = false;
        this.invalidateOptionsMenu();
        Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onFirebaseLoggedOut() {
        super.onFirebaseLoggedOut();
        email = "Android Guest";
        isFlag = true;
        this.invalidateOptionsMenu();
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {

    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
        setEnabledAuthProvider(AuthProviderType.FACEBOOK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }
}
