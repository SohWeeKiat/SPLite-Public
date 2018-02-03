package mapp.com.sg.splite.CustomViewPage;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import mapp.com.sg.splite.R;

/**
 * Created by samue on 9/11/2017.
 */

public class ViewPageHandler implements ViewPager.OnPageChangeListener {

    protected ViewPager mViewPager;
    protected int mCurrentPosition;
    private int mScrollState;
    protected ImageView[] dots;
    protected Context context;

    public ViewPageHandler( ViewPager viewPager, ImageView[] dots, Context context)
    {
        this.mViewPager = viewPager;
        this.dots = dots;
        this.context = context;
    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPosition = position;
        for(int i = 0; i< dots.length; i++)
            dots[i].setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.nonactive_dot));
        dots[position].setImageDrawable(ContextCompat.getDrawable(context, R.drawable.active_dot));
    }

    @Override
    public void onPageScrollStateChanged(final int state)
    {
        handleScrollState(state);
        mScrollState = state;
    }

    private void handleScrollState(final int state)
    {
        if (state == ViewPager.SCROLL_STATE_IDLE)
            setNextItemIfNeeded();
    }

    private void setNextItemIfNeeded()
    {
        if (!isScrollStateSettling())
            handleSetNextItem();
    }

    private boolean isScrollStateSettling()
    {
        return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
    }

    private void handleSetNextItem()
    {
        final int lastPosition = mViewPager.getAdapter().getCount() - 1;
        if(mCurrentPosition == lastPosition)
            mViewPager.setCurrentItem(0, true);
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels)
    {
    }
}
