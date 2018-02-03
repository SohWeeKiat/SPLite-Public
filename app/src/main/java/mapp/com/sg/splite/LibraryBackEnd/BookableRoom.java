package mapp.com.sg.splite.LibraryBackEnd;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Wee Kiat on 9/11/2017.
 */

public class BookableRoom extends Room implements Serializable {

    private final ArrayList<BookableDay> bookable_days;

    public BookableRoom()
    {
        this.bookable_days = new ArrayList<>();
    }

    public BookableRoom(int id,String name)
    {
        super(id,name);
        this.bookable_days = new ArrayList<>();
    }

    public void AddBookableDay(BookableDay day)
    {
        bookable_days.add(day);
    }

    public ArrayList<BookableDay> GetBookableDays()
    {
        return bookable_days;
    }
}
