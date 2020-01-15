package pt.ubi.di.ignite_user;

public class UsersEvents {

    public String username;
    public String id;
    public String event;

    public UsersEvents(String id, String username, String event){
        this.username = username;
        this.event=event;
        this.id=id;
    }

    public UsersEvents(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

}
