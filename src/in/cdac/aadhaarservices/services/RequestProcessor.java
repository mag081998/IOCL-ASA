package in.cdac.aadhaarservices.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import in.cdac.aadhaarservices.common.AppConfigs;
import in.cdac.aadhaarservices.errorhandling.ErrorHandlerFactory;
import in.cdac.aadhaarservices.errorhandling.IErrorHandler;
import in.cdac.aadhaarservices.invokews.ICallRestService;
import in.cdac.aadhaarservices.reqvalidation.IValidate;
import in.cdac.aadhaarservices.reqvalidation.SignatureVerifier;
import in.cdac.aadhaarservices.reqvalidation.ValidationFactory;
import in.cdac.aadhaarservices.util.Utility;

/**
 * RequestProcessor abstract class to be implemented by AUTH, OTP and e-KYC classes
 */
@Component
@Scope(value="prototype")
public abstract class RequestProcessor extends AppConfigs 
{
	//private static final Logger errorLogger = LogManager.getLogger("errorLog");

	@Autowired 
	Utility utilityFunctions;
	@Autowired
	ValidationFactory validationFactory;
	@Autowired
	ErrorHandlerFactory errorHandlerFactory;
	@Autowired
	Environment environment;
	@Autowired
	ICallRestService callRestService;
	IValidate kycValidate;
	IErrorHandler errorHandler;
	IValidate validate;

	/**
	 * This method will be implemented by auth, otp and e-kyc classes
	 * @param xmlMessage
	 * @return
	 */
	//public abstract String processReq(String xmlMessage, String clientIP);
	
	/**
	 * This method will be implemented by auth, otp and e-kyc classes
	 * @param xmlMessage
	 * @return
	 */
	public abstract String processReq(String xmlMessage, String clientIP,String lk);

	/**
	 * 
	 * @param uidaiUrl
	 * @param uid
	 * @param asaLK
	 * @param auaAC
	 * @return
	 */
	protected String generateURL ( String uidaiUrl , char [] uid , String asaLK , String auaAC )	
	{
		Character firstDigit = null;
		Character secondDigit = null;
		firstDigit = uid[0];
		secondDigit = uid[1];
		String url = uidaiUrl+auaAC+"/"+firstDigit+"/"+secondDigit+"/"+asaLK;
		return url;
	}

	/**
	 * 
	 * @param status
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map getMapResult(String status, String data) 
	{
		Map<String, String> resultMap = new HashMap<>();
		resultMap.put("status", status);
		resultMap.put("data", data);
		return resultMap;
	}
	
	protected String verifyUIDCI(String ci) 
	{
			Boolean validDate = utilityFunctions.validateDate(ci);
			if ( validDate )
			{
				return null;
			}
			else 
			{
				return "INVALID_CI";
			}
	}
	// 
	protected String getUIDType(String uid)
	{
		String uidType=null;

		if(uid.length()== Integer.parseInt(environment.getProperty("uidlength")))
		{
			uidType="A";
		}
		else if(uid.length()==Integer.parseInt(environment.getProperty("virtualidlength")))
		{
			uidType="V";
		}
		else if(uid.length()==Integer.parseInt(environment.getProperty("uidtokenlength")))
		{
			uidType="T";
		}
		else 
		{
			uidType = null;
		}
		return uidType;
	}
	
	protected Map<String, String> verifySignature(String authReqXML, byte[] publicKeyFile) {
		return SignatureVerifier.verify(authReqXML, publicKeyFile);
	}


}