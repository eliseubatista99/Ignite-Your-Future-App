package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Menu extends AppCompatActivity {

    public ImageView events;
    public ImageView news;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;
    public DataBase oDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        events = findViewById(R.id.events);
        news = findViewById(R.id.news);
        oDB = new DataBase(this);
    }

    //Para tratar o clique nos botões
    public void selectButton(View v){
        Intent intento;
        switch(v.getId()) {
            case R.id.events:
                intento = new Intent(this, EventsManagement.class);
                break;
            case R.id.news:
                intento = new Intent(this, NewsManagement.class);
                break;
            default:
                intento = new Intent(this, Menu.class);
                break;
        }
        startActivity(intento);
    }
}
