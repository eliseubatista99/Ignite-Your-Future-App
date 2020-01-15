package pt.ubi.di.ignite_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public DataBase oDB;
    private SQLiteDatabase oSQLiteDB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUsers;
    DatabaseReference databaseReferenceNews;
    DatabaseReference databaseReferenceEvents;
    DatabaseReference databaseReferenceUsersEvents;
    public int cont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Para realizar um delay de 3 segundos
        oDB = new DataBase(this);
        oSQLiteDB = oDB.getWritableDatabase();
        oDB.onUpgrade(oSQLiteDB,1,1);
        inicializarFirebase();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                afterDelay();
            }
        }, 3000);   //3 seconds
    }

    //Função chamada após os 3 segundos de delay
    public void afterDelay(){
        Intent goToWelcomeScreen = new Intent(this,WelcomeScreen.class); //Intento para o WelcomeScreen
        startActivity(goToWelcomeScreen); //ir para o Welcome Screen
        super.finish(); //Terminar esta atividade de modo a que não seja possivel voltar a ela ao clicar no back
    }

    //Método para preparar a FireBase
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsers = firebaseDatabase.getReference("Users"); //Referência para a tabela de Utilizadores
        databaseReferenceEvents = firebaseDatabase.getReference("Events"); //referencia para a tabela de eventos
        databaseReferenceNews = firebaseDatabase.getReference("News"); //referencia para a tabela de noticias
        databaseReferenceUsersEvents = firebaseDatabase.getReference("UsersEvents"); //referencia para a tabela de UserEvents
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Método para trocar os dados locais pelos dados armazenados na firebase
    @Override
    public void onStart(){
        super.onStart();
        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loginsnapshot : dataSnapshot.getChildren()){
                    Users user = loginsnapshot.getValue(Users.class);
                    Cursor cursor = oDB.getUsers();
                    cont=0;
                    while(cursor.moveToNext()){
                        if(cursor.getString(5).equals(user.getUsername())){
                            cont=cont+1;
                        }
                    }
                    if(cont==0){
                        oDB.addUser(user.getName(),user.getSurname(),Integer.parseInt(user.getDia()),Integer.parseInt(user.getMes()),Integer.parseInt(user.getAno()),user.getUsername(),user.getPassword());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loginsnapshot : dataSnapshot.getChildren()){ //Percorre a "tabela" de eventos na firebase
                    Events events = loginsnapshot.getValue(Events.class); //Cria uma classe do tipo evento
                    //Adiciona essa clase á base de dados sqlite
                    String img_string = events.getImage_path();
                    byte [] encodeByte= Base64.decode(img_string,Base64.DEFAULT);
                    Cursor cursor = oDB.getEvents();
                    cont=0;
                    while(cursor.moveToNext()){
                        if(cursor.getString(0).equals(events.getTitle())){
                            cont=cont+1;
                        }
                    }
                    if(cont==0){
                        oDB.addEvent(events.getTitle(),events.getDescription(),Integer.parseInt(events.getMin_age()),Integer.parseInt(events.getMax_age()),events.getLocal(),Integer.parseInt(events.getDay()),Integer.parseInt(events.getMonth()),Integer.parseInt(events.getYear()), Integer.parseInt(events.getInscritos()),Integer.parseInt(events.getLimite()),encodeByte);
                    }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReferenceNews.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loginsnapshot : dataSnapshot.getChildren()){ //Percorre a "tabela" de noticias na firebasa
                    News news = loginsnapshot.getValue(News.class); //Cria uma classe do tipo noticia
                    //Adiciona essa classe à base de dados local
                    String img_string = news.getImage_path();
                    byte [] encodeByte= Base64.decode(img_string,Base64.DEFAULT);
                    oDB.addNew(news.getTitle(),news.getContent(),news.getData(),encodeByte);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReferenceUsersEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loginsnapshot : dataSnapshot.getChildren()){ //Percorre a "tabela" de noticias na firebasa
                    UsersEvents usersevents = loginsnapshot.getValue(UsersEvents.class); //Cria uma classe do tipo noticia
                    oDB.addUserEvent(usersevents.getUsername(),usersevents.getEvent());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

