package pt.ubi.di.ignite_admin;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//Esta classe prepara uma list View personalizada

public class CustomListview extends ArrayAdapter<String> {

    private String[] name;
    private String[] description;
    private Bitmap imgid;
    private Activity context;
    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;

    //Construtor da listaView

    public CustomListview(Activity context, String[] name, String[] desc){
        super(context,R.layout.list_view,name);
        this.context=context; //É recebido o contexto da activity que chama o construtor
        this.name=name;
        this.description=desc;
        oDB = new DataBase(context);
    }

    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.list_view,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) r.getTag();
        }
        oSQLiteDB = oDB.getWritableDatabase();
        Cursor cursor = oDB.getEvents();
        //Percorre-se a tabela de eventos
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(name[position])){ //Quando se encontrar o evento pretendido
                imgid=oDB.getImage(name[position]); //Guarda-se a imagem desse evento
            }
        }
        ViewHolder.iv.setImageBitmap(imgid); //Apresentar a imagem
        ViewHolder.tv1.setText(name[position]); //Apresentar o título
        ViewHolder.tv2.setText(description[position]); //Apresentar a descrição

        return r;
    }

    static class ViewHolder{
        static TextView tv1;
        static TextView tv2;
        static ImageView iv;
        ViewHolder (View v)
        {
            tv1 = (TextView) v.findViewById(R.id.name);
            tv2 = (TextView) v.findViewById(R.id.description);
            iv = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
