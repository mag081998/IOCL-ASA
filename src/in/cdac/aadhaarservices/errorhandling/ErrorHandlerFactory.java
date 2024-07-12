package in.cdac.aadhaarservices.errorhandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides the object type to be created 
 * @author root
 *
 */
@Component
public final  class ErrorHandlerFactory 
{
	@Autowired
	AuthErrorResponse authErrorResponse;
	@Autowired
	OtpErrorResponse otpErrorResponse;
	@Autowired
	KycErrorResponse kycErrorResponse;
	
	public IErrorHandler getReqType(String reqType)
	{
		if(reqType == null)
		{
			return null;
		}		
		if(reqType.equalsIgnoreCase("AUTH"))
		{
			return authErrorResponse;

		} 
		else if(reqType.equalsIgnoreCase("OTP"))
		{
			return otpErrorResponse;

		} 
		else if(reqType.equalsIgnoreCase("KYC"))
		{
			return kycErrorResponse;
		}

		return null;
	}
}

