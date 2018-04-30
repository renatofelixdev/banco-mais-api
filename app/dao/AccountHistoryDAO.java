package dao;

import com.avaje.ebean.Model;
import enums.Operation;
import models.AccountHistory;
import models.BankAccount;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.avaje.ebean.Expr.eq;

public class AccountHistoryDAO implements ApiDAO {

    private Model.Finder<Long, AccountHistory> accountHistoryFinder = new Model.Finder<>(AccountHistory.class);

    @Override
    public List<AccountHistory> all() {
        return accountHistoryFinder.where()
                .eq("removed", false)
                .findList();
    }

    @Override
    public Optional<AccountHistory> byId(Long id) {
        return Optional.ofNullable(accountHistoryFinder.byId(id));
    }

    public List<AccountHistory> byBankAccount(BankAccount bankAccount, Date start, Date end){
        return accountHistoryFinder.where()
                .eq("removed", false)
                .or(eq("source", bankAccount), eq("target", bankAccount))
                .ge("date", start)
                .le("date", end)
                .ne("operation", Operation.BANK_STATEMENT)
                .orderBy("date desc")
                .findList();
    }
}
