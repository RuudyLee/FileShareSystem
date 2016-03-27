package sample;

/**
 * Created by rudy on 26/03/16.
 */
public class FileData {
    String filename = "";
    String data = "";

    FileData(String filename, String data) {
        this.filename = filename;
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public String getData() {
        return data;
    }
}
