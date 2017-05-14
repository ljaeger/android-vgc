package android.nik.virtualgeocaching.support;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Laszlo on 5/13/2017.
 */

public final class StringUtils {

    public static String getFileName(String path) {
        File file = new File(path);
        return file.getName();
    }

    public static String getFileNameFromDownloadURL(String url){
        int start = url.lastIndexOf("%")+3;
        int end = url.lastIndexOf("?");
        return url.substring(start,end);
    }

    public static String cutExtension(String fileName){
        if(fileName != null && fileName.contains("."))
            return fileName.substring(0,fileName.lastIndexOf('.'));
        else
            return fileName;
    }
}
