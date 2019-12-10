package hello;

public class Greeting {

    private final String content;
    private final long id;

    public Greeting(long id, String content) {
        this.content = content;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}