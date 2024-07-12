package in.cdac.aadhaarservices.daooperations;

import java.io.Serializable;
import java.util.Date;

 public final class CryptoACLKCert implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean status;
	private String code;
	private String ac;
	private String crypto_ac;
	private String crypto_lk;
	private Date crypto_lk_expiry_date;
	private String key_identifier;
	private String key_algorithm;
	private String crypto_opr;
	private String token_type;
	
	public String getAc() {
		return ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getCrypto_ac() {
		return crypto_ac;
	}
	public void setCrypto_ac(String crypto_ac) {
		this.crypto_ac = crypto_ac;
	}
	public String getCrypto_lk() {
		return crypto_lk;
	}
	public void setCrypto_lk(String crypto_lk) {
		this.crypto_lk = crypto_lk;
	}
	public Date getCrypto_lk_expiry_date() {
		return crypto_lk_expiry_date;
	}
	public void setCrypto_lk_expiry_date(Date crypto_lk_expiry_date) {
		this.crypto_lk_expiry_date = crypto_lk_expiry_date;
	}
	public String getKey_identifier() {
		return key_identifier;
	}
	public void setKey_identifier(String key_identifier) {
		this.key_identifier = key_identifier;
	}
	public String getKey_algorithm() {
		return key_algorithm;
	}
	public void setKey_algorithm(String key_algorithm) {
		this.key_algorithm = key_algorithm;
	}
	public String getCrypto_opr() {
		return crypto_opr;
	}
	public void setCrypto_opr(String crypto_opr) {
		this.crypto_opr = crypto_opr;
	}
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public Boolean getStatus() 
	{
		return status;
	}
	public void setStatus(Boolean status) 
	{
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
	
}
