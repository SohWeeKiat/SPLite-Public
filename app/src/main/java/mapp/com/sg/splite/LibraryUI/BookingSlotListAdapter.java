package mapp.com.sg.splite.LibraryUI;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mapp.com.sg.splite.LibraryBackEnd.BookableDay;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.Util.MsgBox;

/**
 * Created by samue on 21/11/2017.
 */

public class BookingSlotListAdapter extends BaseAdapter implements ListAdapter{

    private LibraryBooking4_1 context;
    private ArrayList<BookableDay> bookableDays;

    public BookingSlotListAdapter(Context context, ArrayList<BookableDay> bookableDays)
    {
        this.context = (LibraryBooking4_1)context;
        this.bookableDays = bookableDays;
    }

    @Override
    public int getCount() {
        return bookableDays.size();
    }

    @Override
    public Object getItem(int pos) {
        return bookableDays.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.librarybooking4_1_listview, null);
            view.setTag(position);
        }else{
            RelativeLayout layout = (RelativeLayout)view;
            if ((int)view.getTag() != position){
                for(int count = 0; count < layout.getChildCount();) {
                    View ctrl = layout.getChildAt(count);
                    if (ctrl instanceof Button)
                        layout.removeView(ctrl);
                    else
                        count++;
                }
                view.setTag(position);
            }else
                return view;
        }
        RelativeLayout layout =(RelativeLayout)view;
        TextView listItemText = (TextView)view.findViewById(R.id.LB4_1_list_item_string);

        if (position < 0 || position >= bookableDays.size())
            return view;

        listItemText.setText(bookableDays.get(position).GetDate());
        listItemText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.date25px);

        View prevView = listItemText;

        BookableDay bd = bookableDays.get(position);
        ArrayList<String> timeslots = bd.GetTimeSlots();
        for (int i = 0; i < timeslots.size(); i++) {
            Button b = new Button(view.getContext());
            String text = timeslots.get(i);
            b.setText((String) text.substring(0,text.indexOf('-')));
            b.setId(i + 1);
            //setTag as date, retrieve time from text
            b.setTag(bd);
            b.setBackgroundColor(b.getResources().getColor(context.IsTimeslotBooked(bd,timeslots.get(i)) ? R.color.darkblue :  R.color.blue, null));

            b.setOnClickListener(new View.OnClickListener() {
                private final int DarkBlue = Color.parseColor("#4286f4");

                @Override
                public void onClick(View v) {
                    Button b = (Button) v;

                    int colorId = ((ColorDrawable) b.getBackground()).getColor();
                    //click
                    if (colorId == DarkBlue) {
                        if (context.GetSelectedCount() < 5) {
                            b.setBackgroundColor(b.getResources().getColor(R.color.darkblue, null));
                            context.AddBookingSlot((BookableDay)b.getTag(),b.getText().toString());
                            context.UpdateSelectedCount();
                        } else {
                            MsgBox.Show(context, "Error", "You have booked the maximum amount of rooms");

                        }
                    }
                    //unclick
                    else {
                        b.setBackgroundColor(b.getResources().getColor(R.color.blue, null));
                        context.RemoveBookingSlot((BookableDay)b.getTag(),b.getText().toString());
                        context.UpdateSelectedCount();
                    }
                }
            });

            RelativeLayout.LayoutParams ly = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            ly.setMargins(3, 3, 3, 3);
            ly.width = 250;

            int Column = i % 3, Row = i / 3;
            ly.addRule(RelativeLayout.END_OF, Column == 0 ? R.id.LB4_1_list_item_string : prevView.getId());
            if (Row > 0)
                ly.addRule(RelativeLayout.BELOW, i - 2);

            b.setLayoutParams(ly);
            layout.addView(b);
            prevView = b;
        }
        return view;
    }
}
