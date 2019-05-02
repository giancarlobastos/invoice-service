package com.sendwyre.invoice.configuration;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class BitcointClientContextConfiguration {

    @Bean
    public NetworkParameters networkParameters() {
        return TestNet3Params.get();
    }

    @Bean
    public WalletAppKit walletAppKit(NetworkParameters networkParameters, BitcoinClientConfigurationProperties configurationProperties) {
        return new WalletAppKit(networkParameters, new File(configurationProperties.getStorageFolder()), configurationProperties.getFilePrefix());
    }
}
