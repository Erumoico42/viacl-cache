package hello;

public class GreetingTimeStamp {
    private Greeting greeting;
    private long time;
    public GreetingTimeStamp(Greeting greeting, long time)
    {
        this.greeting=greeting;
        this.time=time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Greeting getGreeting() {
        return greeting;
    }
}
