package mapp.com.sg.splite.CCAViewerUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.WindowManager;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mapp.com.sg.splite.CCAViewerBackend.CCAGrade;
import mapp.com.sg.splite.CCAViewerBackend.CCARecord;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.SPMobileWebAPI;
import mapp.com.sg.splite.UserManagement.UserInfo;
import mapp.com.sg.splite.UserManagement.UserManagerDB;
import mapp.com.sg.splite.Util.MsgBox;

/**
 * Created by ChenYI on 26/11/2017.
 */
public class cca_chart extends AppCompatActivity implements View.OnClickListener{

    private TextView ccatotalpointv,achievementtv;
    private ListView listView;
    private CCARecord ccaRecord;

    private SharedPreferences sharedPref;
    private ProgressBar pb;
    private RelativeLayout layout;

    private class WebLoginTask extends AsyncTask<Object,Void,Boolean> {

        private final SPMobileWebAPI WebLogin;

        public WebLoginTask()
        {
            WebLogin = new SPMobileWebAPI();
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            if (WebLogin.TryLogin((UserInfo)args[0]))
                ccaRecord = WebLogin.GrabCCARecords();
            return ccaRecord != null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result){
                MsgBox.Show(cca_chart.this,"Error","Encountered an error when trying to grab CCA data");
                return;
            }
            cca_chart.this.DisplayCCA();
        }
    }

    private void DisplayCCA()
    {
        ccatotalpointv.setText(Integer.toString(ccaRecord.getTotalpoints()));
        CCAPointListAdapter customAdapter = new CCAPointListAdapter(this, ccaRecord);
        listView.setAdapter(customAdapter);
        setAchievement(ccaRecord.getCcagrade());
        enableProgressBar(false);
    }

    private void setAchievement(CCAGrade grade) {

        switch (grade) {
            case NO_GRADE:  achievementtv.setText("NO GRADE"); layout.setBackgroundResource(R.drawable.goldcca);break;
            case BRONZE: achievementtv.setText(grade + " Achieved"); layout.setBackgroundResource(R.drawable.bronze); break;
            case SILVER: achievementtv.setText(grade + " Achieved"); layout.setBackgroundResource(R.drawable.silver);break;
            case GOLD: achievementtv.setText(grade + " Achieved"); layout.setBackgroundResource(R.drawable.goldcca);break;
            case HONOURS: achievementtv.setText(grade + " Achieved"); layout.setBackgroundResource(R.drawable.goldcca);break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cca_chart);

        sharedPref = this.getSharedPreferences("GlobalVariables", Context.MODE_PRIVATE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int Height = metrics.heightPixels;
        ImageView imageView = (ImageView)findViewById(R.id.imageplaceholder);
        imageView.getLayoutParams().height = (Height > 1920 ? (int) (Height / 3.2) : (int) (Height / 3.5));

        layout = (RelativeLayout) findViewById(R.id.CCA6_0_layout);
        pb = (ProgressBar) findViewById(R.id.CCA6_0_ProgressBar);
        listView= (ListView)findViewById(R.id.cca_list);
        ccatotalpointv = (TextView) findViewById(R.id.number_tv);
        achievementtv = (TextView) findViewById(R.id.number_tv2);

        long UserID = sharedPref.getLong("UserID",-1);
        new WebLoginTask().execute(new UserManagerDB(this).getUser(UserID));
        enableProgressBar(true);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this,ExpandableListActivity.class);
        intent.putExtra("ccacomponent", ccaRecord.getCCAComponents().get((Integer) view.getTag()));
        startActivity(intent);
    }

    private void enableProgressBar(boolean enable) {
        if (enable) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            for(int count = 0; count < layout.getChildCount(); count ++)
                layout.getChildAt(count).setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            for(int count = 0; count < layout.getChildCount(); count ++)
                layout.getChildAt(count).setVisibility(View.VISIBLE);
            pb.setVisibility(View.INVISIBLE);
        }
    }
}
