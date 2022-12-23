package com.example.appghichu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import com.example.appghichu.interfaces.SimpleCallBack;
import com.example.appghichu.objects.MyCanvas;
import com.example.appghichu.objects.PaintStep;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class Utils
{
    public static final int OPEN_FOR_EDIT = 0;
    public static final int OPEN_FOR_ADD_NEW = 1;
    public static final int DISCARD_NOTE = 2;

    public static String convertToReadable(Date date)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            LocalDate dateContainer = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return "Ng. " + dateContainer.getDayOfMonth() + ", Th. " +
                    dateContainer.getMonthValue()  + ", " + dateContainer.getYear();
        }

        return "Date is not supported in your device";
    }

    public static void drawSteps(ArrayList<PaintStep> steps, Paint paint, Paint clearPaint, Canvas canvas)
    {
        for(PaintStep step : steps)
        {
            if(step.mode == MyCanvas.DRAW)
            {
                paint.setColor(step.color);
                canvas.drawPath(step.step, paint);
            }
            else
                canvas.drawPath(step.step, clearPaint);
        }
    }

    public static void showConfirmDialog(String message, SimpleCallBack listener, Context context)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setTitle(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        listener.run();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                    }
                });
        builder.show();
    }

    public static int dpToPx(int dp, Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
