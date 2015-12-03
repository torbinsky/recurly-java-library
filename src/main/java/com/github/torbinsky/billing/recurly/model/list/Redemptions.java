package com.github.torbinsky.billing.recurly.model.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.github.torbinsky.billing.recurly.model.Redemption;

@XmlRootElement(name = "redemptions")
public class Redemptions extends RecurlyObjects<Redemption> {

    @XmlTransient
    public static final String REDEMPTIONS_RESOURCE = "/redemptions";
    
    @XmlElement(name = "redemption")
    private List<Redemption> redemptions = new ArrayList<Redemption>();

	@Override
	public List<Redemption> getObjects() {
		return redemptions;
	}
	
	public void setInvoices(List<Redemption> redemptionList) {
        this.redemptions = redemptionList;
    }
}
