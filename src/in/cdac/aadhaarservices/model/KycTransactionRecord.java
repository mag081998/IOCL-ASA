package in.cdac.aadhaarservices.model;
import java.sql.Timestamp;
import java.util.Date;
import javax.xml.datatype.XMLGregorianCalendar;

import in.cdac.aadhaarservices.daooperations.SignDet;

/**
 * 
 * @author root
 *
 */
public final class KycTransactionRecord 
{
	private String ac;
	private String ret;
	private String schema;
	private String refNum=null;
	private String errorCode;
	private Timestamp reqReceiptTime;
	private Timestamp reqForwardTime;
	private Timestamp respReceiptTime;
	private Timestamp respForwardTime;
	private Timestamp respPacketTime;
	private	String code;
	private String status;
	private	String transactionId;
	private	String clientIP;
	private XMLGregorianCalendar responseTs;
	private String actn;
	private Date validTill;
	private Timestamp updateTimestamp;
	private boolean isActive;
	private boolean isKRDHActive;
	private String ko;
	private Timestamp ts;
	private String kycRet;
	private String kycErr;
	private String authActn;
	private String authErr;
	private String uidType;
	private String cryptoServiceUrl;
	private int serviceType;
	private String uidaiUrl;
	private String vaultUrl;
	private String info;
	private SignDet signdet;
	private String asaAc;
	private String lk;
	
	
	
	public String getLk() {
		return lk;
	}
	public void setLk(String lk) {
		this.lk = lk;
	}
	public String getAsaAc() {
		return asaAc;
	}
	public void setAsaAc(String asaAc) {
		this.asaAc = asaAc;
	}
	public String getAc() {
		return ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	public String getRefNum() {
		return refNum;
	}
	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Timestamp getReqReceiptTime() {
		return reqReceiptTime;
	}
	public void setReqReceiptTime(Timestamp reqReceiptTime) {
		this.reqReceiptTime = reqReceiptTime;
	}
	public Timestamp getReqForwardTime() {
		return reqForwardTime;
	}
	public void setReqForwardTime(Timestamp reqForwardTime) {
		this.reqForwardTime = reqForwardTime;
	}
	public Timestamp getRespReceiptTime() {
		return respReceiptTime;
	}
	public void setRespReceiptTime(Timestamp respReceiptTime) {
		this.respReceiptTime = respReceiptTime;
	}
	public Timestamp getRespForwardTime() {
		return respForwardTime;
	}
	public void setRespForwardTime(Timestamp respForwardTime) {
		this.respForwardTime = respForwardTime;
	}
	public Timestamp getRespPacketTime() {
		return respPacketTime;
	}
	public void setRespPacketTime(Timestamp respPacketTime) {
		this.respPacketTime = respPacketTime;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public XMLGregorianCalendar getResponseTs() {
		return responseTs;
	}
	public void setResponseTs(XMLGregorianCalendar responseTs) {
		this.responseTs = responseTs;
	}
	public String getActn() {
		return actn;
	}
	public void setActn(String actn) {
		this.actn = actn;
	}
	public Date getValidTill() {
		return validTill;
	}
	public void setValidTill(Date validTill) {
		this.validTill = validTill;
	}
	public Timestamp getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(Timestamp updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isKRDHActive() {
		return isKRDHActive;
	}
	public void setKRDHActive(boolean isKRDHActive) {
		this.isKRDHActive = isKRDHActive;
	}
	public String getKo() {
		return ko;
	}
	public void setKo(String ko) {
		this.ko = ko;
	}
	public Timestamp getTs() {
		return ts;
	}
	public void setTs(Timestamp ts) {
		this.ts = ts;
	}
	public String getKycRet() {
		return kycRet;
	}
	public void setKycRet(String kycRet) {
		this.kycRet = kycRet;
	}
	public String getKycErr() {
		return kycErr;
	}
	public void setKycErr(String kycErr) {
		this.kycErr = kycErr;
	}
	public String getAuthActn() {
		return authActn;
	}
	public void setAuthActn(String authActn) {
		this.authActn = authActn;
	}
	public String getAuthErr() {
		return authErr;
	}
	public void setAuthErr(String authErr) {
		this.authErr = authErr;
	}
	public String getUidType() {
		return uidType;
	}
	public void setUidType(String uidType) {
		this.uidType = uidType;
	}
	public String getCryptoServiceUrl() {
		return cryptoServiceUrl;
	}
	public void setCryptoServiceUrl(String cryptoServiceUrl) {
		this.cryptoServiceUrl = cryptoServiceUrl;
	}
	public int getServiceType() {
		return serviceType;
	}
	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}
	public String getUidaiUrl() {
		return uidaiUrl;
	}
	public void setUidaiUrl(String uidaiUrl) {
		this.uidaiUrl = uidaiUrl;
	}
	public String getVaultUrl() {
		return vaultUrl;
	}
	public void setVaultUrl(String vaultUrl) {
		this.vaultUrl = vaultUrl;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	public SignDet getSigndet() {
		return signdet;
	}
	public void setSigndet(SignDet signdet) {
		this.signdet = signdet;
	}
	
}