package gr.gdschua.bloodapp.Entities;

import java.util.UUID;

public class Post {

    private String authorName;
    private String authorLevel;
    private String title;
    private String body;
    private String flair;
    private String authorType;
    private final String id;

    public Post(String authorName, String authorLevel, String title, String body, String flair, String authorType) {
        this.authorName = authorName;
        this.authorLevel = authorLevel;
        this.title = title;
        this.body = body;
        this.flair = flair;
        this.authorType = authorType;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorLevel() {
        return authorLevel;
    }

    public void setAuthorLevel(String authorLevel) {
        this.authorLevel = authorLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFlair() {
        return flair;
    }

    public void setFlair(String flair) {
        this.flair = flair;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }
}
