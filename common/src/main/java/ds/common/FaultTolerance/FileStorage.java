package ds.common.FaultTolerance;

import java.io.*;
import java.nio.file.Path;

public class FileStorage {
    Path path;
    String filepath;

    public FileStorage() {
        filepath = path.toString();
    }

    public <T> void writeObjToFile(T objectToSerialize) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(objectToSerialize);
            objectOut.close();
            System.out.println("The Object was succesfully written to a file");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T> T readObjFromFile() {
        try {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            T obj = (T) objectIn.readObject();
            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
}
