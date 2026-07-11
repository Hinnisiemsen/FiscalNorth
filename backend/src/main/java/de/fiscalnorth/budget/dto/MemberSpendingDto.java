package de.fiscalnorth.budget.dto;

import java.math.BigDecimal;

public record MemberSpendingDto(String memberName, BigDecimal spent) {}
