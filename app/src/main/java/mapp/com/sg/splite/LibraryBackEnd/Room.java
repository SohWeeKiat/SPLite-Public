package mapp.com.sg.splite.LibraryBackEnd;

import java.io.Serializable;

/**
 * Created by Wee Kiat on 9/11/2017.
 */

public class Room implements Serializable{

    protected final int id;
    protected final String room_name;

    public Room()
    {
        this.id = 0;
        this.room_name = null;
    }

    public Room(int id,String name)
    {
        this.id = id;
        this.room_name = name;
    }

    public int getID()
    {
        return id;
    }

    public String getName()
    {
        return room_name;
    }
}