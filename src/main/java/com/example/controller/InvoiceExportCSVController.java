package com.example.controller;

import com.example.io.InvoiceCsvExporter;
import com.example.repo.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

// Does not work!
//@RepositoryRestController
@RestController
@RequestMapping("/export")
public class InvoiceExportCSVController {


    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceCsvExporter invoiceCsvExporter;

    @RequestMapping(value = "/invoices", method = RequestMethod.GET, produces = "text/csv")
    public ResponseEntity<InputStreamResource> doExportAllCsv() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        this.invoiceCsvExporter.doExport(invoiceRepository.findAll(), bos);


        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=invoices.csv")
                .contentLength(bos.size())
                .contentType(
                        MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(new ByteArrayInputStream(bos.toByteArray())));


    }

    @RequestMapping(value = "/invoice", method = RequestMethod.GET, produces = "text/csv")
    public ResponseEntity<InputStreamResource> doExportCSV(@Param("id")BigInteger id) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        this.invoiceCsvExporter.doExport(invoiceRepository.findOne(id), bos);


        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=invoices.csv")
                .contentLength(bos.size())
                .contentType(
                        MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(new ByteArrayInputStream(bos.toByteArray())));


    }
}
