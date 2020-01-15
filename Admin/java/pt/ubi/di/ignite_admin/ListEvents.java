package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ListEvents extends AppCompatActivity {

    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;
    public ListView lst_events;
    ArrayList<String> name;
    ArrayList<String> desc;
    ArrayList<Integer> imgid;
    public String[] name_array;
    public String[] desc_array;
    public Intent intento;
    public String titulo;
    CustomListview customListview;
    Cursor cursor;
    String tipo;
    public Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        tipo = getIntent().getStringExtra("tipo"); //Obter o tipo de ação que se quer realizar (erase/edit)
        oDB = new DataBase(this);
        oSQLiteDB = oDB.getWritableDatabase();
        lst_events = findViewById(R.id.listview_events);
        refresh(); //Chamada ao método refresh para apresentar os eventos na listview
        //Listener para quando houver um clique num evento
        lst_events.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(tipo.equals("erase")){ //Se a açao pretendida é apagar
                    intent = new Intent (ListEvents.this,EraseEvent.class);
                }
                //Se a ação pretendida é editar
                else if(tipo.equals("edit")){
                    intent = new Intent (ListEvents.this,EditEvent.class);
                }
                else{
                    intent = new Intent (ListEvents.this, ViewInscritos.class);
                }
                String str = lst_events.getItemAtPosition(position).toString(); //Obter a posição do evento clicado
                intent.putExtra("title",""+str); //envia o id do elemento para a próxima ativity
                startActivityForResult(intent,12); //iniciar atividade
            }
        });

    }

    //Método para apresentar os eventos na listview
    public void refresh(){
        cursor = oDB.getEvents();
        name = new ArrayList<>();
        desc = new ArrayList<>();
        imgid = new ArrayList<>();
        //Percorrer a tabela de eventos
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
        lst_events.setAdapter(customListview); //Set adapter para apresentar os dados
    }

    //Copia o conteúdo de uma arraylist de strings para um array de strings
    public void ArrayListStringToArray (ArrayList<String> arrayList, String[] array ){
        for(int i=0;i<arrayList.size();i++){
            array[i]=arrayList.get(i);
        }
    }

    //Aquando o retorno a esta atividade, é chamado o método refresh para apresentar os novos dados na lista
    public void onActivityResult(int iReqCode, int iResultCode, Intent iResult) {
        super.onActivityResult(iReqCode, iResultCode, iResult);
        if ((iReqCode == 12) && (iResultCode == RESULT_OK)) {
            refresh(); //chamar a função que atualiza a lista
        }
    }
}
