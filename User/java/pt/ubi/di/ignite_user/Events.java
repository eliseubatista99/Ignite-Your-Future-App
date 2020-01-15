package pt.ubi.di.ignite_user;

public class Events {

    public String title;
    public String description;
    public String min_age;
    public String max_age;
    public String local;
    public String day;
    public String month;
    public String year;
    public String inscritos;
    public String limite;
    public String image_path;

    public Events(String title, String description, String min_age, String max_age, String local, String day, String month, String year, String inscritos, String limite, String image_path){
        this.title=title;
        this.description=description;
        this.min_age=min_age;
        this.max_age=max_age;
        this.local=local;
        this.day=day;
        this.month=month;
        this.year=year;
        this.inscritos=inscritos;
        this.limite=limite;
        this.image_path=image_path;
    }

    public Events(){

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMin_age() {
        return min_age;
    }

    public String getMax_age() {
        return max_age;
    }

    public String getLocal() {
        return local;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getInscritos() {
        return inscritos;
    }

    public String getLimite() {
        return limite;
    }

    public String getImage_path() {
        return image_path;
    }
}
