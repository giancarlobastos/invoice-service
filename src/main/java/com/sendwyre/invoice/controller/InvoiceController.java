package com.sendwyre.invoice.controller;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/invoices/{invoiceNumber}")
    public Invoice get(@PathVariable Long invoiceNumber) {
        return invoiceService.getInvoice(invoiceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice Not Found"));
    }

    @GetMapping("/invoices")
    public List<Invoice> get() {
        return invoiceService.getInvoices().stream()
                .sorted(Comparator.comparing(Invoice::getInvoiceNumber))
                .collect(Collectors.toList());
    }

    @PostMapping("/invoices")
    public @ResponseBody
    Invoice post(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }
}
