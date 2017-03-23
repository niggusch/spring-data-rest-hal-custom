package com.example.io;

import au.com.bytecode.opencsv.CSVWriter;
import com.example.domain.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class InvoiceCsvExporter {


    public int doExport(Invoice invoice, OutputStream stream) throws Exception {

        OutputStreamWriter osw = new OutputStreamWriter(stream);
        CSVWriter csvWriter = new CSVWriter(osw, ',');

        int count = 0;
        csvWriter.writeNext(toLine(invoice));
        csvWriter.flush();
        csvWriter.close();
        return count;
    }

    public int doExport(Iterable<Invoice> invoices, OutputStream stream) throws Exception {

        OutputStreamWriter osw = new OutputStreamWriter(stream);
        CSVWriter csvWriter = new CSVWriter(osw, ',');

        int count = 0;
        for (Invoice patient : invoices) {
            csvWriter.writeNext(toLine(patient));
            count++;
        }
        csvWriter.flush();
        csvWriter.close();
        return count;
    }

    private String[] toLine(Invoice invoice) {
        List<String> line = new ArrayList<>();

        line.add(invoice.getId().toString());
        line.add(invoice.getFirstName());
        line.add(invoice.getLastName());
        line.add("" + invoice.getAmount());


        return line.toArray(new String[line.size()]);
    }

    private List<String[]> toLines(Iterable<Invoice> invoices) {
        List<String[]> lines = new ArrayList<>();
        for (Invoice invoice : invoices) {
            lines.add(toLine(invoice));
        }
        return lines;
    }

}
