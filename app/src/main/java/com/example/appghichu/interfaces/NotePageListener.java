package com.example.appghichu.interfaces;

public interface NotePageListener
{
    void notifyCanvasSize(int width, int height);
    void onPageFocus(int currentPageIndex);
    boolean isUsingPen();
    int getMode();
    int getColor();
    boolean checkLineLimit(String html);
}
