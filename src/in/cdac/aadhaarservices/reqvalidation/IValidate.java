package in.cdac.aadhaarservices.reqvalidation;

import java.util.Map;

import generated.Kyc;
import in.cdac.otp_v1.Otp;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;


/**
 * This interface defines the methods to be implemented by specific classes of Auth, KYC and OTP
 * to perform request validation 
 * @author root
 *
 */
public interface IValidate 
{
	Map<String,String> validateXMLSyn(String xml); 
	//Map<String,String> validateXMLSem(String xml);
	Map<String,String> validateXMLSem(Otp otpReq );
	Map<String,String> validateXMLSem(Auth authReq, String uidType );
	Map<String,String> validateXMLSem(Kyc kycReq );
}
