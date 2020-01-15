package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    private EditText username;
    private EditText password;
    public DataBase oDB;
    public SQLiteDatabase oSQLiteDB;
    public int cont;
    public String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        oDB = new DataBase(this);
    }

    public void clickLogin(View v){
        cont=0;
        Intent goToMenuScreen = new Intent(this, Menu.class);
        Cursor cursor = oDB.getUsers();
        //Percorrer a tabela de Users
        while(cursor.moveToNext()){
            if(cursor.getString(5).equals(username.getText().toString())){
                cont=cont+1;
                pass=cursor.getString(6);
            }
        }
        //Se o username não foi encontrado
        if(cont==0) {
            username.setError("Username inválido!");
        }
        //Se o username foi encontrado
        else{
            //Se a password está incorreta
            if(!pass.equals(password.getText().toString())){
                password.setError("Password Errada!");
            }
            //Se está tudo em ordem
            else{
                goToMenuScreen.putExtra("username",username.getText().toString()); //Enviar o username para a próxima atividade
                startActivity(goToMenuScreen); //Começar atividade
                super.finish();
            }
        }

    }
}
