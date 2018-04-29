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

    public static BankAccountType parse(String bankAccountType) {
        try {
            if (bankAccountType != null && !bankAccountType.isEmpty())
                return BankAccountType.valueOf(bankAccountType.toUpperCase());
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
