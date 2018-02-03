package mapp.com.sg.splite.AttendanceUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import mapp.com.sg.splite.SPMobileWebAPI;
import mapp.com.sg.splite.MyATSBackEnd.MyATS;
import mapp.com.sg.splite.MyATSBackEnd.MyATSErrorCode;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.UserManagement.UserManagerDB;
import mapp.com.sg.splite.Util.MsgBox;

public class AttendanceTakingActivity extends AppCompatActivity implements View.OnClickListener{

    private MyATS ats;
    private SharedPreferences sharedPref;
    private EditText tBAttendanceCode;
    private Button bSubmitAttendance;
    private ProgressBar pBAts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_taking);

        sharedPref = this.getSharedPreferences("GlobalVariables",Context.MODE_PRIVATE);

        tBAttendanceCode = (EditText)findViewById(R.id.tBAttendanceCode);
        bSubmitAttendance = (Button)findViewById(R.id.bSubmitAttendance);
        pBAts = (ProgressBar)findViewById(R.id.pBAts);

        long UserID = sharedPref.getLong("UserID",-1);
        ats = new MyATS(new UserManagerDB(this).getUser(UserID));
        EnableInput(true);
    }

    private class AtsSubmissionTask extends AsyncTask<String,Void,MyATSErrorCode> {

        @Override
        protected MyATSErrorCode doInBackground(String... args)
        {
            if (!ats.IsLogined()){
                MyATSErrorCode er = ats.TryLogin();
                if (er != MyATSErrorCode.Success)
                    return er;
            }
            return ats.SubmitATSCode(args[0]);
        }

        @Override
        protected void onPostExecute(MyATSErrorCode result) {
            switch (result){
                case Success:
                    Intent i = new Intent(AttendanceTakingActivity.this,ATSResultActivity.class);
                    i.putExtra("result",ats.GetResult());
                    startActivity(i);
                    AttendanceTakingActivity.this.finish();
                    break;
                case IPError:
                    MsgBox.Show(AttendanceTakingActivity.this,"Error","Please connect to SPStudent " +
                            "wifi profile before accessing ATS.");
                    break;
                case WrongUsernamePassword:
                    MsgBox.Show(AttendanceTakingActivity.this,"Error","Your User ID and/or Password are invalid.");
                    break;
                case InvalidAttendanceCode:
                case AlreadySubmitted:
                case NotRegisteredInClass:
                    MsgBox.Show(AttendanceTakingActivity.this,"Error",ats.GetErrorMsg());
                    break;
                case UnknownError:
                    EnableInput(true);
                    break;
            }
            if (result != MyATSErrorCode.Success)
                EnableInput(true);
        }
    }

    private void EnableInput(boolean enable)
    {
        tBAttendanceCode.setEnabled(enable);
        bSubmitAttendance.setEnabled(enable);
        pBAts.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);

        if (enable){
            tBAttendanceCode.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bSubmitAttendance){
            String ATSCode = ((EditText)findViewById(R.id.tBAttendanceCode)).getText().toString();
            EnableInput(false);
            new AtsSubmissionTask().execute(ATSCode);
        }
    }
}
