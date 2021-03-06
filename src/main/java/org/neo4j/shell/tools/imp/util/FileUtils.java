package org.neo4j.shell.tools.imp.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mh on 12.07.13.
 */
public class FileUtils {

    public static CountingReader readerFor(String fileName) throws IOException {
        if (fileName==null) return null;
        if (fileName.startsWith("http") || fileName.startsWith("file:")) {
            URL url = new URL(fileName);
            URLConnection conn = url.openConnection();
            long size = conn.getContentLengthLong();
            Reader reader = new InputStreamReader(url.openStream(),"UTF-8");
            return new CountingReader(reader,size);
        }
        File file = new File(fileName);
        if (!file.exists() || !file.isFile() || !file.canRead()) return null;
        return new CountingReader(file);
    }
}
