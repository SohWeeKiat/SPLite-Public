package mapp.com.sg.splite.LibraryUI;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mapp.com.sg.splite.LibraryBackEnd.BookableDay;
import mapp.com.sg.splite.LibraryBackEnd.BookableRoom;
import mapp.com.sg.splite.LibraryBackEnd.LibraryErrorCode;
import mapp.com.sg.splite.LibraryBackEnd.RoomType;
import mapp.com.sg.splite.LibraryBackEnd.SP_Library;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.Util.MsgBox;

public class LibraryBooking4_1 extends AppCompatActivity implements View.OnClickListener {

    private SP_Library library;
    private MaterialSpinner NUS;
    private ListView lView;
    private RoomType roomType;
    private BookableRoom bookableRoom;
    private int NoOfUser;

    private HashMap<BookableDay,ArrayList<String>> SelectedSlots;
    private ProgressBar pb;

    private class LoadBookingDatesDataTask extends AsyncTask<Object, Void, Boolean> {

        private int Selection;
        private  boolean FirstTime;

        public LoadBookingDatesDataTask()
        {
            this.Selection = 0;
            this.FirstTime = true;
            enableProgressBar(true);
        }

        @Override
        protected Boolean doInBackground(Object... args) {
            if ((Boolean)args[0])
                return library.GetAvailableTimeSlots(roomType,NoOfUser) == LibraryErrorCode.Success;
            this.FirstTime = false;
            Selection = (Integer)args[1];

            return library.GetAvailableTimeSlots(roomType,NoOfUser,roomType.GetBookableRooms().get(Selection)) == LibraryErrorCode.Success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result){
                MsgBox.Show(LibraryBooking4_1.this,"Error","Encountered error when trying to grab information from website");
                return;
            }
            if (FirstTime){
                ArrayList<String> rooms = new ArrayList<>();
                for(BookableRoom br : roomType.GetBookableRooms())
                    rooms.add(br.getName());
                NUS.setItems(rooms);
                if (rooms.size() == 1)
                    NUS.setText(rooms.get(0));
                Selection = library.GetCurrentSelection();
                NUS.setSelectedIndex(Selection);
            }
            bookableRoom = roomType.GetBookableRooms().get(Selection);
            BookingSlotListAdapter bookingadapter = new BookingSlotListAdapter(LibraryBooking4_1.this,
                    bookableRoom.GetBookableDays());
            //handle listview and assign adapter
            lView.setAdapter(bookingadapter);
            enableProgressBar(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_booking4_1);
        pb = (ProgressBar) findViewById(R.id.LB4_1_progressBar);

        library = (SP_Library)this.getIntent().getSerializableExtra("SPLibrary");

        SelectedSlots = new HashMap<>();

        lView = (ListView)findViewById(R.id.LB4_1_ListView);
        NUS = (MaterialSpinner) findViewById(R.id.LB4_1RoomSpinner);

        NUS.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                SelectedSlots.clear();
                UpdateSelectedCount();
                new LoadBookingDatesDataTask().execute(false,position);
            }
        });
        Intent i = this.getIntent();
        roomType = library.GetRoomTypes().get(i.getIntExtra("RoomType",0));
        NoOfUser = i.getIntExtra("NoOfUser",1);

        new LoadBookingDatesDataTask().execute(true);

    }

    public void AddBookingSlot(BookableDay bd,String timeslot)
    {
        if (SelectedSlots.containsKey(bd)){
            SelectedSlots.get(bd).add(timeslot);
        }else{
            ArrayList<String> al = new ArrayList<>();
            al.add(timeslot);
            SelectedSlots.put(bd,al);
        }
    }

    public void RemoveBookingSlot(BookableDay bd,String timeslot)
    {
        if (!SelectedSlots.containsKey(bd))
            return;
        if (SelectedSlots.get(bd).contains(timeslot))
            SelectedSlots.get(bd).remove(timeslot);
        if (SelectedSlots.get(bd).size() <= 0)
            SelectedSlots.remove(bd);
    }

    public boolean IsTimeslotBooked(BookableDay bd,String timeslot)
    {
        if (!SelectedSlots.containsKey(bd))
            return false;
        return SelectedSlots.get(bd).contains(timeslot);
    }

    public int GetSelectedCount()
    {
        int count = 0;
        for(Map.Entry<BookableDay,ArrayList<String>> s : SelectedSlots.entrySet())
            count += s.getValue().size();
        return count;
    }

    public void UpdateSelectedCount()
    {
        ((TextView)findViewById(R.id.bookableroomvalue)).setText(GetSelectedCount() + "/5");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LB4_1BookBtn){
            if (!SelectedSlots.isEmpty()) {
                Intent i = new Intent(this, LibraryBooking4_2.class);
                i.putExtra("Selected", SelectedSlots);
                i.putExtra("Room", bookableRoom);
                i.putExtra("RoomType", roomType);
                i.putExtra("SPLibrary",library);
                startActivity(i);
            }
            else {
                MsgBox.Show(this, "Error", "You must select at least 1 time slot to book");
            }
        }
    }

    private void enableProgressBar(boolean enable) {
        if (enable) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pb.setVisibility(View.VISIBLE);
            lView.setVisibility(View.INVISIBLE);
            pb.bringToFront();
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pb.setVisibility(View.INVISIBLE);
            lView.setVisibility(View.VISIBLE);
        }
    }
}
