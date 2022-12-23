package com.example.appghichu;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.appghichu.daos.FolderDAO;
import com.example.appghichu.daos.NoteDAO;
import com.example.appghichu.daos.TagDAO;
import com.example.appghichu.objects.entities.FolderEntity;
import com.example.appghichu.objects.entities.NoteEntity;
import com.example.appghichu.objects.entities.TagEntity;

@TypeConverters({Converters.class})
@Database(entities = {NoteEntity.class, FolderEntity.class, TagEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    private static final String DB_NAME = "app.db";
    private static AppDatabase db;

    public static synchronized AppDatabase getInstance(Context context)
    {
        if(db == null)
        {
            db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .allowMainThreadQueries().build();
        }

        return db;
    }

    public abstract NoteDAO noteInterface();
    public abstract FolderDAO folderInterface();
    public abstract TagDAO tagInterface();
}