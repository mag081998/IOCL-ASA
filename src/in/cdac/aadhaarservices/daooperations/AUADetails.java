package in.cdac.aadhaarservices.daooperations;

import java.io.Serializable;
import java.util.Date;

 public final class AUADetails implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String aua_code;
	private Date aua_valid_till;
	private Boolean is_auth_allowed;
	private Boolean is_otp_allowed;
	private Boolean is_ekyc_allowed;
	private String lk;
	private Date lk_expiry;
	
	public String getLk() {
		return lk;
	}
	public void setLk(String lk) {
		this.lk = lk;
	}
	
	public Date getLk_expiry() {
		return lk_expiry;
	}
	public void setLk_expiry(Date lk_expiry) {
		this.lk_expiry = lk_expiry;
	}
	private Boolean status;
	private String code;
	
	
	public String getAua_code() {
		return aua_code;
	}
	public void setAua_code(String aua_code) {
		this.aua_code = aua_code;
	}
	public Date getAua_valid_till() {
		return aua_valid_till;
	}
	public void setAua_valid_till(Date aua_valid_till) {
		this.aua_valid_till = aua_valid_till;
	}
	public Boolean getIs_auth_allowed() {
		return is_auth_allowed;
	}
	public void setIs_auth_allowed(Boolean is_auth_allowed) {
		this.is_auth_allowed = is_auth_allowed;
	}
	public Boolean getIs_otp_allowed() {
		return is_otp_allowed;
	}
	public void setIs_otp_allowed(Boolean is_otp_allowed) {
		this.is_otp_allowed = is_otp_allowed;
	}
	public Boolean getIs_ekyc_allowed() {
		return is_ekyc_allowed;
	}
	public void setIs_ekyc_allowed(Boolean is_ekyc_allowed) {
		this.is_ekyc_allowed = is_ekyc_allowed;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	
	
	
}
