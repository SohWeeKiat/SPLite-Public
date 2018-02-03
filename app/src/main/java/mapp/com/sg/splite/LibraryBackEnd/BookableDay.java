package mapp.com.sg.splite.LibraryBackEnd;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Wee Kiat on 28/11/2017.
 */

public class BookableDay implements Serializable {

    private final String date;
    private final ArrayList<String> time_slots;

    public BookableDay()
    {
        this.date = null;

        this.time_slots = new ArrayList<>();
    }

    public BookableDay(String Date,int BookedCount,int MaxBookCount)
    {
        this.date = Date;

        this.time_slots = new ArrayList<>();
    }

    public void AddTime(String time)
    {
        time_slots.add(time);
    }

    public String GetDate()
    {
        return date;
    }

    public ArrayList<String> GetTimeSlots()
    {
        return time_slots;
    }

    public String GetSubmitDate()
    {
        String day = this.date.substring(0,2);
        String month = this.date.substring(3,5);
        String year = this.date.substring(6,10);

        return year + "-" + month + "-" + day;
    }
}
