package com.sendwyre.invoice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

@Data
@RedisHash("Invoice")
public class Invoice {

    private Long invoiceNumber;

    @Id
    private String invoicePaymentAddress;

    private String from;

    private String to;

    private LocalDate date;

    private LocalDate dueDate;

    private String description;

    private double totalAmount;

    private double totalAmountPaid;

    private double totalBalance;
}
