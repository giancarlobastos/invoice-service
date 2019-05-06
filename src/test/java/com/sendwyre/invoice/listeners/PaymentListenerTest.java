package com.sendwyre.invoice.listeners;

import com.sendwyre.invoice.domain.Invoice;
import com.sendwyre.invoice.service.BitcoinService;
import com.sendwyre.invoice.service.InvoiceService;
import com.sendwyre.invoice.storage.InMemoryInvoiceStorage;
import com.sendwyre.invoice.storage.InMemoryRedisAtomicLong;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.wallet.Wallet;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PaymentListenerTest {

    private static final String WALLET_ADDRESS_MOCK = "mvXAXgv1S7UkNkjwpesrMZzgGLQLyDHnMc";

    private PaymentListener paymentListener;

    private InvoiceService invoiceService;

    @Mock
    private BitcoinService bitcoinService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        this.invoiceService = new InvoiceService(bitcoinService, new InMemoryInvoiceStorage(), new InMemoryRedisAtomicLong());
        this.paymentListener = new PaymentListener(invoiceService, bitcoinService);

        when(bitcoinService.createInvoicePaymentAddress()).thenReturn(WALLET_ADDRESS_MOCK);
    }

    @After
    public void after() {
        Mockito.validateMockitoUsage();
    }


    @Test
    public void shouldUpdateTotalAmountPaidWhenOnCoinReceivedEventIsTriggeredForAKnownInvoicePaymentAddress() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(0.00000001);

        invoiceService.createInvoice(invoice);

        Wallet wallet = mock(Wallet.class, RETURNS_DEEP_STUBS);
        Transaction transaction = mock(Transaction.class, RETURNS_DEEP_STUBS);
        TransactionOutput transactionOutput = mock(TransactionOutput.class, RETURNS_DEEP_STUBS);

        when(transaction.getOutputs()).thenReturn(Collections.singletonList(transactionOutput));
        when(transactionOutput.getScriptPubKey().getToAddress(any()).toString()).thenReturn(WALLET_ADDRESS_MOCK);

        paymentListener.onCoinsReceived(wallet, transaction, Coin.valueOf(0), Coin.valueOf(1));

        assertThat(invoiceService.getInvoice(WALLET_ADDRESS_MOCK).get().getTotalAmountPaid(), Is.is(0.00000001d));
    }

    @Test
    public void shouldIgnoreTransactionWhenOnCoinReceivedEventIsTriggeredForAnUnknownInvoicePaymentAddress() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(0.00000001);

        invoiceService.createInvoice(invoice);

        Wallet wallet = mock(Wallet.class, RETURNS_DEEP_STUBS);
        Transaction transaction = mock(Transaction.class, RETURNS_DEEP_STUBS);
        TransactionOutput transactionOutput = mock(TransactionOutput.class, RETURNS_DEEP_STUBS);

        when(transaction.getOutputs()).thenReturn(Collections.singletonList(transactionOutput));
        when(transactionOutput.getScriptPubKey().getToAddress(any()).toString()).thenReturn("unknownInvoicePaymentAddress");

        paymentListener.onCoinsReceived(wallet, transaction, Coin.valueOf(0), Coin.valueOf(1));

        assertThat(invoiceService.getInvoice(WALLET_ADDRESS_MOCK).get().getTotalAmountPaid(), Is.is(0d));
    }
}