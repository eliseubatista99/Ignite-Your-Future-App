package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CheckNews extends AppCompatActivity {

    public String title;
    public Cursor cursor;
    public DataBase oDB;
    public ArrayList<String> name;
    public ArrayList<String> desc;
    public String[] name_array;
    public String[] desc_array;
    public CustomListviewPresentNews customListview;
    public ListView lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_news);
        title = getIntent().getStringExtra("title"); //Obter o título do evento
        oDB = new DataBase(this);
        cursor = oDB.getNews();
        name = new ArrayList<>();
        name.add(title);
        desc = new ArrayList<>();
        lst = findViewById(R.id.list_view);
        //Percorrer a tabela de notícias
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(title)) { //Adicionar o titulo a uma array list de titulos
                desc.add(cursor.getString(1)); //Adicionar a descrição a uma array list de descrições
            }
        }
        name_array = new String[name.size()]; //Criar um array com o size da array list de titulos
        desc_array = new String[desc.size()]; //Criar um array com o size da arraylist de descrições
        ArrayListStringToArray(name,name_array); //Copiar o conteudo da arraylist de titulos para o array de titulos
        ArrayListStringToArray(desc,desc_array); //Copiar o conteúdo da arraylist de descrições para o array de descrições
        customListview = new CustomListviewPresentNews(this,name_array,desc_array); //Criar uma nova custom list view, e enviar os arrays e o contexto para o construtor
        lst.setAdapter(customListview); //Set adapter para apresentar os dados
    }

    //Método que transforma uma arraylist de strings num array de strings
    public void ArrayListStringToArray (ArrayList<String> arrayList, String[] array ){
        for(int i=0;i<arrayList.size();i++){
            array[i]=arrayList.get(i);
        }
    }
}
