package mapp.com.sg.splite.CCAViewerBackend;

import java.io.Serializable;

/**
 * Created by samue on 28/11/2017.
 */

public class CCAActivity implements Serializable, Comparable {

    private String name;
    private String rolename;
    private String startdate;
    private String enddate;
    private int points;

    public CCAActivity()
    {
        this.name = "";
        this.rolename = "";
        this.startdate = "";
        this.enddate = "";
        this.points = 0;
    }

    public CCAActivity(String name, String rolename, String sd, String ed, int points)
    {
        this.name = name;
        this.rolename = rolename;
        this.startdate = sd;
        this.enddate = ed;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // only can be used after ExpandableListActivity oncreate is called
    public String getStartMonthYear() {
        return startdate.substring(3);
    }

    public String getStartDayMonth()
    {
        return startdate.substring(0,enddate.lastIndexOf(" "));
    }

    public String getEndDayMonth()
    {
        return enddate.substring(0,enddate.lastIndexOf(" "));
    }
    // end

    public int getStartYear()
    {
     return Integer.parseInt(startdate.substring(6, startdate.length()));
    }

    public int getStartMonth()
    {
        return Integer.parseInt(startdate.substring(3,5));
    }

    public void convertMonthofDate(boolean StartDate)
    {
        String date = StartDate ? this.getStartdate() : this.getEnddate();
        String day = date.substring(0,2);
        String month = date.substring(3,5);
        String year = date.substring(6,10);
        String monthname = "";
        switch (month) {
            case "01": monthname = "JAN"; break;
            case "02": monthname = "FEB"; break;
            case "03": monthname = "MARCH"; break;
            case "04": monthname = "APRIL"; break;
            case "05": monthname = "MAY"; break;
            case "06": monthname = "JUNE"; break;
            case "07": monthname = "JULY"; break;
            case "08": monthname = "AUG"; break;
            case "09": monthname = "SEP"; break;
            case "10": monthname = "OCT"; break;
            case "11": monthname = "NOV"; break;
            case "12": monthname = "DEC"; break;
        }
        String format = day + " " + monthname + " " + year;
        if (StartDate)
            this.setStartdate(format);
        else
            this.setEnddate(format);
    }

    @Override
    public int compareTo(Object o) {
        CCAActivity next = (CCAActivity) o;
        if (this.getStartYear() > next.getStartYear()) {
            return -1;
        }
        else if (this.getStartYear() < next.getStartYear()) {
            return 1;
        }
        // year equal
        else {
            if (this.getStartMonth() > next.getStartMonth())
                return -1;
            else if (this.getStartMonth() < next.getStartMonth())
                return 1;
            else return 0;
        }
    }
}
