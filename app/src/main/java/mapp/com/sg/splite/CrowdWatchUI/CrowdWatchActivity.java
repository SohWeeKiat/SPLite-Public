package mapp.com.sg.splite.CrowdWatchUI;

import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import mapp.com.sg.splite.CrowdWatchBackEnd.CrowdWatchLocation;
import mapp.com.sg.splite.CrowdWatchBackEnd.FoodCourtInfo;
import mapp.com.sg.splite.R;


public class CrowdWatchActivity extends AppCompatActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    private FoodCourtInfo FCInfo;
    private int CompletionCount;

    private ImageView[] IVFCs;
    private ProgressBar mProgressBar;

    private class RefreshHeatMapTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            FCInfo.RefreshInfo();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ImageView[] IVFCHeatMap = {
                    (ImageView) findViewById(R.id.PH1),
                    (ImageView) findViewById(R.id.PH2),
                    (ImageView) findViewById(R.id.PH3),
                    (ImageView) findViewById(R.id.PH4),
                    (ImageView) findViewById(R.id.PH5),
                    (ImageView) findViewById(R.id.PH6)};

            for(int i = 1;i <= IVFCHeatMap.length;i++){
                CrowdWatchLocation l = FCInfo.GetLocation("Food Court " + i);
                if (l != null){
                    if (l.getHeatMapValue() < 50)
                        IVFCHeatMap[i - 1].setImageResource(R.drawable.user_green);
                    else if (l.getHeatMapValue() < 75)
                        IVFCHeatMap[i - 1].setImageResource(R.drawable.user_yellow);
                    else
                        IVFCHeatMap[i - 1].setImageResource(R.drawable.user_red);
                }
            }
            CrowdWatchActivity.this.OnTaskCompletion();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_watch);

        FCInfo = new FoodCourtInfo();
        IVFCs = new ImageView[]{
                (ImageView) findViewById(R.id.IVFC1),
                (ImageView) findViewById(R.id.IVFC2),
                (ImageView) findViewById(R.id.IVFC3),
                (ImageView) findViewById(R.id.IVFC4),
                (ImageView) findViewById(R.id.IVFC5),
                (ImageView) findViewById(R.id.IVFC6)
            };

        mProgressBar = (ProgressBar) findViewById(R.id.CW5_0_progressBar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ((SwipeRefreshLayout)findViewById(R.id.CW5_0_SwipeRefreshLayout)).setOnRefreshListener(this);

        for (int i = 0; i < IVFCs.length;i++) {
            IVFCs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CrowdWatchActivity.this.onClick(v);
                }
            });
        }
        RefreshFoodCourtData(false);
    }

    private void RefreshFoodCourtData(boolean PullToRefesh)
    {
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.CW5_0_layout);
        for(int count = 0; count < layout.getChildCount(); count ++)
            layout.getChildAt(count).setVisibility(View.INVISIBLE);
        if (!PullToRefesh)
            mProgressBar.setVisibility(View.VISIBLE);

        CompletionCount = 0;
        new RefreshHeatMapTask().execute();
        for (int i = 0; i < IVFCs.length;i++)
            addURL(this,FoodCourtInfo.imageURL[i], IVFCs[i] );
    }

    private void addURL(Context context, String url, final ImageView imageview) {

        Picasso.with(context)
                .load(url)
                .into(imageview, new Callback(){
                    @Override
                    public void onSuccess() {
                        CrowdWatchActivity.this.OnTaskCompletion();
                    }

                    @Override
                    public void onError() {}
                });
    }

    private void OnTaskCompletion()
    {
        if (++CompletionCount >= 7) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.CW5_0_layout);
            for(int count = 0; count < layout.getChildCount(); count ++) {
                layout.getChildAt(count).setVisibility(View.VISIBLE);
            }
            ProgressBar pb = (ProgressBar) findViewById(R.id.CW5_0_progressBar);
            pb.setVisibility(View.INVISIBLE);

            ((SwipeRefreshLayout)findViewById(R.id.CW5_0_SwipeRefreshLayout)).setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(this,CrowdWatch5_1Activity.class);
        switch (v.getId()){
            case R.id.IVFC1:
                i.putExtra("Selection",0); break;
            case R.id.IVFC2:
                i.putExtra("Selection",1); break;
            case R.id.IVFC3:
                i.putExtra("Selection",2); break;
            case R.id.IVFC4:
                i.putExtra("Selection",3); break;
            case R.id.IVFC5:
                i.putExtra("Selection",4); break;
            case R.id.IVFC6:
                i.putExtra("Selection",5); break;

        }
        i.putExtra("FCInfo",FCInfo);
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        RefreshFoodCourtData(true);
    }
}
