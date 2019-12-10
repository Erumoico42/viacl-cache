package hello;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GreetingController {
    private static HashMap<String, GreetingTimeStamp> greetingMap = new HashMap<String, GreetingTimeStamp>();
    private final AtomicLong counter = new AtomicLong();
    private int count=0;
    private static final long TIME_TO_CLEAN_CACHE=20000;
    private static final int MAX_CACHE_SIZE = 20;
    private int failCount=0;
    private boolean connOpen=true, connHalfOpen=false, connClosed=false;
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @RequestMapping("greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="ahoj") String name) throws Exception {
        System.out.println("incoming connection");
        long timeStart=System.currentTimeMillis();
        GreetingTimeStamp ret = greetingMap.get(name);
        if(ret==null) {
            long time=System.currentTimeMillis();
            Greeting gret =sendRequest(name);
            ret= new GreetingTimeStamp(gret,time);
            //if(greeting.getId()!=666)
                greetingMap.put(name,ret);

            System.out.println("Server: "+gret.getContent());
        }
        else{
            System.out.println("Cache: "+ret.getGreeting().getContent());
        }
        long timeStop=System.currentTimeMillis();
        System.out.println("Response time: "+(timeStop-timeStart));
        count++;
        System.out.println("Count: "+count);
        System.out.println("------------------");
        return ret.getGreeting();

    }
    private Greeting sendRequest(String param){

        try {

            HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8081/greeting?name="+param).openConnection();
            con.setRequestMethod("GET");
            //Greeting greeting=null;
            /*if(greeting==null)
            {
                connOpen=false;
                connHalfOpen=true;
                if(connectionHalfOpen(con))
                    greeting=connectionOpen(con);
                else{
                    connectionClosed(con);
                    return new Greeting(666, "Failed connection to server");
                }
            }*/
            int responseCode= 0;
            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String rest=response.toString();
                JSONObject obj=new JSONObject(rest);
                long id=obj.getLong("id");
                String name=obj.getString("content");
                connOpen=true;
                connHalfOpen=false;
                connClosed=false;
                return new Greeting(id, name);

            } else {
                System.out.println("GET request not worked");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*private Greeting connectionOpen(HttpURLConnection con)
    {
        int responseCode= 0;
        try {
            try {
                responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    String rest=response.toString();
                    JSONObject obj=new JSONObject(rest);
                    long id=obj.getLong("id");
                    String name=obj.getString("content");
                    connOpen=true;
                    connHalfOpen=false;
                    connClosed=false;
                    return new Greeting(id, name);

                } else {
                    System.out.println("GET request not worked");
                }
            }catch(SocketTimeoutException e)
            {
                e.printStackTrace();
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/
    private boolean connectionHalfOpen(HttpURLConnection con)
    {
        int succesCount=0;
        for (int i=0; i<10; i++){
            try {
                con.getResponseCode();
                succesCount++;
            } catch(SocketTimeoutException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Half open count: "+succesCount);
        return (succesCount>0);
    }
    private void connectionClosed(HttpURLConnection con)
    {
        connHalfOpen=false;
        connOpen=false;
        connClosed=true;
        System.out.println("Connection failed");
    }

    public static void checkTimeStamp()
    {
        long actualTime=System.currentTimeMillis();
        List<GreetingTimeStamp> list=new ArrayList<>();

        greetingMap.forEach((k, v)->{
            list.add(v);
        });
        for(GreetingTimeStamp gts :list){
            if(actualTime-gts.getTime()>TIME_TO_CLEAN_CACHE)
            {
                boolean removed=greetingMap.remove(gts.getGreeting().getContent(),gts);
                System.out.println("Removed from cache: "+gts.getGreeting().getContent()+"-"+removed);

            }
        }
        /*if(greetingMap.size()>MAX_CACHE_SIZE)
        {
            list.sort(new Comparator<GreetingTimeStamp>() {
                @Override
                public int compare(GreetingTimeStamp o1, GreetingTimeStamp o2) {
                    return o1.getTime() >o2.getTime() ? -1 : (o1.getTime() < o2.getTime() ? 1 :0);
                }
            });
            for (int i=0; i<5; i++)
            {
                GreetingTimeStamp gts = list.get(list.size()-i-1);
                boolean removed=greetingMap.remove(gts.getGreeting().getContent(),gts);
                System.out.println("Removed from cache: "+gts.getGreeting().getContent()+"-"+removed);
            }
        }*/
    }
}
