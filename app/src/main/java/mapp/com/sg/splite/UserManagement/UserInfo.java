package mapp.com.sg.splite.UserManagement;

import android.util.Base64;

import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import mapp.com.sg.splite.Util.HttpRequest;

/**
 * Created by home on 3/2/2018.
 */

public class UserInfo implements Serializable{

    private long Id;
    private String Username;
    private String Password;
    private static final String EncryptionType = "AES/CBC/PKCS5PADDING";

    public UserInfo()
    {
        this.Id = 0;
        this.Username = null;
        this.Password = null;
    }

    public UserInfo(String Username, String Password)
    {
        this.Id = 0;
        this.Username = Username;
        this.Password = Password;
    }

    public UserInfo(int Id,String Username, String Password)
    {
        this.Id = Id;
        this.Username = Username;
        this.Password = Decrypt(Password);
    }

    private String Decrypt(String encrypted_password)
    {
        try {
            String FinalKey = this.Username + HttpRequest.getMacAddr().substring(0,16 - this.Username.length());

            IvParameterSpec iv = new IvParameterSpec(FinalKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(FinalKey.getBytes("UTF-8"), "AES");

            Cipher AesCipher = Cipher.getInstance(EncryptionType);
            AesCipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);

            byte[] decrypted = AesCipher.doFinal(Base64.decode(encrypted_password.getBytes(),Base64.DEFAULT));

            return new String(decrypted);

        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public long getId()
    {
        return Id;
    }

    public void setId(long id)
    {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getEncryptedPassword()
    {
        try {
            String FinalKey = this.Username + HttpRequest.getMacAddr().substring(0,16 - this.Username.length());

            IvParameterSpec iv = new IvParameterSpec(FinalKey.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(FinalKey.getBytes("UTF-8"), "AES");

            Cipher AesCipher = Cipher.getInstance(EncryptionType);
            AesCipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);

            byte[] encrypted = AesCipher.doFinal(this.Password.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
