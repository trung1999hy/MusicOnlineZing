package com.trungtv.Onmuzik.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import com.trungtv.Onmuzik.SongDto;

import java.io.File;
import java.util.ArrayList;

public class Utils {

    @SuppressLint("Range")
    public static ArrayList<SongDto> getAudio(Context context) {
        ArrayList<SongDto> musicInfos = new ArrayList<SongDto>();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return null;
        }
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            @SuppressLint("Range") int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != 0) {
                SongDto music = new SongDto();
                music.path = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                if (!new File(music.path).exists()) {
                    continue;
                }
                music.songId = cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));

                music.songTitle = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                music.songTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                music.album = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                music.songArtist = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                music.duration = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
               try {
                   MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                   mmr.setDataSource(music.path);
                   music.albumArt = getBitmap(mmr.getEmbeddedPicture());
                   mmr.release();
                   musicInfos.add(music);
               }catch (Exception ex){
                   continue;
               }

            }
        }
        return musicInfos;
    }

    public static String millisecondsToTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        String secondsStr = Long.toString(seconds);
        String secs;
        if (secondsStr.length() >= 2) {
            secs = secondsStr.substring(0, 2);
        } else {
            secs = "0" + secondsStr;
        }

        return minutes + ":" + secs;
    }

    private static Bitmap getBitmap(byte[] art){
        if (art != null) return BitmapFactory.decodeByteArray(art, 0, art.length);
        return null;
    }
}
