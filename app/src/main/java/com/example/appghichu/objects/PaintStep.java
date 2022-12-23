package com.example.appghichu.objects;

import android.graphics.Path;
import android.util.Pair;

public class PaintStep
{
    public int mode;
    public int color;
    public Path step;

    PaintStep(int mode, Path step, int color)
    {
        this.mode = mode;
        this.step = step;
        this.color = color;
    }
}