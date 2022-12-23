package com.example.appghichu.daos;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.appghichu.objects.entities.TagEntity;

import java.util.List;

@Dao
public interface TagDAO
{
    @Query("Select * from tag Where noteId = :id order by id;")
    List<TagEntity> getTagsFromNoteID(int id);

    @Query("Select COUNT(*) from tag Where noteId = :noteID AND tagName = :name")
    int checkIfThisTagExist(int noteID, String name);

    @Query("INSERT INTO tag(noteId, tagName) VALUES(:noteID, :name)")
    void insertNewTag(int noteID, String name);

    @Query("Select * from tag Where noteId = :id")
    List<TagEntity> getAllTagsUsingNoteID(int id);

    @Query("Delete from tag Where id=:id")
    void deleteTagById(int id);
}
