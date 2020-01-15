package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Regist extends AppCompatActivity{

    public DataBase oDB;
    private TextView data;
    private DatePickerDialog.OnDateSetListener dataListener;
    private EditText nome;
    private EditText sobrenome;
    private EditText username;
    private EditText password;
    private Button registar;
    public int dia,mes,ano;
    public int cont;
    public FirebaseDatabase firebaseDatabase;
    public DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabaseRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        oDB = new DataBase(this);
        data = findViewById(R.id.DataNasc);
        nome = findViewById(R.id.Nome);
        sobrenome = findViewById(R.id.Sobrenome);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registar = findViewById(R.id.Regist);

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
                        Regist.this,
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
                month=month+1;
                String data_str = dayOfMonth + "/" + month + "/" + year;
                dia=dayOfMonth;
                mes=month;
                ano=year;
                data.setText(data_str);
            }
        };

    }

    public void clickRegist(View v){
        cont=0;
        //Se o nome estiver em branco
        if(nome.getText().toString().isEmpty()){
            nome.setError("O nome não pode ficar em branco");
        }
        else{
            //Se o sobrenome estiver em branco
            if(sobrenome.getText().toString().isEmpty()){
                sobrenome.setError("O sobrenome não pode ficar em branco");
            }
            else{
                //Se a data estiver em branco
                if(data.getText().toString().isEmpty()){
                    data.setError("A data não pode ficar em branco");
                }
                else{
                    //Se o username estiver em branco
                    if(username.getText().toString().isEmpty()){
                        username.setError("O username não pode ficar em branco");
                    }
                    //Se o username ultrapassar os 17 caracteres
                    else if(username.getText().toString().length() <=0 || username.getText().toString().length() >=18){
                        username.setError("O username deve não deve ter mais que 17 caracteres");
                    }
                    else{
                        //Se o username já existir
                        Cursor cursor = oDB.getUsers();
                        while(cursor.moveToNext()){
                            if(cursor.getString(5).equals(username.getText().toString())){
                                cont=cont+1;
                            }
                        }
                        if(cont!=0){
                            username.setError("Nome de utilizador indisponivel");
                        }
                        //-----------------------------------------------------------------------------
                        else {
                            //Se a password estiver vazia
                            if (password.getText().toString().isEmpty()) {
                                password.setError("A password não pode ficar em branco");

                            }
                            //Se a password ultrapassar os 17 caracteres
                            else if (password.getText().toString().length() == 0 || password.getText().toString().length() >= 18) {
                                password.setError("A password não deve ter mais que 17 caracteres");
                            }
                            //Se estiver tudo em ordem
                            else {
                                if (oDB.addUser(nome.getText().toString(), sobrenome.getText().toString(), dia, mes, ano, username.getText().toString(), password.getText().toString())) {
                                    Toast.makeText(this, "Utilizador inserido com sucesso", Toast.LENGTH_SHORT);
                                    firebaseDatabaseRemove = FirebaseDatabase.getInstance();
                                    //ATUALIZAR NA FIREBASE
                                    firebaseDatabaseRemove.getReference().child("Users").removeValue();
                                    Cursor cursor2 = oDB.getUsers();
                                    while(cursor2.moveToNext()){
                                        String name = cursor2.getString(0);
                                        String surname = cursor2.getString(1);
                                        String dia = cursor2.getString(2);
                                        String mes = cursor2.getString(3);
                                        String ano = cursor2.getString(4);
                                        String username = cursor2.getString(5);
                                        String password = cursor2.getString(6);
                                        Users user = new Users(name,surname,dia,mes,ano,username,password);
                                        firebaseDatabase = FirebaseDatabase.getInstance();
                                        databaseReference = firebaseDatabase.getReference();
                                        databaseReference.child("Users").push().setValue(user);
                                    }
                                    super.finish();
                                } else {
                                    Toast.makeText(this, "A inserção do utilizador falhou", Toast.LENGTH_SHORT);
                                    super.finish();
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
