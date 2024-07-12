package in.cdac.aadhaarservices.daooperations;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean Class to get AUA and ASA License and Signing Details
 * @author root
 *
 */
public final class LKDetails implements Serializable
{
	private static final long serialVersionUID = 2L;
	
	private Boolean status;
	private String code;
	private String asa_license_key; 
	private Date asa_valid_till; 
	private String license_type;
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public String getCode() 
	{
		return code;
	}
	public void setCode(String code) 
	{
		this.code = code;
	}

	public String getAsa_license_key() 
	{
		return asa_license_key;
	}
	public void setAsa_license_key(String asa_license_key) 
	{
		this.asa_license_key = asa_license_key;
	}
	
	public Date getAsa_valid_till() 
	{
		return asa_valid_till;
	}
	public void setAsa_valid_till(Date asa_valid_till) 
	{
		this.asa_valid_till = asa_valid_till;
	}

	public String getLicense_type() 
	{
		return license_type;
	}
	public void setLicense_type(String license_type) 
	{
		this.license_type = license_type;
	}
}