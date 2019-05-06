package com.sendwyre.invoice.service;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.domain.InvoiceStatus;
import com.sendwyre.invoice.storage.InvoiceStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
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

        if(invoice.getDueDate() == null || invoice.getDueDate().isBefore(LocalDate.now())) {
            invoice.setDueDate(LocalDate.now());
        }

        return invoiceStorage.save(invoice);
    }

    public List<Invoice> getInvoices() {
        return stream(invoiceStorage.findAll().spliterator(), false)
                .map(this::updateInvoiceStatus)
                .collect(toList());
    }

    public Optional<Invoice> getInvoice(Long invoiceNumber) {
        return stream(invoiceStorage.findAll().spliterator(), false)
                .filter(invoice -> invoice.getInvoiceNumber().equals(invoiceNumber))
                .map(this::updateInvoiceStatus)
                .findFirst();
    }

    public Optional<Invoice> getInvoice(String invoicePaymentAddress) {
        return invoiceStorage.findById(invoicePaymentAddress).map(this::updateInvoiceStatus);
    }

    public Invoice updateInvoice(Invoice invoice) {
        return invoiceStorage.save(updateInvoiceStatus(invoice));
    }

    private Invoice updateInvoiceStatus(Invoice invoice) {
        if (invoice.getTotalAmountPaid() >= invoice.getTotalAmount()) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (invoice.getDueDate().isBefore(LocalDate.now())) {
            invoice.setStatus(InvoiceStatus.EXPIRED);
        } else if (invoice.getTotalAmountPaid() > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        return invoice;
    }
}
