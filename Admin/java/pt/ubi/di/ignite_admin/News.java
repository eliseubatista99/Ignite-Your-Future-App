package pt.ubi.di.ignite_admin;


public class News {

    public String title;
    public String content;
    public String image_path;
    public String data;

    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public News(String title, String content, String image_path, String data){
        this.title=title;
        this.content=content;
        this.data=data;
        this.image_path=image_path;
    }

    public News(){

    }
}
