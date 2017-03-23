# spring-data-rest-hal-custom
Open Questions:

1. How can a controlle be added to the entry point under _"_links"_ ? Is this possible if another mime-type than json is used?

In My Example the controller would be

```
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
       ...
    }

    @RequestMapping(value = "/invoice", method = RequestMethod.GET, produces = "text/csv")
    public ResponseEntity<InputStreamResource> doExportCSV(@Param("id")BigInteger id) throws Exception {
        ...
    }
}
```
And another one
```
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
```


And I would like it to appear as
```
 "export": {
      "href": "http://localhost:8080/customize/export/invoices"
    },
 "custom":{
      "href": "localhost:8080//customize/invoices/search/customList"
 }   
```
How can a custom controller be added to the entry point?

So if the current representation is
```
{
  "_links": {
    "invoices": {
      "href": "http://localhost:8080/customize/invoices{?page,size,sort}",
      "templated": true
    },
    "profile": {
      "href": "http://localhost:8080/customize/profile"
    }
  }
}
```

It should be
```
{
  "_links": {
    "invoices": {
      "href": "http://localhost:8080/customize/invoices{?page,size,sort}",
      "templated": true
    },
    "export": {
      "href": "http://localhost:8080/customize/export/invoices"
    },
     "custom":{
      "href": "localhost:8080//customize/invoices/search/customList"
    }   
    "profile": {
      "href": "http://localhost:8080/customize/profile"
    }
  }
}
```

2. The InvoiceRepository Item representation should be decorated with a new link, how to achieve that?

The Repository as is
```
public interface InvoiceRepository extends MongoRepository<Invoice, BigInteger> {

    public List<Invoice> findByFirstName(@Param("firstName")String firstName);
}
```

will procuce
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
            "href": "http://localhost:8080/customize/invoices/27490450945023268364302849904"
          },
          "invoice": {
            "href": "http://localhost:8080/customize/invoices/27490450945023268364302849904"
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
            "href": "http://localhost:8080/customize/invoices/27490450945023268364302849905"
          },
          "invoice": {
            "href": "http://localhost:8080/customize/invoices/27490450945023268364302849905"
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
So a single Invoice is represented as

```
{
  "firstName": "Chuck",
  "lastName": "Noris",
  "amount": 2.5,
  "exported": false,
  "_links": {
    "self": {
      "href": "http://localhost:8080/customize/invoices/27490450945023268364302849904"
    },
    "invoice": {
      "href": "http://localhost:8080/customize/invoices/27490450945023268364302849904"
    }
  }
}
```

But should be extended to
```
{
  "firstName": "Chuck",
  "lastName": "Noris",
  "amount": 2.5,
  "exported": false,
  "_links": {
    "self": {
      "href": "http://localhost:8080/customize/invoices/27490450945023268364302849904"
    },
    "invoice": {
      "href": "http://localhost:8080/customize/invoices/27490450945023268364302849904"
    }
    "export": {
      "href": "http://localhost:8080/customize/export/invoice/27490450945023268364302849904"
    }
  }
}
```
Adding a custom link:
```
    "export": {
      "href": "http://localhost:8080/customize/export/invoice/27490450945023268364302849904"
    }
```

# Custom Repository works, but does not appear as link on
As logged it may be reached on localhost:8080//customize/invoices/search/customList
```
..
2017-03-23 10:12:11.977  INFO 46364 --- [           main] o.s.d.r.w.RepositoryRestHandlerMapping   : Mapped "{[/customize/invoices/search/customList],methods=[GET],produces=[application/hal+json || application/json]}" onto public org.springframework.http.ResponseEntity<?> com.example.io.CustomeInvoiceRepositoryRestController.doExportAllJson() throws java.lang.Exception

..
```
And produces the following
```
{
  "_embedded" : {
    "invoices" : [ {
      "firstName" : "Chuck",
      "lastName" : "Noris",
      "amount" : 2.5,
      "exported" : true
    }, {
      "firstName" : "Elvis",
      "lastName" : "Costello",
      "amount" : 2.3,
      "exported" : true
    } ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/invoices/search/customList"
    },
    "exportCsv" : {
      "href" : "http://localhost:8080/export/invoices"
    }
  }
}
```
But it does not appear on the entry point.

# Hal Browser

 * http://localhost:8080/customize/browser/index.html#/customize
 
 
