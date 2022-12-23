package com.example.appghichu.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.appghichu.Utils;
import com.example.appghichu.interfaces.NotePageListener;

import java.util.ArrayList;

public class MyCanvas extends View
{
    public static final int DRAW = 0;
    public static final int ERASE = 1;

    private boolean first = true;
    private ArrayList<PaintStep> steps;

    private Paint paint, clearPaint;
    private Bitmap previousBitmap;

    private NotePageListener notePageListener;

    public MyCanvas(Context context)
    {
        super(context);
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(previousBitmap != null)
        {
            Rect wholeCanvas = new Rect();
            wholeCanvas.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(previousBitmap, null, wholeCanvas, null);
        }

        if(first)
        {
            paint  = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            clearPaint = new Paint();
            clearPaint.setStyle(Paint.Style.STROKE);
            clearPaint.setStrokeWidth(30);
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            clearPaint.setAntiAlias(true);

            first = false;
            notePageListener.notifyCanvasSize(getWidth(), getHeight());
        }

        if(steps == null)
            Log.d("From MyCanvas", "can't draw null list of steps");

        Utils.drawSteps(steps, paint, clearPaint, canvas);
    }

    public void draw(float currentX, float currentY)
    {
        steps.get(steps.size() - 1).step.lineTo(currentX, currentY);

        invalidate();
    }

    public void beginPath(float currentX, float currentY)
    {
        Path path = new Path();
        steps.add(new PaintStep(notePageListener.getMode(), path, notePageListener.getColor()));

        path.moveTo(currentX, currentY);
    }

    public void setNotePageListener(NotePageListener notePageListener)
    {
        this.notePageListener = notePageListener;
    }

    public void initSteps(ArrayList<PaintStep> steps)
    {
        this.steps = steps;
    }

    public void setPreviousBitmap(Bitmap bm)
    {
        previousBitmap = bm;
    }

    public void redraw()
    {
        invalidate();
    }
}
