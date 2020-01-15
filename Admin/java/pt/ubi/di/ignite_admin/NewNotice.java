package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class NewNotice extends AppCompatActivity {

    private DataBase oDB;
    private TextView data;
    private EditText nome;
    private EditText conteudo;
    public ImageView imageView;
    public String caminho;
    public String dataz;
    public int cont;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice);
        data = findViewById(R.id.Data);
        nome = findViewById(R.id.nome);
        conteudo = findViewById(R.id.conteudo);
        imageView = findViewById(R.id.image);
        oDB = new DataBase(this);
        //Click Listener para processar a data
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR); //vai buscar o ano atual
        int month = cal.get(Calendar.MONTH); //vai buscar o mês atual
        int day = cal.get(Calendar.DAY_OF_MONTH); //vai buscar o dia atual
        month=month+1;
        dataz = ""+day+"/"+month+"/"+year+"";
        data.setText(dataz);
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
                    caminho=""; //Valor padrão do caminho
                    caminho = getPath(selectedImage); //Obter o path do URI de uma imagem
                    imageView.setImageURI(selectedImage);  //Apresentar a imagem selecionada
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

    //Método para inserir a notícia na base de dados
    public void submeter (View v) throws IOException {
        cont=0;
        if(nome.getText().toString().isEmpty()){ //Se a caixa de título estiver vazia
            nome.setError("O Título não pode ficar vazio!"); //Apresentar uma caixa de erro
        }
        //Se a caixa de texto não estiver vazia...
        else {
            String titulo=nome.getText().toString(); //Obter o título inserido
            Cursor cursor = oDB.getNews();
            //Percorrer a tabela de notícias
            while(cursor.moveToNext()){
                if(titulo.equals(cursor.getString(0))){
                    cont=cont+1;
                }
            }
            //Se existir alguma notícia na base de dados com este título
            if(cont!=0){
                nome.setError("Nome de Notícia inválido!");
            }
            else {
                if (conteudo.getText().toString().isEmpty()) { //Se a caixa do conteudo estiver vazia
                    conteudo.setError("O conteudo não pode ficar em branco!"); //Apresentar caixa de erro
                }
                //Se a caixa do conteudo não estiver vazia
                else {
                    String contente = conteudo.getText().toString();
                    //Se não foi selecionada nenhuma imagem
                    if (caminho == null) {
                        //Apresentar toast de erro
                        Toast.makeText(getApplicationContext(), "Deve selecionar uma imagem!", Toast.LENGTH_LONG).show();
                    }
                    //Se foi selecionada uma imagem
                    else {
                        //Se a data estiver vazia
                        if (data.getText().toString().isEmpty()) {
                            //Apresentar mensagem de erro
                            data.setError("A data não pode ficar vazia!");
                        }
                        //Caso tenha sido selecionada uma data
                        else {
                            //Caso a inserção tenha sido efetuada com sucesso
                            FileInputStream fs = new FileInputStream(caminho);
                            byte[] imgbyte = new byte[fs.available()];
                            fs.read(imgbyte);
                            if (oDB.addNew(titulo, contente, dataz, imgbyte)) {
                                //Apresentar toast de sucesso
                                Toast.makeText(getApplicationContext(), "Sucesso!", Toast.LENGTH_SHORT).show();
                                cursor = oDB.getNews();
                                //Insere todos os eventos na firebase
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
                                super.finish(); //Terminar atividade
                            }
                            //Caso a inserção tenha falhado
                            else {
                                //Apresentar toast de insucesso
                                Toast.makeText(getApplicationContext(), "Sem Sucesso!", Toast.LENGTH_SHORT).show();
                                oDB.close(); //Fechar a base de dasos
                                super.finish(); //Terminar a atividade
                            }
                        }
                    }
                }
            }
        }
    }
}
