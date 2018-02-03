package mapp.com.sg.splite.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * Created by Wee Kiat on 2/11/2017.
 */

public class HttpRequest implements Serializable{

    private final SerializableCookieManager msCookieManager;
    private transient HttpURLConnection conn;
    private int Timeout;

    private static final String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    public HttpRequest()
    {
        msCookieManager = new SerializableCookieManager();
        this.Timeout = 10000;
    }

    private void SetConnCommonProperties()
    {
        conn.setReadTimeout(this.Timeout);
        conn.setConnectTimeout(this.Timeout);
        conn.setRequestProperty("User-Agent", UserAgent);
        conn.setInstanceFollowRedirects(false);

        StringBuilder sb = new StringBuilder();
        for (HttpCookie cookie : msCookieManager.getCookieStore().getCookies()) {
            sb.append(cookie);
            sb.append("; ");
        }
        conn.setRequestProperty("Cookie", sb.toString());
    }

    private void GrabCookies()
    {
        List<String> cookiesHeader = conn.getHeaderFields().get("Set-Cookie");
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }

    public void SetTimeout(int Timeout)
    {
        this.Timeout = Timeout;
    }

    public int DoGetRequest(String Website) throws MalformedURLException
    {
        int status;
        do {
            //System.out.println(Website);
            try {
                conn = (HttpURLConnection)new URL(Website).openConnection();
                this.SetConnCommonProperties();

                conn.connect();
                GrabCookies();

                status = conn.getResponseCode();
            } catch (IOException ex) {
                return HttpURLConnection.HTTP_BAD_REQUEST;
            }

            //System.out.println(status);
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    Website = conn.getHeaderField("Location");
                    if (!Website.contains("http")) {
                        Website = conn.getURL().getProtocol() + "://" + conn.getURL().getHost() + Website;
                    }
                }
            }
        } while (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER);
        return status;
    }

    public int DoPostRequest(String Website, Map<String,Object> params) throws MalformedURLException, IOException
    {
        int status;
        boolean SetItOnce = false;
        do {
            //System.out.println(Website);
            conn = (HttpURLConnection)new URL(Website).openConnection();
            this.SetConnCommonProperties();

            if (!SetItOnce){
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Referer", Website);

                String postData = BuildParam(params);

                conn.setRequestProperty("Content-Length", Integer.toString(postData.length()));
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())){
                    wr.write(postData.getBytes("UTF8"));
                }
                SetItOnce = true;
            }

            conn.connect();
            this.GrabCookies();

            status = conn.getResponseCode();
            //System.out.println(status);
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    Website = conn.getHeaderField("Location");
                    return DoGetRequest(Website);
                }
            }
        } while (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER);
        return status;
    }

    public String GetResponseHTML()
    {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String s = br.readLine();
            while (s != null) {
                sb.append(s);
                sb.append("\r\n");
                s = br.readLine();
            }
        }catch(IOException e){

        }
        return sb.toString();
    }

    public String GetResponseBodyHTML()
    {
        String HTML = GetResponseHTML();
        int BodyIndex = HTML.indexOf("<BODY>");
        HTML = HTML.substring(BodyIndex,HTML.indexOf("</BODY>",BodyIndex) + 7);
        return HTML;
    }

    public InputStream GetInputStream() throws IOException
    {
        return this.conn.getInputStream();
    }

    public String GetCurWebsite()
    {
        return conn.getURL().toString();
    }

    public String GetURLQuery()
    {
        return conn.getURL().getQuery();
    }

    public String GetURLPath()
    {
        return conn.getURL().getPath();
    }

    public boolean ContainsCookie(String Key)
    {
        for (HttpCookie cookie : msCookieManager.getCookieStore().getCookies()) {
            if (cookie.getName().equals(Key))
                return true;
        }
        return false;
    }

    public static void DisableSSLChecking()
    {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        //No need to implement.
                    }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String getMacAddr()
    {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static String BuildParam(Map<String,Object> params)
    {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            try {
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {

            }
        }
        return postData.toString();
    }

    public static Map<String,Object> DecodeParam(String param)
    {
        Map<String,Object> params = new LinkedHashMap<>();
        StringTokenizer st = new StringTokenizer(param,"&",false);
        while(st.hasMoreTokens()){
            try {
                String one_param = st.nextToken();
                int Index = one_param.indexOf("=");

                String Key = one_param.substring(0, Index);
                String value = URLDecoder.decode(one_param.substring(Index + 1), "UTF-8");
                params.put(Key,value);
            }catch (UnsupportedEncodingException ex) {

            }
        }
        return params;
    }

    public static String BuildJson(Map<String,Object> params)
    {
        StringBuilder postData = new StringBuilder();
        postData.append('{');
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() > 1)
                postData.append(',');
            try {
                postData.append('"');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('"');
                postData.append(':');
                if (param.getValue() != null){
                    if (param.getValue() instanceof String){
                        postData.append('"');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                        postData.append('"');
                    }else{
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                    }
                }else{
                    postData.append("null");
                }


            } catch (UnsupportedEncodingException ex) {

            }
        }
        postData.append('}');
        return postData.toString();
    }
}
