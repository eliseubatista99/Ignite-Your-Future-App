package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
    }

    //Função para ir ao ecrã de registo
    public void Registar(View v){
        Intent goToRegist = new Intent(this, Regist.class);
        startActivity(goToRegist);
    }

    //Função para ir ao ecrã de login
    public void Logar(View v){
        Intent goToLogin = new Intent(this, Login.class);
        startActivity(goToLogin);
    }

    //Não terminar esta atividade, pois esta deve ser a ultima atividade a ser fechada
}
