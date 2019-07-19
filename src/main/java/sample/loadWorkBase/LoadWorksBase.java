package sample.loadWorkBase;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.sqlite.JDBC;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadWorksBase {
    void load(){
        try {



            Class.forName("org.sqlite.JDBC");
            String URL = "jdbc:sqlite:C:\\Users\\DAPer\\IdeaProjects\\Smety 2.0\\src\\sample\\smety.sqlite";
            Connection conn = DriverManager.getConnection(URL);

            Statement st = conn.createStatement();




            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream("C:\\Users\\DAPer\\IdeaProjects\\Smety 2.0\\src\\sample\\input.xlsx"));
            XSSFSheet sheet = workbook.getSheet("Смета");
            for(int i=1;i<479;i++){
                Row row = sheet.getRow(i-1);
                String N = row.getCell(0).getStringCellValue();
                String work = row.getCell(1).getStringCellValue();
                String unit = row.getCell(2).getStringCellValue();
                st.executeUpdate("INSERT INTO main.WORKS(WORK_ID,N,WORK_NAME, DEF_PRICE, UNIT) VALUES("+i+", '"+N+"', '"+work+"', 0.0, '"+unit+"')");
            }
            workbook.close();


        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
