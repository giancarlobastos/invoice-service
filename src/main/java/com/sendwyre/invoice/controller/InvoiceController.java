package com.sendwyre.invoice.controller;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/invoice/{invoiceNumber}")
    public Invoice get(@PathVariable Long invoiceNumber) {
        return invoiceService.getInvoice(invoiceNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice Not Found"));
    }

    @PutMapping("/invoice")
    public @ResponseBody
    Invoice put(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }
}
