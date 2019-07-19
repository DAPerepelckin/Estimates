package sample.saveExcel;

import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.PrintException;
import sample.Styles;
import sample.StylesKC2;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.prefs.Preferences;

public class SaveExcel {
    private XSSFWorkbook book = new XSSFWorkbook();
    private XSSFSheet sheet = book.createSheet("Смета");
    private XSSFSheet sheet1 = book.createSheet("КС-2");
    private XSSFSheet sheet2 = book.createSheet("КС-3");
    private Styles styles = new Styles(book);
    private StylesKC2 styles1 = new StylesKC2(book);
    private Cell cell;
    private Row row;
    private Preferences user = Preferences.userRoot();
    private String podrPosition;
    private String podrFIO;
    private String podrAddress;
    private int podrOKPO;
    private String podrOrgName;
    private String contrPosition;
    private String contrFIO;
    private String contrAddress;
    private int contrOKPO;
    private String contrOrgName;


    public void saveEstimateExcel(Connection conn, int ID, Window owner){
    try {
        ResultSet tablename = conn.createStatement().executeQuery("SELECT TABLE_NAME, TRANSPORT, POGRUZ, CONTRACTORS_ID FROM TABLES WHERE TABLE_ID = " + ID);
        int contractorID = tablename.getInt("CONTRACTORS_ID");
        ResultSet rs1 = conn.createStatement().executeQuery("SELECT ORGANIZATION_NAME, ADDRESS, OKPO, FIO, POSITION FROM PROFILES WHERE PROFILE_ID = " + user.getInt("profile_id", 0));
        podrPosition = rs1.getString("POSITION");
        podrFIO = rs1.getString("FIO");
        podrAddress = rs1.getString("ADDRESS");
        podrOKPO = rs1.getInt("OKPO");
        podrOrgName = rs1.getString("ORGANIZATION_NAME");
        ResultSet rs2 = conn.createStatement().executeQuery("SELECT ORGANIZATION_NAME, POSITION, FIO, ADDRESS, OKPO FROM CONTRACTORS WHERE CONTRACTOR_ID = " + contractorID);
        contrPosition = rs2.getString("POSITION");
        contrFIO = rs2.getString("FIO");
        contrAddress = rs2.getString("ADDRESS");
        contrOKPO = rs2.getInt("OKPO");
        contrOrgName = rs2.getString("ORGANIZATION_NAME");




        sheet.setColumnWidth(0, 1278);
        sheet.setColumnWidth(1, 18260);
        sheet.setColumnWidth(2, 2920);
        sheet.setColumnWidth(3, 2920);
        sheet.setColumnWidth(4, 2920);
        sheet.setColumnWidth(5, 2920);


        row = sheet.createRow(0);


        for (int i = 0; i < 6; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.tableNameRow);
            if (i == 0) {
                cell.setCellValue("Ремонтно-отделочные работы ("+tablename.getString("TABLE_NAME")+")");
            }
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        sheet.createRow(1);

        ResultSet groups = conn.createStatement().executeQuery("SELECT GROUP_ID,GROUP_NAME FROM GROUPS WHERE TABLE_ID = "+ID);
        double sumAll = 0.0;
        while (groups.next()){
            double s = 0.0;
            String groupName = groups.getString("GROUP_NAME");
            row = sheet.createRow(sheet.getLastRowNum() + 1);
            //group name
            for (int i = 0; i < 6; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles.groupNameRow);
                if (i == 0) {
                    cell.setCellValue(groupName);
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 5));

            row = sheet.createRow(sheet.getLastRowNum() + 1);
            cell = row.createCell(0);
            cell.setCellStyle(styles.italicCells);
            cell.setCellValue("№");
            cell = row.createCell(1);
            cell.setCellStyle(styles.nameItalCells);
            cell.setCellValue("Наименование");
            cell = row.createCell(2);
            cell.setCellStyle(styles.italicCells);
            cell.setCellValue("Ед. изм.");
            cell = row.createCell(3);
            cell.setCellStyle(styles.italicCells);
            cell.setCellValue("Кол-во");
            cell = row.createCell(4);
            cell.setCellStyle(styles.italicCells);
            cell.setCellValue("Цена(руб.)");
            cell = row.createCell(5);
            cell.setCellStyle(styles.italicCells);
            cell.setCellValue("Сумма");

            row = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int i = 0; i < 6; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles.italicCells);
                if (i == 0) {
                    cell.setCellValue("Работа");
                }
            }
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 5));

            ResultSet works = conn.createStatement().executeQuery("SELECT WORK_ID, N, COUNT, PRICE FROM MAIN_TABLE WHERE GROUP_ID = "+groups.getInt("GROUP_ID")+" ORDER BY N");
            int index = 1;
            while(works.next()){
                ResultSet rs = conn.createStatement().executeQuery("SELECT WORK_NAME, UNIT FROM WORKS WHERE WORK_ID = "+works.getInt("WORK_ID"));
                String name = rs.getString("WORK_NAME");
                String unit = rs.getString("UNIT");
                double count = works.getDouble("COUNT");
                double price = works.getDouble("PRICE");
                double sum = count*price;
                s+=sum;
                String count1 = new DecimalFormat("#0.00").format(count);
                String price1 = new DecimalFormat("#0.00").format(price);
                String sum1 = new DecimalFormat("#0.00").format(sum);
                row = sheet.createRow(sheet.getLastRowNum() + 1);
                cell = row.createCell(0);
                cell.setCellStyle(styles.italicCells);
                cell.setCellValue(index);
                cell = row.createCell(1);
                cell.setCellStyle(styles.nameCells);
                cell.setCellValue(name);
                cell = row.createCell(2);
                cell.setCellStyle(styles.defaultCells);
                cell.setCellValue(unit);
                cell = row.createCell(3);
                cell.setCellStyle(styles.defaultCells);
                cell.setCellValue(count1);
                cell = row.createCell(4);
                cell.setCellStyle(styles.defaultCells);
                cell.setCellValue(price1);
                cell = row.createCell(5);
                cell.setCellStyle(styles.defaultCells);
                cell.setCellValue(sum1);
                index++;
            }
            sumAll+=s;
            row = sheet.createRow(sheet.getLastRowNum() + 1);
            for (int i = 0; i < 5; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles.closeGroupRow);
                if (i == 0) {
                    cell.setCellValue("Итого по разделу " +groupName+ ": ");
                }
            }

            cell = row.createCell(5);
            cell.setCellStyle(styles.groupNameRow);
            cell.setCellValue(new DecimalFormat("#0.00").format(s));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 4));

        }
        row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < 5; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.closeGroupRow);
            if (i == 0) {
                cell.setCellValue("Итого по отделочным работам: ");
            }
        }
        cell = row.createCell(5);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue(new DecimalFormat("#0.00").format(sumAll) );
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 4));


        row = sheet.createRow(sheet.getLastRowNum()+1);
        for (int i = 0; i < 2; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.closeGroupRow);
            if (i == 0) {
                cell.setCellValue("Транспортные расходы:");
            }
        }
        cell = row.createCell(2);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue("%");

        cell = row.createCell(3);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue((new DecimalFormat("#0.00").format(tablename.getDouble("TRANSPORT"))));

        cell = row.createCell(4);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue(new DecimalFormat("#0.00").format(sumAll/100));

        cell = row.createCell(5);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue(new DecimalFormat("#0.00").format(sumAll/100*tablename.getDouble("TRANSPORT")));

        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));


        row = sheet.createRow(sheet.getLastRowNum()+1);
        for (int i = 0; i < 2; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.closeGroupRow);
            if (i == 0) {
                cell.setCellValue("Погрузочно-разгрузочные расходы:");
            }
        }
        cell = row.createCell(2);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue("%");

        cell = row.createCell(3);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue((new DecimalFormat("#0.00").format(tablename.getDouble("POGRUZ"))));

        cell = row.createCell(4);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue(new DecimalFormat("#0.00").format(sumAll/100));

        cell = row.createCell(5);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue(new DecimalFormat("#0.00").format(sumAll/100*tablename.getDouble("POGRUZ")));

        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

        sumAll = sumAll*(100+tablename.getDouble("POGRUZ")+tablename.getDouble("TRANSPORT"))/100;

        row = sheet.createRow(sheet.getLastRowNum()+1);

        for (int i = 0; i < 5; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles.closeGroupRow);
            if (i == 0) {
                cell.setCellValue("Итого по смете(БЕЗ УЧЕТА МАТЕРИАЛОВ): ");
            }
        }
        cell = row.createCell(5);
        cell.setCellStyle(styles.groupNameRow);
        cell.setCellValue(new DecimalFormat("#0.00").format(sumAll));
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 4));

        saveKS2Excel(conn,ID);
        saveKS3Excel(conn,ID);
        File file;
        if(user.getBoolean("ask",true)){
        DirectoryChooser fileChooser = new DirectoryChooser();
        file = fileChooser.showDialog(owner);}else{
            file = new File(user.get("pathToSave",""));
        }

        if(file!=null){
            File path = new File(file.getAbsolutePath()+"/"+tablename.getString("TABLE_NAME")+".xlsx");
            book.write(new FileOutputStream(path));
            book.close();
            if(user.getBoolean("open",true)){Desktop.getDesktop().open(path);}
        }
        }catch (Exception ex){PrintException.print(ex);}
    }

    private void saveKS2Excel(Connection conn,int ID){
        //ширина столбцов
        {
            sheet1.setColumnWidth(0, 950);
            sheet1.setColumnWidth(1, 950);
            sheet1.setColumnWidth(2, 950);
            sheet1.setColumnWidth(3, 950);
            sheet1.setColumnWidth(4, 850);
            sheet1.setColumnWidth(5, 850);
            sheet1.setColumnWidth(6, 850);
            sheet1.setColumnWidth(7, 850);
            sheet1.setColumnWidth(8, 850);
            sheet1.setColumnWidth(9, 850);
            sheet1.setColumnWidth(10, 850);
            sheet1.setColumnWidth(11, 850);
            sheet1.setColumnWidth(12, 850);
            sheet1.setColumnWidth(13, 850);
            sheet1.setColumnWidth(14, 4768);

            sheet1.setColumnWidth(15, 850);
            sheet1.setColumnWidth(16, 850);
            sheet1.setColumnWidth(17, 850);
            sheet1.setColumnWidth(18, 850);
            sheet1.setColumnWidth(19, 850);
            sheet1.setColumnWidth(20, 850);
            sheet1.setColumnWidth(21, 850);
            sheet1.setColumnWidth(22, 850);
            sheet1.setColumnWidth(23, 850);
            sheet1.setColumnWidth(24, 850);
            sheet1.setColumnWidth(25, 850);
            sheet1.setColumnWidth(26, 850);
            sheet1.setColumnWidth(27, 850);
            sheet1.setColumnWidth(28, 850);
            sheet1.setColumnWidth(29, 850);
            sheet1.setColumnWidth(30, 850);
            sheet1.setColumnWidth(31, 850);

            sheet1.setColumnWidth(32, 450);
            sheet1.setColumnWidth(33, 450);
            sheet1.setColumnWidth(34, 450);
            sheet1.setColumnWidth(35, 450);
            sheet1.setColumnWidth(36, 450);
            sheet1.setColumnWidth(37, 850);
        }

        //шапка
        double sum1 = 0.0;
        String s = "";
        {
            row = sheet1.createRow(0);

            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderLittle);
                cell.setCellValue("Унифицированная форма № КС - 2");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderLittle);
                cell.setCellValue("Утверждена постановлением Госкомстата России");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderLittle);
                cell.setCellValue("от 11.11.99 № 100");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 14; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Форма по ОКУД");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 30));
            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBoldBorder);
                cell.setCellValue("322005");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Инвестор");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(организация, адрес, телефон, факс)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(contrOKPO!=0)cell.setCellValue(contrOKPO);
            }


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 7; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Заказчик (Генподрядчик)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 6));

            for (int i = 7; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
                cell.setCellValue(contrOrgName+" "+contrAddress);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 7, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(contrOKPO!=0)cell.setCellValue(contrOKPO);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(организация, адрес, телефон, факс)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(podrOKPO!=0)cell.setCellValue(podrOKPO);
            }


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 8; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Подрядчик (Субподрядчик)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));


            for (int i = 8; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
                cell.setCellValue(podrOrgName+" "+podrAddress);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 8, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(podrOKPO!=0)cell.setCellValue(podrOKPO);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(организация, адрес, телефон, факс)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));


            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Стройка");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 26));


            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(наименование, адрес)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Обьект");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            for (int i = 2; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 26));


            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(наименование)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 20; i < 30; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Вид деятельности по ОКПД");
            }

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            for (int i = 20; i < 30; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Вид деятельности по ОКПД");
            }

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 20; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Вид деятельности по ОКПД");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 20, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 20; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Договор подряда (контракт)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithBorder);
                cell.setCellValue("номер");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithBorder);
                cell.setCellValue("дата");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Вид операции");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLnBBorder);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 15; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Номер документа");
            }
            for (int i = 19; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Дата составления");
            }
            for (int i = 24; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Отчетный период");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 31));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 15; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Номер документа");
            }
            for (int i = 19; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Дата составления");
            }
            for (int i = 24; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("с");
            }
            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("по");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 15, 18));
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 19, 22));
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 27));
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 3; i < 5; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBigBold);
                cell.setCellValue("АКТ");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 4));

            for (int i = 15; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 15, 18));

            for (int i = 19; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 22));

            for (int i = 24; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBigBold);
                cell.setCellValue("О ПРИЕМКЕ ВЫПОЛНЕННЫХ РАБОТ");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 0; i < 26; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
                cell.setCellValue("Сметная (договорная) стоимость в соответствии с договором подряда (субподряда)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 25));
            double sum = 0.0;
            sum1 = 0.0;
            try {
                ResultSet rs = conn.createStatement().executeQuery("SELECT PRICE,COUNT FROM MAIN_TABLE WHERE TABLE_ID = " + ID);
                ResultSet rs1 = conn.createStatement().executeQuery("SELECT TRANSPORT, POGRUZ FROM TABLES WHERE TABLE_ID = " + ID);

                while (rs.next()) {
                    sum += rs.getDouble("PRICE") * rs.getDouble("COUNT");
                    sum1 += rs.getDouble("PRICE") * rs.getDouble("COUNT");
                }

                sum = sum * (100 + rs1.getDouble("TRANSPORT") + rs1.getDouble("POGRUZ")) / 100;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            s = new DecimalFormat("#0.00").format(sum);

            for (int i = 26; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
                cell.setCellValue(s);
            }

            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 26, 31));

            for (int i = 33; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("руб.");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 33, 37));

            row = sheet1.createRow(sheet1.getLastRowNum() + 1);


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            row.setHeightInPoints(25);
            for (int i = 0; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Номер");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 3));

            for (int i = 4; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Наименование работ");
            }

            for (int i = 19; i < 22; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Номер единичной расценки");
            }

            for (int i = 22; i < 25; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Единица измерения");
            }

            for (int i = 25; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Выполнено работ");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            row.setHeightInPoints(25);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("по порядку");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            for (int i = 2; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("по смете");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

            for (int i = 4; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Наименование работ");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 4, 18));

            for (int i = 19; i < 22; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Номер единичной расценки");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 19, 21));

            for (int i = 22; i < 25; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Единица измерения");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 22, 24));

            for (int i = 25; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("количество");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("цена за единицу, руб.");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            for (int i = 32; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("стоимость, руб.");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("1");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            for (int i = 2; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("2");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

            for (int i = 4; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("3");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 18));

            for (int i = 19; i < 22; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("4");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 21));

            for (int i = 22; i < 25; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("5");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 22, 24));

            for (int i = 25; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("6");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("7");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            for (int i = 32; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("8");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));

        }

        //основной блок
        try {
            ResultSet groups = conn.createStatement().executeQuery("SELECT GROUP_NAME,GROUP_ID FROM GROUPS WHERE TABLE_ID = " + ID);
            int indexPOR = 1;
            while (groups.next()) {
                double groupSum = 0.0;
                String groupName = groups.getString("GROUP_NAME");
                int groupID = groups.getInt("GROUP_ID");
                ResultSet works = conn.createStatement().executeQuery("SELECT WORK_ID, COUNT, PRICE FROM MAIN_TABLE WHERE GROUP_ID = " + groupID + " AND TABLE_ID = " + ID);
                int indexSMET = 1;

                row = sheet1.createRow(sheet1.getLastRowNum() + 1);
                for (int i = 0; i < 38; i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(styles1.centerWithBorderKurs);
                    cell.setCellValue(groupName);
                }
                sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));


                while (works.next()) {
                    ResultSet wrn = conn.createStatement().executeQuery("SELECT WORK_NAME, UNIT FROM WORKS WHERE WORK_ID = " + works.getInt("WORK_ID"));
                    String workName = wrn.getString("WORK_NAME");
                    String unit = wrn.getString("UNIT");
                    double count = works.getDouble("COUNT");
                    double price = works.getDouble("PRICE");
                    double sumWork = count * price;
                    groupSum += sumWork;
                    String suming = new DecimalFormat("#0.00").format(sumWork);
                    String countS = new DecimalFormat("#0.00").format(count);
                    String priceS = new DecimalFormat("#0.00").format(price);

                    {
                        row = sheet1.createRow(sheet1.getLastRowNum() + 1);

                        for (int i = 0; i < 2; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue(indexPOR);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

                        for (int i = 2; i < 4; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue(indexSMET);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

                        for (int i = 4; i < 19; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.leftWithBorderKurs);
                            cell.setCellValue(workName);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 18));

                        for (int i = 19; i < 22; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue("--");
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 21));

                        for (int i = 22; i < 25; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue(unit);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 22, 24));

                        for (int i = 25; i < 28; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue(countS);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

                        for (int i = 28; i < 32; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue(priceS);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


                        for (int i = 32; i < 38; i++) {
                            cell = row.createCell(i);
                            cell.setCellStyle(styles1.centerWithBorderKurs);
                            cell.setCellValue(suming);
                        }
                        sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));
                    }
                    indexPOR++;
                    indexSMET++;
                }
                {
                    row = sheet1.createRow(sheet1.getLastRowNum() + 1);

                    for (int i = 0; i < 2; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue("");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

                    for (int i = 2; i < 4; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue("");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

                    for (int i = 4; i < 19; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.rightWithBorderKurs);
                        cell.setCellValue("Итого по разделу");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 18));

                    for (int i = 19; i < 22; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue("--");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 21));

                    for (int i = 22; i < 25; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue("");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 22, 24));

                    for (int i = 25; i < 28; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue("");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

                    for (int i = 28; i < 32; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue("");
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


                    for (int i = 32; i < 38; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(styles1.centerWithBorderKurs);
                        cell.setCellValue(new DecimalFormat("#0.00").format(groupSum));
                    }
                    sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));
                }

            }

        }catch (Exception ex){
            PrintException.print(ex);}

        //итого по всем разделам
        {
            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            for (int i = 2; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

            for (int i = 4; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithBorderKurs);
                cell.setCellValue("Итого по всем разделам");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 18));

            for (int i = 19; i < 22; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("--");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 21));

            for (int i = 22; i < 25; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 22, 24));

            for (int i = 25; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            for (int i = 32; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(new DecimalFormat("#0.00").format(sum1));
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));
        }

        //Транспортные расходы
        try{
            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            for (int i = 2; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

            for (int i = 4; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithBorderKurs);
                cell.setCellValue("Транспортные расходы");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 18));

            for (int i = 19; i < 22; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("--");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 21));

            for (int i = 22; i < 25; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("%");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 22, 24));
            double c1d = conn.createStatement().executeQuery("SELECT TRANSPORT FROM TABLES WHERE TABLE_ID = " + ID).getDouble("TRANSPORT");
            String c1 = new DecimalFormat("#0.00").format(c1d);
            for (int i = 25; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(c1);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(new DecimalFormat("#0.00").format(sum1 /100));
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            for (int i = 32; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(new DecimalFormat("#0.00").format(sum1 /100* c1d));
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));
        }catch (Exception ex){PrintException.print(ex);}

        //Погрузочно-разгрузочные расходы
        try{
            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

            for (int i = 2; i < 4; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 3));

            for (int i = 4; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithBorderKurs);
                cell.setCellValue("Погрузочно-разгрузочые расходы");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 4, 18));

            for (int i = 19; i < 22; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("--");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 21));

            for (int i = 22; i < 25; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue("%");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 22, 24));
            double c2d = conn.createStatement().executeQuery("SELECT POGRUZ FROM TABLES WHERE TABLE_ID = " + ID).getDouble("POGRUZ");
            String c2 = new DecimalFormat("#0.00").format(c2d);
            for (int i = 25; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(c2);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 25, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(new DecimalFormat("#0.00").format(sum1 /100));
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            for (int i = 32; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKurs);
                cell.setCellValue(new DecimalFormat("#0.00").format(sum1 /100* c2d));
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 32, 37));
        }catch (Exception ex){PrintException.print(ex);}

        //Итого по акту
        {
            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 20; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderBoldItal);
                cell.setCellValue("Итого по акту:");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKursBold);
                cell.setCellValue(s);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 20; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderBoldItal);
                cell.setCellValue("без НДС");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKursBold);
                cell.setCellValue("0,00");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 20; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderBoldItal);
                cell.setCellValue("Всего по акту");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKursBold);
                cell.setCellValue(s);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));
        }

        row = sheet1.createRow(sheet1.getLastRowNum() + 1);
        row = sheet1.createRow(sheet1.getLastRowNum() + 1);


        //1 подпись
        {
            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            row.setHeightInPoints(30);

            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Сдал");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(podrPosition);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(podrFIO);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(должность)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(подпись)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(расшифровка)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 3; i < 6; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("М.П");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 5));
        }


        //2 подпись
        {
            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            row.setHeightInPoints(30);
            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Принял");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(contrPosition);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue("");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(contrFIO);
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);
            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(должность)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(подпись)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(расшифровка)");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet1.createRow(sheet1.getLastRowNum() + 1);

            for (int i = 3; i < 6; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("М.П.");
            }
            sheet1.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 5));
        }


    }

    private void saveKS3Excel(Connection conn,int ID){
        //ширина столбцов
        {
            sheet2.setColumnWidth(0, 950);
            sheet2.setColumnWidth(1, 950);
            sheet2.setColumnWidth(2, 950);
            sheet2.setColumnWidth(3, 950);
            sheet2.setColumnWidth(4, 850);
            sheet2.setColumnWidth(5, 850);
            sheet2.setColumnWidth(6, 850);
            sheet2.setColumnWidth(7, 850);
            sheet2.setColumnWidth(8, 850);
            sheet2.setColumnWidth(9, 850);
            sheet2.setColumnWidth(10, 850);
            sheet2.setColumnWidth(11, 850);
            sheet2.setColumnWidth(12, 850);
            sheet2.setColumnWidth(13, 850);
            sheet2.setColumnWidth(14, 4768);

            sheet2.setColumnWidth(15, 850);
            sheet2.setColumnWidth(16, 850);
            sheet2.setColumnWidth(17, 850);
            sheet2.setColumnWidth(18, 850);
            sheet2.setColumnWidth(19, 850);
            sheet2.setColumnWidth(20, 850);
            sheet2.setColumnWidth(21, 850);
            sheet2.setColumnWidth(22, 850);
            sheet2.setColumnWidth(23, 850);
            sheet2.setColumnWidth(24, 850);
            sheet2.setColumnWidth(25, 850);
            sheet2.setColumnWidth(26, 850);
            sheet2.setColumnWidth(27, 850);
            sheet2.setColumnWidth(28, 850);
            sheet2.setColumnWidth(29, 850);
            sheet2.setColumnWidth(30, 850);
            sheet2.setColumnWidth(31, 850);

            sheet2.setColumnWidth(32, 450);
            sheet2.setColumnWidth(33, 450);
            sheet2.setColumnWidth(34, 450);
            sheet2.setColumnWidth(35, 450);
            sheet2.setColumnWidth(36, 450);
            sheet2.setColumnWidth(37, 850);
        }

        //шапка
        double sum1 = 0.0;
        String s = "";
        {
            row = sheet2.createRow(0);

            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderLittle);
                cell.setCellValue("Унифицированная форма № КС - 3");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderLittle);
                cell.setCellValue("Утверждена постановлением Госкомстата России");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderLittle);
                cell.setCellValue("от 11.11.99 № 100");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 14; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Форма по ОКУД");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 30));
            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBoldBorder);
                cell.setCellValue("322001");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Инвестор");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(организация, адрес, телефон, факс)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(contrOKPO!=0)cell.setCellValue(contrOKPO);
            }


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 0; i < 7; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Заказчик (Генподрядчик)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 6));

            for (int i = 7; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
                cell.setCellValue(contrOrgName+" "+contrAddress);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 7, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(contrOKPO!=0)cell.setCellValue(contrOKPO);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(организация, адрес, телефон, факс)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(podrOKPO!=0)cell.setCellValue(podrOKPO);
            }


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 0; i < 8; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Подрядчик (Субподрядчик)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 7));

            for (int i = 8; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
                cell.setCellValue(podrOrgName+" "+ podrAddress);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 8, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("по ОКПО");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                if(podrOKPO!=0)cell.setCellValue(podrOKPO);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(организация, адрес, телефон, факс)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));


            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Стройка");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBottomBorder);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 26));


            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 31, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 9; i < 20; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(наименование, адрес)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 9, 19));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 20; i < 27; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Договор подряда (контракт)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 26));

            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithBorder);
                cell.setCellValue("номер");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithBorder);
                cell.setCellValue("дата");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLBorder);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 27; i < 31; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorder);
                cell.setCellValue("Вид операции");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 27, 30));

            for (int i = 31; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerRnLnBBorder);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 31, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            for (int i = 15; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Номер документа");
            }
            for (int i = 19; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Дата составления");
            }
            for (int i = 24; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Отчетный период");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 31));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 15; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Номер документа");
            }
            for (int i = 19; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("Дата составления");
            }
            for (int i = 24; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("с");
            }
            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThin);
                cell.setCellValue("по");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 15, 18));
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 19, 22));
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 27));
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 3; i < 7; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBigBold);
                cell.setCellValue("СПРАВКА");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 6));

            for (int i = 15; i < 19; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 15, 18));

            for (int i = 19; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 19, 22));

            for (int i = 24; i < 28; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 24, 27));

            for (int i = 28; i < 32; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThick);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 28, 31));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            for (int i = 0; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftBigBold);
                cell.setCellValue("О СТОИМОСТИ ВЫПОЛНЕННЫХ РАБОТ И ЗАТРАТ");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            row.setHeightInPoints(25);
            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Номер по порядку");
            }


            for (int i = 2; i < 15; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Наименование пусковых комплексов, этапов, объектов, видов выполненных работ, оборудования, затрат");
            }

            for (int i = 15; i < 17; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Код");
            }


            for (int i = 17; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Стоимость выполненных работ и затрат, руб.");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 17, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            row.setHeightInPoints(25);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Номер по порядку");
            }

            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum()-1, row.getRowNum(), 0, 1));

            for (int i = 2; i < 15; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Наименование пусковых комплексов, этапов, объектов, видов выполненных работ, оборудования, затрат");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 2, 14));

            for (int i = 15; i < 17; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("Код");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum() - 1, row.getRowNum(), 15, 16));


            for (int i = 17; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("с начала проведения работ");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 17, 22));

            for (int i = 23; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("с начала года");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 28));


            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("в том числе за отчетный период");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("1");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));


            for (int i = 2; i < 15; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("2");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 14));

            for (int i = 15; i < 17; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("3");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 15, 16));

            for (int i = 17; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("4");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 17, 22));

            for (int i = 23; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("5");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
                cell.setCellValue("6");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));

        }

        //основной блок
        {

        row = sheet2.createRow(sheet2.getLastRowNum() + 1);
        row.setHeightInPoints(25);

        for (int i = 0; i < 2; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles1.centerAllThinDef);
            cell.setCellValue("1");
        }
        sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));


        for (int i = 2; i < 15; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles1.leftAllThinDef);
            cell.setCellValue("Всего работ и затрат, включаемых в стоимость работ");
        }
        sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 14));

        for (int i = 15; i < 17; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles1.centerAllThinDef);
            cell.setCellValue("");
        }
        sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 15, 16));

        double sum = 0.0;
        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT PRICE,COUNT FROM MAIN_TABLE WHERE TABLE_ID = " + ID);
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT TRANSPORT, POGRUZ FROM TABLES WHERE TABLE_ID = " + ID);

            while (rs.next()) {
                sum += rs.getDouble("PRICE") * rs.getDouble("COUNT");
                sum1 += rs.getDouble("PRICE") * rs.getDouble("COUNT");
            }

            sum = sum * (100 + rs1.getDouble("TRANSPORT") + rs1.getDouble("POGRUZ")) / 100;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        s = new DecimalFormat("#0.00").format(sum);


        for (int i = 17; i < 23; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles1.centerAllThinDef);
            cell.setCellValue(s);
        }
        sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 17, 22));

        for (int i = 23; i < 29; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles1.centerAllThinDef);
            cell.setCellValue(s);
        }
        sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 28));

        for (int i = 29; i < 38; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(styles1.centerAllThinDef);
            cell.setCellValue(s);
        }
        sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));

            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            for (int i = 0; i < 2; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));


            for (int i = 2; i < 15; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 14));

            for (int i = 15; i < 17; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 15, 16));

            for (int i = 17; i < 23; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 17, 22));

            for (int i = 23; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerAllThinDef);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));


        }




        //Итого по акту
        {
            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 20; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderBoldItal);
                cell.setCellValue("Итого по акту:");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKursBold);
                cell.setCellValue(s);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 20; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderBoldItal);
                cell.setCellValue("без НДС");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKursBold);
                cell.setCellValue("0,00");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 20; i < 29; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.rightWithoutBorderBoldItal);
                cell.setCellValue("Всего по акту");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 20, 28));

            for (int i = 29; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerWithBorderKursBold);
                cell.setCellValue(s);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 29, 37));
        }

        row = sheet2.createRow(sheet2.getLastRowNum() + 1);
        row = sheet2.createRow(sheet2.getLastRowNum() + 1);


        //1 подпись
        {
            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            row.setHeightInPoints(30);

            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Заказчик");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(contrPosition);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(contrFIO);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(должность)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(подпись)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(расшифровка)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            for (int i = 3; i < 6; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("М.П");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 5));
        }


        //2 подпись
        {
            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            row.setHeightInPoints(30);
            for (int i = 0; i < 3; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("Подрядчик");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 2));

            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(podrPosition);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue("");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerBottom);
                cell.setCellValue(podrFIO);
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);
            for (int i = 3; i < 12; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(должность)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 11));


            for (int i = 14; i < 21; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(подпись)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 14, 20));


            for (int i = 23; i < 38; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.centerLittle);
                cell.setCellValue("(расшифровка)");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 23, 37));


            row = sheet2.createRow(sheet2.getLastRowNum() + 1);

            for (int i = 3; i < 6; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(styles1.leftWithoutBorder);
                cell.setCellValue("М.П.");
            }
            sheet2.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 5));
        }


    }

}