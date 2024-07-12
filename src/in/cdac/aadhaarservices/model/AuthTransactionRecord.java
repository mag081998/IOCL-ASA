package in.cdac.aadhaarservices.model;
import java.sql.Timestamp;
import javax.xml.datatype.XMLGregorianCalendar;
import in.cdac.aadhaarservices.daooperations.SignDet;

/**
 * 
 * @author root
 *
 */
public final class AuthTransactionRecord 
{
	private String asaAc;
	private String ret;
	private String schema;
	private String errorCode;
	private Timestamp reqReceiptTime;
	private Timestamp reqForwardTime;
	private Timestamp respReceiptTime;
	private Timestamp respForwardTime;
	private Timestamp respPacketTime;
	private	String code;
	private	String transactionId;
	private XMLGregorianCalendar responseTs;
	private String actn;
	private String uidToken;
	private String clientIP;
	private String uidType;
	private String cryptoServiceUrl;
	private int serviceType;
	private String uidaiUrl;
	private String info;
	private SignDet signdet;
	private String auaAc;
	
	private String reqAC;
	private String reqSA;
	private String reqLk;
	
	private String lk;
	
	
	
	public String getLk() {
		return lk;
	}
	public void setLk(String lk) {
		this.lk = lk;
	}
	public String getReqAC() {
		return reqAC;
	}
	public void setReqAC(String reqAC) {
		this.reqAC = reqAC;
	}
	public String getReqSA() {
		return reqSA;
	}
	public void setReqSA(String reqSA) {
		this.reqSA = reqSA;
	}
	public String getReqLk() {
		return reqLk;
	}
	public void setReqLk(String reqLk) {
		this.reqLk = reqLk;
	}
	public String getAuaAc() {
		return auaAc;
	}
	public void setAuaAc(String auaAc) {
		this.auaAc = auaAc;
	}
	
	public String getAsaAc() 
	{
		return asaAc;
	}
	public void setAsaAc(String asaAc) 
	{
		this.asaAc = asaAc;
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
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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
	public String getUidToken() {
		return uidToken;
	}
	public void setUidToken(String uidToken) {
		this.uidToken = uidToken;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
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