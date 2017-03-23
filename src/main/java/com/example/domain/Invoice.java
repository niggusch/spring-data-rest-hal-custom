package com.example.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

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
