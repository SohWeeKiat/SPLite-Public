package mapp.com.sg.splite.LibraryUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mapp.com.sg.splite.LibraryBackEnd.BookableDay;
import mapp.com.sg.splite.LibraryBackEnd.BookableRoom;
import mapp.com.sg.splite.LibraryBackEnd.LibraryErrorCode;
import mapp.com.sg.splite.LibraryBackEnd.RoomType;
import mapp.com.sg.splite.LibraryBackEnd.SP_Library;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.Util.MsgBox;

public class LibraryBooking4_2 extends AppCompatActivity implements View.OnClickListener{

    private ListView lView;
    private RoomType rt;
    private BookableRoom br;
    private SP_Library library;
    private HashMap<BookableDay,ArrayList<String>> SelectedTimeSlot;
    private int NoOfUsers;

    private EditText tBUser1,tBUser2,tBUser3;

    private class BookRoomTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... args) {
            List<String> SpiceIds = (List<String>)args[0];
            LibraryErrorCode er = library.InsertBooking(br,SelectedTimeSlot);
            if (er != LibraryErrorCode.Success)
                return false;
            return library.SubmitSpiceId(NoOfUsers,SpiceIds) == LibraryErrorCode.Success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result){
                LibraryBooking4_2.this.EnableInput(true);
                if (library.GetErrorString() != null)
                    MsgBox.Show(LibraryBooking4_2.this,"Error",library.GetErrorString());
                else
                    MsgBox.Show(LibraryBooking4_2.this,"Error","Something went wrong!");
                return;
            }
            Intent i = new Intent(LibraryBooking4_2.this, LibraryBooking4_3.class);
            i.putExtra("Selected", SelectedTimeSlot);
            i.putExtra("Room", br);
            i.putExtra("RoomType", rt);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_booking4_2);

        lView = (ListView)findViewById(R.id.LB4_2_ListView);
        tBUser1 = (EditText)findViewById(R.id.bookerAdminIdText);
        tBUser2 = (EditText)findViewById(R.id.editText3);
        tBUser3 = (EditText)findViewById(R.id.editText);

        SharedPreferences sharedPref = this.getSharedPreferences("GlobalVariables", Context.MODE_PRIVATE);

        library = (SP_Library)this.getIntent().getSerializableExtra("SPLibrary");
        ((EditText)findViewById(R.id.bookerAdminIdText)).setText(sharedPref.getString("Username",""));
        NoOfUsers = 3;
        // a new user would want to see what is going on before they key in anything
        // a keyboard blocks half of the screen, if you look at the interview video, most of the time they will remove the keyboard
        // then open the keyboard again after they figure out whats going on
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ((TextView)findViewById(R.id.TVUserIcon4_2)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.user);
        SelectedTimeSlot = (HashMap<BookableDay,ArrayList<String>>)this.getIntent().getSerializableExtra("Selected");
        rt = (RoomType)this.getIntent().getSerializableExtra("RoomType");
        br = (BookableRoom)this.getIntent().getSerializableExtra("Room");

        BookingSlotConfirmListAdapter adapter = new BookingSlotConfirmListAdapter(this,rt.getName() + " - " + br.getName(),SelectedTimeSlot);
        lView.setAdapter(adapter);

        tBUser2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        tBUser3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LB4_2ConfirmBtn){
            List<String> strings = Arrays.asList(
                    tBUser1.getText().toString().toLowerCase(),
                    tBUser2.getText().toString().toLowerCase(),
                    tBUser3.getText().toString().toLowerCase());

            String validation = checkAdminIds(strings);
            if (validation != null){
                MsgBox.Show(this, "Error", validation);
                return;
            }
            EnableInput(false);
            new BookRoomTask().execute(strings);
        }
    }

    private String checkAdminIds(List<String> strings) {

        //Remember to change to 7 for actual admin ids, current hardcode uses 6 digits  "p|P\\d{7}
        //Alternative to (\d{6}|\d{7}) is \\d{6}(\d{1}?) as ? means optional
        String pattern = "p|P(\\d{6}|\\d{7})";

        Pattern p = Pattern.compile(pattern);
        for (String userid : strings) {
            Matcher m = p.matcher(userid);
            if (!m.find())
                return "One or more admin id is invalid";
        }
        if (new HashSet<>(strings).size() < 2)
            return "Admin ids cannot be the same";
        return null;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void EnableInput(boolean fEnable) {
        findViewById(R.id.pBLibrary4_2).setVisibility(fEnable ? View.INVISIBLE : View.VISIBLE);
        tBUser2.setEnabled(fEnable);
        tBUser3.setEnabled(fEnable);
    }
}

