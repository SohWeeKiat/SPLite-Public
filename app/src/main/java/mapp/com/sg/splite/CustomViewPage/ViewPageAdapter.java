package mapp.com.sg.splite.CustomViewPage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by samue on 9/11/2017.
 */

public class ViewPageAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> arrayList;
    private ArrayList<Integer> drawables;
    private int iv;
    private int layout;

    public ViewPageAdapter(Context context, ArrayList<String> arrayList, int layout,
                           int iv, ArrayList<Integer> drawables)
    {
        this.context = context;
        this.arrayList = arrayList;
        this.drawables = drawables;
        this.layout = layout;
        this.iv = iv;
    }

    @Override
    public int getCount()
    {
        if (drawables != null)
            return drawables.size();
        else if(arrayList != null)
            return arrayList.size();
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(layout, null);
        ImageView imageView = (ImageView)view.findViewById(iv);

        if (drawables != null) {
            Picasso.with(context)
                    .load(drawables.get(position))
                    .into(imageView);
        }
        else {
            Picasso.with(context)
                    .load(arrayList.get(position))
                    .into(imageView);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((LinearLayout) object);
    }
}
