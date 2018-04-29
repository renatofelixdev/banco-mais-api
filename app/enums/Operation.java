package enums;

public enum Operation {

    BANK_STATEMENT ("Extrato Bancário"),
    ACCOUNT_WITHDRAWAL ("Saque"),
    DEPOSIT_INTO_ACCOUNT ("Depósito"),
    BANK_TRANSFER ("Transferência Bancária");

    private String description;

    private Operation(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
