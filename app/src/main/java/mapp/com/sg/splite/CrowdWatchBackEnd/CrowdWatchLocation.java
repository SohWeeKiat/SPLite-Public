package mapp.com.sg.splite.CrowdWatchBackEnd;

import java.io.Serializable;

/**
 * Created by Wee Kiat on 9/11/2017.
 */

public class CrowdWatchLocation implements Serializable{

    private final String type;
    private final String name;
    private final int heatMapValue;
    private final String location;

    public CrowdWatchLocation(String type,String name,int heatMapValue, String location)
    {
        this.type = type;
        this.name = name;
        this.heatMapValue = heatMapValue;
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public String getLocation()
    {
        return location;
    }

    public int getHeatMapValue()
    {
        return heatMapValue;
    }
}
