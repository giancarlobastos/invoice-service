package com.sendwyre.invoice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bitcoin-client")
public class BitcoinClientConfigurationProperties {

    private String storageFolder;

    private String filePrefix;
}
