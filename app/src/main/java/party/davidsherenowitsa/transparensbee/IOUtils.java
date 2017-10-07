package party.davidsherenowitsa.transparensbee;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class IOUtils {
    public static String slurpInputStreamUTF8(InputStream is) throws IOException {
        Reader reader = new InputStreamReader(is, "UTF-8");
        try {
            StringBuilder result = new StringBuilder();
            char[] buf = new char[1024];
            int read;
            while ((read = reader.read(buf)) != -1) {
                result.append(buf, 0, read);
            }
            return result.toString();
        } finally {
            reader.close();
        }
    }
}
