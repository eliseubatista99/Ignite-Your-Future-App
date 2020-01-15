package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EraseEvent extends AppCompatActivity {

    public DataBase oDB;
    private TextView data;
    private TextView nome;
    private TextView desc;
    private TextView idadeMin;
    private TextView idadeMax;
    private TextView local;
    public ImageView imageView;
    public String iCameFromList;
    public Cursor cursor;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erase_event);
        iCameFromList = getIntent().getStringExtra("title"); //Obter o título do evento a apagar
        data = findViewById(R.id.Data);
        nome = findViewById(R.id.nome);
        desc = findViewById(R.id.desc);
        idadeMin = findViewById(R.id.idade_min);
        idadeMax = findViewById(R.id.idade_max);
        local = findViewById(R.id.local);
        imageView = findViewById(R.id.image);
        oDB = new DataBase(this);
        cursor = oDB.getEvents();
        //Percorrer a tabela de eventos
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(iCameFromList)){ //Se o título do evento for o título recebido do intento
                nome.setText(cursor.getString(0)); //Apresentar o título
                desc.setText(cursor.getString(1)); //Apresentar a descrição
                idadeMin.setText(cursor.getString(2)); //Apresentar a idade mínima
                idadeMax.setText(cursor.getString(3)); //Apresentar a idade máxima
                local.setText(cursor.getString(4)); //Apresentar o local
                data.setText(cursor.getString(5)+"/"+cursor.getString(6)+"/"+cursor.getString(7)); //Apresentar a data
                imageView.setImageBitmap(oDB.getImage(cursor.getString(0))); //Apresentar a imagem
            }
        }


    }

    //Método para remover o evento da base de dados
    public void submeter (View v){
        //Se houve sucesso ao remover
        if(oDB.eraseEvent(iCameFromList)){
            Toast.makeText(getApplicationContext(),"Sucesso!",Toast.LENGTH_SHORT).show();
        }
        //Se a remoção falhou
        else{
            Toast.makeText(getApplicationContext(),"Sem Sucesso :c",Toast.LENGTH_SHORT).show();
        }
        Intent iResult = new Intent(); //Criar o intento para devolver o resultado
        setResult(RESULT_OK,iResult); //Colocar o resultado como RESULT_OK
        cursor = oDB.getEvents();
        firebaseDatabaseRemove = FirebaseDatabase.getInstance();
        firebaseDatabaseRemove.getReference().child("Events").removeValue();
        //Insere todos os eventos na firebase
        while(cursor.moveToNext()){
            String title = cursor.getString(0);
            String description = cursor.getString(1);
            String mi_age = cursor.getString(2);
            String ma_age = cursor.getString(3);
            String local = cursor.getString(4);
            String day = cursor.getString(5);
            String month = cursor.getString(6);
            String year = cursor.getString(7);
            String inscritos = cursor.getString(9);
            String limite = cursor.getString(10);
            byte[] image_path = cursor.getBlob(8);
            String temp= Base64.encodeToString(image_path, Base64.DEFAULT);
            Events events = new Events(title,description,mi_age,ma_age,local,day,month,year,inscritos,limite,temp);
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.child("Events").push().setValue(events);
        }
        oDB.close();
        super.finish(); //Terminar a atividade
    }
}
