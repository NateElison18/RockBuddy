import com.sun.security.ntlm.Server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BackEnd {
    ObjectOutputStream outputToFile;
    ObjectInputStream inputFromClient;

    public static void main(String[] args) {
        new BackEnd();
    }

    public BackEnd() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            outputToFile = new ObjectOutputStream(new FileOutputStream("samples.dat", true));

            while (true) {
                Socket socket = serverSocket.accept();
                inputFromClient = new ObjectInputStream(socket.getInputStream());
                Sample sample = (Sample) inputFromClient.readObject();
                outputToFile.writeObject(sample);
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
    }

    public static HashMap<String, Sample> getCollection() {
        HashMap<String, Sample> collection = new HashMap<>();
        Scanner fileReader = new Scanner("samples.dat");
        ArrayList<SamplePhoto> examplePhotos = new ArrayList<>();
        examplePhotos.add(new SamplePhoto("diorite.jpg", "A real nice piece of diorite"));
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
}
