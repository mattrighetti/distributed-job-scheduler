package ds.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileStorage {
    private final String filepath;

    private static final Logger log = LogManager.getLogger(FileStorage.class.getName());

    public FileStorage() {
        Path path = Paths.get("./");
        this.filepath = path.toString();
    }

    public <T> void writeObjToFile(T objectToSerialize) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(objectToSerialize);
            objectOut.close();
            log.info("The Object was successfully written to file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T> T readObjFromFile() {
        try (
                FileInputStream fileIn = new FileInputStream(filepath);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
            T obj = (T) objectIn.readObject();
            log.info("The Object has been read from file");
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
