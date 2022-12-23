package com.example.appghichu.objects.dtos;

import android.graphics.Bitmap;

import androidx.room.Entity;

import com.example.appghichu.objects.PaintStep;

import java.util.ArrayList;

public class NotePageDTO
{
    private String html;
    private Bitmap previousBitmap;

    private ArrayList<PaintStep> steps;

    public NotePageDTO(Bitmap previousBitmap, String html, int fontSize)
    {
        this.previousBitmap = previousBitmap;
        this.html = html;

        steps = new ArrayList<>();
    }

    public Bitmap getPreviousBitmap() {
        return previousBitmap;
    }

    public String getHtml() {
        return html;
    }

    public ArrayList<PaintStep> getSteps() {
        return steps;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}