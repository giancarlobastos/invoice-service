package com.sendwyre.invoice.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Invoice {

    private Long invoiceNumber;

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
