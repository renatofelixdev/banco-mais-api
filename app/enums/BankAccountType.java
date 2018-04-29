package enums;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, String> toMap(){
        Map<String, String> map = new HashMap<>();

        for(BankAccountType bat : BankAccountType.values()){
            map.put(bat.name(), bat.getDescription());
        }
        return map;
    }
}
