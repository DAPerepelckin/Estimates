package sample;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class StylesKC2 {
    public XSSFCellStyle rightWithoutBorder;
    public XSSFCellStyle centerWithBoldBorder;
    public XSSFCellStyle leftWithoutBorder;
    public XSSFCellStyle leftBottomBorder;
    public XSSFCellStyle centerRnLBorder;
    public XSSFCellStyle centerLittle;
    public XSSFCellStyle rightWithBorder;
    public XSSFCellStyle centerRnLnBBorder;
    public XSSFCellStyle centerAllThin;
    public XSSFCellStyle centerAllThick;
    public XSSFCellStyle leftBigBold;
    public XSSFCellStyle centerAllThinDef;
    public XSSFCellStyle rightWithoutBorderLittle;
    public XSSFCellStyle rightWithBorderKurs;
    public XSSFCellStyle leftWithBorderKurs;
    public XSSFCellStyle centerWithBorderKurs;
    public XSSFCellStyle rightWithoutBorderBoldItal;
    public XSSFCellStyle centerWithBorderKursBold;
    public XSSFCellStyle leftAllThinDef;
    public XSSFCellStyle centerBottom;

    public StylesKC2(XSSFWorkbook workbook){


        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setBold(false);
        defaultFont.setFontName("Times New Roman");
        defaultFont.setItalic(false);
        defaultFont.setFontHeightInPoints((short) 10);
        defaultFont.setColor(IndexedColors.BLACK.getIndex());

        XSSFFont littleFont = workbook.createFont();
        littleFont.setBold(false);
        littleFont.setFontName("Times New Roman");
        littleFont.setItalic(false);
        littleFont.setFontHeightInPoints((short) 8);
        littleFont.setColor(IndexedColors.BLACK.getIndex());

        XSSFFont bigBoldFont = workbook.createFont();
        bigBoldFont.setBold(true);
        bigBoldFont.setFontName("Times New Roman");
        bigBoldFont.setItalic(false);
        bigBoldFont.setFontHeightInPoints((short) 12);
        bigBoldFont.setColor(IndexedColors.BLACK.getIndex());

        XSSFFont kursFont = workbook.createFont();
        kursFont.setBold(false);
        kursFont.setFontName("Times New Roman");
        kursFont.setItalic(true);
        kursFont.setFontHeightInPoints((short) 10);
        kursFont.setColor(IndexedColors.BLACK.getIndex());

        XSSFFont boldItalFont = workbook.createFont();
        boldItalFont.setBold(true);
        boldItalFont.setFontName("Times New Roman");
        boldItalFont.setItalic(true);
        boldItalFont.setFontHeightInPoints((short) 10);
        boldItalFont.setColor(IndexedColors.BLACK.getIndex());

        rightWithoutBorder = workbook.createCellStyle();
        rightWithoutBorder.setFont(defaultFont);
        rightWithoutBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        rightWithoutBorder.setAlignment(HorizontalAlignment.RIGHT);
        rightWithoutBorder.setBorderLeft(BorderStyle.NONE);
        rightWithoutBorder.setBorderTop(BorderStyle.NONE);
        rightWithoutBorder.setBorderRight(BorderStyle.NONE);
        rightWithoutBorder.setBorderBottom(BorderStyle.NONE);

        rightWithoutBorderLittle = workbook.createCellStyle();
        rightWithoutBorderLittle.setFont(littleFont);
        rightWithoutBorderLittle.setVerticalAlignment(VerticalAlignment.CENTER);
        rightWithoutBorderLittle.setAlignment(HorizontalAlignment.RIGHT);
        rightWithoutBorderLittle.setBorderLeft(BorderStyle.NONE);
        rightWithoutBorderLittle.setBorderTop(BorderStyle.NONE);
        rightWithoutBorderLittle.setBorderRight(BorderStyle.NONE);
        rightWithoutBorderLittle.setBorderBottom(BorderStyle.NONE);

        rightWithoutBorderBoldItal = workbook.createCellStyle();
        rightWithoutBorderBoldItal.setFont(boldItalFont);
        rightWithoutBorderBoldItal.setVerticalAlignment(VerticalAlignment.CENTER);
        rightWithoutBorderBoldItal.setAlignment(HorizontalAlignment.RIGHT);
        rightWithoutBorderBoldItal.setBorderLeft(BorderStyle.NONE);
        rightWithoutBorderBoldItal.setBorderTop(BorderStyle.NONE);
        rightWithoutBorderBoldItal.setBorderRight(BorderStyle.NONE);
        rightWithoutBorderBoldItal.setBorderBottom(BorderStyle.NONE);


        rightWithBorder = workbook.createCellStyle();
        rightWithBorder.setFont(defaultFont);
        rightWithBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        rightWithBorder.setAlignment(HorizontalAlignment.RIGHT);
        rightWithBorder.setBorderLeft(BorderStyle.THIN);
        rightWithBorder.setBorderTop(BorderStyle.THIN);
        rightWithBorder.setBorderRight(BorderStyle.THIN);
        rightWithBorder.setBorderBottom(BorderStyle.THIN);


        rightWithBorderKurs = workbook.createCellStyle();
        rightWithBorderKurs.setFont(kursFont);
        rightWithBorderKurs.setVerticalAlignment(VerticalAlignment.CENTER);
        rightWithBorderKurs.setAlignment(HorizontalAlignment.RIGHT);
        rightWithBorderKurs.setBorderLeft(BorderStyle.THIN);
        rightWithBorderKurs.setBorderTop(BorderStyle.THIN);
        rightWithBorderKurs.setBorderRight(BorderStyle.THIN);
        rightWithBorderKurs.setBorderBottom(BorderStyle.THIN);

        leftWithoutBorder = workbook.createCellStyle();
        leftWithoutBorder.setFont(defaultFont);
        leftWithoutBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        leftWithoutBorder.setAlignment(HorizontalAlignment.LEFT);
        leftWithoutBorder.setBorderLeft(BorderStyle.NONE);
        leftWithoutBorder.setBorderTop(BorderStyle.NONE);
        leftWithoutBorder.setBorderRight(BorderStyle.NONE);
        leftWithoutBorder.setBorderBottom(BorderStyle.NONE);

        leftWithBorderKurs = workbook.createCellStyle();
        leftWithBorderKurs.setFont(kursFont);
        leftWithBorderKurs.setVerticalAlignment(VerticalAlignment.CENTER);
        leftWithBorderKurs.setAlignment(HorizontalAlignment.LEFT);
        leftWithBorderKurs.setBorderLeft(BorderStyle.THIN);
        leftWithBorderKurs.setBorderTop(BorderStyle.THIN);
        leftWithBorderKurs.setBorderRight(BorderStyle.THIN);
        leftWithBorderKurs.setBorderBottom(BorderStyle.THIN);


        leftAllThinDef = workbook.createCellStyle();
        leftAllThinDef.setFont(defaultFont);
        leftAllThinDef.setVerticalAlignment(VerticalAlignment.CENTER);
        leftAllThinDef.setAlignment(HorizontalAlignment.LEFT);
        leftAllThinDef.setBorderLeft(BorderStyle.THIN);
        leftAllThinDef.setBorderTop(BorderStyle.THIN);
        leftAllThinDef.setBorderRight(BorderStyle.THIN);
        leftAllThinDef.setBorderBottom(BorderStyle.THIN);


        leftBottomBorder = workbook.createCellStyle();
        leftBottomBorder.setFont(defaultFont);
        leftBottomBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        leftBottomBorder.setAlignment(HorizontalAlignment.LEFT);
        leftBottomBorder.setBorderLeft(BorderStyle.NONE);
        leftBottomBorder.setBorderTop(BorderStyle.NONE);
        leftBottomBorder.setBorderRight(BorderStyle.NONE);
        leftBottomBorder.setBorderBottom(BorderStyle.THIN);


        leftBigBold = workbook.createCellStyle();
        leftBigBold.setFont(bigBoldFont);
        leftBigBold.setVerticalAlignment(VerticalAlignment.CENTER);
        leftBigBold.setAlignment(HorizontalAlignment.LEFT);
        leftBigBold.setBorderLeft(BorderStyle.NONE);
        leftBigBold.setBorderTop(BorderStyle.NONE);
        leftBigBold.setBorderRight(BorderStyle.NONE);
        leftBigBold.setBorderBottom(BorderStyle.NONE);

        centerWithBoldBorder = workbook.createCellStyle();
        centerWithBoldBorder.setFont(defaultFont);
        centerWithBoldBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        centerWithBoldBorder.setAlignment(HorizontalAlignment.CENTER);
        centerWithBoldBorder.setBorderBottom(BorderStyle.THIN);
        centerWithBoldBorder.setBorderRight(BorderStyle.THICK);
        centerWithBoldBorder.setBorderTop(BorderStyle.THICK);
        centerWithBoldBorder.setBorderLeft(BorderStyle.THICK);

        centerRnLBorder = workbook.createCellStyle();
        centerRnLBorder.setFont(defaultFont);
        centerRnLBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        centerRnLBorder.setAlignment(HorizontalAlignment.CENTER);
        centerRnLBorder.setBorderBottom(BorderStyle.THIN);
        centerRnLBorder.setBorderRight(BorderStyle.THICK);
        centerRnLBorder.setBorderLeft(BorderStyle.THICK);

        centerRnLnBBorder = workbook.createCellStyle();
        centerRnLnBBorder.setFont(defaultFont);
        centerRnLnBBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        centerRnLnBBorder.setAlignment(HorizontalAlignment.CENTER);
        centerRnLnBBorder.setBorderBottom(BorderStyle.THICK);
        centerRnLnBBorder.setBorderRight(BorderStyle.THICK);
        centerRnLnBBorder.setBorderLeft(BorderStyle.THICK);

        centerLittle = workbook.createCellStyle();
        centerLittle.setFont(littleFont);
        centerLittle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerLittle.setAlignment(HorizontalAlignment.CENTER);
        centerLittle.setBorderBottom(BorderStyle.NONE);
        centerLittle.setBorderRight(BorderStyle.NONE);
        centerLittle.setBorderLeft(BorderStyle.NONE);
        centerLittle.setBorderTop(BorderStyle.NONE);

        centerAllThin = workbook.createCellStyle();
        centerAllThin.setFont(littleFont);
        centerAllThin.setVerticalAlignment(VerticalAlignment.CENTER);
        centerAllThin.setAlignment(HorizontalAlignment.CENTER);
        centerAllThin.setBorderBottom(BorderStyle.THIN);
        centerAllThin.setBorderRight(BorderStyle.THIN);
        centerAllThin.setBorderLeft(BorderStyle.THIN);
        centerAllThin.setBorderTop(BorderStyle.THIN);

        centerAllThinDef = workbook.createCellStyle();
        centerAllThinDef.setFont(defaultFont);
        centerAllThinDef.setVerticalAlignment(VerticalAlignment.CENTER);
        centerAllThinDef.setAlignment(HorizontalAlignment.CENTER);
        centerAllThinDef.setBorderBottom(BorderStyle.THIN);
        centerAllThinDef.setBorderRight(BorderStyle.THIN);
        centerAllThinDef.setBorderLeft(BorderStyle.THIN);
        centerAllThinDef.setBorderTop(BorderStyle.THIN);
        centerAllThinDef.setWrapText(true);


        centerWithBorderKurs = workbook.createCellStyle();
        centerWithBorderKurs.setFont(kursFont);
        centerWithBorderKurs.setVerticalAlignment(VerticalAlignment.CENTER);
        centerWithBorderKurs.setAlignment(HorizontalAlignment.CENTER);
        centerWithBorderKurs.setBorderLeft(BorderStyle.THIN);
        centerWithBorderKurs.setBorderTop(BorderStyle.THIN);
        centerWithBorderKurs.setBorderRight(BorderStyle.THIN);
        centerWithBorderKurs.setBorderBottom(BorderStyle.THIN);

        centerWithBorderKursBold = workbook.createCellStyle();
        centerWithBorderKursBold.setFont(boldItalFont);
        centerWithBorderKursBold.setVerticalAlignment(VerticalAlignment.CENTER);
        centerWithBorderKursBold.setAlignment(HorizontalAlignment.CENTER);
        centerWithBorderKursBold.setBorderLeft(BorderStyle.THIN);
        centerWithBorderKursBold.setBorderTop(BorderStyle.THIN);
        centerWithBorderKursBold.setBorderRight(BorderStyle.THIN);
        centerWithBorderKursBold.setBorderBottom(BorderStyle.THIN);


        centerAllThick = workbook.createCellStyle();
        centerAllThick.setFont(defaultFont);
        centerAllThick.setVerticalAlignment(VerticalAlignment.CENTER);
        centerAllThick.setAlignment(HorizontalAlignment.CENTER);
        centerAllThick.setBorderBottom(BorderStyle.THICK);
        centerAllThick.setBorderRight(BorderStyle.THICK);
        centerAllThick.setBorderLeft(BorderStyle.THICK);
        centerAllThick.setBorderTop(BorderStyle.THICK);


        centerBottom = workbook.createCellStyle();
        centerBottom.setFont(defaultFont);
        centerBottom.setVerticalAlignment(VerticalAlignment.CENTER);
        centerBottom.setAlignment(HorizontalAlignment.CENTER);
        centerBottom.setBorderBottom(BorderStyle.THIN);
        centerBottom.setBorderRight(BorderStyle.NONE);
        centerBottom.setBorderLeft(BorderStyle.NONE);
        centerBottom.setBorderTop(BorderStyle.NONE);
        centerBottom.setWrapText(true);



    }
}
