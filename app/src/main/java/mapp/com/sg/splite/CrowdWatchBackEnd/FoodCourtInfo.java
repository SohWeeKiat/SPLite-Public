package mapp.com.sg.splite.CrowdWatchBackEnd;

import java.io.Serializable;
import java.util.HashMap;

import mapp.com.sg.splite.Util.HttpRequest;

/**
 * Created by Wee Kiat on 10/11/2017.
 */

public class FoodCourtInfo implements Serializable{

    public static final String baseURL = "http://mobileapp.sp.edu.sg/ipcam/fcimages/";
    public static final String[] imageURL = {
            baseURL + "fc1.jpg",
            baseURL + "fc2.jpg",
            baseURL + "fc3.jpg",
            baseURL + "fc4.jpg",
            baseURL + "fc5.jpg",
            baseURL + "fc6.jpg"
    };
    private static final String HeatMapURL = "https://mobileapp.sp.edu.sg/fcheatmap/foodcourtxml.php";

    private final HttpRequest conn;
    private HashMap<String,CrowdWatchLocation> info;

    public FoodCourtInfo()
    {
        this.conn = new HttpRequest();
    }

    public void RefreshInfo()
    {
        FCHeatMapParser fcHeatMapParser = new FCHeatMapParser();
        try {
            this.conn.DoGetRequest(HeatMapURL);
            info = (HashMap<String,CrowdWatchLocation>)fcHeatMapParser.parse(this.conn.GetInputStream());
        }catch(Exception e){
            //System.out.println(e.getMessage());
        }
    }

    public CrowdWatchLocation GetLocation(String Name)
    {
        if (this.info == null)
            return null;
        return this.info.get(Name);
    }
}
