package com.example.salvadortech;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseFuncionario extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "salvadorTech.db";
    private static final String TABLE_FUNCIONARIO = "funcionario";
    private static final int DATABASE_VERSION = 1;

    public DataBaseFuncionario(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_FUNCIONARIO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome_completo TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "cpf TEXT NOT NULL, " +
                "senha TEXT NOT NULL, " +
                "administrador INTEGER NOT NULL DEFAULT 1)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FUNCIONARIO);
        onCreate(db);
    }

    public boolean addFuncionario(String nome, String email, String cpf, String senha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nome_completo", nome);
        contentValues.put("email", email);
        contentValues.put("cpf", cpf);
        contentValues.put("senha", senha);
        contentValues.put("administrador", 1); // Valor fixo para administrador

        long result = db.insert(TABLE_FUNCIONARIO, null, contentValues);
        return result != -1; // Retorna true se a inserção foi bem-sucedida
    }

    // Método para ler todos os funcionários
    public Cursor getAllFuncionarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FUNCIONARIO, null);
    }
}
