package gr.gdschua.bloodapp.Entities;

public class Comment {
    private String body;
    private String author;
    private String userLvl;
    private String authorProperty;

    public Comment(String body, String author, String userLvl, String authorProperty) {
        this.body = body;
        this.author = author;
        this.userLvl = userLvl;
        this.authorProperty = authorProperty;
    }

    public Comment() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUserLvl() {
        return userLvl;
    }

    public void setUserLvl(String userLvl) {
        this.userLvl = userLvl;
    }

    public String getAuthorProperty() {
        return authorProperty;
    }

    public void setAuthorProperty(String authorProperty) {
        this.authorProperty = authorProperty;
    }
}
