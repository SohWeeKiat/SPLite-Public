package mapp.com.sg.splite.LibraryUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import mapp.com.sg.splite.LibraryBackEnd.BookableDay;
import mapp.com.sg.splite.LibraryBackEnd.BookableRoom;
import mapp.com.sg.splite.LibraryBackEnd.RoomType;
import mapp.com.sg.splite.MainMenu;
import mapp.com.sg.splite.R;

public class LibraryBooking4_3 extends AppCompatActivity {

    private ListView lView;
    private RoomType rt;
    private BookableRoom br;
    private HashMap<BookableDay,ArrayList<String>> SelectedTimeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_booking4_3);

        lView = (ListView)findViewById(R.id.LB4_3_ListView);

        SelectedTimeSlot = (HashMap<BookableDay,ArrayList<String>>)this.getIntent().getSerializableExtra("Selected");
        rt = (RoomType)this.getIntent().getSerializableExtra("RoomType");
        br = (BookableRoom)this.getIntent().getSerializableExtra("Room");

        BookingSlotConfirmListAdapter adapter = new BookingSlotConfirmListAdapter(this,rt.getName() + " - " + br.getName(),SelectedTimeSlot);
        lView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this,MainMenu.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
