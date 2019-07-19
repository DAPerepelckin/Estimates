package sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.prefs.Preferences;

public class PrintException {

    public static void print(Exception ex){
        Preferences user = Preferences.userRoot();
        if(user.getBoolean("logs",true)){
            try {
                PrintStream prntStream = new PrintStream(new File("logs.txt"));
                ex.printStackTrace(prntStream);
                prntStream.close();
            } catch (FileNotFoundException exc) {
                exc.printStackTrace();}
        }else{
            ex.printStackTrace();}
    }

}
