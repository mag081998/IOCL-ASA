package in.cdac.aadhaarservices.reqvalidation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides the object type to be created for request validation
 * @author root
 *
 */
@Component
public final class ValidationFactory 
{
	@Autowired
	AuthValidate authValidate;
	@Autowired
	OtpValidate otpValidate;
	@Autowired
	KycValidate kycValidate;
	
	public IValidate getReqType(String reqType)
	{
		if(reqType == null)
		{
			return null;
		}		
		if(reqType.equalsIgnoreCase("AUTH"))
		{
			return authValidate;

		} 
		else if(reqType.equalsIgnoreCase("OTP"))
		{
			return otpValidate;

		} 
		else if(reqType.equalsIgnoreCase("KYC"))
		{
			return kycValidate;
		}

		return null;
	}
}

