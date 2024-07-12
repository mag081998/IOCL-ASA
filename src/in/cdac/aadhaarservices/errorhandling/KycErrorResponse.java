package in.cdac.aadhaarservices.errorhandling;

import java.io.StringWriter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import in.gov.uidai.kyc.common.types._1.YesNoType;
import in.gov.uidai.kyc.uid_kyc_response._1.Resp;




/**
 * KYC Error Response
 * @author root
 *
 */
@Component
public final class KycErrorResponse implements IErrorHandler
{
	@Autowired
	private Jaxb2Marshaller marshaller;
	private static final Logger logger = LogManager.getLogger(IErrorHandler.class);

	/**
	 * Prepare and Returns KYC Error Response
	 */
	@Override
	public String errorResponse(String txnID, String errorCode,XMLGregorianCalendar ts) 
	{
		String kycMessageResponse = null;
		Resp kycResp = new Resp();
		kycResp.setStatus("-1");
		kycResp.setRet(YesNoType.N);
		kycResp.setTs(ts);
		if (txnID != null) 
		{
			kycResp.setTxn(txnID);
		} 
		else 
		{
			kycResp.setTxn("");
		}
		kycResp.setErr(errorCode);
		try
		{
			kycMessageResponse = marshalXml(kycResp);
		}
		catch (Exception excp)
		{
			logger.info("Error in KYC Error Response Marshalling in Class_Name: kycErrorResponse Method_Name: errorResponse for Txn:"+txnID +"ErrorCode:" +errorCode,excp);
			kycMessageResponse="Response Error Marshalling Issue";
		}
		return kycMessageResponse;
	}
	
	/**
	 * @param otpReqObj
	 * @return
	 */
	private String marshalXml(Resp resp) 
	{
		String modifiedOtpXml;
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
		marshaller.marshal(resp, result);
		modifiedOtpXml = sw.toString();
		return modifiedOtpXml;
	}
}
