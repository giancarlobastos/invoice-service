package com.sendwyre.invoice.service;

import org.bitcoinj.kits.WalletAppKit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.bitcoinj.wallet.KeyChain.KeyPurpose.RECEIVE_FUNDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class BitcoinServiceTest {

    private static final String WALLET_ADDRESS_MOCK = "mvXAXgv1S7UkNkjwpesrMZzgGLQLyDHnMc";

    private BitcoinService bitcoinService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WalletAppKit walletAppKit;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.bitcoinService = new BitcoinService(walletAppKit);
    }

    @After
    public void after() {
        Mockito.validateMockitoUsage();
    }

    @Test
    public void shouldCreateNewAddress() {
        when(walletAppKit.wallet().freshAddress(RECEIVE_FUNDS).toString()).thenReturn(WALLET_ADDRESS_MOCK);
        assertThat(bitcoinService.createInvoicePaymentAddress(), equalTo(WALLET_ADDRESS_MOCK));
    }
}