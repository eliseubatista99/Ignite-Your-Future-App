package pt.ubi.di.ignite_admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EraseNews extends AppCompatActivity {

    public DataBase oDB;
    private TextView data;
    private TextView nome;
    private TextView content;
    public ImageView imageView;
    public String iCameFromList;
    public Cursor cursor;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erase_new);
        iCameFromList = getIntent().getStringExtra("title"); //Obter o título da notícia a apagar
        data = findViewById(R.id.Data);
        nome = findViewById(R.id.nome);
        content = findViewById(R.id.conteudo);
        imageView = findViewById(R.id.image);
        oDB = new DataBase(this);
        cursor = oDB.getNews();
        //Percorrer a tabela de eventos
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(iCameFromList)){ //Se o título da notícia for o título recebido do intento
                nome.setText(cursor.getString(0)); //Apresentar o título
                content.setText(cursor.getString(1)); //Apresentar a descrição
                data.setText(cursor.getString(3)); //Apresentar a data
                imageView.setImageBitmap(oDB.getImageNews(cursor.getString(0))); //Apresentar a imagem
            }
        }


    }

    //Método para remover a notícia da base de dados
    public void submeter (View v){
        //Se houve sucesso ao remover
        if(oDB.eraseNews(iCameFromList)){
            Toast.makeText(getApplicationContext(),"Sucesso!",Toast.LENGTH_SHORT).show();
        }
        //Se a remoção falhou
        else{
            Toast.makeText(getApplicationContext(),"Sem Sucesso :c",Toast.LENGTH_SHORT).show();
        }
        Intent iResult = new Intent(); //Criar o intento para devolver o resultado
        setResult(RESULT_OK,iResult); //Colocar o resultado como RESULT_OK
        cursor = oDB.getNews();
        firebaseDatabaseRemove = FirebaseDatabase.getInstance();
        firebaseDatabaseRemove.getReference().child("News").removeValue();
        while(cursor.moveToNext()){
            String title = cursor.getString(0);
            String description = cursor.getString(1);
            String data = cursor.getString(3);
            byte[] image_path = cursor.getBlob(2);
            String temp= Base64.encodeToString(image_path, Base64.DEFAULT);
            News news = new News(title,description,temp,data);
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.child("News").push().setValue(news);
        }
        oDB.close(); //Fechar a base de dados
        super.finish(); //Terminar a atividade
    }
}
