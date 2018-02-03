package mapp.com.sg.splite.CCAViewerUI;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.splite.CCAViewerBackend.CCAActivity;
import mapp.com.sg.splite.R;

/**
 * Created by ChenYi on 23/11/2017.
 */

public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> mGroupList = null;
    private ArrayList<ArrayList<CCAActivity>> activitiesList;

    public MyExpandableListViewAdapter(Context context, ArrayList<ArrayList<CCAActivity>> activitiesList) {
        this.mContext = context;
        this.activitiesList = activitiesList;
    }

    /*
     *To get the number of group
     */
    @Override
    public int getGroupCount() {
        return activitiesList.size();
    }

    /*
     *To get the number of things inside a group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return activitiesList.get(groupPosition).size();
    }

    /*
     *To get the specific group data
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    /*
     *To get the things inside the specific group
     */

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return activitiesList.get(groupPosition).get(childPosition);
    }

    /*
     *To get the ID of the specific group (Unique ID)
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /*
     * To get the ID of the things inside the specific group
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    class GroupHolder{
        public TextView groupNametv;
        public ImageView groupImg;
        public TextView numbertv;
    }

    class ItemHolder{
        public TextView nameTV;
        public ImageView iconImg;
        public TextView infoTV;
        public TextView pointsTV;
    }

    /*
     * groupPosition-> the position of the group
     * isExpanded -> if the group is expanded or not expanded
     * reuse existent layout
     * the parent of the layout which return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_cca_expendlist_group,null);

            groupHolder = new GroupHolder();
            groupHolder.groupNametv = (TextView)convertView.findViewById(R.id.groupname_tv);
            groupHolder.groupImg = (ImageView) convertView.findViewById(R.id.group_img);
            groupHolder.numbertv = (TextView)convertView.findViewById(R.id.number_tv);
            convertView.setTag(groupHolder);
        } else{
            groupHolder = (GroupHolder)convertView.getTag();
        }
        if (isExpanded)
            groupHolder.groupImg.setImageResource(R.drawable.bar);

        groupHolder.groupNametv.setText(activitiesList.get(groupPosition).get(0).getStartMonthYear());
        //total points for all activities each group
        groupHolder.numbertv.setText(Integer.toString(getTotalPoints(activitiesList.get(groupPosition))));

        return convertView;
    }

    private int getTotalPoints(ArrayList<CCAActivity> A) {
        int pts = 0;
        for (CCAActivity a : A)
            pts += a.getPoints();
        return pts;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        ItemHolder itemHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_cca_expendlist_item,null);

            itemHolder = new ItemHolder();
            itemHolder.nameTV = (TextView)convertView.findViewById(R.id.itemname_tv);
            itemHolder.iconImg = (ImageView)convertView.findViewById(R.id.icon_img);
            itemHolder.infoTV = (TextView)convertView.findViewById(R.id.info_tv);
            itemHolder.pointsTV = (TextView)convertView.findViewById(R.id.cca_points);

            convertView.setTag(itemHolder);
        }
        else{
            itemHolder = (ItemHolder)convertView.getTag();
        }

        /*
         * The following part is the part different from the parent
         */
        CCAActivity cca = activitiesList.get(groupPosition).get(childPosition);

        //header
        itemHolder.nameTV.setText(cca.getName());
        itemHolder.iconImg.setBackgroundResource(R.drawable.thumb80);
        String duration = cca.getStartDayMonth().equals(cca.getEndDayMonth()) ? cca.getStartDayMonth() :
                    cca.getStartDayMonth() + " - " + cca.getEndDayMonth();
        String rolename = cca.getRolename();
        SpannableString str = new SpannableString(duration + "\n" + rolename);
        str.setSpan(new StyleSpan(Typeface.BOLD), duration.length(), str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        itemHolder.infoTV.setText(str);
        itemHolder.pointsTV.setText(cca.getPoints() + "pts");

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
