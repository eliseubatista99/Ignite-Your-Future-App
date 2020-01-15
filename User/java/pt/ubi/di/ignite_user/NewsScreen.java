package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NewsScreen extends AppCompatActivity {

    public String username;
    public ListView lst;
    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;
    ArrayList<String> name;
    ArrayList<String> desc;
    ArrayList<Integer> imgid;
    public String[] name_array;
    public String[] desc_array;
    public String titulo;
    CustomListview customListview;
    Cursor cursor;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_screen);
        username = getIntent().getStringExtra("username");
        oDB = new DataBase(this);
        oSQLiteDB = oDB.getWritableDatabase();
        lst = findViewById(R.id.list_view);
        refresh(); //Chamada ao método refresh para apresentar as notícias na listview
        //Listener para quando houver um clique na notícia
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                intent = new Intent(NewsScreen.this,CheckNews.class);
                String str = lst.getItemAtPosition(position).toString(); //Obter a posição da notícia clicada
                intent.putExtra("title",str);
                startActivity(intent); //iniciar atividade
            }
        });
    }

    //Método para apresentar as notícias na listview
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
        name_array = new String[name.size()]; //Criar um array com o size da array list de titulos
        desc_array = new String[desc.size()]; //Criar um array com o size da arraylist de descrições
        ArrayListStringToArray(name,name_array); //Copiar o conteudo da arraylist de titulos para o array de titulos
        ArrayListStringToArray(desc,desc_array); //Copiar o conteúdo da arraylist de descrições para o array de descrições
        customListview = new CustomListview(this,name_array,desc_array); //Criar uma nova custom list view, e enviar os arrays e o contexto para o construtor
        lst.setAdapter(customListview); //Set adapter para apresentar os dados
    }

    //Copia o conteúdo de uma arraylist de strings para um array de strings
    public void ArrayListStringToArray (ArrayList<String> arrayList, String[] array ){
        for(int i=0;i<arrayList.size();i++){
            array[i]=arrayList.get(i);
        }
    }

    public void selectButton(View v) {
        switch (v.getId()) {
            case R.id.home:
                intent = new Intent(this, Menu.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.news:
                break;
            case R.id.events:
                intent = new Intent(this, EventsScreen.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.info:
                intent = new Intent(this, InfoScreen.class);
                startActivity(intent);
                super.finish();
                break;
            case R.id.profile:
                intent = new Intent(this, ProfileScreen.class);
                intent.putExtra("username", username); //Enviar o username para a próxima atividade
                startActivity(intent);
                break;
            default:
                //nada
        }
    }
}
