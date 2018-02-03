package mapp.com.sg.splite.LibraryBackEnd;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Wee Kiat on 9/11/2017.
 */

public class RoomType extends Room implements Serializable {

    private final ArrayList<String> NoOfUsers;
    private final ArrayList<BookableRoom> BookableRooms;

    private final ArrayList<String> Images;
    private String SelectedDate;
    private Date MinDate;
    private Date MaxDate;

    public RoomType()
    {
        this.NoOfUsers = new ArrayList<>();
        this.BookableRooms = new ArrayList<>();
        this.Images = new ArrayList<>();
    }

    public RoomType(int id, String name)
    {
        super(id,name);
        this.NoOfUsers = new ArrayList<>();
        this.BookableRooms = new ArrayList<>();
        this.Images = new ArrayList<>();
    }

    public void AddUserCount(String Count)
    {
        NoOfUsers.add(Count);
    }

    public void AddBookableRoom(BookableRoom rm)
    {
        BookableRooms.add(rm);
    }

    public void AddImageLink(String Link)
    {
        System.out.println(Link);
        Images.add(Link);
    }

    public ArrayList<String> GetImages()
    {
        return Images;
    }

    public ArrayList<String> GetNoOfUsers()
    {
        return NoOfUsers;
    }

    public ArrayList<BookableRoom> GetBookableRooms()
    {
        return BookableRooms;
    }

    public String GetSelectedDate()
    {
        return SelectedDate;
    }

    public void SetSelectedDate(String Date)
    {
        SelectedDate = Date;
    }


    public void setMinDate(Date minDate)
    {
        MinDate = minDate;
    }

    public void setMaxDate(Date maxDate)
    {
        MaxDate = maxDate;
    }

    public String GetDateRange()
    {
        String dates = "[";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(MinDate);

        while (calendar.getTime().before(MaxDate)) {
            if (dates.length() > 1)
                dates += ",";
            Date result = calendar.getTime();
            dates += "\"" + formatter.format(result) + "\"";
            calendar.add(Calendar.DATE, 1);
        }
        dates += "]";
        return dates;
    }

}