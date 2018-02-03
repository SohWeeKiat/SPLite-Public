package mapp.com.sg.splite.AttendanceUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import mapp.com.sg.splite.MyATSBackEnd.AtsResult;
import mapp.com.sg.splite.R;

public class ATSResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atsresult);

        AtsResult result = (AtsResult)this.getIntent().getSerializableExtra("result");
        ((TextView)findViewById(R.id.tVAttendanceCode)).setText(result.getAtsCode());
        ((TextView)findViewById(R.id.tVLessonTime)).setText(result.getLessonTime());
        ((TextView)findViewById(R.id.tVClass)).setText(result.getAtsClass());
        ((TextView)findViewById(R.id.tVStudentID)).setText(result.getStudentID());

        String State = result.getState();
        ((TextView)findViewById(R.id.tVAbsentee)).setText(State);

        if (State.equals("Late"))
            ((ImageView)findViewById(R.id.IVATS)).setImageResource(R.drawable.warning);
    }
}
