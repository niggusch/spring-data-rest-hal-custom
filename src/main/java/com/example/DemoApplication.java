package com.example;

import com.example.controller.InvoiceExportCSVController;
import com.example.domain.Invoice;
import com.example.repo.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.*;

import javax.annotation.PostConstruct;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Slf4j
@SpringBootApplication
public class DemoApplication {

    @Autowired
    InvoiceRepository repository;




    public
    @PostConstruct
    void init() {
        repository.deleteAll();
        Invoice invoice = repository.save(Invoice.builder().firstName("Chuck").lastName("Noris").amount(2.5).build());
        log.info("Saved invoice {}", invoice);
        invoice = repository.save(Invoice.builder().firstName("Elvis").lastName("Costello").amount(2.3).build());
        log.info("Saved invoice {}", invoice);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
