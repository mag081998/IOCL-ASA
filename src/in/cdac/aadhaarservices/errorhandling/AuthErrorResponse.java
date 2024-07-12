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
import in.gov.uidai.authentication.uid_auth_response._2.AuthRes;
import in.gov.uidai.authentication.uid_auth_response._2.AuthResult;

/**
 * Auth Error Response
 * @author root
 */
@Component
public final  class AuthErrorResponse implements IErrorHandler
{
	private static final Logger logger = LogManager.getLogger(AuthErrorResponse.class);
	@Autowired
	private Jaxb2Marshaller marshaller;
	
	/**
	 * Prepare and Returns Auth Error Response
	 */
	@Override
	public String errorResponse(String txnID, String errorCode,XMLGregorianCalendar ts) 
	{
		String authMessageResponse = null;
		AuthRes resp=new AuthRes();
		resp.setRet(AuthResult.N);
		resp.setTs(ts);

		if (txnID != null) 
		{
			resp.setTxn(txnID);
		} 
		else 
		{
			resp.setTxn("");
		}
		resp.setErr(errorCode);

		try
		{
			authMessageResponse=marshalXml(resp);
		}
		catch (Exception excp)
		{
			logger.info("Error in Auth Error Response Marshalling in Class_Name: AuthErrorResponse Method_Name: errorResponse for Txn:" +txnID +"ErrorCode:" +errorCode,excp);
			authMessageResponse="Response Error Marshalling Issue";
		}
		return authMessageResponse;
	}

	/**
	 * @param otpReqObj
	 * @return
	 */
	private String marshalXml(AuthRes resp) 
	{
		String modifiedOtpXml;
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
		marshaller.marshal(resp, result);
		modifiedOtpXml = sw.toString();
		return modifiedOtpXml;
	}
}