import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class BackEnd {
    static ObjectOutputStream outputToFile;

    static {
        try {
            outputToFile = new ObjectOutputStream(new FileOutputStream("samples.txt", true));
            outputToFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static DataOutputStream outputToClient;
    static ObjectInputStream inputFromClient;
    static BufferedInputStream inputFromFile;

    static ServerSocket serverSocket;
    static Socket socket;



    public static void main(String[] args) throws IOException {
        new BackEnd();
    }

    public BackEnd() throws IOException {
        new Thread( () -> {
            try {
                serverSocket = new ServerSocket(8000);
                socket = serverSocket.accept();
                inputFromFile = new BufferedInputStream(new FileInputStream("samples.txt"));
                outputToClient = new DataOutputStream(socket.getOutputStream());

                while (true) {
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
    public static void sendCollection() throws ClassNotFoundException, IOException {
        outputToFile.flush();
        inputFromFile.read();

        try {
            while (true) {
                System.out.println("Are we getting here?");
                Sample sample = (Sample) inputFromFile.readObject();
                System.out.println("Sending sample " + sample.getRockName());
                outputToClient.writeInt(sample.getSamplePhotos().size());
                for (int i = 0; i < sample.getSamplePhotos().size() - 1; i++) {
                    outputToClient.writeUTF(sample.getSamplePhotos().get(i).getPhotoPathName());
                    outputToClient.writeUTF(sample.getSamplePhotos().get(i).getPhotoDescription());
                }
                outputToClient.writeInt(sample.getGeneralType());
                outputToClient.writeUTF(sample.getRockName());
                outputToClient.writeUTF(sample.getId());
                outputToClient.writeUTF(sample.getLocation());
                outputToClient.writeUTF(sample.getColor());
                outputToClient.writeUTF(sample.getComposition());
                outputToClient.writeUTF(sample.getTexture());
                outputToClient.writeUTF(sample.getStructures());
                outputToClient.writeUTF(sample.getRounding());
                outputToClient.writeUTF(sample.getLuster());
                outputToClient.writeUTF(sample.getGrainSize());
                outputToClient.writeUTF(sample.getCleavage());
                outputToClient.writeUTF(sample.getMineralSize());
                outputToClient.writeUTF(sample.getOtherFeatures());
                outputToClient.writeUTF(sample.getFossilDescription());
                outputToClient.writeBoolean(sample.getFossilContent());
                outputToClient.writeUTF(sample.getSize());
                outputToClient.flush();
            }
        }
        catch (RuntimeException e) {
            System.out.println("All samples sent");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(testSample.toString());


    }
}


