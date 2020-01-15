package pt.ubi.di.ignite_admin;

import android.content.Context;

public class Logins {

    public String username;
    public String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Logins(String username, String password){
        this.username = username;
        this.password = password;
    }

    public Logins(){

    }

}
