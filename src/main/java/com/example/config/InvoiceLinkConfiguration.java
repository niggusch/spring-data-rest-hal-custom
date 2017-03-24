package com.example.config;

import com.example.controller.InvoiceExportCSVController;
import com.example.domain.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@Configuration
@Slf4j
public class InvoiceLinkConfiguration {
    /**
     * Adds a link to export for lists of invoices
     *
     * @return
     */
    @Bean
    public ResourceProcessor<PagedResources<Resource<Invoice>>> invoicesProcessorPagesddExportLinks() {

        return new ResourceProcessor<PagedResources<Resource<Invoice>>>() {


            @Override
            public PagedResources<Resource<Invoice>> process(PagedResources<Resource<Invoice>> resource) {
                log.info("PagedResources<Resource<Invoice>> add custom link");
                // add custom export link for single invoice
                try {
                    resource.add(linkTo(methodOn(InvoiceExportCSVController.class).doExportAllCsv()).withRel("csv"));
                } catch (Exception e) {
                    // swallow
                }
                return resource;
            }
        };
    }


    @Bean
    public ResourceProcessor<Resources<Resource<Invoice>>> invoicesProcessorListAddExportLinks() {

        return new ResourceProcessor<Resources<Resource<Invoice>>>() {


            @Override
            public Resources<Resource<Invoice>> process(Resources<Resource<Invoice>> resources) {
                // add custom export link for single invoice
                log.info("Resources<Resource<Invoice>> add custom link");
                try {
                    resources.add(linkTo(methodOn(InvoiceExportCSVController.class).doExportAllCsv()).withRel("csv"));
                } catch (Exception e) {
                    // swallow
                }
                return resources;
            }
        };
    }

    /**
     * Adds a link to export to a single  invoices
     *
     * @return
     */
    @Bean
    public ResourceProcessor<Resource<Invoice>> invoiceProcessorAddExportLinks() {

        return new ResourceProcessor<Resource<Invoice>>() {

            @Override
            public Resource<Invoice> process(Resource<Invoice> resource) {
                // add custom export link for single invoice
                log.info("Resource<Invoice> add custom link");
                try {
                    resource.add(linkTo(methodOn(InvoiceExportCSVController.class).doExportCSV(resource.getContent().getId())).withRel("csv"));
                } catch (Exception e) {
                    // swallowed
                }
                return resource;
            }
        };
    }
}
