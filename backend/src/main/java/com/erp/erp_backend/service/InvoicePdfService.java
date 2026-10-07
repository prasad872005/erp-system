package com.erp.erp_backend.service;

import com.erp.erp_backend.entity.Invoice;
import com.erp.erp_backend.entity.SalesOrder;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.Cell;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoicePdfService {

    public byte[] generateInvoicePdf(Invoice invoice) {

        try {
            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    20
            );

            Font headingFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD,
                    12
            );

            document.add(
                    new Paragraph("ERP SYSTEM", titleFont)
            );

            document.add(
                    new Paragraph("INVOICE")
            );

            document.add(
                    new Paragraph(
                            "Invoice Number: "
                                    + invoice.getInvoiceNumber()
                    )
            );

            document.add(
                    new Paragraph(
                            "Invoice Date: "
                                    + invoice.getInvoiceDate()
                    )
            );

            document.add(
                    new Paragraph(
                            "Customer: "
                                    + invoice.getCustomer()
                                            .getCustomerName()
                    )
            );

            document.add(
                    new Paragraph(
                            "Email: "
                                    + invoice.getCustomer().getEmail()
                    )
            );

            document.add(
                    new Paragraph(
                            "Phone: "
                                    + invoice.getCustomer().getPhone()
                    )
            );

            document.add(new Paragraph(" "));

            SalesOrder salesOrder = invoice.getSalesOrder();

            Table table = new Table(5);

            table.addCell(
                    new Cell(
                            new Paragraph("Product", headingFont)
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph("SKU", headingFont)
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph("Quantity", headingFont)
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph("Unit Price", headingFont)
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph("Total", headingFont)
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph(
                                    salesOrder.getProduct()
                                            .getProductName()
                            )
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph(
                                    salesOrder.getProduct().getSku()
                            )
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph(
                                    String.valueOf(
                                            salesOrder.getQuantity()
                                    )
                            )
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph(
                                    salesOrder.getProduct()
                                            .getUnitPrice()
                                            .toString()
                            )
                    )
            );

            table.addCell(
                    new Cell(
                            new Paragraph(
                                    invoice.getTotalAmount()
                                            .toString()
                            )
                    )
            );

            document.add(table);

            document.add(new Paragraph(" "));

            document.add(
                    new Paragraph(
                            "Total Amount: ₹"
                                    + invoice.getTotalAmount()
                    )
            );

            document.add(
                    new Paragraph(
                            "Status: "
                                    + invoice.getStatus()
                    )
            );

            document.close();

            return outputStream.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException(
                    "Failed to generate invoice PDF",
                    e
            );
        }
    }
}