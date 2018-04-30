package enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    //JAVA 8 STREAM
    public static Map<BankAccountType, String> toMap(){
        return Arrays.stream(BankAccountType.values()).collect(Collectors.toMap(d -> d, BankAccountType::getDescription));
    }
}
