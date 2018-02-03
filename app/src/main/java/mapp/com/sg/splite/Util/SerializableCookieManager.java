package mapp.com.sg.splite.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.List;

/**
 * Created by Wee Kiat on 28/11/2017.
 */

public class SerializableCookieManager extends CookieManager implements Serializable{

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        List<HttpCookie> cookies = this.getCookieStore().getCookies();
        out.writeInt(cookies.size());
        for(HttpCookie cookie : cookies){
            out.writeUTF(cookie.getName());
            out.writeUTF(cookie.getValue());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        int Count = in.readInt();
        for(int i = 0;i < Count;i++){
            String Name = in.readUTF();
            String value = in.readUTF();
            this.getCookieStore().add(null,HttpCookie.parse(Name + "=" + value).get(0));
        }
    }
}
