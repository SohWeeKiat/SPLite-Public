package mapp.com.sg.splite.CCAViewerUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import mapp.com.sg.splite.CCAViewerBackend.CCAActivity;
import mapp.com.sg.splite.CCAViewerBackend.CCAComponent;
import mapp.com.sg.splite.CCAViewerBackend.CCARecord;
import mapp.com.sg.splite.R;

/**
 * Created by ChenYi on 23/11/2017.
 */

public class ExpandableListActivity extends AppCompatActivity{

    private ExpandableListView mExpandableListView;
    private MyExpandableListViewAdapter mAdapter;
    private LinkedHashMap<String, ArrayList<CCAActivity>> activitiesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cca_expandablelistview);

        activitiesMap = new LinkedHashMap<>();

        CCAComponent ccaComponent = (CCAComponent) this.getIntent().getSerializableExtra("ccacomponent");
        setTitle(ccaComponent.getCapitalisedName() + ": " + ccaComponent.getTotalpoint() + " points");
        ccaComponent.sortActivities();

        for (CCAActivity ccaA : ccaComponent.getCcaActivtyArrayList()) {
            String month = ccaA.getStartMonthYear();
            if (!activitiesMap.containsKey(month)) {
                ArrayList<CCAActivity> ccaActivities = new ArrayList<>();
                ccaActivities.add(ccaA);
                activitiesMap.put(month, ccaActivities);
            }
            else {
                activitiesMap.get(month).add(ccaA);
            }
        }
        ArrayList<ArrayList<CCAActivity>> activitiesList = new ArrayList<>(activitiesMap.values());

        //Get the expandable part
        mExpandableListView = (ExpandableListView)findViewById(R.id.expendlist);
        mExpandableListView.setGroupIndicator(null);

        mAdapter = new MyExpandableListViewAdapter(this,activitiesList);
        mExpandableListView.setAdapter(mAdapter);
    }

}