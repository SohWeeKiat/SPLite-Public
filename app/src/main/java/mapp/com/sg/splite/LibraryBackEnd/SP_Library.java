package mapp.com.sg.splite.LibraryBackEnd;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import mapp.com.sg.splite.UserManagement.UserInfo;
import mapp.com.sg.splite.Util.HttpRequest;

/**
 * Created by Wee Kiat on 28/11/2017.
 */

public class SP_Library implements Serializable{

    private final UserInfo user;
    private final HttpRequest conn;
    private ArrayList<RoomType> room_types;
    private int CurSelection;
    private String RecordID;
    private String FailReason;

    private static final String LibrarySite = "https://eliser.lib.sp.edu.sg/";

    public SP_Library()
    {
        this.user = null;
        this.RecordID = null;
        this.conn = new HttpRequest();
    }

    public SP_Library(UserInfo user)
    {
        this.user = user;
        this.RecordID = null;
        this.conn = new HttpRequest();
    }

    public LibraryErrorCode TryLogin()
    {
        Map<String,Object> params = new LinkedHashMap<>();

        params.put("RedirectToIdentityProvider", "http://adfs.sp.edu.sg/adfs/services/trust");
        try {
            this.conn.DoGetRequest(LibrarySite + "RoomBooking/");
            String Website = this.conn.GetCurWebsite() + "&" + HttpRequest.BuildParam(params);

            params.clear();
            params.put("UserName", "student\\" + user.getUsername());
            params.put("Password", user.getPassword());
            params.put("AuthMethod", "FormsAuthentication");

            this.conn.DoPostRequest(Website, params);
            String HTML = this.conn.GetResponseHTML();
            if (!HTML.contains("Working"))
                return LibraryErrorCode.LoginFail;

            Document htmlDocument = Jsoup.parse(HTML);

            Website = htmlDocument.getElementsByTag("form").attr("action");
            params.clear();
            for (Element e : htmlDocument.getElementsByTag("input")) {
                if (e.attr("type").equals("hidden") && e.attr("name").length() > 0) {
                    params.put(e.attr("name"), e.attr("value"));
                }

            }
            this.conn.DoPostRequest(Website, params);
            if (!this.conn.GetCurWebsite().equals("https://eliser.lib.sp.edu.sg:443/RoomBooking/"))
                return LibraryErrorCode.LoginFail;

            ParseRoomTypes();
        }catch (Exception e){
            return LibraryErrorCode.LoginFail;
        }
        return LibraryErrorCode.Success;
    }

    private void ParseRoomTypes()
    {
        Document htmlDocument = Jsoup.parse(this.conn.GetResponseHTML());

        Element roomSelect = htmlDocument.getElementById("rtddl");

        room_types = new ArrayList<>();
        for(Element e : roomSelect.getElementsByTag("option")){
            //<option value="31">Audio Studio (Level 4A)</option>
            room_types.add(new RoomType(Integer.parseInt(e.attr("value")),
                    e.text()));
        }
        for(RoomType r : room_types){
            Element no_of_users = htmlDocument.getElementById("usernumberddl-" + r.getID());
            for(Element e : no_of_users.getElementsByTag("option"))
                r.AddUserCount(e.text());

            Element description = htmlDocument.getElementById("descriptionspan-" + r.getID());
            for(Element e : description.getElementsByTag("img")){
                String src = e.attr("src");
                if (!src.contains("http"))
                    src = LibrarySite.substring(0,LibrarySite.length() - 1) + src;
                else if (!src.contains("https")){
                    try{
                        HttpRequest temp = new HttpRequest();
                        temp.DoGetRequest(src);
                        src = temp.GetCurWebsite();
                    }catch (Exception ex){
                    }
                }
                r.AddImageLink(src);
            }

        }
    }

    private String GenerateRTSelectionJSON(RoomType rt,int NoOfUsers)
    {
        Map<String,Object> json = new LinkedHashMap<>();
        json.put("roomtypeid",Integer.toString(rt.getID()));
        json.put("onbehalfpatron","");
        json.put("usernumber",Integer.toString(NoOfUsers));
        json.put("selectedIds",null);
        return HttpRequest.BuildJson(json);
    }

    private void GrabTableTimeSlots(Document htmlDocument,BookableRoom br)
    {
        Element table = htmlDocument.getElementById("scheduletable");
        int RowCount = 1;
        for(Element row : table.getElementsByTag("tr")){
            if (RowCount++ <= 2)
                continue;
            Elements columns = row.getElementsByTag("td");
            if (columns.size() != 2)
                continue;
            String first_column = columns.get(0).text();
            BookableDay bd = new BookableDay(first_column.substring(0,first_column.indexOf(")") + 1),
                    Character.getNumericValue(first_column.charAt(first_column.length() - 3)),
                    Character.getNumericValue(first_column.charAt(first_column.length() - 1)));

            for(Element time : columns.get(1).getElementsByTag("div")){
                if (time.getElementsByTag("input").size() <= 0)
                    continue;
                bd.AddTime(time.getElementsByTag("span").get(0).text().replace(" ",""));
            }
            if (bd.GetTimeSlots().size() <= 0)
                continue;
            br.AddBookableDay(bd);
        }
    }

