package com.example.io;

import com.example.controller.InvoiceExportCSVController;
import com.example.domain.Invoice;
import com.example.repo.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RepositoryRestController
public class CustomeInvoiceRepositoryRestController {

    private final InvoiceRepository repository;
    private final InvoiceCsvExporter exporter;

    @Autowired
    public CustomeInvoiceRepositoryRestController(InvoiceRepository repo, InvoiceCsvExporter exp) {
        this.repository = repo;
        this.exporter = exp;

    }

    @RequestMapping(value = "/invoices/search/customList", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<?> doExportAllJson() throws Exception {

        List<Invoice> invoices = repository.findAll();
        // some custom logic
        for (Invoice invoice : invoices) {
            invoice.setExported(true);

        }
        repository.save(invoices);

        Resources<Invoice> resources = new Resources<>(invoices);

        resources.add(linkTo(methodOn(CustomeInvoiceRepositoryRestController.class).doExportAllJson()).withSelfRel());
        resources.add(linkTo(methodOn(InvoiceExportCSVController.class).doExportAllCsv()).withRel("exportCsv"));
        return ResponseEntity.ok(resources);
    }


}
