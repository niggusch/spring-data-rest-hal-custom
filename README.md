# spring-data-rest-hal-custom

1. Shows how to add _"_links"_  into the entry point adding a custom controller.
2. How to decorate links of an entity (using repositories).

This is shown by adding the ability to export as CSV. Either for a single entity or for a list or page, in the latter case all entities are exported for simplicity.

## Domain Class
This sample app has one single Entity/Document
```
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@Document
public class Invoice {
    @Id
    private BigInteger id;

    private String firstName;

    private String lastName;

    private double amount;

    private boolean exported = false;
}
```
## Repository
And a simple MongoRepository of typ e
```
public interface InvoiceRepository extends MongoRepository<Invoice, BigInteger> {

    public List<Invoice> findByFirstName(@Param("firstName")String firstName);
}
```

## Additional Controller (Export as CSV) 
```
@RestController
@RequestMapping("/csv")
public class InvoiceExportCSVController implements
        ResourceProcessor<RepositoryLinksResource> {
    ...


    @RequestMapping(value = "/invoices", method = RequestMethod.GET, produces = "text/csv")
    public ResponseEntity<InputStreamResource> doExportAllCsv() throws Exception {
        ...

    }

    @RequestMapping(value = "/invoice", method = RequestMethod.GET, produces = "text/csv")
    public ResponseEntity<InputStreamResource> doExportCSV(@RequestParam(required = true, name = "id") BigInteger id) throws Exception {
        ...

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
```


To add to the root the Controller must implement `ResourceProcessor<RepositoryLinksResource>`. In this case a link to the controller itself with rel _csv_ will be rendered.
```
    "csv": {
      "href": "http://localhost:8080/csv"
    }
```

The full resource root if called _http://localhost:8080/customize/_ will be:

```


{
  "_links": {
    "invoices": {
      "href": "http://localhost:8080/customize/invoices{?page,size,sort}",
      "templated": true
    },
    "csv": {
      "href": "http://localhost:8080/csv"
    },
    "profile": {
      "href": "http://localhost:8080/customize/profile"
    }
  }
}

```

## Decorating Links of an Entity/Document
Three Beans are create each for its own purpose:
 1. To add _Link_ to _PagedResources_ - when using _PagingAndSortingRepository_ to a pages JSON representation
 1. To add _Link_ to _Resources_ of _Resource_ of _Invoice_ for Lists, i.e when using _CrudRepository_ to the List restul JSON resresentation.
 1. To add _Link_ to _Resource_  of _Invoice_ itself, means to a single Invoice Objects JSON representation.

```
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
```
Result would look like this for a request on _http://localhost:8080/customize/invoices_:
```
{
  "_embedded": {
    "invoices": [
      {
        "firstName": "Chuck",
        "lastName": "Noris",
        "amount": 2.5,
        "exported": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/customize/invoices/27491967894575425914688276251"
          },
          "invoice": {
            "href": "http://localhost:8080/customize/invoices/27491967894575425914688276251"
          },
          "csv": {
            "href": "http://localhost:8080/csv/invoice?id=27491967894575425914688276251"
          }
        }
      },
      {
        "firstName": "Elvis",
        "lastName": "Costello",
        "amount": 2.3,
        "exported": false,
        "_links": {
          "self": {
            "href": "http://localhost:8080/customize/invoices/27491967894575425914688276252"
          },
          "invoice": {
            "href": "http://localhost:8080/customize/invoices/27491967894575425914688276252"
          },
          "csv": {
            "href": "http://localhost:8080/csv/invoice?id=27491967894575425914688276252"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/customize/invoices"
    },
    "profile": {
      "href": "http://localhost:8080/customize/profile/invoices"
    },
    "search": {
      "href": "http://localhost:8080/customize/invoices/search"
    },
    "csv": {
      "href": "http://localhost:8080/csv/invoices"
    }
  },
  "page": {
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "number": 0
  }
}
```

Note all the csv links added.

# Run the example
Having Maven and Java installed simply call `$ mvn spring-boot:run`

# Hal Browser
 Run the example and [Open Hal Browser][1]


 
[1]:http://localhost:8080/customize/browser/index.html#/customize
