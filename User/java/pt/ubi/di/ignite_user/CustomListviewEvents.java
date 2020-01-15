package pt.ubi.di.ignite_user;

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

public class CustomListviewEvents extends ArrayAdapter<String> {

    private String[] name;
    private String[] description;
    private Bitmap imgid;
    private String data,day,month,year,limite,inscritos,inscritions;
    private Activity context;
    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;

    //Construtor
    public CustomListviewEvents(Activity context, String[] name, String[] desc){
        super(context,R.layout.list_view_events,name);
        this.context=context;
        this.name=name;
        this.description=desc;
        oDB = new DataBase(context);
    }

    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.list_view_events,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) r.getTag();
        }
        oSQLiteDB = oDB.getWritableDatabase();
        Cursor cursor = oDB.getEvents();
        //Percorre-se a tabela de eventos
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(name[position])){ //Quando se encontrar o evento pretendido, guardar os dados do mesmo
                imgid=oDB.getImage(name[position]);
                data = cursor.getString(3);
                day = cursor.getString(5);
                month = cursor.getString(6);
                year = cursor.getString(7);
                data = day +"/"+month+"/"+year;
                inscritos= cursor.getString(9);
                limite = cursor.getString(10);
                //Se o número de inscritos atingir o limite
                if(Integer.parseInt(inscritos)>=Integer.parseInt(limite)){
                    inscritions = "Sem Vagas! ("+inscritos+"/"+limite+")";
                }
                //Se ainda houver vagas
                else{
                    inscritions = "Aberto ("+inscritos+"/"+limite+")";
                }
            }
        }
        //Apresentar os dados
        ViewHolder.iv.setImageBitmap(imgid);
        ViewHolder.tv1.setText(name[position]);
        ViewHolder.tv2.setText(data);
        ViewHolder.tv3.setText(inscritions);

        return r;
    }

    //Construtor dos ViewHolders
    static class ViewHolder{
        static TextView tv1;
        static TextView tv2;
        static TextView tv3;
        static ImageView iv;
        ViewHolder (View v)
        {
            tv1 = (TextView) v.findViewById(R.id.name);
            tv2 = (TextView) v.findViewById(R.id.data);
            tv3 = (TextView) v.findViewById(R.id.inscricoes);
            iv = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
