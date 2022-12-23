package com.example.appghichu.objects.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tag",
        foreignKeys = {@ForeignKey(entity = NoteEntity.class, parentColumns = "id", childColumns = "noteId", onDelete = ForeignKey.CASCADE)},
        indices = {@Index(name = "noteIndex", value = "noteId")}
)
public class TagEntity implements Parcelable
{
    public TagEntity(int id, int noteId, String tagName) {
        this.id = id;
        this.noteId = noteId;
        this.tagName = tagName;
    }

    @Ignore
    public TagEntity(String tagName) {
        this.tagName = tagName;
    }

    @Ignore
    public TagEntity(int noteId, String tagName) {
        this.noteId = noteId;
        this.tagName = tagName;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int noteId;
    private String tagName;

    protected TagEntity(Parcel in) {
        id = in.readInt();
        noteId = in.readInt();
        tagName = in.readString();
    }

    public static final Creator<TagEntity> CREATOR = new Creator<TagEntity>() {
        @Override
        public TagEntity createFromParcel(Parcel in) {
            return new TagEntity(in);
        }

        @Override
        public TagEntity[] newArray(int size) {
            return new TagEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(noteId);
        parcel.writeString(tagName);
    }
}
