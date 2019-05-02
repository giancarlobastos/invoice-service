package com.sendwyre.invoice.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendwyre.invoice.domain.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvoiceStorage {

    private final ObjectMapper objectMapper;

    private Map<String, String> invoicesMap;

    private AtomicLong invoiceNumberSequence;

    @PostConstruct
    public void postConstruct() {
        invoicesMap = new HashMap<>();
        invoiceNumberSequence = new AtomicLong();
    }

    public Invoice persist(Invoice invoice) {
        Long invoiceNumber = invoice.getInvoiceNumber() == null
                ? invoiceNumberSequence.incrementAndGet()
                : invoice.getInvoiceNumber();

        invoice.setInvoiceNumber(invoiceNumber);
        invoicesMap.put(invoice.getInvoicePaymentAddress(), serializeInvoice(invoice));
        return invoice;
    }

    public Invoice getInvoice(String invoicePaymentAddress) {
        String invoiceJson = invoicesMap.get(invoicePaymentAddress);
        return deserializeInvoice(invoiceJson);
    }

    @SneakyThrows
    private String serializeInvoice(Invoice invoice) {
        return objectMapper.writeValueAsString(invoice);
    }

    @SneakyThrows
    private Invoice deserializeInvoice(String invoiceJson) {
        return objectMapper.readValue(invoiceJson, Invoice.class);
    }
}
