package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Thread t=new Thread(){
            @Override
            public void run(){
                while(true)
                {
                    try {
                        Thread.sleep(500);
                        GreetingController.checkTimeStamp();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        t.run();
    }
}
