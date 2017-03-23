package com.example.repo;

import com.example.domain.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, BigInteger> {

    public List<Invoice> findByFirstName(@Param("firstName")String firstName);
}
