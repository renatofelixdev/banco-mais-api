package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@MappedSuperclass
public class ModelMaster extends Model {

    @Id
    @GeneratedValue
    private Long id;

    private boolean active = true;

    private boolean removed = false;

    @CreatedTimestamp
    private Timestamp dateCreated;

    @UpdatedTimestamp
    private Timestamp dateUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Timestamp getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Timestamp dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Transient
    public String getDateCreatedFormated() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateCreated);
    }

    @Transient
    public String getDateUpdatedFormated() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateUpdated);
    }
}
