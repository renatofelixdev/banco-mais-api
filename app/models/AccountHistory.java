package models;

import enums.Operation;
import play.data.format.Formats;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class AccountHistory extends ModelMaster {

    @Temporal(TemporalType.DATE)
    @Formats.DateTime(pattern="dd/MM/yyyy")
    private Date date;

    @Enumerated(EnumType.STRING)
    private Operation operation;

    @Column(precision=10, scale=2)
    private BigDecimal value;

    @ManyToOne
    private BankAccount source;

    @ManyToOne
    private BankAccount target;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BankAccount getSource() {
        return source;
    }

    public void setSource(BankAccount source) {
        this.source = source;
    }

    public BankAccount getTarget() {
        return target;
    }

    public void setTarget(BankAccount target) {
        this.target = target;
    }
}
