package in.cdac.aadhaarservices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class provides the type of object to be created to process the request
 * @author root
 *
 */
@Component
@Scope(value = "prototype")
public final class RequestProcessorFactory 
{
	@Autowired
	AuthRequestProcessor authRequestProcessor;
	@Autowired
	OtpRequestProcessor otpRequestProcessor;
	@Autowired
	KycRequestProcessor kycRequestProcessor;
	@Autowired
	BfdRequestProcessor bfdRequestProcessor;
	
	
	public RequestProcessor getReqType(String reqType)
	{ 
		if(reqType == null)
		{
			return null;
		}		
		if(reqType.equalsIgnoreCase("AUTH"))
		{
			return authRequestProcessor;

		}
		else if(reqType.equalsIgnoreCase("OTP"))
		{
			return otpRequestProcessor;

		} 
		else if(reqType.equalsIgnoreCase("KYC"))
		{
			return kycRequestProcessor;
		}
		else if(reqType.equalsIgnoreCase("BFD"))
		{
			return otpRequestProcessor;

		} 
		return null;
	}
}

