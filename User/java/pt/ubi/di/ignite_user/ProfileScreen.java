package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class ProfileScreen extends AppCompatActivity {

    public DataBase oDB;
    public SQLiteDatabase oSQLite;
    private TextView data;
    public String icameFromMenu,old_nome,password_velha,day,month,year,password_set;
    private EditText nome;
    private EditText sobrenome;
    private TextView username;
    private EditText old_password,new_password,new_password_check;
    public int dia,mes,ano;
    public boolean passwordchanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        dia=0;
        mes=0;
        ano=0;
        icameFromMenu = getIntent().getStringExtra("username"); //Obter o username da atividade anterior
        passwordchanged=false;
        nome = findViewById(R.id.Nome);
        sobrenome = findViewById(R.id.Sobrenome);
        data = findViewById(R.id.DataNasc);
        username = findViewById(R.id.username);
        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        new_password_check = findViewById(R.id.new_password_check);
        oDB = new DataBase(this);

        Cursor cursor = oDB.getUsers();
        //Percorrer a tabela de Utilizadores
        while(cursor.moveToNext()){
            if(cursor.getString(5).equals(icameFromMenu)){ //Ao encontrar o user pretendido, apresentar os dados
                nome.setText(cursor.getString(0));
                old_nome=cursor.getString(0);
                sobrenome.setText(cursor.getString(1));
                day = cursor.getString(2);
                month = cursor.getString(3);
                year = cursor.getString(4);
                data.setText(day+"/"+month+"/"+year);
                username.setText(cursor.getString(5));
                password_velha = cursor.getString(6);
            }
        }
    }

    //Método para submeter a edição do user
    public void submeter (View v) throws IOException {
        //Se o nome estiver em branco
        if(nome.getText().toString().isEmpty()){
            nome.setError("O Nome não pode ficar em branco!");
        }
        else{
            //Se o sobrenome estiver em branco
            if(sobrenome.getText().toString().isEmpty()){
                sobrenome.setError("O Sobrenome não pode ficar em branco!");
            }
            else{
                //Se os campos das novas passwords estiverem em branco, nao alterar a password
                if(new_password.getText().toString().isEmpty() && new_password_check.getText().toString().isEmpty()){
                    password_set=password_velha;
                    if (oDB.editUsers(username.getText().toString(), nome.getText().toString(),sobrenome.getText().toString(),Integer.parseInt(day),Integer.parseInt(month), Integer.parseInt(year),username.getText().toString(),password_set)) {
                        //Apresentar toast de sucesso
                        Toast.makeText(getApplicationContext(), "Sucesso!", Toast.LENGTH_SHORT).show();
                        Intent iResult = new Intent(); //Intento para devolver o resultado
                        setResult(RESULT_OK, iResult); //Set result okay
                        oDB.close();
                        super.finish(); //Terminar a atividade
                    }
                    //Caso a inserção tenha falhado
                    else {
                        //Apresentar toast de insucesso
                        Toast.makeText(getApplicationContext(), "Sem Sucesso!", Toast.LENGTH_SHORT).show();
                        Intent iResult = new Intent(); //Intento para devolver o resultado
                        setResult(RESULT_OK, iResult); //Set result okay
                        oDB.close();
                        super.finish(); //Terminar a atividade
                    }
                }
                //Caso contrário, alterar a password
                else {
                    //Se os campos das novas password não coincidirem
                    if (!(new_password.getText().toString().equals(new_password_check.getText().toString()))) {
                        new_password.setError("As passwords não coincidem");
                        new_password_check.setError("As passwords não coincidem");
                    }
                    //Se as novas passwords coincidirem
                    else {
                        //Se a password antiga estiver errada
                        if (!old_password.getText().toString().equals(password_velha)) {
                            old_password.setError("A password antiga está incorreta");
                        }
                        else if(new_password.getText().toString().length()>=18){
                            old_password.setError("A password não deve ter mais de 17 caracteres");
                        }
                        //Se estiver tudo em ordem
                        else {
                            password_set = new_password.getText().toString();
                            if (oDB.editUsers(username.getText().toString(), nome.getText().toString(),sobrenome.getText().toString(),Integer.parseInt(day),Integer.parseInt(month), Integer.parseInt(year),username.getText().toString(),password_set)) {
                                //Apresentar toast de sucesso
                                Toast.makeText(getApplicationContext(), "Sucesso!", Toast.LENGTH_SHORT).show();
                                Intent iResult = new Intent(); //Intento para devolver o resultado
                                setResult(RESULT_OK, iResult); //Set result okay
                                oDB.close();
                                super.finish(); //Terminar a atividade
                            }
                            //Caso a inserção tenha falhado
                            else {
                                //Apresentar toast de insucesso
                                Toast.makeText(getApplicationContext(), "Sem Sucesso!", Toast.LENGTH_SHORT).show();
                                Intent iResult = new Intent(); //Intento para devolver o resultado
                                setResult(RESULT_OK, iResult); //Set result okay
                                oDB.close();
                                super.finish(); //Terminar a atividade
                            }
                        }
                    }
                }

            }
        }
    }
}
