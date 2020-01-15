package pt.ubi.di.ignite_admin;

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
    public String tmp_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        oDB = new DataBase(this);
    }

    //Método para tratar o clique no botão Login
    public void clickLogin(View v){
        oSQLiteDB = oDB.getWritableDatabase();
        cont=0;
        Cursor cursor = oDB.getLogins();
        //Percorrer a tabela de Logins
        while(cursor.moveToNext()){
            if(cursor.getString(0).equals(username.getText().toString())){ //Se o username existir na base de dados
                cont=cont+1;
                tmp_password=cursor.getString(1); //guardar a password
            }
        }
        if(cont==0){ //Se não encontrou o username na base de dados
            username.setError("Username inválido!");
        }
        else{ //Se encontrou o username
            if(tmp_password.equals(password.getText().toString())){ //Se a password corresponder, ir para o menu
                Intent goToMenuScreen = new Intent(this, Menu.class);
                startActivity(goToMenuScreen);
                oDB.close();
                super.finish();
            }
            //Caso contrário, mensagem de erro
            else{
                password.setError("Password inválida!");
            }
        }

    }
}
