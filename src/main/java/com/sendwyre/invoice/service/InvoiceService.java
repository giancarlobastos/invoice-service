package com.sendwyre.invoice.service;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.storage.InvoiceStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.stream.StreamSupport.stream;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceService {

    private final BitcoinService bitcoinService;

    private final InvoiceStorage invoiceStorage;

    private final RedisAtomicLong invoiceNumberCounter;

    public Invoice createInvoice(Invoice invoice) {
        String invoicePaymentAddress = bitcoinService.createInvoicePaymentAddress();
        invoice.setInvoiceNumber(invoiceNumberCounter.incrementAndGet());
        invoice.setInvoicePaymentAddress(invoicePaymentAddress);
        return invoiceStorage.save(invoice);
    }

    public Optional<Invoice> getInvoice(Long invoiceNumber) {
        return stream(invoiceStorage.findAll().spliterator(), false)
                .filter(invoice -> invoice.getInvoiceNumber().equals(invoiceNumber))
                .findFirst();
    }
}
