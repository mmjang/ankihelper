package com.folioreader.model.sqlite;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ExternalDatabaseContext extends ContextWrapper {

    private static final String DEBUG_CONTEXT = "ExternalDatabaseContext";
    private static final String EXTERNAL_STORAGE_DIRECTORY = "ankihelper";

    public ExternalDatabaseContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        String relativePath = name.endsWith(".db") ? name : name + ".db";

        // 如果旧位置 (Environment.getExternalStorageDirectory()/ankihelper) 存在数据库文件，尝试迁移到 App 专属存储位置
        try {
            File legacyDir = new File(Environment.getExternalStorageDirectory(), EXTERNAL_STORAGE_DIRECTORY);
            if (legacyDir.exists()) {
                File legacyDb = new File(legacyDir, relativePath);
                File standardDb = getStandardDatabasePath(relativePath);
                migrateDatabase(legacyDb, standardDb);
            }
        } catch (Exception e) {
            Log.e(DEBUG_CONTEXT, "Error during database migration check", e);
        }

        return getStandardDatabasePath(relativePath);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getStandardDatabasePath(String relativePath) {
        File extFilesDir = getExternalFilesDir(null);
        if (extFilesDir != null) {
            File standardDb = new File(extFilesDir, EXTERNAL_STORAGE_DIRECTORY + File.separator + relativePath);
            File parent = standardDb.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (parent != null && parent.exists()) {
                if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
                    Log.w(DEBUG_CONTEXT, "getStandardDatabasePath(" + relativePath + ") = " + standardDb.getAbsolutePath());
                }
                return standardDb;
            }
        }

        // 备用：应用内部数据库路径
        File internalDb = super.getDatabasePath(relativePath);
        File internalParent = internalDb.getParentFile();
        if (internalParent != null && !internalParent.exists()) {
            internalParent.mkdirs();
        }
        if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
            Log.w(DEBUG_CONTEXT, "getStandardDatabasePath(" + relativePath + ") [internal] = " + internalDb.getAbsolutePath());
        }
        return internalDb;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void migrateDatabase(File legacyDb, File standardDb) {
        if (legacyDb.exists() && !standardDb.exists()) {
            File parent = standardDb.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (copyFile(legacyDb, standardDb)) {
                legacyDb.delete();
                Log.i(DEBUG_CONTEXT, "Migrated database from " + legacyDb.getAbsolutePath() + " to " + standardDb.getAbsolutePath());

                // 迁移 journal / wal / shm 辅助文件
                String[] dbSuffixes = new String[]{"-journal", "-wal", "-shm"};
                for (String suffix : dbSuffixes) {
                    File legacyAux = new File(legacyDb.getAbsolutePath() + suffix);
                    File standardAux = new File(standardDb.getAbsolutePath() + suffix);
                    if (legacyAux.exists() && !standardAux.exists()) {
                        copyFile(legacyAux, standardAux);
                        legacyAux.delete();
                    }
                }
            }
        }
    }

    private boolean copyFile(File src, File dest) {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (Exception e) {
            Log.e(DEBUG_CONTEXT, "Failed to copy file " + src.getAbsolutePath() + " to " + dest.getAbsolutePath(), e);
            return false;
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        File dbFile = getDatabasePath(name);
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return SQLiteDatabase.openOrCreateDatabase(dbFile.getPath(), factory, errorHandler);
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        File dbFile = getDatabasePath(name);
        File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        return SQLiteDatabase.openOrCreateDatabase(dbFile.getPath(), factory);
    }
}
