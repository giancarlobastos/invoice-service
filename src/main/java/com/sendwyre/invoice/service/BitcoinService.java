package com.sendwyre.invoice.service;

import com.sendwyre.invoice.listeners.PaymentListener;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.kits.WalletAppKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.bitcoinj.wallet.KeyChain.KeyPurpose.RECEIVE_FUNDS;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BitcoinService {

    private final WalletAppKit walletAppKit;

    private final PaymentListener paymentListener;

    @PostConstruct
    public void postConstruct() {
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        walletAppKit.wallet().addCoinsReceivedEventListener(paymentListener);
    }

    public String createInvoicePaymentAddress() {
        return walletAppKit.wallet().freshAddress(RECEIVE_FUNDS).toString();
    }
}
