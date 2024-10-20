package com.example.pdfgenerator.service;


import com.example.pdfgenerator.model.InvoiceRequest;
import com.example.pdfgenerator.util.FileHashUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class PDFService {

    @Value("${pdf.storage.path}")
    private String pdfStoragePath;

    public File generateOrRetrievePDF(InvoiceRequest request) throws IOException, DocumentException {
        String fileName = generateFileName(request);
        File pdfFile = new File(pdfStoragePath + fileName);

        if (pdfFile.exists()) {
            System.out.println("PDF already exists. Returning existing file.");
            return pdfFile;
        } else {
            System.out.println("PDF does not exist. Generating a new one.");
            createPDF(request, pdfFile);
            return pdfFile;
        }
    }
    private String generateFileName(InvoiceRequest request) {
        String hashedData = FileHashUtil.hashInvoiceRequest(request);
        return request.getSeller() + "_" + request.getBuyer() + "_" + hashedData + ".pdf";
    }

//private void createPDF(InvoiceRequest request, File pdfFile) throws DocumentException, IOException {
//    Document document = new Document();
//    PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
//    document.open();
//
//    // Create table for Seller and Buyer without extra spacing
//    PdfPTable sellerBuyerTable = new PdfPTable(2); // 2 columns
//    sellerBuyerTable.setWidthPercentage(100);
//    sellerBuyerTable.setSpacingBefore(19f);
//    sellerBuyerTable.getDefaultCell().setBorder(Rectangle.BOX); // Set outer border
//
//    // Add Seller Cell
//    PdfPCell sellerCell = new PdfPCell();
//    sellerCell.setBorder(Rectangle.BOX); // Set border for seller cell
//    sellerCell.addElement(new Paragraph("Seller: " + request.getSeller()));
//    sellerCell.addElement(new Paragraph("Address: " + request.getSellerAddress()));
//    sellerCell.addElement(new Paragraph("GSTIN: " + request.getSellerGstin()));
//
//    // Add Buyer Cell
//    PdfPCell buyerCell = new PdfPCell();
//    buyerCell.setBorder(Rectangle.BOX); // Set border for buyer cell
//    buyerCell.addElement(new Paragraph("Buyer: " + request.getBuyer()));
//    buyerCell.addElement(new Paragraph("Address: " + request.getBuyerAddress()));
//    buyerCell.addElement(new Paragraph("GSTIN: " + request.getBuyerGstin()));
//    sellerBuyerTable.addCell(sellerCell);
//    sellerBuyerTable.addCell(buyerCell);
//    document.add(sellerBuyerTable); // Add seller and buyer table to the document
//    // Add a table for item details
//    PdfPTable itemsTable = new PdfPTable(4); // 4 columns: Item, Quantity, Rate, Amount
//    itemsTable.setWidthPercentage(100);
//
//
//    itemsTable.setSpacingBefore(0); // Set spacing to 0 to avoid gap
//    itemsTable.setWidths(new float[]{5, 2, 2, 3});
//    addTableHeader(itemsTable, "Item");
//    addTableHeader(itemsTable, "Quantity");
//    addTableHeader(itemsTable, "Rate");
//    addTableHeader(itemsTable, "Amount");
//
//    // Add item rows
//    for (InvoiceRequest.Item item : request.getItems()) {
//        itemsTable.addCell(item.getName());
//        itemsTable.addCell(String.valueOf(item.getQuantity()));
//        itemsTable.addCell(String.format("%.2f", item.getRate()));
//        itemsTable.addCell(String.format("%.2f", item.getAmount()));
//    }
//    document.add(itemsTable);
//
//    document.close();
//    System.out.println("PDF generated at: " + pdfFile.getPath());
//}
//
//    private void addTableHeader(PdfPTable table, String headerTitle) {
//        PdfPCell header = new PdfPCell();
////        header.setBorderWidth(2);
//        header.setPhrase(new Phrase(headerTitle));
//        table.addCell(header);
//    }

    private void createPDF(InvoiceRequest request, File pdfFile) throws DocumentException, IOException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        // Create table for Seller and Buyer with padding
        PdfPTable sellerBuyerTable = new PdfPTable(2); // 2 columns
        sellerBuyerTable.setWidthPercentage(100);
        sellerBuyerTable.setSpacingBefore(10f); // Adjust as necessary for spacing
        sellerBuyerTable.getDefaultCell().setBorder(Rectangle.BOX);

        // Add Seller Cell with padding and centered content
        PdfPCell sellerCell = new PdfPCell();
        sellerCell.setBorder(Rectangle.BOX);
        sellerCell.setPadding(10); // Add padding around content
        sellerCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center content horizontally
        sellerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);   // Center content vertically
        sellerCell.addElement(new Paragraph("Seller: " + request.getSeller()));
        sellerCell.addElement(new Paragraph("Address: " + request.getSellerAddress()));
        sellerCell.addElement(new Paragraph("GSTIN: " + request.getSellerGstin()));

        // Add Buyer Cell with padding and centered content
        PdfPCell buyerCell = new PdfPCell();
        buyerCell.setBorder(Rectangle.BOX);
        buyerCell.setPadding(30); // Add padding around content
        buyerCell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center content horizontally
        buyerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);   // Center content vertically
        buyerCell.addElement(new Paragraph("Buyer: " + request.getBuyer()));
        buyerCell.addElement(new Paragraph("Address: " + request.getBuyerAddress()));
        buyerCell.addElement(new Paragraph("GSTIN: " + request.getBuyerGstin()));

        // Add cells to the table
        sellerBuyerTable.addCell(sellerCell);
        sellerBuyerTable.addCell(buyerCell);

        // Add the seller-buyer table to the document
        document.add(sellerBuyerTable);

        // Add a table for item details
        PdfPTable itemsTable = new PdfPTable(4); // 4 columns: Item, Quantity, Rate, Amount
        itemsTable.setWidthPercentage(100);
        itemsTable.setSpacingBefore(0); // Adjust as necessary for spacing
        itemsTable.setWidths(new float[]{5, 2, 2, 3});

        // Add table headers
        addTableHeader(itemsTable, "Item");
        addTableHeader(itemsTable, "Quantity");
        addTableHeader(itemsTable, "Rate");
        addTableHeader(itemsTable, "Amount");

        // Add item rows
        for (InvoiceRequest.Item item : request.getItems()) {
            PdfPCell itemCell = new PdfPCell(new Phrase(item.getName()));
            itemCell.setPadding(5); // Add padding
            itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            itemsTable.addCell(itemCell);

            PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity())));
            quantityCell.setPadding(5); // Add padding
            quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            quantityCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            itemsTable.addCell(quantityCell);

            PdfPCell rateCell = new PdfPCell(new Phrase(String.format("%.2f", item.getRate())));
            rateCell.setPadding(5); // Add padding
            rateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            rateCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            itemsTable.addCell(rateCell);

            PdfPCell amountCell = new PdfPCell(new Phrase(String.format("%.2f", item.getAmount())));
            amountCell.setPadding(5); // Add padding
            amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            amountCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            itemsTable.addCell(amountCell);
        }

        // Add the items table to the document
        document.add(itemsTable);

        // Add blank space (2 rows worth)
        document.add(new Paragraph("\n\n"));

        // Close the document
        document.close();
        System.out.println("PDF generated at: " + pdfFile.getPath());
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();

        header.setPadding(5); // Add padding for header cells
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setPhrase(new Phrase(headerTitle));
        table.addCell(header);
    }


}
