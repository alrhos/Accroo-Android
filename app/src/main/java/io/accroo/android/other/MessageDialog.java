package io.accroo.android.other;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import io.accroo.android.R;

public class MessageDialog {

    public static void show(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();
    }

}
