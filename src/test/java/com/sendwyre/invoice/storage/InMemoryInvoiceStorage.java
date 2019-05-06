package com.sendwyre.invoice.storage;

import com.sendwyre.invoice.domain.Invoice;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryInvoiceStorage implements InvoiceStorage {

    @Getter
    private Map<String, Invoice> storage = new HashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        storage.put(invoice.getInvoicePaymentAddress(), invoice);
        return storage.get(invoice.getInvoicePaymentAddress());
    }

    @Override
    public <S extends Invoice> Iterable<S> saveAll(Iterable<S> iterable) {
        return null;
    }

    @Override
    public Optional<Invoice> findById(String s) {
        return Optional.ofNullable(storage.get(s));
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public Iterable<Invoice> findAll() {
        return storage.values();
    }

    @Override
    public Iterable<Invoice> findAllById(Iterable<String> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Invoice invoice) {

    }

    @Override
    public void deleteAll(Iterable<? extends Invoice> iterable) {

    }

    @Override
    public void deleteAll() {

    }
}