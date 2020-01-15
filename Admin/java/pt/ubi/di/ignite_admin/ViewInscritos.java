package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewInscritos extends AppCompatActivity {

    public DataBase oDB;
    private TextView data;
    private TextView nome;
    private TextView desc;
    private TextView inscrito;
    public ImageView imageView;
    public String iCameFromList;
    public Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inscritos);

        iCameFromList = getIntent().getStringExtra("title"); //Obter o título da noticia a apagar
        data = findViewById(R.id.Data);
        nome = findViewById(R.id.nome);
        desc = findViewById(R.id.desc);
        imageView = findViewById(R.id.image);
        inscrito=findViewById(R.id.inscritos);
        oDB = new DataBase(this);
        cursor = oDB.getEvents();
        //Percorrer a tabela de noticias
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(iCameFromList)){ //Se o título da noticia for o título recebido do intento
                nome.setText(cursor.getString(0)); //Apresentar o título
                desc.setText(cursor.getString(1)); //Apresentar a descrição
                inscrito.setText("Inscritos: "+cursor.getString(9)+"/"+cursor.getString(10)); //Apresentar o local
                data.setText(cursor.getString(5)+"/"+cursor.getString(6)+"/"+cursor.getString(7)); //Apresentar a data
                imageView.setImageBitmap(oDB.getImage(cursor.getString(0))); //Apresentar a imagem
            }
        }

    }
}
