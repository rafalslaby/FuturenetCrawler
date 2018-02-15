import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

class Logger {
    static void initializeLogFile() throws IOException  {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                .format(Calendar.getInstance().getTime());
        if(Files.notExists(Paths.get("log"))) {
            Files.createDirectory(Paths.get("log"));
        }
        String fileName ="log/" + timeStamp + ".txt";
        FileOutputStream f = new FileOutputStream(fileName);
        System.setErr(new PrintStream(f));
    }

    static void log(String message){
        System.err.println(LocalDateTime.now() + ":  " + message);
    }
}
