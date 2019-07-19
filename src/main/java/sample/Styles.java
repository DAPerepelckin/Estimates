package sample;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Styles {
    public XSSFCellStyle groupNameRow ;
    public XSSFCellStyle closeGroupRow;
    public XSSFCellStyle tableNameRow;
    public XSSFCellStyle italicCells;
    public XSSFCellStyle defaultCells;
    public XSSFCellStyle nameCells;
    public XSSFCellStyle nameItalCells;

    public Styles(XSSFWorkbook workbook) {
        XSSFFont fontName = workbook.createFont();
        XSSFFont onlyItalfont = workbook.createFont();
        XSSFFont defaultFont = workbook.createFont();
        groupNameRow = workbook.createCellStyle();
        closeGroupRow = workbook.createCellStyle();
        tableNameRow = workbook.createCellStyle();
        italicCells = workbook.createCellStyle();
        defaultCells = workbook.createCellStyle();
        nameCells = workbook.createCellStyle();
        nameItalCells = workbook.createCellStyle();

        fontName.setBold(true);
        fontName.setFontName("Calibri");
        fontName.setItalic(true);
        fontName.setFontHeightInPoints((short) 11);
        fontName.setColor(IndexedColors.BLACK.getIndex());


        onlyItalfont.setBold(false);
        onlyItalfont.setFontName("Calibri");
        onlyItalfont.setItalic(true);
        onlyItalfont.setFontHeightInPoints((short) 11);
        onlyItalfont.setColor(IndexedColors.BLACK.getIndex());


        defaultFont.setBold(false);
        defaultFont.setFontName("Calibri");
        defaultFont.setItalic(false);
        defaultFont.setFontHeightInPoints((short) 11);
        defaultFont.setColor(IndexedColors.BLACK.getIndex());


        groupNameRow.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        groupNameRow.setVerticalAlignment(VerticalAlignment.CENTER);
        groupNameRow.setBorderBottom(BorderStyle.THIN);
        groupNameRow.setBorderRight(BorderStyle.THIN);
        groupNameRow.setBorderTop(BorderStyle.THIN);
        groupNameRow.setBorderLeft(BorderStyle.THIN);
        groupNameRow.setFont(fontName);


        closeGroupRow.setVerticalAlignment(VerticalAlignment.CENTER);
        closeGroupRow.setAlignment(HorizontalAlignment.RIGHT);
        closeGroupRow.setBorderBottom(BorderStyle.THIN);
        closeGroupRow.setBorderRight(BorderStyle.THIN);
        closeGroupRow.setBorderTop(BorderStyle.THIN);
        closeGroupRow.setBorderLeft(BorderStyle.THIN);
        closeGroupRow.setFont(fontName);


        tableNameRow.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        tableNameRow.setVerticalAlignment(VerticalAlignment.CENTER);
        tableNameRow.setFont(fontName);


        italicCells.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        italicCells.setVerticalAlignment(VerticalAlignment.CENTER);
        italicCells.setBorderBottom(BorderStyle.THIN);
        italicCells.setBorderRight(BorderStyle.THIN);
        italicCells.setBorderTop(BorderStyle.THIN);
        italicCells.setBorderLeft(BorderStyle.THIN);
        italicCells.setFont(onlyItalfont);


        defaultCells.setAlignment(HorizontalAlignment.CENTER_SELECTION);
        defaultCells.setVerticalAlignment(VerticalAlignment.CENTER);
        defaultCells.setBorderBottom(BorderStyle.THIN);
        defaultCells.setBorderRight(BorderStyle.THIN);
        defaultCells.setBorderTop(BorderStyle.THIN);
        defaultCells.setBorderLeft(BorderStyle.THIN);
        defaultCells.setFont(defaultFont);

        nameCells.setAlignment(HorizontalAlignment.LEFT);
        nameCells.setVerticalAlignment(VerticalAlignment.CENTER);
        nameCells.setBorderBottom(BorderStyle.THIN);
        nameCells.setBorderRight(BorderStyle.THIN);
        nameCells.setBorderTop(BorderStyle.THIN);
        nameCells.setBorderLeft(BorderStyle.THIN);
        nameCells.setFont(defaultFont);

        nameItalCells.setAlignment(HorizontalAlignment.LEFT);
        nameItalCells.setVerticalAlignment(VerticalAlignment.CENTER);
        nameItalCells.setBorderBottom(BorderStyle.THIN);
        nameItalCells.setBorderRight(BorderStyle.THIN);
        nameItalCells.setBorderTop(BorderStyle.THIN);
        nameItalCells.setBorderLeft(BorderStyle.THIN);
        nameItalCells.setFont(onlyItalfont);



    }
}
