
package tolt.server.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.ByteBuffer;

import tolt.server.service.logging.Logging;

public class Idgen {

    private static final String indexPath = "./db/idx/";

    public static long generateId (String category) { try {

        long id = -1;

        File indexFile = new File(indexPath + category + ".idx");
        if (indexFile.exists())
            id = ByteBuffer.wrap(Files.readAllBytes(indexFile.toPath())).getLong();
        else
            indexFile.getParentFile().mkdirs();

        id += 1;

        Files.write(indexFile.toPath(), ByteBuffer.allocate(8).putLong(id).array());

        return id;

    } catch (Exception e) {

        Logging.stackErr(e);
        return -1;
    } }
}
