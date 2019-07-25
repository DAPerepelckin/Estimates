package sample;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;

public class PrintException {

    public static void print(Exception ex){
        Preferences user = Preferences.userRoot();
        if(user.getBoolean("logs",true)){
            try {
                FileWriter fileWriter = new FileWriter(new File("logs.txt"),true);
                PrintWriter prntStream = new PrintWriter(fileWriter);
                prntStream.println("");
                prntStream.println("");
                prntStream.println("");
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                prntStream.println("      ///"+format.format(date)+"///   ");
                prntStream.println(ex);
                for(int i=0;i<ex.getStackTrace().length;i++) {
                    prntStream.println(ex.getStackTrace()[i]);
                }
                prntStream.close();
            } catch (IOException exc) {
                exc.printStackTrace();}
        }else{
            ex.printStackTrace();}
    }

}
