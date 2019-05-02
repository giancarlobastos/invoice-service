package com.sendwyre.invoice.controller;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PutMapping("/invoice")
    public @ResponseBody
    Invoice put(@RequestBody Invoice invoice) {
        return invoiceService.createInvoice(invoice);
    }
}
