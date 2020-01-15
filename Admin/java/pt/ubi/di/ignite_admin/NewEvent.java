package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

public class NewEvent extends AppCompatActivity {

    private DataBase oDB;
    private TextView data;
    private DatePickerDialog.OnDateSetListener dataListener;
    private EditText nome;
    private EditText desc;
    private EditText idadeMin;
    private EditText idadeMax;
    private EditText local;
    private EditText limit;
    public ImageView imageView;
    public int dia,mes,ano;
    public String caminho;
    public int limite;
    public int cont;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);data = findViewById(R.id.Data);
        nome = findViewById(R.id.nome);
        desc = findViewById(R.id.desc);
        idadeMin = findViewById(R.id.idade_min);
        idadeMax = findViewById(R.id.idade_max);
        local = findViewById(R.id.local);
        imageView = findViewById(R.id.image);
        limit=findViewById(R.id.limite);
        oDB = new DataBase(this);
        //Click Listener para processar a data
        data.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR); //vai buscar o ano atual
                int month = cal.get(Calendar.MONTH); //vai buscar o mês atual
                int day = cal.get(Calendar.DAY_OF_MONTH); //vai buscar o dia atual

                //Prepara o diálogo
                DatePickerDialog dialog = new DatePickerDialog(
                        NewEvent.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, //tema
                        dataListener,
                        year,month,day); //valores
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Preparar a window
                dialog.show(); //Mostrar o diálogo
            }
        });

        //Para guardar os valores selecionados no diálogo
        dataListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1; //Incrementar o month em uma unidade, pois o valor devolvido dos month começa em 0
                String data_str = dayOfMonth + "/" + month + "/" + year;
                //Guardar os valores da data para inserir na BD
                dia=dayOfMonth;
                mes=month;
                ano=year;
                data.setText(data_str); //Apresentar a data
            }
        };

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

    //Método para inserir o evento na base de dados
    public void submeter (View v) throws IOException {
        cont=0;
        if(nome.getText().toString().isEmpty()){ //Se a caixa de título estiver vazia
            nome.setError("O Título não pode ficar vazio!"); //Apresentar uma caixa de erro
        }
        //Se a caixa de texto não estiver vazia...
        else {
            String titulo=nome.getText().toString(); //Obter o título inserido
            Cursor cursor = oDB.getEvents();
            //Percorrer a tabela de eventos
            while (cursor.moveToNext()){
                if(titulo.equals(cursor.getString(0))){
                    cont=cont+1;
                }
            }
            //Se encontrar algum evento com esse nome, apresentar mensagem de erro
            if(cont!=0){
                nome.setError("Nome de Evento inválido!");
            }
            else {
                if (desc.getText().toString().isEmpty()) { //Se a caixa da descrição estiver vazia
                    desc.setError("A descrição não pode ficar em branco!"); //Apresentar caixa de erro
                }
                //Se a caixa da descrição não estiver vazia
                else {
                    String descricao = desc.getText().toString(); //Obter a descrição inserida
                    if (limit.getText().toString().isEmpty()) { //Se a caixa do limite de inscritos estiver vazia
                        limit.setError("O limite não pode ficar vazio!"); //Apresentar caixa de erro
                    }
                    //Se a caixa de limite de inscritos não estiver vazia
                    else {
                        limite = Integer.parseInt(limit.getText().toString()); //Obter o limite inserido
                        if (limite == 0) { //Se o limite for 0
                            limit.setError("O limite não pode ser nulo!"); //Apresentar mensagem de erro
                        }
                        //Se o limite for uma valor válido
                        else {
                            //Se a idade mínima estiver vazia
                            if (idadeMin.getText().toString().isEmpty()) {
                                idadeMin.setError("Introduza uma idade válida"); //Apresentar caixa de erro
                            }
                            //Caso a idade mínima não esteja vazia
                            else {
                                int min_age = Integer.parseInt(idadeMin.getText().toString()); //Obter a idade mínima
                                if (idadeMax.getText().toString().isEmpty()) { //Se a caixa da idade máxima estiver vazia
                                    idadeMax.setError("Introduza uma idade válida"); //Apresentar mensagem de erro
                                }
                                //Caso a idade máxima não esteja vazia
                                else {
                                    int max_age = Integer.parseInt(idadeMax.getText().toString()); //Obter a idade máxima
                                    //Se a idade mínima for 0 ou inferior, ou se a idade mínima for maior ou igual à idade máxima
                                    if (min_age <= 0 || min_age >= max_age) {
                                        //Apresentar Toast de erro
                                        Toast.makeText(getApplicationContext(), "As idades não podem ser 0, e a idade mínima deve ser menor que a idade máxima!", Toast.LENGTH_LONG).show();
                                    } else {
                                        String place = local.getText().toString();
                                        if (local.getText().toString().isEmpty()) {
                                            local.setError("O local não pode ficar vazio!");
                                        }
                                        //Caso os valores da idade estejam em ordem
                                        else {
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
                                                    FileInputStream fs = new FileInputStream(caminho);
                                                    byte[] imgbyte = new byte[fs.available()];
                                                    fs.read(imgbyte);
                                                    //Caso a inserção tenha sido efetuada com sucesso
                                                    if (oDB.addEvent(titulo, descricao, min_age, max_age, place, dia, mes, ano, 0, limite, imgbyte)) {
                                                        //Apresentar toast de sucesso
                                                        Toast.makeText(getApplicationContext(), "Sucesso!", Toast.LENGTH_SHORT).show();
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
                    }
                }
            }
        }

    }

}
