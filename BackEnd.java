import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BackEnd {
    ObjectOutputStream outputToFile = new ObjectOutputStream(new FileOutputStream("samples.dat", false));
    static ObjectOutputStream outputToClient;
    ObjectInputStream inputFromClient;
    static ObjectInputStream inputFromFile;
    static ServerSocket serverSocket;

    static {
        try {
            serverSocket = new ServerSocket(8000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        new BackEnd();
    }

    public BackEnd() throws IOException {
        new Thread( () -> {
            try {

                while (true) {
                    Socket socket = serverSocket.accept();
                    inputFromClient = new ObjectInputStream(socket.getInputStream());
                    Sample sample = (Sample) inputFromClient.readObject();
                    outputToFile.writeObject(sample);
                    outputToFile.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    inputFromClient.close();
                    outputToFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static HashMap<String, Sample> getTestCollection() {
        HashMap<String, Sample> collection = new HashMap<>();
        ArrayList<SamplePhoto> examplePhotos = new ArrayList<>();
        SamplePhoto picOne = new SamplePhoto("Images/diorite.jpg", "A real nice piece of diorite");
        SamplePhoto picTwo = new SamplePhoto("Images/diorite2.jpg", "Another picture of a real nice diorite.");
        examplePhotos.add(picOne);
        examplePhotos.add(picTwo);
        Sample example = new Sample(1, "Diorite", "Ig0001",
                "SUU Campus", "Milky white, black-darkgray crystals",
                "Plagioclase, quartz, amphiboles", "phaneritic",
                "None", "None", "shiny crystals, dull plag",
                "none", "none noted", "1-5 mm amphibols",
                "Real nice diorite", "No fossils", false,
                "5 cm across", examplePhotos);

        collection.put("Ig0001", example);
        return collection;
    }
    public static void sendCollection() throws IOException, ClassNotFoundException {
        Socket socket = serverSocket.accept();
        inputFromFile = new ObjectInputStream(new FileInputStream("samples.dat"));
        outputToClient = new ObjectOutputStream(socket.getOutputStream());
        try {
            while (true) {
                Sample sample = (Sample) inputFromFile.readObject();
                outputToClient.writeObject(sample);
                outputToClient.flush();
            }
        }
        catch (RuntimeException e) {
            System.out.println("All samples sent");
        }
//        System.out.println(testSample.toString());


    }
}


