package pt.ubi.di.ignite_user;

public class Users {

    public String name;
    public String surname;
    public String dia;
    public String mes;
    public String ano;
    public String username;
    public String password;

    public Users(String name, String surname,String dia, String mes, String ano, String username, String password){
        this.name=name;
        this.surname=surname;
        this.dia=dia;
        this.mes=mes;
        this.ano=ano;
        this.username = username;
        this.password = password;
    }

    public Users(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDia() {
        return dia;
    }

    public String getMes() {
        return mes;
    }

    public String getAno() {
        return ano;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
}
