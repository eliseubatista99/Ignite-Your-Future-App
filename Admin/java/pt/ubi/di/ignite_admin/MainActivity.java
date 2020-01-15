package pt.ubi.di.ignite_admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
    DatabaseReference databaseReferenceLogins;
    DatabaseReference databaseReferenceEvents;
    DatabaseReference databaseReferenceNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oDB = new DataBase(this);
        oSQLiteDB = oDB.getWritableDatabase();
        oDB.onUpgrade(oSQLiteDB,1,1);
        inicializarFirebase();
        //Para realizar um delay de 3 segundos
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
        databaseReferenceLogins = firebaseDatabase.getReference("Admins");
        databaseReferenceEvents = firebaseDatabase.getReference("Events"); //referencia para a tabela de eventos
        databaseReferenceNews = firebaseDatabase.getReference("News"); //referencia para a tabela de noticias
    }

    @Override
    public void onStart(){
        super.onStart();
        databaseReferenceLogins.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loginsnapshot : dataSnapshot.getChildren()){
                    Logins login = loginsnapshot.getValue(Logins.class);
                    oDB.addLogin(login.getUsername(),login.getPassword());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //LISTENERS PARA ATUALIZAÇÔES NA FIREBASE
        databaseReferenceEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot loginsnapshot : dataSnapshot.getChildren()){ //Percorre a "tabela" de eventos na firebase
                    Events events = loginsnapshot.getValue(Events.class); //Cria uma classe do tipo evento
                    //Adiciona essa clase á base de dados sqlite
                    String img_string = events.getImage_path();
                    byte [] encodeByte= Base64.decode(img_string,Base64.DEFAULT);
                    oDB.addEvent(events.getTitle(),events.getDescription(),Integer.parseInt(events.getMin_age()),Integer.parseInt(events.getMax_age()),events.getLocal(),Integer.parseInt(events.getDay()),Integer.parseInt(events.getMonth()),Integer.parseInt(events.getYear()), Integer.parseInt(events.getInscritos()),Integer.parseInt(events.getLimite()),encodeByte);
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
    }

}
