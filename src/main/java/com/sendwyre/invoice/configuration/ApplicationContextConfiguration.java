package com.sendwyre.invoice.configuration;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContextConfiguration {

    @Value("${mapdb.storage-file}")
    private String storageFolder;

    @Bean
    public DB invoiceStorageDB() {
        return DBMaker.fileDB(storageFolder)
                .checksumHeaderBypass()
                .make();
    }
}
