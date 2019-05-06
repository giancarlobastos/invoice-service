package com.sendwyre.invoice.service;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.domain.InvoiceStatus;
import com.sendwyre.invoice.storage.InMemoryInvoiceStorage;
import com.sendwyre.invoice.storage.InMemoryRedisAtomicLong;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class InvoiceServiceTest {

    private static final String WALLET_ADDRESS_MOCK = "mvXAXgv1S7UkNkjwpesrMZzgGLQLyDHnMc";

    private InvoiceService invoiceService;

    @Mock
    private BitcoinService bitcoinService;

    private InMemoryInvoiceStorage invoiceStorage;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        this.invoiceStorage = new InMemoryInvoiceStorage();
        this.invoiceService = new InvoiceService(bitcoinService, invoiceStorage, new InMemoryRedisAtomicLong());

        when(bitcoinService.createInvoicePaymentAddress()).thenReturn(WALLET_ADDRESS_MOCK);
    }

    @After
    public void after() {
        Mockito.validateMockitoUsage();
    }

    @Test
    public void shouldPersistInvoiceWhenCreateInvoice() {
        Invoice invoice = invoiceService.createInvoice(new Invoice());
        validateBasicAssertions(invoice);
    }

    @Test
    public void shouldAssignInvoiceNumberAndPaymentAddressWhenCreateInvoice() {
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now());

        Invoice createdInvoice = invoiceService.createInvoice(invoice);
        validateBasicAssertions(createdInvoice);
    }

    @Test
    public void shouldAssignDueDateOfTodayWhenInvoiceDueDateIsNull() {
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now());

        Invoice createdInvoice = invoiceService.createInvoice(invoice);
        validateBasicAssertions(createdInvoice);

        assertThat(createdInvoice.getDueDate(), equalTo(LocalDate.now()));
    }

    @Test
    public void shouldAssignDueDateOfTodayWhenInvoiceDueDateIsBeforeToday() {
        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().minusDays(1));

        Invoice createdInvoice = invoiceService.createInvoice(invoice);
        validateBasicAssertions(createdInvoice);

        assertThat(createdInvoice.getDueDate(), equalTo(LocalDate.now()));
    }

    @Test
    public void shouldAssignNonRepeatedInvoiceNumberAndPaymentAddressWhenCreatingMultipleInvoices() {

        when(bitcoinService.createInvoicePaymentAddress()).thenReturn(UUID.randomUUID().toString());
        Invoice invoice1 = invoiceService.createInvoice(new Invoice());

        when(bitcoinService.createInvoicePaymentAddress()).thenReturn(UUID.randomUUID().toString());
        Invoice invoice2 = invoiceService.createInvoice(new Invoice());

        assertThat(invoice1.getInvoicePaymentAddress(), not(equalTo(invoice2.getInvoicePaymentAddress())));
        assertThat(invoice1.getInvoiceNumber(), is(1L));
        assertThat(invoice2.getInvoiceNumber(), is(2L));
    }

    @Test
    public void getInvoices() {
    }

    @Test
    public void shouldGetInvoiceByInvoiceNumberWhenInvoiceIsStored() {
        Invoice invoice = invoiceService.createInvoice(new Invoice());
        validateBasicAssertions(invoice);

        Optional<Invoice> optionalInvoice = invoiceService.getInvoice(invoice.getInvoiceNumber());
        assertThat(optionalInvoice.isPresent(), is(true));
        assertThat(optionalInvoice.get(), equalTo(invoice));
    }

    @Test
    public void shouldGetEmptyOptionalInvoiceWhenGetInvoiceByInvoiceNumberIsNotStored() {
        Invoice invoice = invoiceService.createInvoice(new Invoice());
        validateBasicAssertions(invoice);

        Optional<Invoice> optionalInvoice = invoiceService.getInvoice(2L);
        assertThat(optionalInvoice.isPresent(), is(false));
    }

    @Test
    public void shouldGetInvoiceByInvoicePaymentAddressWhenInvoiceIsStored() {
        Invoice invoice = invoiceService.createInvoice(new Invoice());
        validateBasicAssertions(invoice);

        Optional<Invoice> optionalInvoice = invoiceService.getInvoice(invoice.getInvoicePaymentAddress());
        assertThat(optionalInvoice.isPresent(), is(true));
        assertThat(optionalInvoice.get(), equalTo(invoice));
    }

    @Test
    public void shouldGetEmptyOptionalInvoiceWhenGetInvoiceByPaymentAddressIsNotStored() {
        Invoice invoice = invoiceService.createInvoice(new Invoice());
        validateBasicAssertions(invoice);

        Optional<Invoice> optionalInvoice = invoiceService.getInvoice("unknownInvoicePaymentAddress");
        assertThat(optionalInvoice.isPresent(), is(false));
    }

    @Test
    public void shouldUpdateInvoiceInStorage() {
        Invoice invoice = invoiceService.createInvoice(new Invoice());
        validateBasicAssertions(invoice);

        invoice.setTotalAmountPaid(1d);
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);

        assertThat(updatedInvoice.getTotalAmountPaid(), is(1d));

        Optional<Invoice> optionalInvoice = invoiceService.getInvoice(invoice.getInvoiceNumber());
        assertThat(optionalInvoice.isPresent(), is(true));
        assertThat(optionalInvoice.get(), equalTo(updatedInvoice));
    }

    @Test
    public void shouldUpdateInvoiceStatusToPaidWhenInvoiceTotalAmountPaidIsGreaterOrEqualToTotalAmountAfterUpdate() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(1d);
        invoice = invoiceService.createInvoice(invoice);
        validateBasicAssertions(invoice);

        invoice.setTotalAmountPaid(1d);
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);

        assertThat(updatedInvoice.getTotalAmountPaid(), is(1d));
        assertThat(updatedInvoice.getStatus(), equalTo(InvoiceStatus.PAID));
    }

    @Test
    public void shouldUpdateInvoiceStatusToPartiallyPaidWhenInvoiceTotalAmountPaidIsGreaterThanZeroButLessThaTotalAmountAfterUpdate() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(1d);
        invoice = invoiceService.createInvoice(invoice);
        validateBasicAssertions(invoice);

        invoice.setTotalAmountPaid(0.9d);
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);

        assertThat(updatedInvoice.getTotalAmountPaid(), is(0.9d));
        assertThat(updatedInvoice.getStatus(), equalTo(InvoiceStatus.PARTIALLY_PAID));
    }

    @Test
    public void shouldUpdateInvoiceStatusToExpiredWhenInvoiceDueDateIsBeforeTodayAfterUpdate() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(1d);
        invoice = invoiceService.createInvoice(invoice);
        validateBasicAssertions(invoice);

        invoice.setDueDate(LocalDate.now().minusDays(1));
        Invoice updatedInvoice = invoiceService.updateInvoice(invoice);

        assertThat(updatedInvoice.getStatus(), equalTo(InvoiceStatus.EXPIRED));
    }

    private void validateBasicAssertions(Invoice invoice) {
        assertThat(invoice.getInvoiceNumber(), is(1L));
        assertThat(invoice.getInvoicePaymentAddress(), equalTo(WALLET_ADDRESS_MOCK));
        assertThat(invoice.getDueDate(), notNullValue());
        assertThat(invoice.getTotalAmountPaid(), is(0D));
        assertThat(invoiceStorage.getStorage().containsKey(WALLET_ADDRESS_MOCK), is(true));
    }
}
