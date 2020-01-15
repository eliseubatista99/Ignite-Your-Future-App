package pt.ubi.di.ignite_user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class InfoScreen extends AppCompatActivity {

    public ListView oLV;
    public ArrayList<String> List;
    public Intent intent;
    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);
        List = new ArrayList<>();
        oLV = findViewById(R.id.listview);
        username = getIntent().getStringExtra("username");
        //prepara o texto a ser apresentado
        String text_more="\n\t\t\t\t\t\t\tO Ignite Your Future é um evento de cariz tecnológico " +
                "promovido pelo Município do Fundão, Universidade da Beira Interior, Altran " +
                "e Agência Gardunha XXI, que tem lugar na cidade do Fundão.\n\n" +
                "\t\t\t\t\t\t\tEsta iniciativa dirige-se a estudantes do 9º ao 12º ano de escolaridade, " +
                "que tenham gosto, apetência e vontade de adquirir competências no domínio " +
                "das novas tecnologias, nomeadamente informática e robótica.\n\n" +
                "\t\t\t\t\t\t\tAlém dos dias de imersão nas novas tecnologias, os participantes têm a " +
                "oportunidade de conhecer as instalações da Altran Portugal e as áreas de " +
                "negócio da empresa\n\n";
        List.add(text_more);
        oLV.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, List));
    }

    public void selectButton(View v) {
        switch (v.getId()) {
            case R.id.home:
                intent = new Intent(this, Menu.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.news:
                intent = new Intent(this, NewsScreen.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.events:
                intent = new Intent(this, EventsScreen.class);
                intent.putExtra("username", username); //enviar o username para a próxima atividade
                startActivity(intent);
                super.finish();
                break;
            case R.id.info:
                break;
            case R.id.profile:
                intent = new Intent(this, ProfileScreen.class);
                intent.putExtra("username", username); //Enviar o username para a próxima atividade
                startActivity(intent);
                break;
            default:
                //nada
        }
    }
}
