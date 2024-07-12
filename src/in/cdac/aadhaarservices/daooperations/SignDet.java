package in.cdac.aadhaarservices.daooperations;

import java.io.Serializable;

public final class SignDet implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ac;
	private Boolean is_sign_at_asa;
	private Boolean decrypt_ekyc_at_asa;
	private Boolean is_struid;
	private String code;
	private Boolean status;
	
	public Boolean getIs_struid() {
		return is_struid;
	}
	public void setIs_struid(Boolean is_struid) {
		this.is_struid = is_struid;
	}
	public Boolean getDecrypt_ekyc_at_asa() 
	{
		return decrypt_ekyc_at_asa;
	}
	public void setDecrypt_ekyc_at_aua( Boolean decrypt_ekyc_at_asa ) 
	{
		this.decrypt_ekyc_at_asa = decrypt_ekyc_at_asa;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getAc() 
	{
		return ac;
	}
	public void setAc(String ac) 
	{
		this.ac = ac;
	}
	public Boolean getIs_sign_at_asa() 
	{
		return is_sign_at_asa;
	}
	public void setIs_sign_at_asa(Boolean is_sign_at_asa) 
	{
		this.is_sign_at_asa = is_sign_at_asa;
	}
}
