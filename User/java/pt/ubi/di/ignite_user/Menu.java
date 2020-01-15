package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    public Intent intento;
    public String username;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;
    public DataBase oDB;
    public ListView lst;
    public ListView lst_events;
    Cursor cursor;
    Cursor cursor2;
    public Intent intent;
    public ArrayList<String> toNextIntent;
    public SQLiteDatabase oSQLiteDB;
    ArrayList<String> name;
    ArrayList<String> desc;
    ArrayList<Integer> imgid;
    public String[] name_array;
    public String[] desc_array;
    public String[] final_name_array;
    public String[] final_desc_array;
    public String titulo;
    ArrayList<String> name_eve;
    ArrayList<String> desc_eve;
    ArrayList<Integer> imgid_eve;
    public String[] name_array_eve;
    public String[] desc_array_eve;
    public String[] final_name_array_eve;
    public String[] final_desc_array_eve;
    public String titulo_eve;
    CustomListview customListview;
    CustomListviewEvents customListviewEventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        username = getIntent().getStringExtra("username");
        oDB = new DataBase(this);
        lst = findViewById(R.id.list_view);
        lst_events = findViewById(R.id.list_view_events);
        refresh(); //Chamada ao método refresh para apresentar as notícias na listview
        //Listener para quando houver um clique na notícia
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                intent = new Intent(Menu.this,CheckNews.class);
                String str = lst.getItemAtPosition(position).toString(); //Obter a posição da notícia clicada
                intent.putExtra("title",str);
                startActivity(intent); //iniciar atividade
            }
        });
        //Listener para quando houver um clique no evento
        lst_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                intent = new Intent(Menu.this,CheckEvents.class);
                String str = lst_events.getItemAtPosition(position).toString(); //Obter a posição do evento clicado
                toNextIntent = new ArrayList<>();
                toNextIntent.add(username);
                toNextIntent.add(str);
                intent.putStringArrayListExtra("username",toNextIntent); //enviar o username para a próxima atividade
                startActivityForResult(intent,12); //iniciar atividade*/
            }
        });
    }

    //Para tratar os cliques nos botões
    public void selectButton(View v) {
        switch (v.getId()) {
            case R.id.home:
                break;
            case R.id.news:
                intento = new Intent(this, NewsScreen.class);
                intento.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intento);
                super.finish();
                break;
            case R.id.events:
                intento = new Intent(this, EventsScreen.class);
                intento.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intento);
                super.finish();
                break;
            case R.id.info:
                intento = new Intent(this, InfoScreen.class);
                startActivity(intento);
                super.finish();
                break;
            case R.id.profile:
                intento = new Intent(this, ProfileScreen.class);
                intento.putExtra("username", username); //Enviar o username para a próxima atividade
                startActivity(intento);
                break;
            default:
                //nada
        }
    }

    //Ao terminar a atividade do perfil
    public void onActivityResult(int iReqCode, int iResultCode, Intent iResult) {
        super.onActivityResult(iReqCode, iResultCode, iResult);
        if ((iReqCode == 11) && (iResultCode == RESULT_OK)) {
            firebaseDatabaseRemove = FirebaseDatabase.getInstance();
            //Remove os eventos e as noticias da firebase
            firebaseDatabaseRemove.getReference().child("Users").removeValue();
            Cursor cursor = oDB.getUsers();
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String surname = cursor.getString(1);
                String dia = cursor.getString(2);
                String mes = cursor.getString(3);
                String ano = cursor.getString(4);
                String username = cursor.getString(5);
                String password = cursor.getString(6);
                Users user = new Users(name, surname, dia, mes, ano, username, password);
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference();
                databaseReference.child("Users").push().setValue(user);
            }
        }

    }

    public void refresh(){
        cursor = oDB.getNews();
        name = new ArrayList<>();
        desc = new ArrayList<>();
        imgid = new ArrayList<>();
        //Percorrer a tabela de notícias
        while(cursor.moveToNext()){
            titulo = cursor.getString(0); //Guardar o título
            name.add(titulo); //Adicionar o titulo a uma array list de titulos
            desc.add(cursor.getString(1)); //Adicionar a descrição a uma array list de descrições
        }
        final_name_array = new String[1];
        final_desc_array = new String[1];
        name_array = new String[name.size()]; //Criar um array com o size da array list de titulos
        desc_array = new String[desc.size()]; //Criar um array com o size da arraylist de descrições
        ArrayListStringToArray(name,name_array); //Copiar o conteudo da arraylist de titulos para o array de titulos
        ArrayListStringToArray(desc,desc_array); //Copiar o conteúdo da arraylist de descrições para o array de descrições
        final_name_array[0]=name_array[name_array.length-1];
        final_desc_array[0]=desc_array[desc_array.length-1];
        customListview = new CustomListview(this,final_name_array,final_desc_array); //Criar uma nova custom list view, e enviar os arrays e o contexto para o construtor
        lst.setAdapter(customListview); //Set adapter para apresentar os dados

        cursor2 = oDB.getEvents();
        name_eve = new ArrayList<>();
        desc_eve = new ArrayList<>();
        imgid_eve = new ArrayList<>();
        //Percorrer a tabela de eventos
        while(cursor2.moveToNext()){
            titulo_eve = cursor2.getString(0); //Guardar o título
            name_eve.add(titulo_eve); //Adicionar o titulo a uma array list de titulos
            desc_eve.add(cursor2.getString(1)); //Adicionar a descrição a uma array list de descrições
        }
        final_name_array_eve = new String[1];
        final_desc_array_eve = new String[1];
        name_array_eve = new String[name_eve.size()]; //Criar um array com o size da array list de titulos
        desc_array_eve = new String[desc_eve.size()]; //Criar um array com o size da arraylist de descrições
        ArrayListStringToArray(name_eve,name_array_eve); //Copiar o conteudo da arraylist de titulos para o array de titulos
        ArrayListStringToArray(desc_eve,desc_array_eve); //Copiar o conteúdo da arraylist de descrições para o array de descrições
        final_name_array_eve[0]=name_array_eve[name_array_eve.length-1];
        final_desc_array_eve[0]=desc_array_eve[desc_array_eve.length-1];
        customListviewEventos = new CustomListviewEvents(this,final_name_array_eve,final_desc_array_eve); //Criar uma nova custom list view, e enviar os arrays e o contexto para o construtor
        lst_events.setAdapter(customListviewEventos); //Set adapter para apresentar os dados
    }

    //Copia o conteúdo de uma arraylist de strings para um array de strings
    public void ArrayListStringToArray (ArrayList<String> arrayList, String[] array ){
        for(int i=0;i<arrayList.size();i++){
            array[i]=arrayList.get(i);
        }
    }
}
