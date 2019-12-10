package hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {
    private final AtomicLong counter = new AtomicLong();

    //Operace klientského GET požadavku pro získání záznamů
    @RequestMapping("greeting")
    public Greeting getter(@RequestParam(value="name", defaultValue="Ahoj") String name) {
        try {
            Greeting client = new Greeting(counter.incrementAndGet(), String.format("%s", name));
            double random = Math.random() * 3000;
            /**/System.out.println((int)random + "\n" + client.getId());
            Thread.sleep((int)random);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}