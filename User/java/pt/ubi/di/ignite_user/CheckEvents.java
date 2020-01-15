package pt.ubi.di.ignite_user;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class CheckEvents extends AppCompatActivity {

    public String title;
    public Cursor cursor;
    public DataBase oDB;
    public ArrayList<String> name;
    public ArrayList<String> desc;
    public String[] name_array;
    public String[] desc_array;
    public CustomListviewPresentEvents customListview;
    public ListView lst;
    public ArrayList<String> fromIntent;
    public String username;
    public int born_day,born_month,born_year,year,month,day;
    public boolean subscribed;
    public int years_difference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_events);
        //Obter o username e o titulo do evento
        fromIntent = getIntent().getStringArrayListExtra("username");
        title = fromIntent.get(1);
        username = fromIntent.get(0);
        //-------------------------------------
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR); //vai buscar o ano atual
        month = cal.get(Calendar.MONTH); //vai buscar o mês atual
        month=month+1;
        day = cal.get(Calendar.DAY_OF_MONTH); //vai buscar o dia atual
        //Apresentar o título
        oDB = new DataBase(this);
        cursor = oDB.getUsers();
        while(cursor.moveToNext()){
            if(cursor.getString(5).equals(username)){
                born_day=cursor.getInt(2);
                born_month=cursor.getInt(3);
                born_year=cursor.getInt(4);
            }
        }
        cursor = oDB.getEvents();
        name = new ArrayList<>();
        name.add(title);
        desc = new ArrayList<>();
        lst = findViewById(R.id.list_view);
        //Atualizar a lista
        refresh();
    }

    //Método para atualizar a lista
    public void refresh(){
        subscribed = false;
        Cursor cursor2 = oDB.getUsersEvents();
        //Percorrer a tabela de Utilizadores-Eventos
        while (cursor2.moveToNext()) {
            //Se se encontar a linha prentendiada
            if (username.equals(cursor2.getString(1))
                    &&
                    title.equals(cursor2.getString(2))) {
                subscribed = true; //O utilizador já se inscreveu nesse evento
            }
        }
        //Percorrer a tabela de Eventos
        while(cursor.moveToNext()){
            //Ao encontrar o evento pretendido
            if(cursor.getString(0).equals(title)) { //Adicionar o titulo a uma array list de titulos
                desc.add(cursor.getString(1)); //Adicionar a descrição a uma array list de descrições
            }
        }
        name_array = new String[name.size()]; //Criar um array com o size da array list de titulos
        desc_array = new String[desc.size()]; //Criar um array com o size da arraylist de descrições
        ArrayListStringToArray(name,name_array); //Copiar o conteudo da arraylist de titulos para o array de titulos
        ArrayListStringToArray(desc,desc_array); //Copiar o conteúdo da arraylist de descrições para o array de descrições
        customListview = new CustomListviewPresentEvents(this,name_array,desc_array,title,username,subscribed); //Criar uma nova custom list view, e enviar os arrays e o contexto para o construtor
        lst.setAdapter(customListview); //Set adapter para apresentar os dados
    }

    //Método para inscrever um utilizador num evento
    public void inscrever(View v){
        Cursor cursor = oDB.getEvents();
        String desc="", min="",local="";
        int inscrits=0,limits=0,dia=0,mes=0,ano=0,min_age=0,max_age=0;
        byte[] imagem = {};
        //Percorrer a tabela de eventos
        while(cursor.moveToNext()){
            //Ao encontrar o evento pretendido, guardar os seus dados
            if(cursor.getString(0).equals(title)){
                desc = cursor.getString(1);
                min_age = Integer.parseInt(cursor.getString(2));
                max_age = Integer.parseInt(cursor.getString(3));
                local = cursor.getString(4);
                dia = Integer.parseInt(cursor.getString(5));
                mes = Integer.parseInt(cursor.getString(6));
                ano = Integer.parseInt(cursor.getString(7));
                imagem = cursor.getBlob(8);
                inscrits = Integer.parseInt(cursor.getString(9));
                limits = Integer.parseInt(cursor.getString(10));
            }
        }
        years_difference=(year-born_year);
        if(month<born_month){
            years_difference=years_difference-1;
        }
        else if(month==born_month){
            if(day<=born_day){
                years_difference=years_difference-1;
            }
        }
        Log.i("tag","min_age: "+min_age+", max_age: "+max_age+", difference: "+years_difference);
        if((min_age > years_difference || max_age < years_difference )){
            Toast.makeText(this,"A sua idade não se encontra no intervalo permitido",Toast.LENGTH_SHORT).show();
        }
        else {
            //Se o utilizador já subscreveu o evento
            if (subscribed &&(inscrits < limits) ){
                oDB.eraseUserEvent(username, title); //Remover a inscrição
                Log.i("tag", "removi");
                oDB.editEvent(title, title, desc, min_age, max_age, local, dia, mes, ano, inscrits - 1, limits, imagem); //Diminuir o número de inscritos no evento em 1 unidade
            }
        else
            if (!subscribed && (inscrits < limits)) {
                oDB.addUserEvent(username, title); //Adicionar a inscrição
                Log.i("tag", "adicionei");
                oDB.editEvent(title, title, desc, min_age, max_age, local, dia, mes, ano, inscrits + 1, limits, imagem); //Aumentar o número de inscritos no evento em 1 unidade
            }
        }
        Intent iResult = new Intent(); //Intento para devolver o resultado
        setResult(RESULT_OK, iResult); //Set result okay
        oDB.close();
        super.finish(); //Terminar a atividade
    }

    //Método que transforma uma arraylist de strings em um array de strings
    public void ArrayListStringToArray (ArrayList<String> arrayList, String[] array ){
        for(int i=0;i<arrayList.size();i++){
            array[i]=arrayList.get(i);
        }
    }
}
