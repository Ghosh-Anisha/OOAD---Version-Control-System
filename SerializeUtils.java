import java.io.*;

public class SerializeUtils {

    public static void storeObjectToFile(Object obj, String filePath) {
        File outFile = new File(filePath);
        try {
            ObjectOutputStream out =
                    new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(obj);
            out.close();
        } catch (IOException excp) {
            System.out.println("Error storing object to file.");
        }
    }

    public static byte[] toByteArray(Object obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            throw new IllegalArgumentException("Internal error serializing commit.");
        }
    }

    public static <T> T deserialize(String fileName, Class<T> type) {
        T obj;
        File inFile = new File(fileName);
        try {
            ObjectInputStream inp =
                    new ObjectInputStream(new FileInputStream(inFile));
            obj = (T) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;
    }

    public static void writeStringToFile(String text, String filepath, boolean appending) {
        try {
            File logFile = new File(filepath);
            BufferedWriter out = new BufferedWriter(new FileWriter(logFile, appending));
            out.write(text);
            out.close();
        } catch (IOException excp) {
            return;
        }
    }

    public static String readStringFromFile(String filepath) {
        File readingFrom = new File(filepath);
        try (BufferedReader br = new BufferedReader(new FileReader(readingFrom))) {
            String line = null;
            String everything = "";
            while ((line = br.readLine()) != null) {
                everything += line;
            }
            return everything;
        } catch (IOException excp) {
            return "error";
        }
    }

}
