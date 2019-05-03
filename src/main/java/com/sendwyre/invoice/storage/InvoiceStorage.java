package com.sendwyre.invoice.storage;

import com.sendwyre.invoice.domain.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceStorage extends CrudRepository<Invoice, String> {
}
