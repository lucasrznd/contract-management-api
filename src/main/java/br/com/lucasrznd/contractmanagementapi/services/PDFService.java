package br.com.lucasrznd.contractmanagementapi.services;

import br.com.lucasrznd.contractmanagementapi.entities.Contract;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.time.LocalDate;

import static br.com.lucasrznd.contractmanagementapi.utils.StringUtils.*;
import static com.itextpdf.kernel.geom.PageSize.A4;
import static com.itextpdf.layout.borders.Border.NO_BORDER;
import static com.itextpdf.layout.properties.TextAlignment.*;

@Service
public class PDFService {

    @Value("${pdf.target}")
    private String pdfTarget;

    public String generatePDF(Contract contract) throws FileNotFoundException {
        String path = generateFileName(contract, pdfTarget);
        PdfWriter pdfWriter = new PdfWriter(path);

        // Create the document
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(A4);
        Document document = new Document(pdfDocument);

        // Add image to the document header
        document.add(headerImage("src/main/resources/images/logo.png", pdfDocument));

        // Columns
        float twoCol = 285F;
        float twoCol150 = twoCol + 150F;
        float threeCol = 190F;
        float[] oneTwo = {threeCol + 125F, threeCol * 2};
        float[] twoColumnWidth = {twoCol150, twoCol};
        float[] threeColWidth = {threeCol, threeCol, threeCol};
        float[] fullWidth = {threeCol * 3};
        Paragraph onesp = new Paragraph("\n");

        Table table = new Table(twoColumnWidth);
        table.addCell(new Cell().add(new Paragraph("CONTRATO").setFontSize(20F)).setBorder(NO_BORDER).setBold());
        Table nestedTable = new Table(new float[]{twoCol / 2, twoCol / 2});
        nestedTable.addCell(getHeaderTextCell("Ordem de Propaganda").setTextAlignment(LEFT));
        nestedTable.addCell(getHeaderTextCellValue(String.valueOf(contract.getAdvertisingOrder())).setTextAlignment(RIGHT));
        nestedTable.addCell(getHeaderTextCell("Data").setTextAlignment(LEFT));
        nestedTable.addCell(getHeaderTextCellValue(formatDateToLocal(LocalDate.now())).setTextAlignment(RIGHT));

        table.addCell(new Cell().add(nestedTable).setBorder(NO_BORDER));
        document.add(table);

        Border border = new SolidBorder(ColorConstants.GRAY, 1F / 2F);
        Table divider = new Table(fullWidth);
        divider.setBorder(border);
        document.add(onesp);
        document.add(divider);
        document.add(onesp);

        Table twoColTable = new Table(twoColumnWidth);
        twoColTable.addCell(getBillingShippingCell("Dados do Contrato"));
        twoColTable.addCell(getBillingShippingCell("Dados do Anunciante"));
        document.add(twoColTable.setMarginBottom(10F));

        Table twoColTable2 = new Table(twoColumnWidth);
        twoColTable2.addCell(getCell10Left("Data de Ínicio", true));
        twoColTable2.addCell(getCell10Left("Razão Social", true));
        twoColTable2.addCell(getCell10Left(formatDateToLocal(contract.getStartDate()), false));
        twoColTable2.addCell(getCell10Left(contract.getClientCompany().getTradeName().toUpperCase(), false));
        document.add(twoColTable2);

        Table twoColTable3 = new Table(twoColumnWidth);
        twoColTable3.addCell(getCell10Left("Data de Término", true));
        if (contract.getClientCompany().getRegistrationNumber().equals("00000000000000")) {
            twoColTable3.addCell(getCell10Left("CPF", true));
        } else {
            twoColTable3.addCell(getCell10Left("CNPJ", true));
        }
        twoColTable3.addCell(getCell10Left(formatDateToLocal(contract.getEndDate()), false));
        if (contract.getClientCompany().getRegistrationNumber().equals("00000000000000")) {
            twoColTable3.addCell(getCell10Left(contract.getClientCompany().getCpf(), false));
        } else {
            twoColTable3.addCell(getCell10Left(contract.getClientCompany().getRegistrationNumber(), false));
        }
        document.add(twoColTable3);

        Table twoColTable4 = new Table(twoColumnWidth);
        twoColTable4.addCell(getCell10Left("Dia de Pagamento", true));
        twoColTable4.addCell(getCell10Left("Endereço", true));
        twoColTable4.addCell(getCell10Left(String.valueOf(contract.getPaymentDueDay()), false));
        twoColTable4.addCell(getCell10Left(contract.getClientCompany().getFullAddress(), false));
        document.add(twoColTable4);

        Table twoColTable5 = new Table(twoColumnWidth);
        twoColTable5.addCell(getCell10Left("Forma de Pagamento", true));
        twoColTable5.addCell(getCell10Left("Telefone", true));
        twoColTable5.addCell(getCell10Left(contract.getPaymentMethod().getDescription().toUpperCase(), false));
        twoColTable5.addCell(getCell10Left("4399923923", false));
        document.add(twoColTable5.setMarginBottom(10F));

        Table divider2 = new Table(fullWidth);
        Border border2 = new DashedBorder(ColorConstants.GRAY, 0.5F);
        document.add(divider2.setBorder(border2));
        Paragraph productsParagraph = new Paragraph("Produtos");
        document.add(productsParagraph.setBold());

        Table threeColTable = new Table(threeColWidth);
        threeColTable.setBackgroundColor(ColorConstants.BLACK, 0.7F);
        threeColTable.addCell(new Cell().add(new Paragraph("Descrição").setBold().setFontColor(ColorConstants.WHITE)).setBorder(NO_BORDER));
        threeColTable.addCell(new Cell().add(new Paragraph("Duração").setBold().setFontColor(ColorConstants.WHITE)).setTextAlignment(CENTER).setBorder(NO_BORDER));
        threeColTable.addCell(new Cell().add(new Paragraph("Quantidade").setBold().setFontColor(ColorConstants.WHITE)).setTextAlignment(RIGHT).setBorder(NO_BORDER));
        document.add(threeColTable);

        Table threeColTable2 = new Table(threeColWidth);

        threeColTable2.addCell(new Cell().add(new Paragraph("Spots")).setBorder(NO_BORDER).setMarginLeft(10F));
        threeColTable2.addCell(new Cell().add(new Paragraph(String.format("%.2f", contract.getSpotDuration()))).setTextAlignment(CENTER).setBorder(NO_BORDER));
        threeColTable2.addCell(new Cell().add(new Paragraph(String.valueOf(contract.getQuantitySpotDay()))).setTextAlignment(RIGHT).setBorder(NO_BORDER).setMarginRight(15F));

        if (contract.getTestimonialQuantity() != 0) {
            threeColTable2.addCell(new Cell().add(new Paragraph("Testemunhais")).setBorder(NO_BORDER).setMarginLeft(10F));
            threeColTable2.addCell(new Cell().add(new Paragraph(String.format("%.2f", contract.getTestimonialDuration()))).setTextAlignment(CENTER).setBorder(NO_BORDER));
            threeColTable2.addCell(new Cell().add(new Paragraph(String.valueOf(contract.getTestimonialQuantity()))).setTextAlignment(RIGHT).setBorder(NO_BORDER).setMarginRight(15F));
        }

        if (!contract.getNewspaperParticipation().isBlank()) {
            threeColTable2.addCell(new Cell().add(new Paragraph("Participação no Jornal")).setBorder(NO_BORDER).setMarginLeft(10F));
            threeColTable2.addCell(new Cell().add(new Paragraph(String.valueOf(contract.getNewspaperParticipation()))).setTextAlignment(CENTER).setBorder(NO_BORDER));
            threeColTable2.addCell(new Cell().add(new Paragraph(String.valueOf(0))).setTextAlignment(RIGHT).setBorder(NO_BORDER).setMarginRight(15F));
        }
        document.add(threeColTable2.setMarginBottom(20F));

        Table threeColTable4 = new Table(oneTwo);
        threeColTable4.addCell(new Cell().add(new Paragraph("")).setBorder(NO_BORDER));
        threeColTable4.addCell(new Cell().add(divider2).setBorder(NO_BORDER));
        document.add(threeColTable4);

        Table threeColTable3 = new Table(threeColWidth);
        threeColTable3.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER)).setMarginLeft(10f);
        threeColTable3.addCell(new Cell().add(new Paragraph("Total Mensal").setBold()).setTextAlignment(CENTER).setBorder(Border.NO_BORDER));
        threeColTable3.addCell(new Cell().add(new Paragraph(NumberFormat.getCurrencyInstance().format(contract.getMonthlyPrice()))).setTextAlignment(RIGHT).setBorder(Border.NO_BORDER).setMarginRight(15F));
        document.add(threeColTable3);

        document.add(divider2);
        document.add(new Paragraph("\n"));
        document.add(divider.setBorder(new SolidBorder(ColorConstants.GRAY, 1)).setMarginBottom(15F));

        Table tb = new Table(fullWidth);
        tb.addCell(new Cell().add(new Paragraph("OBSERVAÇÕES").setBold()).setBorder(NO_BORDER).setTextAlignment(CENTER));
        tb.addCell(new Cell().add(new Paragraph(contract.getObservation()).setTextAlignment(CENTER)).setBorder(NO_BORDER));
        document.add(tb);

        Table tbDate = new Table(fullWidth);
        tbDate.addCell(new Cell().add(new Paragraph(currentDate())).setBorder(NO_BORDER).setTextAlignment(CENTER));
        document.add(new Paragraph("\n"));
        document.add(tbDate);

        float[] twoColMin = {100, 100};
        Table divider3 = new Table(twoColMin);
        divider3.setBorder(new SolidBorder(ColorConstants.BLACK, 0.2F));

        document.add(new Paragraph("\n\n\n"));
        Table twoColTable6 = new Table(twoColumnWidth);
        twoColTable6.addCell(new Cell().add(divider3).setBorder(NO_BORDER));
        twoColTable6.addCell(new Cell().add(divider3).setBorder(NO_BORDER));
        twoColTable6.addCell(getCell10Left("Assinatura do Responsavél", true));
        twoColTable6.addCell(getCell10Left("Assinatura do Anunciante", true).setTextAlignment(RIGHT));
        document.add(twoColTable6);

        document.close();

        return path;
    }

    static Image headerImage(String imagePath, PdfDocument pdfDocument) {
        try {
            ImageData imageData = ImageDataFactory.create(imagePath);
            Image image = new Image(imageData);

            float pageWidth = pdfDocument.getDefaultPageSize().getWidth();
            float imageOriginalWidth = image.getImageScaledWidth();
            float imageOriginalHeight = image.getImageScaledHeight();
            float aspectRatio = imageOriginalHeight / imageOriginalWidth;

            image.setWidth(pageWidth);

            float newHeight = pageWidth * aspectRatio;
            image.setHeight(newHeight);
            image.setFixedPosition(0, pdfDocument.getDefaultPageSize().getHeight() - newHeight);
            image.setOpacity(0.1F);

            return image;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Cell getHeaderTextCell(String value) {
        return new Cell().add(new Paragraph(value)).setBorder(NO_BORDER).setBold().setTextAlignment(RIGHT);
    }

    static Cell getHeaderTextCellValue(String value) {
        return new Cell().add(new Paragraph(value)).setBorder(NO_BORDER);
    }

    static Cell getBillingShippingCell(String value) {
        return new Cell().add(new Paragraph(value).setFontSize(12F)).setBold().setBorder(NO_BORDER).setTextAlignment(LEFT);
    }

    static Cell getCell10Left(String value, Boolean isBold) {
        Cell cell = new Cell().add(new Paragraph(value).setFontSize(10F)).setBorder(NO_BORDER).setTextAlignment(LEFT);
        return isBold ? cell.setBold() : cell;
    }

}
