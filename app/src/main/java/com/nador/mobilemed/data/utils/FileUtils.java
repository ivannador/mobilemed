package com.nador.mobilemed.data.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;

import timber.log.Timber;

/**
 * Created by nador on 09/08/16.
 */
public class FileUtils {

    private FileUtils() {}

    public static void unzip(final String zipFile, final String targetLocation) {
        unzip(zipFile, targetLocation, null);
    }

    public static void unzip(final String zipFile, final String targetLocation, @Nullable final String password) {
        Timber.d("Unzipping file: %s", zipFile);
        try {
            ZipFile zip = new ZipFile(zipFile);
            if (zip.isEncrypted() && password != null) {
                zip.setPassword(password);
            }
            zip.extractAll(targetLocation);
        } catch (ZipException e) {
            Timber.e(e, "Unzip has failed");
        }
    }

    /**
     * Check if directory exists, create if not
     *
     * @param directoryPath Target directory path
     * @return True if directory exists
     */
    public static boolean touchDirectory(final String directoryPath) {
        File f = new File(directoryPath);
        if (!f.isDirectory()) {
            f.mkdirs();
            return false;
        }
        return true;
    }

    /**
     * Create temporary file
     *
     * @param context
     * @param url
     * @return
     */
    public static File getTempFile(final Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }

    public static File createImageFile(final Context context, String fileName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        File image = null;
        try {
            image = File.createTempFile(
                    "JPEG_" + fileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Timber.e(e, "Could not create image file");
        }
        return image;
    }

    public static String getFileExtension(final String filePath) {
        return filePath.substring(filePath.lastIndexOf("."));
    }

    public static String getFileExtension(final File file) {
        return getFileExtension(file.getAbsolutePath());
    }

    public static String getFileNameFirstPart(final String fileName) {
        int index = fileName.indexOf(".");
        return fileName.substring(0, (index == -1) ? fileName.length() : index);
    }

    public static String getFileNameFirstPart(final File file) {
        return getFileNameFirstPart(file.getName());
    }

    public static void deleteFileRecursive(File targetFile) {
        if (targetFile.isDirectory()) {
            for (File child : targetFile.listFiles()) {
                deleteFileRecursive(child);
            }
        }
        targetFile.delete();
    }
}
