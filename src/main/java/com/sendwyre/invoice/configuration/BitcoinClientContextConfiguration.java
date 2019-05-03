package com.sendwyre.invoice.configuration;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class BitcoinClientContextConfiguration {

    @Value("${bitcoin-client.storage-folder}")
    private String storageFolder;

    @Value("${bitcoin-client.file-prefix}")
    private String filePrefix;

    @Bean
    public NetworkParameters networkParameters() {
        return TestNet3Params.get();
    }

    @Bean
    public WalletAppKit walletAppKit(NetworkParameters networkParameters) {
        return new WalletAppKit(networkParameters, new File(storageFolder), filePrefix);
    }
}
