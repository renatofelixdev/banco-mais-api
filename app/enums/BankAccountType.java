package enums;

public enum BankAccountType {
    SAVINGS_ACCOUNT("Conta Poupança"),
    CHECKING_ACCOUNT("Conta Corrente");

    private String description;

    private BankAccountType(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
