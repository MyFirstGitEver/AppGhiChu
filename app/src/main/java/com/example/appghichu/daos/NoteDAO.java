package com.example.appghichu.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.entities.TagEntity;

import java.util.List;

@Dao
public interface NoteDAO
{
    @Query("Select * from note Where folderID = :folderID;")
    List<NoteEntity> getAllNotesInFolder(int folderID);

    @Query("select seq from sqlite_sequence WHERE name = \'note\'")
    int getCurrentNoteID();

    @Query("Select COUNT(*) from note Where folderID = :id")
    int countNotesInsideFolder(int id);

    @Query("Update note SET folderID = :parent Where id = :id")
    void move(int id, int parent);

    @Query("Select * from note")
    List<NoteEntity> listAll();

    @Query("Select * from note Where title LIKE :term")
    List<NoteEntity> searchUsingTitle(String term);

    @Insert
    void insertNewNote(NoteEntity note);

    @Update
    void updateNoteContent(NoteEntity note);

    @Delete
    void deleteNote(NoteEntity note);

    @RawQuery
    List<NoteEntity> searchUsingTags(SupportSQLiteQuery query);
}