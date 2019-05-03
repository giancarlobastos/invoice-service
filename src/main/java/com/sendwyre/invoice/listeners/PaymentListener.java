package com.sendwyre.invoice.listeners;

import com.sendwyre.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PaymentListener implements WalletCoinsReceivedEventListener {

    private final InvoiceService invoiceService;

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        tx.getOutputs().forEach(transactionOutput -> {
            String invoicePaymentAddress = transactionOutput.getScriptPubKey().getToAddress(wallet.getParams()).toString();
            log.info("Coins received for invoice address {}. Previous balance: {}, new balance: {}",
                    invoicePaymentAddress, prevBalance, newBalance);

            invoiceService.getInvoice(invoicePaymentAddress)
                    .ifPresent(invoice -> {
                        long paidAmount = newBalance.getValue() - prevBalance.getValue();
                        invoice.setTotalAmountPaid(invoice.getTotalAmountPaid() + paidAmount);
                        invoice.setTotalBalance(newBalance.getValue());
                        invoiceService.updateInvoice(invoice);
                    });
        });
    }

}
