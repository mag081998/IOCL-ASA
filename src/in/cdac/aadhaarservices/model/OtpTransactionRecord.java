package in.cdac.aadhaarservices.model;
import java.sql.Timestamp;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 
 * @author root
 *
 */
public final class OtpTransactionRecord 
{
	private String auaAc;
	private String asaAc;
	private String ret;
	private String schema;
	private String errorCode;
	private Timestamp reqReceiptTime;
	private Timestamp reqForwardTime;
	private Timestamp respReceiptTime;
	private Timestamp respForwardTime;
	private Timestamp respCidrTime;
	private String code;
	private String transactionId;
	private XMLGregorianCalendar responseTs;
	private String clientIP;
	private String cryptoServiceUrl;
	private int serviceType;
	private String uidaiUrl;
	private String info;
	
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
	public String getAsaAc() {
		return asaAc;
	}
	public void setAsaAc(String asaAc) {
		this.asaAc = asaAc;
	}
	
	public String setInfo(String info)	{
		return this.info=info;
	}
	public String getInfo()	{
		return info;
	}
	public Timestamp getReqReceiptTime()
	{
		return this.reqReceiptTime;
	}

	public void setReqReceiptTime(Timestamp reqReceiptTime)
	{
		this.reqReceiptTime = reqReceiptTime;
	}

	public Timestamp getReqForwardTime()
	{
		return this.reqForwardTime;
	}

	public void setReqForwardTime(Timestamp reqForwardTime)
	{
		this.reqForwardTime = reqForwardTime;
	}

	public Timestamp getRespReceiptTime()
	{
		return this.respReceiptTime;
	}

	public void setRespReceiptTime(Timestamp respReceiptTime)
	{
		this.respReceiptTime = respReceiptTime;
	}

	public Timestamp getRespForwardTime()
	{
		return this.respForwardTime;
	}

	public void setRespForwardTime(Timestamp respForwardTime)
	{
		this.respForwardTime = respForwardTime;
	}

	public Timestamp getRespCidrTime()
	{
		return this.respCidrTime;
	}

	public void setRespCidrTime(Timestamp respCidrTime)
	{
		this.respCidrTime = respCidrTime;
	}
	public String getTransactionId()
	{
		return this.transactionId;
	}

	public void setTransactionId(String transactionId)
	{
		this.transactionId = transactionId;
	}
	
	public String getRet()
	{
		return this.ret;
	}

	public void setRet(String ret)
	{
		this.ret = ret;
	}

	public String getErrorCode()
	{
		return this.errorCode;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	
	public String getCode()
	{
		return this.code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getSchema()
	{
		return this.schema;
	}

	public void setSchema(String schema)
	{
		this.schema = schema;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
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

	public void setServiceType(int serviceType) 
	{
		this.serviceType = serviceType;
	}

	public String getUidaiUrl() {
		return uidaiUrl;
	}

	public void setUidaiUrl(String uidaiUrl) {
		this.uidaiUrl = uidaiUrl;
	}

	public XMLGregorianCalendar getResponseTs() {
		return responseTs;
	}

	public void setResponseTs(XMLGregorianCalendar responseTs) {
		this.responseTs = responseTs;
	}
	
	/*public void setSa(String sac) 
	{
		this.sa = sac;
	}
	public void setSalk(String salk) 
	{
		this.salk = salk;
	}
	
	public String getSalk() {
		return salk;
	}
	public String getSa() {
		return sa;
	}*/
}