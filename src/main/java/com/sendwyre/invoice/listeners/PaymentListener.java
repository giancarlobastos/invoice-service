package com.sendwyre.invoice.listeners;

import com.sendwyre.invoice.service.BitcoinService;
import com.sendwyre.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentListener implements WalletCoinsReceivedEventListener {

    private static final double BITCOIN_MINIMAL_UNIT = 0.00000001;

    private final InvoiceService invoiceService;

    private final BitcoinService bitcoinService;

    @PostConstruct
    public void postConstruct() {
        bitcoinService.addCoinsReceivedEventListener(this);
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        tx.getOutputs().forEach(transactionOutput -> {
            String invoicePaymentAddress = transactionOutput.getScriptPubKey().getToAddress(wallet.getParams()).toString();

            Coin transactionValue = transactionOutput.getValue();

            log.info("Coins received for invoice address {}. Transcation value: {}", invoicePaymentAddress, transactionValue.getValue() * BITCOIN_MINIMAL_UNIT);

            invoiceService.getInvoice(invoicePaymentAddress)
                    .ifPresent(invoice -> {
                        invoice.setTotalAmountPaid(invoice.getTotalAmountPaid() + transactionValue.getValue() * BITCOIN_MINIMAL_UNIT);
                        invoiceService.updateInvoice(invoice);
                    });
        });
    }
}
