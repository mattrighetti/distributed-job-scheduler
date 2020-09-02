package ds.common;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.util.Objects;

import static ds.common.Utils.Strings.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapDaoTest {

    @Test
    public void writeReadTest() {
        MapDao<String, String> mapDao = new MapDao<>("./target/mapDao1");
        mapDao.put("val1", NULL.toString());
        mapDao.put("val2", "val2");
        mapDao.put("val3", NULL.toString());
        mapDao.put("val2", "val3");

        MapDao<String, String> mapDaoRead = new MapDao<>("./target/mapDao1");

        assertTrue(mapDaoRead.containsKey("val1"));
        assertTrue(mapDaoRead.containsKey("val2"));
        assertTrue(mapDaoRead.containsKey("val3"));
        assertEquals(NULL.toString(), mapDaoRead.get("val1"));
        assertEquals(NULL.toString(), mapDaoRead.get("val3"));
        assertEquals("val3", mapDaoRead.get("val2"));
    }

    @AfterClass
    public static void clean() {
        File dir = new File("./target/");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().contains("file")) {
                System.out.println("Deleting " + file.getName());
                file.delete();
            }
        }
    }

}
