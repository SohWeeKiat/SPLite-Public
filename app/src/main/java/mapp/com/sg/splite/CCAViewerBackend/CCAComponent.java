package mapp.com.sg.splite.CCAViewerBackend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by samue on 28/11/2017.
 */

public class CCAComponent implements Serializable {

    private String name;
    private ArrayList<CCAActivity> ccaActivtyArrayList;
    private int totalpoint;

    public CCAComponent()
    {
        this.name = "";
        this.totalpoint = 0;
    }

    public CCAComponent(String name, ArrayList<CCAActivity> list, int totalpoint)
    {
        this.name = name;
        this.ccaActivtyArrayList = list;
        this.totalpoint = totalpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<CCAActivity> getCcaActivtyArrayList() {
        return ccaActivtyArrayList;
    }

    public void setCcaActivtyArrayList(ArrayList<CCAActivity> ccaActivtyArrayList) {
        this.ccaActivtyArrayList = ccaActivtyArrayList;
    }

    public int getTotalpoint() {
        return totalpoint;
    }

    public void setTotalpoint(int totalpoint) {
        this.totalpoint = totalpoint;
    }

    public String getCapitalisedName()
    {
        String ccaComponentName = getName();
        String capitalisedLetter = ccaComponentName.substring(0, 1).toUpperCase();
        String actualComponentName = capitalisedLetter + ccaComponentName.substring(1);
        return actualComponentName;

    }
    public void sortActivities()
    {
        Collections.sort(ccaActivtyArrayList);
        for (CCAActivity ccaA : ccaActivtyArrayList) {
            ccaA.convertMonthofDate(true);
            ccaA.convertMonthofDate(false);
        }
    }




}