    public LibraryErrorCode GetAvailableTimeSlots(RoomType rt, int NoOfUsers)
    {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("RTSelection",GenerateRTSelectionJSON(rt,NoOfUsers));

        try {
            this.conn.DoPostRequest(LibrarySite + "RoomBooking/GetRooms/",params);

            Document htmlDocument = Jsoup.parse(this.conn.GetResponseHTML());
            rt.SetSelectedDate(htmlDocument.getElementById("datetxt").attr("value"));

            for(Element e : htmlDocument.getElementsByTag("script")){
                if (!e.data().contains("datepicker"))
                    continue;
                String script = e.data();
                int Index = script.indexOf("minDate");
                script = script.substring(script.indexOf("(",Index) + 1,script.indexOf("beforeShowDay",Index) - 10)
                        .trim().replace(" ","").replace("-1","");
                DateFormat dateFormat = new SimpleDateFormat("yyyy,MM,dd");
                rt.setMinDate(dateFormat.parse(script.substring(0,script.indexOf(")"))));
                rt.setMaxDate(dateFormat.parse(script.substring(script.indexOf("(") + 1,script.lastIndexOf(")"))));
            }

            Element rooms = htmlDocument.getElementById("selRoom");
            CurSelection = 0;
            int Count = -1;
            for(Element e : rooms.getElementsByTag("option"))
            {
                Count++;
                BookableRoom br = new BookableRoom(Integer.parseInt(e.attr("value")),
                        e.text());
                rt.AddBookableRoom(br);
                if (e.attr("selected") == null || !e.attr("selected").equals("selected"))
                    continue;
                CurSelection = Count;
                //current selected
                GrabTableTimeSlots(htmlDocument,br);
            }
        } catch (Exception ex) {
            //System.out.println(ex.getStackTrace().toString());
            return LibraryErrorCode.UnknownError;
        }
        return LibraryErrorCode.Success;
    }

    public LibraryErrorCode InsertBooking(BookableRoom br, HashMap<BookableDay,ArrayList<String>> selected_slots)
    {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("roomid",br.getID() + "|");
        for(Map.Entry<BookableDay,ArrayList<String>> e : selected_slots.entrySet()){
            for(String time : e.getValue()){
                String StartTime = time.replace(':','-');//time.substring(0,time.indexOf('-')).replace(':','-');

                String EndTime = Integer.toString(Integer.parseInt(StartTime.substring(0,2)) + 1);
                EndTime = (EndTime.length() == 1 ? ("0" + EndTime) : EndTime) + "-00";//time.substring(time.indexOf('-') + 1).replace(':','-');
                String Date = e.getKey().GetSubmitDate();
                params.put("startdatetime",Date + "-" + StartTime + "|");
                params.put("enddatetime",Date + "-" + EndTime + "|");
            }
        }
        params.put("SpiceId",null);
        this.FailReason = null;

        try {
            this.conn.DoPostRequest(LibrarySite + "RoomBooking/InsertBooking",params);

            String result = this.conn.GetResponseHTML();
            JSONObject obj = new JSONObject(result);

            if (!obj.getBoolean("success")){
                this.FailReason = (String)obj.getJSONArray("errors").get(0);
                return LibraryErrorCode.RequestError;
            }
            this.RecordID = obj.getString("recordid");
            this.RecordID = this.RecordID.substring(0,this.RecordID.length() - 1);
        } catch (Exception ex) {
            return LibraryErrorCode.UnknownError;
        }
        return LibraryErrorCode.Success;
    }

    public LibraryErrorCode SubmitSpiceId(int NoOfUsers,List<String> SpiceIds)
    {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("bookingids",this.RecordID + "|");
        params.put("purpose","");
        params.put("projectname","");
        params.put("projectsupervisor","");
        params.put("sendalert",false);
        String SpiceIdsS = "";
        for(String spiceId : SpiceIds)
            SpiceIdsS += spiceId + "|";
        params.put("SpiceIds",SpiceIdsS);
        params.put("patronemail","");
        params.put("usernumber",NoOfUsers);
        this.FailReason = null;
        try {
            this.conn.DoPostRequest(LibrarySite + "RoomBooking/UpdateBooking",params);

            JSONObject obj = new JSONObject(this.conn.GetResponseHTML());
            if (!obj.getBoolean("success")) {
                this.FailReason = (String)obj.getJSONArray("errors").get(0);
                return LibraryErrorCode.RequestError;
            }
            this.RecordID = obj.getString("BookingIds");
            this.RecordID = this.RecordID.substring(0,this.RecordID.length() - 1);
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().toString());
            return LibraryErrorCode.UnknownError;
        }
        return LibraryErrorCode.Success;
    }

    public int GetCurrentSelection()
    {
        return this.CurSelection;
    }

    public LibraryErrorCode GetAvailableTimeSlots(RoomType rt, int NoOfUsers, BookableRoom br)
    {
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("RTSelection",GenerateRTSelectionJSON(rt,NoOfUsers));

        Map<String,Object> json = new LinkedHashMap<>();
        json.put("roomtypeid",Integer.toString(rt.getID()));
        json.put("roomid",Integer.toString(br.getID()));
        json.put("usernumber",Integer.toString(NoOfUsers));
        params.put("PrevNextData",HttpRequest.BuildJson(json));
        params.put("showdates",rt.GetDateRange());
        params.put("selecteddate",rt.GetSelectedDate());

        try {
            this.conn.DoPostRequest(LibrarySite + "RoomBooking/GetRooms/",params);

            Document htmlDocument = Jsoup.parse(this.conn.GetResponseHTML());
            br.GetBookableDays().clear();
            GrabTableTimeSlots(htmlDocument,br);
        } catch (Exception ex) {
            return LibraryErrorCode.UnknownError;
        }
        return LibraryErrorCode.Success;
    }

    public ArrayList<RoomType> GetRoomTypes()
    {
        return room_types;
    }

    public String GetErrorString()
    {
        return this.FailReason;
    }
}
