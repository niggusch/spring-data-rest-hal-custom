package com.example.controller;

import com.example.domain.Invoice;
import com.example.io.InvoiceCsvExporter;
import com.example.repo.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Collections;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController
@RequestMapping("/csv")
public class InvoiceExportCSVController implements
        ResourceProcessor<RepositoryLinksResource> {


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
    public ResponseEntity<InputStreamResource> doExportCSV(@RequestParam(required = true, name = "id") BigInteger id) throws Exception {
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


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Resources<Resource<Invoice>>> showLinks() throws Exception {
        Resources<Resource<Invoice>> resources = new Resources<>(Collections.EMPTY_LIST,
                ControllerLinkBuilder.linkTo(InvoiceExportCSVController.class).withSelfRel(),
                linkTo(methodOn(InvoiceExportCSVController.class).doExportAllCsv()).withRel("csv"));
        return ResponseEntity.ok(resources);

    }

    @Override
    public RepositoryLinksResource process(RepositoryLinksResource resource) {
        // add to root
        resource.add(ControllerLinkBuilder.linkTo(InvoiceExportCSVController.class).withRel("csv"));
        return resource;
    }
}
