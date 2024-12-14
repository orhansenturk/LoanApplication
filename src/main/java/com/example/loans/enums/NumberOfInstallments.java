package com.example.loans.enums;

public enum NumberOfInstallments {
    SIX(6),
    NINE(9),
    TWELVE(12),
    TWENTY_FOUR(24);

    private final Integer numOfInstallments;

    NumberOfInstallments(final int numOfInstallments) {
        this.numOfInstallments = numOfInstallments;
    }

    public int getNumberOfInstallments() {
        return numOfInstallments;
    }
}
