package mapp.com.sg.splite.CrowdWatchUI;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import at.grabner.circleprogress.CircleProgressView;
import mapp.com.sg.splite.CrowdWatchBackEnd.FoodCourtInfo;
import mapp.com.sg.splite.CustomViewPage.FCViewPageHandler;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.CustomViewPage.ViewPageAdapter;

public class CrowdWatch5_1Activity extends AppCompatActivity {

    private ViewPager viewpager;
    private ImageView[] dots;
    private FoodCourtInfo FCInfo;
    private CircleProgressView cpv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crowd_watch5_1);
        cpv = (CircleProgressView) findViewById(R.id.circleView);
        cpv.setBarColor(getResources().getColor(R.color.green,null),
                        getResources().getColor(R.color.orange,null),
                        getResources().getColor(R.color.red,null));

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.cw5_1custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.CW5_1_title_text);
        mTitleTextView.setText("Food Court 1");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        viewpager = (ViewPager) findViewById(R.id.CW5_1_Viewpager);
        ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(this,new ArrayList<>((Arrays.asList(FoodCourtInfo.imageURL))),
                R.layout.cw5_1custom_layout, R.id.CW5_1_IV, null);
        viewpager.setAdapter(viewPagerAdapter);

        LinearLayout sliderDotspanel = (LinearLayout)findViewById(R.id.SliderDots);
        dots = new ImageView[viewPagerAdapter.getCount()];
        for(int i = 0; i < dots.length; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        FCInfo = (FoodCourtInfo)this.getIntent().getSerializableExtra("FCInfo");
        viewpager.addOnPageChangeListener(new FCViewPageHandler(viewpager, dots,
                getApplicationContext(), mTitleTextView, cpv, FCInfo));
        setSelection(getIntent().getIntExtra("Selection", 0));
    }

    private void setSelection(int selection)
    {
        viewpager.setCurrentItem(selection++);
        cpv.setValue(FCInfo.GetLocation("Food Court " + Integer.toString(selection)).getHeatMapValue());
    }
}