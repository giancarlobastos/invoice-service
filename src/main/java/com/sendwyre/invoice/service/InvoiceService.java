package com.sendwyre.invoice.service;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.storage.InvoiceStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceService {

    private final BitcoinService bitcoinService;

    private final InvoiceStorage invoiceStorage;

    public Invoice createInvoice(Invoice invoice) {
        String invoicePaymentAddress = bitcoinService.createInvoicePaymentAddress();
        invoice.setInvoicePaymentAddress(invoicePaymentAddress);
        return invoiceStorage.persist(invoice);
    }
}
