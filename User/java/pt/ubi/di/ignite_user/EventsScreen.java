package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EventsScreen extends AppCompatActivity {

    public String username;
    public ListView lst;
    public DataBase oDB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;
    public SQLiteDatabase oSQLiteDB;
    ArrayList<String> name;
    ArrayList<String> desc;
    ArrayList<Integer> imgid;
    public String[] name_array;
    public String[] desc_array;
    public String titulo;
    CustomListviewEvents customListview;
    Cursor cursor;
    public Intent intent;
    public ArrayList<String> toNextIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_screen);
        username = getIntent().getStringExtra("username");
        oDB = new DataBase(this);
        oSQLiteDB = oDB.getWritableDatabase();
        lst = findViewById(R.id.list_view);
        refresh(); //Chamada ao método refresh para apresentar os eventos na listview
        //Listener para quando houver um clique no evento
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(EventsScreen.this, CheckEvents.class);
                String str = lst.getItemAtPosition(position).toString(); //Obter a posição do evento clicado
                toNextIntent = new ArrayList<>();
                toNextIntent.add(username);
                toNextIntent.add(str);
                intent.putStringArrayListExtra("username", toNextIntent); //enviar o username para a próxima atividade
                startActivityForResult(intent, 12); //iniciar atividade*/
            }
        });
    }

    //Método para apresentar os eventos na listview
    public void refresh() {
        cursor = oDB.getEvents();
        name = new ArrayList<>();
        desc = new ArrayList<>();
        imgid = new ArrayList<>();
        //Percorrer a tabela de eventos
        while (cursor.moveToNext()) {
            titulo = cursor.getString(0); //Guardar o título
            name.add(titulo); //Adicionar o titulo a uma array list de titulos
            desc.add(cursor.getString(1)); //Adicionar a descrição a uma array list de descrições
        }
        name_array = new String[name.size()]; //Criar um array com o size da array list de titulos
        desc_array = new String[desc.size()]; //Criar um array com o size da arraylist de descrições
        ArrayListStringToArray(name, name_array); //Copiar o conteudo da arraylist de titulos para o array de titulos
        ArrayListStringToArray(desc, desc_array); //Copiar o conteúdo da arraylist de descrições para o array de descrições
        customListview = new CustomListviewEvents(this, name_array, desc_array); //Criar uma nova custom list view, e enviar os arrays e o contexto para o construtor
        lst.setAdapter(customListview); //Set adapter para apresentar os dados
    }

    //Copia o conteúdo de uma arraylist de strings para um array de strings
    public void ArrayListStringToArray(ArrayList<String> arrayList, String[] array) {
        for (int i = 0; i < arrayList.size(); i++) {
            array[i] = arrayList.get(i);
        }
    }

    //Aquando o retorno a esta atividade, é chamado o método refresh para apresentar os novos dados na lista
    public void onActivityResult(int iReqCode, int iResultCode, Intent iResult) {
        super.onActivityResult(iReqCode, iResultCode, iResult);
        if ((iReqCode == 12) && (iResultCode == RESULT_OK)) {
            refresh(); //chamar a função que atualiza a lista
            //ATUALIZAR OS DADOS NA FIREBASE
            firebaseDatabaseRemove = FirebaseDatabase.getInstance();
            //UserEvents
            firebaseDatabaseRemove.getReference().child("UsersEvents").removeValue();
            Cursor cursor = oDB.getUsersEvents();
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String username = cursor.getString(1);
                String event = cursor.getString(2);
                UsersEvents userevent = new UsersEvents(id, username, event);
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference();
                databaseReference.child("UsersEvents").push().setValue(userevent);
            }

            firebaseDatabaseRemove = FirebaseDatabase.getInstance();
            //Events
            firebaseDatabaseRemove.getReference().child("Events").removeValue();
            cursor = oDB.getEvents();
            while (cursor.moveToNext()) {
                String title = cursor.getString(0);
                String description = cursor.getString(1);
                String min_age = cursor.getString(2);
                String max_age = cursor.getString(3);
                String local = cursor.getString(4);
                String day = cursor.getString(5);
                String month = cursor.getString(6);
                String year = cursor.getString(7);
                String inscritos = cursor.getString(9);
                String limite = cursor.getString(10);
                byte[] image_path = cursor.getBlob(8);
                String temp = Base64.encodeToString(image_path, Base64.DEFAULT);
                Events event = new Events(title, description, min_age, max_age, local, day, month, year, inscritos, limite, temp);
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference();
                databaseReference.child("Events").push().setValue(event);
            }
        }
    }

    //Para tratar os cliques nos botões
    //Para tratar os cliques nos botões
    public void selectButton(View v) {
        switch (v.getId()) {
            case R.id.home:
                intent = new Intent(this, Menu.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.news:
                intent = new Intent(this, NewsScreen.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.events:
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
