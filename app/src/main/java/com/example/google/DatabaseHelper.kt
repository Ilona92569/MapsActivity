package com.example.google

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream


class DatabaseHelper(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        // База данных уже создана, поэтому в этом методе ничего делать не нужно
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Вы можете обновить базу данных здесь, если необходимо
    }

    private fun installDatabaseFromAssets() {
        val dbPath = context.getDatabasePath("your_database_name.sqlite").path
        if (File(dbPath).exists()) {
            return
        }
        // Открываем поток для чтения из assets
        val inputStream = context.assets.open("your_database_name.sqlite")
        // Открываем поток для записи в каталог приложения
        val outputStream = FileOutputStream(dbPath)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.flush()
        outputStream.close()
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        installDatabaseFromAssets()
    }


    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $DATABASE_NAME database is not writable.")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    companion object {
        private const val DATABASE_NAME = "your_database_name.sqlite"
        private const val DATABASE_VERSION = 1
    }

}