package com.virtual.herbal.garden.Virtual_Herbs_Garden.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueDataPoint {
    private LocalDate date;
    private BigDecimal totalRevenue;
}