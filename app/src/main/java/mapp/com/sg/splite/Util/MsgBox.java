package mapp.com.sg.splite.Util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Wee Kiat on 11/11/2017.
 */

public class MsgBox {

    public static void Show(Activity a, String Title, String Message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(a).create();
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void AskYesNo(Activity a,String Title, String Message,DialogInterface.OnClickListener dialogClickListener)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(a).create();
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",dialogClickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",dialogClickListener);
        alertDialog.show();
    }

}
