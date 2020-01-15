package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Regist extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button registar;    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;
    public int cont;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registar = findViewById(R.id.Regist);
        oDB = new DataBase(this);
    }

    //Método para tratar o clique no botão Login
    public void clickRegist(View v){
        oSQLiteDB = oDB.getWritableDatabase();
        cont=0;
        if(username.getText().toString().isEmpty()){ //Se a caixa do username estiver vazia, apresentar mensagem de erro
            username.setError("O username não pode ficar em branco!");
        }
        else if(username.getText().toString().length()>=17){ //Se o username for maior que 17caracteres
            username.setError("O username não pode ter mais que 17 carat«cteres");
        }
        else {
            if(password.getText().toString().isEmpty()){ //Se a caixa da password estiver em branco
                password.setError("A password não pode ficar em branco");
            }
            else if(password.getText().toString().length()>=18){
                password.setError("A password não pode ter mais que 17 caracteres!");
            }
            else { //Se as condições anteriores forem cumpridas
                Cursor cursor = oDB.getLogins();
                //Percorre a tabela de logins
                while (cursor.moveToNext()) {
                    if (username.getText().toString().equals(cursor.getString(0))) { //Se encontar algum username igual na base de dados
                        cont = cont + 1;
                    }
                }
                if (cont != 0) { //Caso tenha encontrado algum, apresentar mensagem de erro
                    username.setError("Username já em uso!");
                }
                //Caso não tenha encontrado nenhum, prosseguir
                else {
                    oDB.addLogin(username.getText().toString(), password.getText().toString());
                    firebaseDatabaseRemove = FirebaseDatabase.getInstance();
                    //Remove os eventos e as noticias da firebase
                    firebaseDatabaseRemove.getReference().child("Admins").removeValue();
                    cursor = oDB.getLogins();
                    while(cursor.moveToNext()){
                        String username = cursor.getString(0);
                        String password = cursor.getString(1);
                        Logins login = new Logins(username,password);
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        databaseReference = firebaseDatabase.getReference();
                        databaseReference.child("Admins").push().setValue(login);
                    }
                    oDB.close();
                    super.finish();

                }
            }
        }

    }


}
