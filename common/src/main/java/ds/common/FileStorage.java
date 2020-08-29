package ds.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class FileStorage {
    private static final Logger log = LogManager.getLogger(FileStorage.class.getName());

    public static <T> void writeObjToFile(T objectToSerialize, String filepath, boolean verbose) throws Exception {
        try (
                FileOutputStream fileOut = new FileOutputStream(filepath);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)
        ) {
            objectOut.writeObject(objectToSerialize);
            if (verbose) {
                log.info("The Object was successfully written to file");
            }
        } catch (FileNotFoundException e) {
            log.warn("File was not found");
            throw new Exception();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public static <T> T readObjFromFile(String filepath, boolean verbose) throws Exception {
        try (
                FileInputStream fileIn = new FileInputStream(filepath);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)
        ) {
            T obj = (T) objectIn.readObject();
            if (verbose) {
                log.info("The Object has been read from file");
            }
            return obj;
        } catch (FileNotFoundException e) {
            log.warn("File was not found");
            throw new Exception();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

}
