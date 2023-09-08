import com.sun.security.ntlm.Server;

import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// TODO Fix issue where samples are not appending between program runs
public class BackEnd {
    static ObjectInputStream objectFromFile;
    static HashMap<String, Sample> collection = new HashMap<>();


    static DataOutputStream outputToClient;
    static ObjectInputStream inputFromClient;
//    static DataInputStream inputFromFile;
    static ObjectOutputStream outputToFile;

    static ServerSocket serverSocket;
    static Socket socket;

    static int terminateCode = 100;
    static int sendCollectionCode = 200;
    static int editSampleCode = 300;
    static int deleteSampleCode = 400;



    public static void main(String[] args) throws IOException {
        new BackEnd();
    }

    public BackEnd() throws IOException, NullPointerException {
        new Thread( () -> {
            // Update collection at the start of the program
            try {
                objectFromFile = new ObjectInputStream(new FileInputStream("samples.txt"));
                collection = (HashMap<String, Sample>) objectFromFile.readObject();
                System.out.println(collection.size());
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }

            try {
                serverSocket = new ServerSocket(8000);
                socket = serverSocket.accept();
//                inputFromFile = new DataInputStream(Files.newInputStream(Paths.get("samples.txt")));
                outputToClient = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    inputFromClient = new ObjectInputStream(socket.getInputStream());
                    Sample sampleReceived = (Sample) inputFromClient.readObject();
                    // Check for the code in the generalType of the sample and perform the action indicated.
                    if (sampleReceived.getGeneralType() == sendCollectionCode) {
                        try {
                            collection.forEach((id, sample) -> {
                                System.out.println("Pulled sample named " + sample.getRockName());
                                try {
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
                                    // Sending info for the photoSamples
                                    ArrayList<SamplePhoto> samplePhotos = sample.getSamplePhotos();
                                    outputToClient.writeInt(samplePhotos.size());
                                    System.out.println("Sample " + sample.getRockName() + " has a photo array list size of " + samplePhotos.size());
                                    for (int j = 0; j < samplePhotos.size(); j++) {
                                        outputToClient.writeUTF(samplePhotos.get(j).getPhotoPathName());
                                        outputToClient.writeUTF(samplePhotos.get(j).getPhotoDescription());
                                    }
                                    outputToClient.writeUTF(sample.getDateLogged());
                                    outputToClient.flush();
                                    System.out.println("Sent sample named " + sample.getRockName());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                            // Send flag int to tell client to stop expecting more samples
                            outputToClient.writeInt(terminateCode);
                        } catch (IOException e) {

                            System.out.println(e.getStackTrace());
                        }
//                        Sample sample = new Sample(1, "Diorite", "Ig0001",
//                                "SUU Campus", "Milky white, black-darkgray crystals",
//                                "Plagioclase, quartz, amphiboles", "phaneritic",
//                                "None", "None", "shiny crystals, dull plag",
//                                "none", "none noted", "1-5 mm amphibols",
//                                "Real nice diorite", "No fossils", false,
//                                "5 cm across", new ArrayList<SamplePhoto>());

                    }
                    else if (sampleReceived.getGeneralType() == editSampleCode){
                        Sample editedSample = (Sample) inputFromClient.readObject();
                        String originalId = inputFromClient.readUTF();
                        // Recompile collection, just in case it has been changed
                        objectFromFile = new ObjectInputStream(new FileInputStream("samples.txt")); // reset input stream so that it reads the first object
                        collection = (HashMap<String, Sample>) objectFromFile.readObject();
                        // Remove old Sample, put new sample
                        collection.remove(originalId);
                        collection.put(editedSample.getId(), editedSample);
                        // Save updated collection to the file
                        outputToFile = new ObjectOutputStream(new FileOutputStream("samples.txt", false));
                        outputToFile.writeObject(collection);
                        outputToFile.flush();

                    }
                    else if (sampleReceived.getGeneralType() == deleteSampleCode) {
                        String idToBeDeleted = inputFromClient.readUTF();
                        // Recompile collection, just in case it has been changed
                        objectFromFile = new ObjectInputStream(new FileInputStream("samples.txt")); // reset input stream so that it reads the first object
                        collection = (HashMap<String, Sample>) objectFromFile.readObject();
                        // Remove sample
                        collection.remove(idToBeDeleted);
                        // Save updated collection to the file
                        outputToFile = new ObjectOutputStream(new FileOutputStream("samples.txt", false));
                        outputToFile.writeObject(collection);
                        outputToFile.flush();
                    }
                    // If the generalType is between 0-3, save the sample to the file.
                    else {
                        collection.put(sampleReceived.getId(), sampleReceived);
                        outputToFile = new ObjectOutputStream(new FileOutputStream("samples.txt", false));

                        outputToFile.writeObject(collection);
                        outputToFile.flush();


                    }

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
    public static void sendCollection() throws ClassNotFoundException, IOException, NullPointerException {
        BufferedReader input = new BufferedReader(new FileReader("samples.txt"));
        BufferedWriter output = new BufferedWriter(new FileWriter("samples.txt", true));
        //ObjectOutputStream objectToFile = new ObjectOutputStream(new FileOutputStream("samples.txt", true));
        ObjectInputStream objectFromFile = new ObjectInputStream(new FileInputStream("samples.txt"));
        //outputToFile.flush();
        //System.out.println(input.readLine());
//        Sample test = (Sample) objectFromFile.readObject();
//        System.out.println(test.getGrainSize());
//        System.out.println(test.getSamplePhotos().size());
        //String rockType = objectFromFile.readUTF();
//        System.out.println(test.getRockName() + " ");

        try {
            while (true) {
                Sample sample = (Sample) objectFromFile.readObject();
                System.out.println("Created Sample from file");
//                System.out.println("Sending sample " + sample.getRockName());
//                outputToClient.writeInt(sample.getSamplePhotos().size());
//                System.out.println("Sent photo album size of " + sample.getSamplePhotos().size());
//                for (int i = 0; i < sample.getSamplePhotos().size() - 1; i++) {
//                    outputToClient.writeUTF(sample.getSamplePhotos().get(i).getPhotoPathName());
//                    outputToClient.writeUTF(sample.getSamplePhotos().get(i).getPhotoDescription());
//                }
                int generalType = sample.getGeneralType();
                System.out.println("set generalType to " + generalType + " from the sample pulled from file");
                outputToClient.writeInt(generalType);
                System.out.println("Sent general type as an int");
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
            e.printStackTrace();
        }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(testSample.toString());


    }
}


