package mapp.com.sg.splite.LibraryUI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mapp.com.sg.splite.LibraryBackEnd.BookableDay;
import mapp.com.sg.splite.R;

/**
 * Created by Wee Kiat on 7/12/2017.
 */

public class BookingSlotConfirmListAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private HashMap<BookableDay,ArrayList<String>> selected_timeslot;
    private String RoomName;

    public BookingSlotConfirmListAdapter(Context context, String RoomName,HashMap<BookableDay,ArrayList<String>> selected_timeslot)
    {
        this.context = context;
        this.RoomName = RoomName;
        this.selected_timeslot = selected_timeslot;
    }

    @Override
    public int getCount() {
        int count = 0;
        for(Map.Entry<BookableDay,ArrayList<String>> s : selected_timeslot.entrySet()){
            count += s.getValue().size();
        }
        return count;
    }

    @Override
    public Object getItem(int pos) {
        return selected_timeslot.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view != null)
            return view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.librarybooking4_2_listview, null);

        TextView SelectedDate = view.findViewById(R.id.lSelectedDate);
        TextView SelectedRoom = view.findViewById(R.id.lSelectedRoom);
        TextView SelectedTimeslot = view.findViewById(R.id.lSelectedTimeSlot);

        TextView listItemText = (TextView)view.findViewById(R.id.LB4_2_list_item_string);
        listItemText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.clock);

        int i = 0;
        for(Map.Entry<BookableDay,ArrayList<String>> s : selected_timeslot.entrySet()){
            if (i + s.getValue().size() > position){
                int time_slot = position - i;
                SelectedDate.setText(s.getKey().GetDate());
                SelectedRoom.setText(RoomName);
                SelectedTimeslot.setText(s.getValue().get(time_slot));
                break;
            }else{
                i += s.getValue().size();
            }
        }
        return view;
    }
}
