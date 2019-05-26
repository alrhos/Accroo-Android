package io.accroo.android.other;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import io.accroo.android.R;

public class MaintenanceDialog {

    public static void show(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.maintenance_title);
        builder.setMessage(R.string.maintenance_message);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

}
