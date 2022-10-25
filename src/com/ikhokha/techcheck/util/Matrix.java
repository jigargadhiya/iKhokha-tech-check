package com.ikhokha.techcheck.util;

public enum Matrix {

    SHORTER_THAN_15(Operation.LESS_THEN,"15"),MOVER_MENTIONS(Operation.CONTAINS,"Mover"),
    SHAKER_MENTIONS(Operation.CONTAINS,"Shaker"),QUESTIONS(Operation.CONTAINS,"?"),
    SPAM(Operation.REGEX, Constant.LINK_REGEX);

    private final Operation operation;
    private final String value;

    Matrix(Operation operation,String value) {
        this.value = value;
        this.operation=operation;
    }

    public Operation getOperation() {
        return operation;
    }
    public String getValue(){return value;}
}
