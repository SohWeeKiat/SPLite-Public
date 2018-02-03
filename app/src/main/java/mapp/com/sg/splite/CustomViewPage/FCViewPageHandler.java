package mapp.com.sg.splite.CustomViewPage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import at.grabner.circleprogress.CircleProgressView;
import mapp.com.sg.splite.CrowdWatchBackEnd.FoodCourtInfo;
import mapp.com.sg.splite.R;

/**
 * Created by Wee Kiat on 30/11/2017.
 */

public class FCViewPageHandler extends ViewPageHandler{

    private FoodCourtInfo FCInfo;
    private TextView title;
    private CircleProgressView cpv;

    public FCViewPageHandler(ViewPager viewPager, ImageView[] dots,
                           Context context, TextView title, CircleProgressView cpv,
                             FoodCourtInfo FCInfo)
    {
        super(viewPager,dots,context);
        this.title = title;
        this.cpv = cpv;
        this.FCInfo = FCInfo;
    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPosition = position;
        for(int i = 0; i< dots.length; i++)
            dots[i].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.nonactive_dot));

        dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));
        if (title != null) {
            String foodcourt = "Food Court " + (position + 1);
            title.setText(foodcourt);
            cpv.setValue(FCInfo.GetLocation(foodcourt).getHeatMapValue());
        }
    }
}
