package viacl_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VIACL_Client {

    public static String proxy_address = "http://localhost:8080";
    //public static String[] cars = {"Alfa Romeo", "Audi", "BMW", "Chevrolet", "Citroën", "Dacia", "DS", "Ferrari", "Fiat", "Ford", "Honda", "Hyundai", "Jaguar", "Jeep", "Kia", "Lada", "Lamborghini", "Lancia", "Land Rover", "Lexus", "Mazda", "Mercedes-Benz", "Mini", "Mitsubishi", "Nissan", "Opel", "Peugeot", "Porsche", "Renault", "Seat", "Škoda", "Subaru", "Suzuki", "Toyota", "Volkswagen", "Volvo"};   
    public static String[] cars = {"Alfa Romeo", "Audi", "BMW", "Chevrolet", "Citroën", "Dacia"};

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    newThread();
                }
            };
            thread.start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void newThread() {
        try {
            long timeStart = System.currentTimeMillis();
            HttpURLConnection con = (HttpURLConnection) new URL(proxy_address + "/greeting?name=" + cars[(int) (Math.random() * cars.length)]).openConnection();
            con.setRequestMethod("GET");
            long timeEnd = System.currentTimeMillis();
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            System.out.println((timeEnd - timeStart) + "\t-" + response.toString());
        } catch (IOException e) {
            System.out.println("Čas na připojení k serveru vypršel :/");
        }
    }
}