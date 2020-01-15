package pt.ubi.di.ignite_admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {
    //Construtor da database
    public DataBase(Context context){
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }

    //Definição de Strings para executar os comandos SQL

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Ignite_admin.db";

    protected static final String TABLE_USERS = "Users";
    protected static final String COLUMN_0_1 = "name";
    protected static final String COLUMN_0_2 = "surname";
    protected static final String COLUMN_0_3 = "dia";
    protected static final String COLUMN_0_4 = "mes";
    protected static final String COLUMN_0_5 = "ano";
    protected static final String COLUMN_0_6 = "username";
    protected static final String COLUMN_0_7 = "password";

    protected static final String TABLE_LOGIN = "Logins";
    protected static final String COLUMN_1_1 = "name";
    protected static final String COLUMN_1_2 = "password";

    protected static final String TABLE_EVENTS = "Events";
    protected static final String COLUMN_2_1 = "title";
    protected static final String COLUMN_2_2 = "description";
    protected static final String COLUMN_2_3= "min_age";
    protected static final String COLUMN_2_4= "max_age";
    protected static final String COLUMN_2_5= "local";
    protected static final String COLUMN_2_6= "day";
    protected static final String COLUMN_2_7= "month";
    protected static final String COLUMN_2_8= "year";
    protected static final String COLUMN_2_9= "image";
    protected static final String COLUMN_2_10= "inscritos";
    protected static final String COLUMN_2_11= "limite";

    protected static final String TABLE_USERS_EVENTS = "UsersEvents";
    protected static final String COLUMN_3_1 = "id";
    protected static final String COLUMN_3_2 = "username";
    protected static final String COLUMN_3_3 = "event";


    protected static final String TABLE_NEWS = "News";
    protected static final String COLUMN_4_1 = "title";
    protected static final String COLUMN_4_2 = "content";
    protected static final String COLUMN_4_3= "image";
    protected static final String COLUMN_4_4 = "data";


    //CREATE TABLES

    private static final String LOGIN_TABLE_CREATE = "CREATE TABLE " + TABLE_LOGIN + "("
            + COLUMN_1_1 + " VARCHAR(18) PRIMARY KEY,"
            + COLUMN_1_2 + " VARCHAR(18));";

    private static final String USERS_TABLE_CREATE = "CREATE TABLE " + TABLE_USERS+ "("
            + COLUMN_0_1 + " TEXT,"
            + COLUMN_0_2 + " TEXT,"
            + COLUMN_0_3 + " INTEGER,"
            + COLUMN_0_4 + " INTEGER,"
            + COLUMN_0_5 + " INTEGER,"
            + COLUMN_0_6 + " VARCHAR(20) PRIMARY KEY,"
            + COLUMN_0_7 + " VARCHAR(20));";

    private static final String EVENTS_TABLE_CREATE = "CREATE TABLE " + TABLE_EVENTS+ "("
            + COLUMN_2_1 + " VARCHAR(50) PRIMARY KEY,"
            + COLUMN_2_2 + " TEXT,"
            + COLUMN_2_3 + " INTEGER,"
            + COLUMN_2_4 + " INTEGER,"
            + COLUMN_2_5 + " TEXT,"
            + COLUMN_2_6 + " INTEGER,"
            + COLUMN_2_7 + " INTEGER,"
            + COLUMN_2_8 + " INTEGER,"
            + COLUMN_2_9 + " BLOB,"
            + COLUMN_2_10 + " INTEGER,"
            + COLUMN_2_11 + " INTEGER);";

    private static final String NEWS_TABLE_CREATE = "CREATE TABLE " + TABLE_NEWS+ "("
            + COLUMN_4_1 + " VARCHAR(50) PRIMARY KEY,"
            + COLUMN_4_2 + " TEXT,"
            + COLUMN_4_3 + " BLOB,"
            + COLUMN_4_4 + " TEXT);";

    private static final String USERS_EVENTS_TABLE_CREATE = "CREATE TABLE " + TABLE_USERS_EVENTS + "("
            + COLUMN_3_1 + " INTEGER PRIMARY KEY,"
            + COLUMN_3_2 + " VARCHAR(18), "
            + COLUMN_3_3 + " TEXT, FOREIGN KEY ("
            + COLUMN_3_2 + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_0_6 + "), FOREIGN KEY ("
            + COLUMN_3_3 +") REFERENCES " + TABLE_EVENTS + "(" + COLUMN_2_1 + "));";

    //DROP TABLES

    private static final String DROP_LOGIN = "DROP TABLE IF EXISTS "+TABLE_LOGIN+";";
    private static final String DROP_EVENTS= "DROP TABLE IF EXISTS "+TABLE_EVENTS+";";
    private static final String DROP_USERS= "DROP TABLE IF EXISTS "+TABLE_USERS+";";
    private static final String DROP_USERS_EVENTS= "DROP TABLE IF EXISTS "+TABLE_USERS_EVENTS+";";
    private static final String DROP_NEWS= "DROP TABLE IF EXISTS "+TABLE_NEWS+";";


    //Método OnCreate
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOGIN_TABLE_CREATE);
        db.execSQL(EVENTS_TABLE_CREATE);
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(USERS_EVENTS_TABLE_CREATE);
        db.execSQL(NEWS_TABLE_CREATE);
    }

    //Método onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_LOGIN);
        db.execSQL(LOGIN_TABLE_CREATE);
        db.execSQL(DROP_EVENTS);
        db.execSQL(EVENTS_TABLE_CREATE);
        db.execSQL(DROP_USERS);
        db.execSQL(USERS_TABLE_CREATE);
        db.execSQL(DROP_USERS_EVENTS);
        db.execSQL(USERS_EVENTS_TABLE_CREATE);
        db.execSQL(DROP_NEWS);
        db.execSQL(NEWS_TABLE_CREATE);
    }


    //Método para adicionar um login
    public boolean addLogin(String name, String password ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contenvalues = new ContentValues();
        //preparar o conteúdo a inserir
        contenvalues.put(COLUMN_1_1,name);
        contenvalues.put(COLUMN_1_2,password);
        //---------------------------------
        long result = db.insert(TABLE_LOGIN,null,contenvalues); //insere na base de dados
        //Se falhou a inserir
        if(result==-1){
            db.close();
            return false;
            //Se inseriu com sucesso
        }else{
            db.close();
            return true;
        }
    }

    //Método para adicionar um evento
    public boolean addEvent(String name, String desc, int minage, int maxage,String local, int day, int month, int year, int inscritos, int limite, byte[] img ){
        SQLiteDatabase db = this.getWritableDatabase();

                ContentValues contenvalues = new ContentValues();
                //preparar o conteúdo a inserir
                Log.i("tag", "titulo=" + name);
                contenvalues.put(COLUMN_2_1, name);
                contenvalues.put(COLUMN_2_2, desc);
                contenvalues.put(COLUMN_2_3, minage);
                contenvalues.put(COLUMN_2_4, maxage);
                contenvalues.put(COLUMN_2_5, local);
                contenvalues.put(COLUMN_2_6, day);
                contenvalues.put(COLUMN_2_7, month);
                contenvalues.put(COLUMN_2_8, year);
                contenvalues.put(COLUMN_2_9, img);
                contenvalues.put(COLUMN_2_10, inscritos);
                contenvalues.put(COLUMN_2_11, limite);
                db.insert(TABLE_EVENTS, null, contenvalues); //insere na base de dados
                db.close();
                return true;
    }



    //Método para editar um evento
    public boolean editEvent(String id, String name, String desc, int minage, int maxage,String local, int day, int month, int year, int inscritos, int limite, byte[] img ){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] nome = {id};
                //-------------------------------------------------//
                ContentValues contenvalues = new ContentValues();
                //preparar o conteúdo a inserir
                contenvalues.put(COLUMN_2_1, name);
                contenvalues.put(COLUMN_2_2, desc);
                contenvalues.put(COLUMN_2_3, minage);
                contenvalues.put(COLUMN_2_4, maxage);
                contenvalues.put(COLUMN_2_5, local);
                contenvalues.put(COLUMN_2_6, day);
                contenvalues.put(COLUMN_2_7, month);
                contenvalues.put(COLUMN_2_8, year);
                contenvalues.put(COLUMN_2_9, img);
                contenvalues.put(COLUMN_2_10, inscritos);
                contenvalues.put(COLUMN_2_11, limite);
                db.update(TABLE_EVENTS, contenvalues, COLUMN_2_1 + "=?", nome); //update na database
                db.close();
                return true;
    }

    //Método para apagar um evento
    public boolean eraseEvent(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] nome = {id};
        long result=db.delete(TABLE_EVENTS, COLUMN_2_1 + "=?",nome); //update na database
        if(result==-1){
            db.close();
            return false;
        }
        else{
            return true;
        }

    }

    //Método para adicionar uma noticia
    public boolean addNew(String name, String content, String data, byte[] imgbyte ){
        SQLiteDatabase db = this.getWritableDatabase();
            //---------------------------------------------------//
            ContentValues contenvalues = new ContentValues();
            //preparar o conteúdo a inserir
            Log.i("tag", "titulo=" + name);
            contenvalues.put(COLUMN_4_1, name);
            contenvalues.put(COLUMN_4_2, content);
            contenvalues.put(COLUMN_4_3, imgbyte);
            contenvalues.put(COLUMN_4_4, data);
            db.insert(TABLE_NEWS, null, contenvalues); //insere na base de dados
            db.close();
            return true;
    }



    //Método para editar uma noticia
    public boolean editNews(String id, String name, String content, String data, byte[] img){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] nome = {id};
            //-------------------------------------------------//
            ContentValues contenvalues = new ContentValues();
            //preparar o conteúdo a inserir
            contenvalues.put(COLUMN_4_1, name);
            contenvalues.put(COLUMN_4_2, content);
            contenvalues.put(COLUMN_4_3, img);
            contenvalues.put(COLUMN_4_4, data);
            db.update(TABLE_NEWS, contenvalues, COLUMN_4_1 + "=?", nome); //update na database
            db.close();
            return true;
    }

    //Método para apagar uma noticia
    public boolean eraseNews(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] nome = {id};
        long result=db.delete(TABLE_NEWS, COLUMN_4_1 + "=?",nome); //update na database
        if(result==-1){
            db.close();
            return false;
        }
        else{
            return true;
        }

    }

    /* Conversão da imagem guardada na base de dados em Birmap
     * Feito com base em: https://www.youtube.com/watch?v=J3PvMandbIc
     */

    public Bitmap getImage(String titulo){
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap bt = null;
        String query ="SELECT * FROM Events";
        Cursor cursor = db.rawQuery(query,null);
        //Percorre-se a tabela de eventos
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(titulo)){ //Ao encontrar o evento pertendido, guardar os dados da imagem
                byte[] img = cursor.getBlob(8);
                bt = BitmapFactory.decodeByteArray(img,0,img.length);
            }

        }
        return bt;
    }

    public Bitmap getImageNews(String titulo){
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap bt = null;
        String query ="SELECT * FROM News";
        Cursor cursor = db.rawQuery(query,null);
        //Percorre-se a tabela de eventos
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(titulo)){ //Ao encontrar o evento pertendido, guardar os dados da imagem
                byte[] img = cursor.getBlob(2);
                bt = BitmapFactory.decodeByteArray(img,0,img.length);
            }

        }
        return bt;
    }

    //Método para retornar um cursor para a tabela de eventos
    public Cursor getEvents(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="SELECT * FROM Events";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    //Método para retornar um cursor para a tabela de eventos
    public Cursor getNews(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="SELECT * FROM News";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }

    //Método para retornar um cursor para a tabela de logins
    public Cursor getLogins(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query ="SELECT * FROM Logins";
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
}

