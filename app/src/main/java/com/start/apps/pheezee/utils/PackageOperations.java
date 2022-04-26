package com.start.apps.pheezee.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import start.apps.pheezee.R;

import static com.start.apps.pheezee.utils.PackageTypes.ACHEDAMIC_TEACH_PLUS;
import static com.start.apps.pheezee.utils.PackageTypes.GOLD_PACKAGE;
import static com.start.apps.pheezee.utils.PackageTypes.GOLD_PLUS_PACKAGE;
import static com.start.apps.pheezee.utils.PackageTypes.STANDARD_PACKAGE;
import static com.start.apps.pheezee.utils.PackageTypes.TEACH_PACKAGE;

public class PackageOperations {
    public static void featureNotAvailable(Context context, String phizioemail, int package_type, String phizioname, String phone){
        if(context!=null) {
            String curent_package = getPackage(package_type);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Upgrade Package");
            builder.setMessage(context.getResources().getString(R.string.feature_not_available));
            builder.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:"));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"care@startoonlabs.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "upgrade for " + phizioemail);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + phizioname + '\n' + "Mobile Number: " + phone + '\n' + "My Current Package: " + curent_package);
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    private static String getPackage(int package_type) {
        if(package_type==STANDARD_PACKAGE){
            return "Standard";
        }else if(package_type==GOLD_PACKAGE){
            return "Gold";
        }else if(package_type==TEACH_PACKAGE){
            return "Teach Package";
        }else if(package_type==GOLD_PLUS_PACKAGE){
            return "Gold Plus";
        }else if(package_type==ACHEDAMIC_TEACH_PLUS){
            return "Achedamic Teach Plus";
        }else {
            return "No Package";
        }
    }
}
