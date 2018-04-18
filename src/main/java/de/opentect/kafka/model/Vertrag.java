package de.opentect.kafka.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Vertrag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String vertragUUID;
    private Partner partner;
    private String sparte;
    private String beitrag;
    private Date lastChanged;

    public String getVertragUUID() {
        return vertragUUID;
    }

    public void setVertragUUID(String vertragUUID) {
        this.vertragUUID = vertragUUID;
    }

    public String getSparte() {
        return sparte;
    }

    public void setSparte(String sparte) {
        this.sparte = sparte;
    }

    public String getBeitrag() {
        return beitrag;
    }

    public void setBeitrag(String beitrag) {
        this.beitrag = beitrag;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }
}

