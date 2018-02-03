package mapp.com.sg.splite;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import mapp.com.sg.splite.CCAViewerBackend.CCAParser;
import mapp.com.sg.splite.CCAViewerBackend.CCARecord;
import mapp.com.sg.splite.UserManagement.UserInfo;
import mapp.com.sg.splite.Util.HttpRequest;

/**
 * Created by Wee Kiat on 9/11/2017.
 */

public class SPMobileWebAPI {

    private HttpRequest conn;
    private UserInfo user;

    private static final String WebAuth = "https://mobileweb.sp.edu.sg/pkmslogin.form";
    private static final String CCALink = "https://mobileweb.sp.edu.sg/ccaws/student/studentcca/ccastudentrequest.jsp";
    private static final String EncryptionType = "AES/CBC/PKCS5PADDING";

    public SPMobileWebAPI()
    {
        this.conn = new HttpRequest();
    }

    public boolean TryLogin(UserInfo user)
    {
        this.user = user;
        Map<String,Object> params = new LinkedHashMap<>();

        params.put("username", user.getUsername());
        params.put("password", user.getPassword());
        params.put("login-form-type", "pwd");

        try{
            this.conn.DoPostRequest(WebAuth,params);
            if (!this.conn.ContainsCookie("PD-H-SESSION-ID"))
                return false;
            return true;
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public CCARecord GrabCCARecords()
    {
        try{
            this.conn.DoGetRequest(CCALink);

            CCAParser cp = new CCAParser();
            CCARecord cr = cp.parse(this.conn.GetInputStream());

            // System.out.println("test" + mp.getTotalpoints() + "\n" + mp.getCcagrade() + "\n" + mp.getCCAComponents().get(0).getName());
            /*for (CCAComponent ccap : cr.getCCAComponents()) {
                System.out.println("=======Component=======");
                System.out.println("Name of Component: " + ccap.getName());
                System.out.println("Total points for Component: " + ccap.getTotalpoint());
                System.out.println("=======Activities=======");
                try {
                    for (CCAActivity ca : ccap.getCcaActivtyArrayList()) {
                        System.out.println("Name of Activity: " + ca.getName());
                        System.out.println("Role in Activity: " + ca.getRolename());
                        System.out.println("Start Date of Activity: " + ca.getStartdate());
                        System.out.println("End Date of Activity: " + ca.getEnddate());
                         System.out.println("Points of Activity: " + ca.getPoints());
                        System.out.println("=============");
                    }
                }
                catch (Exception e) {
                    System.out.println("No activities found");
                }
                System.out.println("\n\n");*/
            return cr;
        }catch(Exception e){
            //System.out.println(e.getMessage());
        }
        return null;
    }

    public UserInfo getUser()
    {
        return user;
    }
}
