package com.sendwyre.invoice.service;

import com.sendwyre.invoice.storage.InvoiceStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.bitcoinj.wallet.KeyChain.KeyPurpose.RECEIVE_FUNDS;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BitcoinService implements WalletCoinsReceivedEventListener {

    private final WalletAppKit walletAppKit;

    private final InvoiceStorage invoiceStorage;

    @PostConstruct
    public void postConstruct() {
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        walletAppKit.wallet().addCoinsReceivedEventListener(this);
    }

    public String createInvoicePaymentAddress() {
        return walletAppKit.wallet().freshAddress(RECEIVE_FUNDS).toString();
    }

    @Override
    public void onCoinsReceived(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
        tx.getOutputs().forEach(transactionOutput -> {
            String invoicePaymentAddress = transactionOutput.getScriptPubKey().getToAddress(wallet.getParams()).toString();
            log.info("Coins received for invoice address {}. Previous balance: {}, new balance: {}",
                    invoicePaymentAddress, prevBalance, newBalance);

            invoiceStorage.findById(invoicePaymentAddress)
                    .ifPresent(invoice -> {
                        long paidAmount = newBalance.getValue() - prevBalance.getValue();
                        invoice.setTotalAmountPaid(invoice.getTotalAmountPaid() + paidAmount);
                        invoice.setTotalBalance(newBalance.getValue());
                        invoiceStorage.save(invoice);
                    });
        });
    }
}
