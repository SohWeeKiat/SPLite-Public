package mapp.com.sg.splite.MyATSBackEnd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;

import mapp.com.sg.splite.UserManagement.UserInfo;
import mapp.com.sg.splite.Util.HttpRequest;

import static mapp.com.sg.splite.Util.HttpRequest.BuildParam;

/**
 * Created by Wee Kiat on 11/11/2017.
 */

public class MyATS {

    private static final String MyATSUrl = "https://myats.sp.edu.sg/psc/cs90atstd/EMPLOYEE/HRMS/s/WEBLIB_A_ATS.ISCRIPT1.FieldFormula.IScript_SubmitAttendance";
    private static final String MyATSUrlResult = "https://myats.sp.edu.sg/psc/cs90atstd/EMPLOYEE/HRMS/s/WEBLIB_A_ATS.ISCRIPT1.FieldFormula.IScript_Acknowledgement";

    private final HttpRequest conn;
    private final UserInfo user;

    private AtsResult result;
    private Map<String,Object> ats_params;
    private String ErrorMsg;

    public MyATS(UserInfo user)
    {
        this.user = user;
        this.conn = new HttpRequest();
        this.ats_params = new LinkedHashMap<>();
    }

    private void ParseParams()
    {
        String html = this.conn.GetResponseBodyHTML();
        //System.out.println(html);
        Document htmlDocument = Jsoup.parse(html);
        Elements elements = htmlDocument.getElementsByTag("input");
        for(Element e : elements){
            if (e.hasAttr("name")){
                ats_params.put(e.attr("name"),e.attr("value"));
            }
        }
        if (ats_params.containsKey("ERROR") && ats_params.get("ERROR").equals("Y")){
            Element popup = htmlDocument.getElementById("popup1");
            ErrorMsg = popup.children().get(1).text();
        }
        Elements tables = htmlDocument.getElementsByTag("table");
        if (tables.size() >= 2){
            boolean parse = false;
            int type = 0;
            this.result = new AtsResult();
            for(Element column : tables.get(1).getElementsByTag("td")){
                if (parse){
                    parse = false;
                    switch (type){
                        case 0: this.result.setStudentID(column.text()); break;
                        case 1: this.result.setSubmissionDateTime(column.text()); break;
                        case 2: this.result.setState(column.text()); break;
                        case 3: this.result.setAtsClass(column.text()); break;
                        case 4: this.result.setLessonTime(column.text()); break;
                        case 5: this.result.setAtsCode(column.text()); break;
                    }
                }else {
                    final String[] names = { "Student ID:","Submission Time:",
                            "Attendance Status:","Module Class:","Class Date & Time:","Attendance Code:"};
                    for(int i = 0;i < names.length;i++){
                        if (column.text().equals(names[i])) {
                            parse = true;
                            type = i;
                        }
                    }
                }
            }
        }
    }

    public MyATSErrorCode TryLogin()
    {
        String Website = MyATSUrl;
        try {
            this.conn.DoGetRequest(Website);
        } catch (MalformedURLException e) {
            return MyATSErrorCode.UnknownError;
        }

        Website = this.conn.GetCurWebsite();

        if (Website.contains("redirect_ats"))
            return MyATSErrorCode.IPError;

        Map<String,Object> params = new LinkedHashMap<>();
        params.put("cmd", "login");
        params.put("languageCd", "ENG");
        Website += "?" + BuildParam(params);
        System.out.println(Website);

        params.clear();
        params.put("timezoneOffset", "-480");
        params.put("userid", user.getUsername());
        params.put("pwd", user.getPassword());

        try {
            this.conn.DoPostRequest(Website,params);
            String param = this.conn.GetURLQuery();
            if (param.length() > 1){
                Map<String,Object> query = HttpRequest.DecodeParam(param);
                if (query.containsKey("errorCode") && Integer.parseInt(query.get("errorCode").toString()) == 105)
                    return MyATSErrorCode.WrongUsernamePassword;
            }

        } catch (IOException e) {
            return MyATSErrorCode.UnknownError;
        }

        ParseParams();
        if (!ats_params.containsKey("ATT_CODE"))
            return MyATSErrorCode.UnknownError;

        return MyATSErrorCode.Success;
    }

    public boolean IsLogined()
    {
        return ats_params.containsKey("ATT_CODE");
    }

    public String GetErrorMsg()
    {
        return this.ErrorMsg;
    }

    public AtsResult GetResult()
    {
        return this.result;
    }

    public MyATSErrorCode SubmitATSCode(String Code)
    {
        ats_params.put("ATT_CODE",Code);
        try {
            this.conn.DoPostRequest(MyATSUrl,ats_params);
            ParseParams();
            if (ats_params.get("ERROR").equals("Y")){
                if (ErrorMsg.contains("Invalid attendance"))
                    return MyATSErrorCode.InvalidAttendanceCode;
                else if (ErrorMsg.contains("already submitted"))
                    return MyATSErrorCode.AlreadySubmitted;
                else if (ErrorMsg.contains("not registered"))
                    return MyATSErrorCode.NotRegisteredInClass;
                else if (ErrorMsg.contains("has ended"))
                    return MyATSErrorCode.ClassEnded;
            }else{
                this.conn.DoPostRequest(MyATSUrlResult,ats_params);
                ParseParams();
            }
        } catch (IOException e) {
            //System.out.println(e.getStackTrace().toString());
            return MyATSErrorCode.UnknownError;
        }
        if (result == null)
            return MyATSErrorCode.UnknownError;
        return MyATSErrorCode.Success;
    }
}
