package pt.ubi.di.ignite_admin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class EditNotice extends AppCompatActivity {

    public DataBase oDB;
    private TextView data;
    private DatePickerDialog.OnDateSetListener dataListener;
    private EditText nome;
    private EditText conteudo;
    public ImageView imageView;
    public String iCameFromList;
    public Cursor cursor;
    public String caminho;
    public byte[] img_path;
    public String old_nome;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notice);
        iCameFromList = getIntent().getStringExtra("title");
        data = findViewById(R.id.Data);
        nome = findViewById(R.id.nome);
        conteudo = findViewById(R.id.conteudo);
        imageView = findViewById(R.id.image);
        oDB = new DataBase(this);
        cursor = oDB.getNews();
        //Percorrer a tabela de notícias
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(iCameFromList)){ //Ao encontrar a notícia pretendida
                nome.setText(cursor.getString(0)); //Apresentar o seu título
                old_nome=cursor.getString(0);
                conteudo.setText(cursor.getString(1)); //Apresentar a descrição
                data.setText(cursor.getString(3));
                imageView.setImageBitmap(oDB.getImageNews(cursor.getString(0))); //Apresentar a imagem
                /* Ao editar uma notícia, se não se selecionar uma nova imagem, o valor da imagem inserido é null
                 * Para contornar esse problema, guatrda-se o path da imagem inserido aquando a inserção/ultima edição do notícia
                 */
                img_path = cursor.getBlob(2); //guardar o path da imagem
            }
        }


    }

    /*Função para ir buscar uma imagem à galeria
     * Feito com base em: https://androidclarified.com/pick-image-gallery-camera-android/
     */
    public void pickFromGallery(View v){
        Intent intent=new Intent(Intent.ACTION_PICK); //Criar um novo intento enviando a ação PICK
        intent.setType("image/*"); //Selecionar o tipo como imagem, isto garante que apenas imagens possam ser selecionadas
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes); //Colocar os mimeTypes aceites, isto garante que apenas imagens com estes formatos sejam aceites
        startActivityForResult(intent,12); //Iniciar o intento
    }

    /*Função para tratar a imagem selecionada
     * Feito com base em: https://androidclarified.com/pick-image-gallery-camera-android/
     */
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        //O código resultante é RESULT_OK apenas se for selecionada uma imagem
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 12:
                    Uri selectedImage = data.getData(); //Para obter o URI da imagem selecionada
                    caminho = getPath(selectedImage); //Obter o path do URI de uma imagem
                    imageView.setImageURI(selectedImage); //Apresentar a imagem selecionada
                    break;
            }
    }

    /*Função para obter o path a partir do URI de uma imagem
     * Feito com base em: https://www.youtube.com/watch?v=6wZeSJ0U1t4
     */
    public String getPath(Uri uri){
        //Se não houver qualquer imagem
        if(uri==null){
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA}; //obter o local onde está armazenada a imagem
        Cursor cursor = managedQuery(uri,projection,null,null,null);
        //Se o cursor devolvido não for null
        if(cursor!=null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        //Caso contrário
        return uri.getPath();
    }

    //Método para submeter a edição do notícia na base de dados
    public void submeter (View v) throws IOException {
        if(caminho!=null){
            FileInputStream fs = new FileInputStream(caminho);
            byte[] imgbyte = new byte[fs.available()];
            fs.read(imgbyte);
            img_path=imgbyte;
        }
        if(nome.getText().toString().isEmpty()){ //Se a caixa de título estiver vazia
            nome.setError("O Título não pode ficar vazio!"); //Apresentar uma caixa de erro
        }
        //Se a caixa de texto não estiver vazia...
        else {
            String titulo=nome.getText().toString(); //Obter o título inserido
            if(conteudo.getText().toString().isEmpty()){ //Se a caixa da descrição estiver vazia
                conteudo.setError("A descrição não pode ficar em branco!"); //Apresentar caixa de erro
            }
            //Se a caixa da descrição não estiver vazia
            else {
                String contente = conteudo.getText().toString(); //Obter a descrição inserida
                    //Se a data estiver vazia
                    if(data.getText().toString().isEmpty()){
                        //Apresentar mensagem de erro
                        data.setError("A data não pode ficar vazia!");
                    }
                    //Caso tenha sido selecionada uma data
                    else {
                        String dataz=data.getText().toString();
                        //Caso a inserção tenha sido efetuada com sucesso
                        if (oDB.editNews(old_nome,titulo, contente, dataz, img_path)) {
                            //Apresentar toast de sucesso
                            Toast.makeText(getApplicationContext(), "Sucesso!", Toast.LENGTH_SHORT).show();
                            Intent iResult = new Intent(); //Intento para devolver o resultado
                            setResult(RESULT_OK,iResult); //Set result okay
                            firebaseDatabaseRemove = FirebaseDatabase.getInstance();
                            firebaseDatabaseRemove.getReference().child("News").removeValue();
                            cursor = oDB.getNews();
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
                            oDB.close();
                            super.finish(); //Terminar a atividade
                        }
                        //Caso a inserção tenha falhado
                        else {
                            //Apresentar toast de insucesso
                            Toast.makeText(getApplicationContext(), "Sem Sucesso!", Toast.LENGTH_SHORT).show();
                            Intent iResult = new Intent(); //Intento para devolver o resultado
                            setResult(RESULT_OK,iResult); //Set result okay
                            oDB.close();
                            super.finish(); //Terminar a atividade
                        }
                    }
            }
        }
    }
}