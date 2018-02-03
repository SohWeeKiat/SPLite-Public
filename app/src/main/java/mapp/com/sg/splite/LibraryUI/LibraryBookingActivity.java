package mapp.com.sg.splite.LibraryUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;

import mapp.com.sg.splite.LibraryBackEnd.LibraryErrorCode;
import mapp.com.sg.splite.LibraryBackEnd.RoomType;
import mapp.com.sg.splite.LibraryBackEnd.SP_Library;
import mapp.com.sg.splite.MyATSBackEnd.MyATS;
import mapp.com.sg.splite.R;
import mapp.com.sg.splite.CustomViewPage.ViewPageAdapter;
import mapp.com.sg.splite.CustomViewPage.ViewPageHandler;
import mapp.com.sg.splite.SPMobileWebAPI;
import mapp.com.sg.splite.UserManagement.UserManagerDB;

public class LibraryBookingActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewpager;
    private LinearLayout sliderDotspanel;
    private MaterialSpinner RTS;
    private MaterialSpinner NUS;

    private SharedPreferences sharedPref;
    private SP_Library library;
    private RelativeLayout layout;
    private ProgressBar pb;

    private class LoadLibraryDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... args) {
            return library.TryLogin() == LibraryErrorCode.Success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result)
                return;
            ArrayList<RoomType> roomTypes = library.GetRoomTypes();

            ArrayList<String> arrayList = new ArrayList<>();
            for(RoomType rt : roomTypes)
                arrayList.add(rt.GetImages().get(0));

            ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(LibraryBookingActivity.this,arrayList,
                    R.layout.librarybooking4_0custom_layout, R.id.LB4_0_IV, null);
            viewpager.setAdapter(viewPagerAdapter);

            ImageView[] dots = new ImageView[viewPagerAdapter.getCount()];
            for(int i = 0; i < dots.length; i++){
                dots[i] = new ImageView(LibraryBookingActivity.this);
                dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);

                sliderDotspanel.addView(dots[i], params);
            }
            dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            viewpager.addOnPageChangeListener(new ViewPageHandler(viewpager, dots, getApplicationContext()){
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    LibraryBookingActivity.this.SetSelection(position);
                }
            });

            arrayList = new ArrayList<>();
            for(RoomType rt : roomTypes)
                arrayList.add(rt.getName());
            RTS.setItems(arrayList);
            SetSelection(0);

            enableProgressBar(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_booking);
        layout = (RelativeLayout) findViewById(R.id.LB4_0_layout);
        pb = (ProgressBar) findViewById(R.id.LB4_0_progressBar);

        sharedPref = this.getSharedPreferences("GlobalVariables", Context.MODE_PRIVATE);
        long UserID = sharedPref.getLong("UserID",-1);
        library = new SP_Library(new UserManagerDB(this).getUser(UserID));

        viewpager = (ViewPager) findViewById(R.id.LB4_0_Viewpager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.LBSliderDots);
        RTS = (MaterialSpinner) findViewById(R.id.LB_4_0RoomTypeSpinner);
        NUS = (MaterialSpinner) findViewById(R.id.LB_4_0NoUsersSpinner);

        RTS.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                LibraryBookingActivity.this.SetSelection(position);
            }
        });

        enableProgressBar(true);
        new LoadLibraryDataTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LB_4_0CheckAvailBtn:
                Intent i = new Intent(this,LibraryBooking4_1.class);
                i.putExtra("RoomType",RTS.getSelectedIndex());
                i.putExtra("NoOfUser",Integer.parseInt(NUS.getItems().get(NUS.getSelectedIndex()).toString()));
                i.putExtra("SPLibrary",library);
                startActivity(i);
                break;
        }
    }

    private void SetSelection(int selection)
    {
        RTS.setSelectedIndex(selection);
        NUS.setItems(library.GetRoomTypes().get(selection).GetNoOfUsers());
        if (NUS.getItems().size() <= 1)
            NUS.setText(NUS.getItems().get(0).toString());
        viewpager.setCurrentItem(selection);
    }

    private void enableProgressBar(boolean enable) {
         if (enable) {
             getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                     WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
             for(int count = 0; count < layout.getChildCount(); count ++)
                 layout.getChildAt(count).setVisibility(View.INVISIBLE);
             pb.setVisibility(View.VISIBLE);
         }
         else {
             getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
             for(int count = 0; count < layout.getChildCount(); count ++)
                 layout.getChildAt(count).setVisibility(View.VISIBLE);
             pb.setVisibility(View.INVISIBLE);

         }
    }
}
