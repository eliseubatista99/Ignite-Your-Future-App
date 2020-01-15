package pt.ubi.di.ignite_user;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomListviewPresentEvents extends ArrayAdapter<String> {

    private String data,day,month, year,inscritos,limite,inscritions, titulo, username;
    private String[] name;
    private String[] description;
    private Bitmap imgid;
    private Activity context;
    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;
    public boolean subscribed;
    public int min_age, max_age;

    //Construtor
    public CustomListviewPresentEvents(Activity context, String[] name, String[] desc, String titulo, String username, boolean subscribed){
        super(context,R.layout.list_viewpresentevents,name);
        this.context=context;
        this.name=name;
        this.description=desc;
        this.titulo = titulo;
        this.username = username;
        this.subscribed = subscribed; //Obter o estado de subscrição do utilizador
        oDB = new DataBase(context);
    }

    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.list_viewpresentevents,null,true);
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
                min_age = cursor.getInt(2);
                max_age = cursor.getInt(3);
                day = cursor.getString(5);
                month = cursor.getString(6);
                year = cursor.getString(7);
                data = day +"/"+month+"/"+year;
                inscritos= cursor.getString(9);
                limite = cursor.getString(10);
                //Se o número de inscritos atingir o limite
                if(Integer.parseInt(inscritos)>=Integer.parseInt(limite)){
                    inscritions = "Sem Vagas! ("+inscritos+"/"+limite+")";
                    ViewHolder.submit.setVisibility(View.INVISIBLE);
                }
                //Se ainda houver vagas
                else{
                    inscritions = "Aberto ("+inscritos+"/"+limite+")";
                }
            }
        }
        //Se o utilizador já subscreveu o evento
        if(subscribed) {
            ViewHolder.submit.setText("Desinscrever"); //Apresentar a possibilidade de desinscrever
        }
        //Se o utilizador ainda não subscreveu
        else{
            ViewHolder.submit.setText("Inscrever"); //Apresentar a possibilidade de inscrever
        }
        //Apresentar dados
        ViewHolder.iv.setImageBitmap(imgid);
        ViewHolder.tv1.setText(description[position]);
        ViewHolder.tv2.setText(data);
        ViewHolder.tv3.setText(inscritions);
        ViewHolder.tv4.setText("Idade Mínima: "+min_age+"\tIdade Máxima: "+max_age);

        return r;
    }

    static class ViewHolder{
        static TextView tv1;
        static TextView tv2;
        static TextView tv3;
        static TextView tv4;
        static Button submit;
        static ImageView iv;
        ViewHolder (View v)
        {
            tv1 = (TextView) v.findViewById(R.id.description);
            tv2 = (TextView) v.findViewById(R.id.data);
            tv3 = (TextView) v.findViewById(R.id.inscricoes);
            tv4 = (TextView) v.findViewById(R.id.idade);
            submit = (Button) v.findViewById(R.id.submit);
            iv = (ImageView) v.findViewById(R.id.imageView);
        }
    }
}
