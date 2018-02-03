package mapp.com.sg.splite;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import mapp.com.sg.splite.UserManagement.UserInfo;
import mapp.com.sg.splite.UserManagement.UserManagerDB;
import mapp.com.sg.splite.Util.MsgBox;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sharedPref;
    private EditText tBAdminID;
    private EditText tBPassword;

    private UserManagerDB UserDB;

    private class WebLoginTask extends AsyncTask<String,Void,Boolean>{

        private final SPMobileWebAPI WebLogin;

        public WebLoginTask()
        {
            WebLogin = new SPMobileWebAPI();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            return WebLogin.TryLogin(new UserInfo(args[0],args[1]));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                UserManagerDB userDB = new UserManagerDB(LoginActivity.this);
                LoginActivity.this.setUserIDAndEnterMainMenu(userDB.insertUser(WebLogin.getUser()));
            }else{
                LoginActivity.this.EnableInputs(true);
                MsgBox.Show(LoginActivity.this,"Error","Wrong username/password");
            }
        }
    }

    private void setUserIDAndEnterMainMenu(long Id)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("UserID", Id);
        editor.commit();

        startActivity(new Intent(LoginActivity.this,MainMenu.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        tBPassword = (EditText)findViewById(R.id.tBPassword);
        tBPassword.setTypeface(Typeface.DEFAULT);
        tBPassword.setTransformationMethod(new PasswordTransformationMethod());

        tBAdminID = (EditText)findViewById(R.id.tBAdminID);

        sharedPref = this.getSharedPreferences("GlobalVariables",Context.MODE_PRIVATE);

        tBAdminID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    hideKeyboard(v);
            }
        });

        tBPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    hideKeyboard(v);
            }
        });

        UserDB = new UserManagerDB(this);
        if (UserDB.getUserCount() > 0)
            findViewById(R.id.bQuickLogin).setVisibility(View.VISIBLE);
    }

    private void EnableInputs(boolean enable)
    {
        tBAdminID.setEnabled(enable);
        tBPassword.setEnabled(enable);
        findViewById(R.id.bLogin).setEnabled(enable);
        findViewById(R.id.pBLogin).setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bLogin:
                String Username = tBAdminID.getText().toString();
                String Password = tBPassword.getText().toString();
                String SubMessage = null;
                if (Username.length() <= 0)
                    SubMessage = "username";
                if (Password.length() <= 0)
                    SubMessage = SubMessage == null ? "password" : (SubMessage + " and password");
                if (SubMessage != null)
                    MsgBox.Show(this,"Error", "Please provide your " + SubMessage);
                else{
                    EnableInputs(false);
                    new WebLoginTask().execute(Username,Password);
                }
                break;
            case R.id.bQuickLogin:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick a user");
                ArrayList<String> usernames = UserDB.getAllUsername();
                final CharSequence[] cs = usernames.toArray(new CharSequence[usernames.size()]);
                builder.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView lw = ((AlertDialog)dialog).getListView();
                        LoginActivity.this.setUserIDAndEnterMainMenu(UserDB.getUserID(lw.getAdapter().getItem(which).toString()));
                    }
                });
                builder.show();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
