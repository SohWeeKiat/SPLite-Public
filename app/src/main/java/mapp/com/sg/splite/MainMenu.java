package mapp.com.sg.splite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Field;

import mapp.com.sg.splite.AttendanceUI.AttendanceTakingActivity;
import mapp.com.sg.splite.CCAViewerUI.cca_chart;
import mapp.com.sg.splite.CrowdWatchUI.CrowdWatchActivity;
import mapp.com.sg.splite.LibraryUI.LibraryBookingActivity;
import mapp.com.sg.splite.UserManagement.UserManagerDB;
import mapp.com.sg.splite.Util.MsgBox;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        sharedPref = this.getSharedPreferences("GlobalVariables",Context.MODE_PRIVATE);

        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.mainmenu_custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.mainmenu_title_text);
        mTitleTextView.setText("  SINGAPORE\nPOLYTECHNIC");

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        ImageButton imageButton = (ImageButton) findViewById(R.id.mainmenu_title_profile);

        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenu);
        final PopupMenu mPopupMenu = new PopupMenu(wrapper, imageButton);

        MenuInflater menuInflater = mPopupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu_menu, mPopupMenu.getMenu());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long Id = sharedPref.getLong("UserID",-1);
                MenuItem userID = mPopupMenu.getMenu().findItem(R.id.mainmenu_menu_userid);
                userID.setTitle(new UserManagerDB(MainMenu.this).getUser(Id).getUsername());
                userID.setEnabled(false);

                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    Object menuHelper = fMenuHelper.get(mPopupMenu);
                    Class[] argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {

                }
                mPopupMenu.show();
            }
        });
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = null;
                switch (item.getItemId()) {
                    case R.id.about:
                        i = new Intent(MainMenu.this, AboutActivity.class);
                        startActivity(i);
                        break;
                    case R.id.logout:
                        MsgBox.AskYesNo(MainMenu.this,"Logout","Are you sure you want to logout?",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which != DialogInterface.BUTTON_POSITIVE)
                                    return;
                                MsgBox.AskYesNo(MainMenu.this,"Remember me","Do you want SPLite to remember your credential for quick login?",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_NEGATIVE)
                                            new UserManagerDB(MainMenu.this).deleteUser(sharedPref.getLong("UserID",-1));
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putLong("UserID", -1);
                                        editor.commit();
                                        Intent i = new Intent(MainMenu.this, LoginActivity.class);
                                        startActivity(i);
                                        MainMenu.this.finish();
                                    }
                                });
                            }
                        });
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch(v.getId()){
            case R.id.bCrowdWatch:
                i = new Intent(this,CrowdWatchActivity.class);
                break;
            case R.id.bAttendence:
                i = new Intent(this,AttendanceTakingActivity.class);
                break;
            case R.id.bLibray:
                i = new Intent(this,LibraryBookingActivity.class);
                break;
            case R.id.bCCARecord:
                i = new Intent(this,cca_chart.class);
                break;
        }
        if (i != null)
            startActivity(i);
    }
}
