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
import in.cdac.aadhaarservices.util.OtpRes;
import in.cdac.aadhaarservices.util.OtpResult;

/**
 * OTP Error Response
 * @author root
 *
 */
@Component
public final class OtpErrorResponse implements IErrorHandler
{
	@Autowired
	private Jaxb2Marshaller marshaller;
	private static final Logger logger = LogManager.getLogger(OtpErrorResponse.class);

	/**
	 * Prepare and Returns OTP Error Response 
	 */
	@Override
	public String errorResponse(String txnID, String errorCode,XMLGregorianCalendar ts) 
	{
		String otpMessageResponse = null;
		OtpRes resp=new OtpRes();
		resp.setRet(OtpResult.N);
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
			otpMessageResponse = marshalXml(resp);
		}
		catch (Exception excp)
		{
			logger.info("Error in OTP Error Response Marshalling in Class_Name: OtpErrorResponse Method_Name: errorResponse for Txn:" +txnID +"ErrorCode:" +errorCode,excp);
			otpMessageResponse="Response Error Marshalling Issue";
		}
		return otpMessageResponse;
	}

	/**
	 * @param otpReqObj
	 * @return
	 */
	private String marshalXml(OtpRes resp) 
	{
		String modifiedOtpXml;
		StringWriter sw = new StringWriter();
		Result result = new StreamResult(sw);
		marshaller.marshal(resp, result);
		modifiedOtpXml = sw.toString();
		return modifiedOtpXml;
	}
}
