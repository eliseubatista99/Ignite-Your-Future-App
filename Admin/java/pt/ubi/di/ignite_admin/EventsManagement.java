package pt.ubi.di.ignite_admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EventsManagement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_management);
    }

    //Tratar o botão que recebeu o clique
    public void selectButton(View v) {
        Intent intento;
        switch (v.getId()){
            case R.id.add:
                intento = new Intent(this, NewEvent.class);
            break;
            case R.id.edit:
                intento = new Intent(this, ListEvents.class);
                intento.putExtra("tipo","edit");
            break;
            case R.id.erase:
                intento = new Intent(this, ListEvents.class);
                intento.putExtra("tipo","erase");
            break;
            case R.id.inscritos:
                intento = new Intent(this, ListEvents.class);
                intento.putExtra("tipo","inscritos");
                break;
            default:
                intento = new Intent(this,EventsManagement.class);
                break;
        }
        startActivity(intento);
    }
}
