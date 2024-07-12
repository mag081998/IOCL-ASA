package in.cdac.aadhaarservices.services;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.w3._2000._09.xmldsig_.Signature;
import org.w3._2000._09.xmldsig_.X509Data;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import in.cdac.aadhaarservices.daooperations.AUADetails;
import in.cdac.aadhaarservices.daooperations.CryptoACLKCert;
import in.cdac.aadhaarservices.daooperations.LKDetails;
import in.cdac.aadhaarservices.daooperations.SignDet;
import in.cdac.aadhaarservices.daooperations.auth.AuthDaoOperations;
import in.cdac.aadhaarservices.model.AuthTransactionRecord;
import in.cdac.aadhaarservices.util.Utility;
import in.cdac.cryptoservice.DataElement;
import in.cdac.cryptoservice.KeyType;
import in.cdac.cryptoservice.Operations;
import in.cdac.cryptoservice.Request;
import in.cdac.cryptoservice.Response;
import in.cdac.cryptoservice.Status;
import in.cdac.cryptoservice.TokenType;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;
import in.gov.uidai.authentication.uid_auth_response._2.AuthRes;

/**
 * This class carries out authentication request processing.
 */
@Service
@Scope(value="prototype")
public final class AuthRequestProcessor extends RequestProcessor
{
	private static final Logger logger = LogManager.getLogger(AuthRequestProcessor.class);

	@Autowired
	private AuthDaoOperations authDaoOperations;

	@Autowired
	private Jaxb2Marshaller marshaller;

	/**
	 * @param authReqXML
	 * @param clientIP
	 * 
	 * @return
	 */
	@Override
	public String processReq(String authReqXML, String clientIP, String lk) 
	{
		Auth authReqObj=null;
		String authResponse=null;
		LKDetails lkDet=null;
		AUADetails auaDet = null;
		String url=null;
		String signedXml=null;
		String uidaiAuthResponse=null;
		String uidType=null;
		String errorCode=null;
		AuthTransactionRecord txnRecord = new AuthTransactionRecord();
		txnRecord.setClientIP( clientIP );
		txnRecord.setLk(lk);
		txnRecord.setReqReceiptTime( new Timestamp(System.currentTimeMillis()) );
		errorHandler = errorHandlerFactory.getReqType("AUTH");
		validate = validationFactory.getReqType("AUTH");
		try
		{
			if(syntactValidate)
			{
				Map<String, String> validateXMLSyn = validate.validateXMLSyn( authReqXML );
				if ( validateXMLSyn.get("status").equalsIgnoreCase("n") ) 
				{
					return errorResponse( validateXMLSyn.get("code"), false, txnRecord,authReqObj );
				} 
			}
			try 
			{
				authReqObj = (Auth) marshaller.unmarshal(new StreamSource(new StringReader(authReqXML)));
			} 
			catch (Exception excp) 
			{
				logger.info("Error in Parsing the Auth XML in Class Name: AuthRequestProcessor Method Name: processReq ", excp);
				return errorResponse("UNABLE_AUTH_XML_PARSING",false, txnRecord,authReqObj);
			}
			//To check duplicate transaction
			Map<String, String> checkDuplicateTxn=authDaoOperations.insertTxnAtStart(authReqObj.getTxn(), "AUTH");
			if(checkDuplicateTxn.get("status").equalsIgnoreCase("n")) 
			{
				return errorResponse(((String)checkDuplicateTxn.get("code")),false, txnRecord,null);
			}
			txnRecord.setTransactionId(authReqObj.getTxn());
			txnRecord.setReqAC(authReqObj.getAc());
			txnRecord.setReqSA(authReqObj.getSa());
			uidType = getUIDType( authReqObj.getUid() );
			txnRecord.setSchema(commonSchemaName);
			
			Map<String, Object> resultSchemaName = authDaoOperations.getSchemaName( authReqObj.getAc(), commonSchemaName );
			if (((String)resultSchemaName.get("status")).equalsIgnoreCase("n")) 
			{
				return errorResponse(((String)resultSchemaName.get("errorCode")),false, txnRecord,authReqObj);
			} 
			else 
			{
				txnRecord.setAsaAc((String)resultSchemaName.get("ac"));
			}
			if ( uidType == null )	
			{
				return errorResponse("INVALID_UID_VALUE",true, txnRecord,authReqObj);
			}
			else
			{
				txnRecord.setUidType(uidType);
			}
			
			if(semanticValidate)
			{
				errorCode= semanticValidation( authReqObj, uidType );	
				if(errorCode != null) 
				{
					return errorResponse(errorCode,true, txnRecord,authReqObj);
				}
			}

			auaDet = getAUADetails(authReqObj.getAc(), txnRecord.getSchema());
			if ( !auaDet.getStatus())
			{
				return errorResponse( auaDet.getCode(), true, txnRecord,authReqObj );
			}
			//if( verifyLk )
			//{
			errorCode = verifyAUA( authReqObj.getAc(), auaDet, txnRecord.getTransactionId(),txnRecord.getLk() );
			if ( errorCode != null )
			{
				return errorResponse( errorCode, true, txnRecord,authReqObj );
			}
			//}
			lkDet = getAsaLkDetails( txnRecord.getAsaAc(), txnRecord.getSchema() );
			if ( !lkDet.getStatus() ) 
			{
				return errorResponse( lkDet.getCode(), true, txnRecord,authReqObj );
			}
			errorCode = verifyASALKValidity( lkDet, authReqObj.getTxn() );
			if( errorCode != null )
			{
				return errorResponse( errorCode, true, txnRecord,authReqObj );
			}
			
			
			// check if sign required or not 
			if (signrequired)
			{
				//Check already signed or not
				//Else
				//Sign the XML
				Signature s = authReqObj.getSignature();
				//X509Data x509Data = null;
				if (s != null )
				{
					return errorResponse( "XML_IS_ALREADY_SIGNED", true, txnRecord,authReqObj );
				}
				//return errorResponse(verSign.get("code"), true, txnRecord, otpReqObj);
				SignDet signdet = authDaoOperations.getSignDet( authReqObj.getAc(), txnRecord.getSchema() );
				if ( !signdet.getStatus() )
				{
					return errorResponse( signdet.getCode(), true, txnRecord,authReqObj );
				}
				txnRecord.setSigndet( signdet );
				try 
				{
					//modifiedOtpXml = marshalXml(otpReqObj);
					if( signdet.getIs_sign_at_asa()) 
					{
						CryptoACLKCert crypaclkcert = authDaoOperations.getCryptoAcLkCertDet( txnRecord.getAsaAc(), txnRecord.getSchema(), Operations.SIGN.value() );
						if ( !crypaclkcert.getStatus() )
						{
							return errorResponse( crypaclkcert.getCode(), true, txnRecord ,authReqObj);
						}	
						Map<String, String> digitalSigning = digitalSigning( txnRecord, authReqXML, crypaclkcert );
						if ( digitalSigning.get("status").equalsIgnoreCase("n") ) 
						{
							errorCode = digitalSigning.get("data");
							return errorResponse(errorCode, true, txnRecord,authReqObj);
						}
						else
						{
							signedXml=digitalSigning.get("data");
						}
					} 
					else 
					{
						signedXml=authReqXML;
					}
										
				} 
				catch (Exception excp) 
				{
					logger.info("Error in Parsing the modified Auth XML in Class Name:AuthRequestProcessor Method Name: processReq",excp);
					return errorResponse( "UNABLE_AUTH_XML_PARSING", false, txnRecord ,authReqObj);
				}
			}
			//else
			if(verifySign)
			{
				// if  sign  is not  required means verify xml
				Signature s = authReqObj.getSignature();
				X509Data x509Data = null;
				if (s == null ||  s.getKeyInfo() ==null || s.getKeyInfo().getContent() == null)
				{
					return errorResponse( "SIGN_VERIFICATION_FAILED", true, txnRecord,authReqObj );
				}
				x509Data = (X509Data)s.getKeyInfo().getContent().get(0);
				if ( x509Data == null )
				{
					return errorResponse( "SIGN_VERIFICATION_FAILED", true, txnRecord,authReqObj );
				}
				@SuppressWarnings("rawtypes")
				JAXBElement el2e = (JAXBElement) x509Data.getX509IssuerSerialsAndX509SKISAndX509SubjectNames().get(1);
							
				if ( el2e == null )
				{
					return errorResponse( "SIGN_VERIFICATION_FAILED", true, txnRecord,authReqObj );
				}
							
				byte[] cert = (byte[])el2e.getValue();
				Map<String, String> verSign= verifySignature(authReqXML, cert);
				if (verSign.get("status").equalsIgnoreCase("n"))
				{
					return errorResponse(verSign.get("code"), true, txnRecord, authReqObj);
							
				}
					signedXml=authReqXML;
			}
			if(!verifySign)
			{
				signedXml=authReqXML;
			}
			if (verifyCi)
			{
				errorCode = verifyUIDCI(authReqObj.getSkey().getCi());
				if( errorCode != null )
				{
					return errorResponse(errorCode, true, txnRecord, authReqObj);
				}
			}
			char[] uidCharArray = {'0','0'};
			if (uidType.equalsIgnoreCase("A"))	
			{
				uidCharArray = authReqObj.getUid().toCharArray();
			}
			url = generateURL( uidaiAuthUrl, uidCharArray, lkDet.getAsa_license_key(), authReqObj.getAc() );
			txnRecord.setUidaiUrl( url );
			txnRecord.setServiceType( UIDAI_SERVICE );
			Map<String, String> uidaiResponse=callWebService( signedXml, txnRecord );
			if ( uidaiResponse.get("status").equalsIgnoreCase("n") ) 
			{
				authResponse=errorResponse( uidaiResponse.get("data"), true, txnRecord ,authReqObj);
				return authResponse;
			}
			uidaiAuthResponse=uidaiResponse.get("data");
			authResponse=updateResponse( uidaiAuthResponse, txnRecord,authReqObj );
			if( authResponse !=null )
			{
				return authResponse;	
			}
		}
		catch (Exception excp) 
		{
			logger.info("Main Exception in Class Name:AuthRequestProcessor Method Name: processReq",excp);
			authResponse=errorResponse("AUTH_SERVICE_ISSUE",false, txnRecord,authReqObj);
			return authResponse;
		}
		return uidaiAuthResponse;
	}

	/**
	 * 
	 * @param ac
	 * @param schema
	 * @return
	 */
	private AUADetails getAUADetails(String ac, String schema) 
	{
		AUADetails auaDet =	 authDaoOperations.getAUADetails(ac, schema);
		return auaDet;
	}

	/**
	 * 
	 * @param ac
	 * @param lkdet
	 * @param transactionId
	 * @return
	 */
	private String verifyAUA( String ac, AUADetails lkdet, String transactionId,String lk ) 
	{
		if( !lkdet.getAua_code().trim().equalsIgnoreCase(ac.trim()) )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		if( !lkdet.getLk().trim().equalsIgnoreCase(lk.trim()) )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		int lk_dateDiff = utilityFunctions.validateDate( lkdet.getLk_expiry() );
		if ( lk_dateDiff > 0 )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		int aua_dateDiff = utilityFunctions.validateDate( lkdet.getAua_valid_till() );
		if ( aua_dateDiff > 0 )
		{
			return "AUA_VERIFICATION_FAILED";
		}
		return null;
	}

	/**
	 * 
	 * @param lkdet
	 * @param txn
	 * @return
	 */
	private String verifyASALKValidity(LKDetails lkdet, String txn) 
	{
		int asa_dateDiff = utilityFunctions.validateDate( lkdet.getAsa_valid_till() );
		if ( asa_dateDiff > 0 )
		{
			return "AUTH_ASA_LK_EXPIRED";
		}
		return null;
	}
	/**
	 * 
	 * @param authReqObj
	 * @param uidType
	 * @return
	 * @throws Exception
	 */
	private String semanticValidation( Auth authReqObj, String uidType ) throws Exception 
	{
		String errorCode=null;
		Map<String, String> validateXMLSem = validate.validateXMLSem(authReqObj, uidType);
		if (validateXMLSem.get("status").equalsIgnoreCase("n")) 
		{
			errorCode=validateXMLSem.get("code");
			return errorCode;
		} 
		return errorCode;
	}
	/**
	 * 
	 * @param auaCode
	 * @param schema
	 * @return
	 */
	private LKDetails getAsaLkDetails(String auaCode, String schema )
	{
		String licenseType = asaLicenseType;
		LKDetails auaLkDetails = authDaoOperations.asaLkDetails( auaCode, schema, licenseType);
		return auaLkDetails;
	}
	/**
	 * 
	 * @param txnRecord
	 * @param authReqXml
	 * @param certdet
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> digitalSigning( AuthTransactionRecord txnRecord, String authReqXml, CryptoACLKCert certdet )
	{
		Map<String, String> mapResult=new HashMap<>();
		String cryptoUrl = cryptoServiceBaseUrl + "/"+SIGNING;
		txnRecord.setCryptoServiceUrl(cryptoUrl);
		txnRecord.setServiceType(CRYPTO_SERVICE);
		Request cryptoReq=new Request();
		Response crytpoResp;
		try
		{
			cryptoReq.setAc( certdet.getCrypto_ac() );
			cryptoReq.setLk( certdet.getCrypto_lk() );
			cryptoReq.setTxn( txnRecord.getTransactionId() );
			cryptoReq.setOpr( Operations.fromValue(certdet.getCrypto_opr().trim()) );
			cryptoReq.setKeyidentifier( certdet.getKey_identifier() );
			cryptoReq.setKeytype( KeyType.fromValue(certdet.getKey_algorithm().trim()) );
			cryptoReq.setTkntype( TokenType.fromValue(certdet.getToken_type().trim()) );
			cryptoReq.setTs(Utility.generateTimeStamp());
			DataElement dle = new DataElement();
			dle.setValue(Base64.getEncoder().encode(authReqXml.getBytes("UTF-8")));
			cryptoReq.setData(dle);
			String crytpoXML=marshalCryptoXml(cryptoReq);
			mapResult = callWebService(crytpoXML, txnRecord);
			if (mapResult.get("status").equalsIgnoreCase("y")) 
			{
				crytpoResp = (Response) marshaller.unmarshal(new StreamSource(new StringReader(mapResult.get("data"))));
				if( crytpoResp.getStatus().equals(Status.N) )
				{
					mapResult = getMapResult("n", "AUTH_SIGNING_ISSUE");
					return mapResult;
				}
				mapResult = getMapResult("y", new String( Base64.getDecoder().decode( crytpoResp.getData().getValue()), StandardCharsets.UTF_8) );
				logger.info("Auth Request Signed Succesfully by Crypto-Service");
			} 
			else 
			{
				mapResult = getMapResult("n", "AUTH_SIGNING_ISSUE");
				return mapResult;
			}
		}
		catch(Exception excp)
		{
			logger.info("Crypto Service Invocation or Marshalling error in Class Name:AuthRequestProcessor Method Name: digitalSigning",excp);
			mapResult = getMapResult("n", "AUTH_SIGNING_ISSUE");
			return mapResult;
		}
		return mapResult;
	}
	/**
	 * 
	 * @param errMsg
	 * @param logInDB
	 * @param txnRecord
	 * @param auth
	 * @return
	 */
	private String errorResponse( String errMsg, boolean logInDB, AuthTransactionRecord txnRecord,Auth auth ) 
	{
		String errorCode=null;
		String authResponse=null;
		txnRecord.setResponseTs(Utility.generateTimeStamp());
		errorCode = environment.getProperty(errMsg);
		txnRecord.setErrorCode(errorCode);
		txnRecord.setRet("n");
		Timestamp respForwardTime = new Timestamp(System.currentTimeMillis());
		txnRecord.setRespForwardTime(respForwardTime);
		txnRecord.setRespPacketTime(new Timestamp(txnRecord.getResponseTs().toGregorianCalendar().getTimeInMillis()));
		authResponse = errorHandler.errorResponse(txnRecord.getTransactionId(), errorCode, txnRecord.getResponseTs());
		if(logInDB)
		{
			authDaoOperations.insertASAAuthRecord(auth,txnRecord);
		}
		logger.info("Error returned in Class Name:AuthRequestProcessor Method Name: errorResponse:"+ "Txn:" +txnRecord.getTransactionId()+ "ErrorCode:" +errorCode + "ErrorMessage:" +errMsg);
		return authResponse;
	}
	/**
	 * 
	 * @param responseXml
	 * @param txnRecord
	 * @return
	 */
	private String updateResponse( String responseXml, AuthTransactionRecord txnRecord ,Auth authReqObj)
	{
		String authResponse=null;
		AuthRes authResObj=null;
		try 
		{
			authResObj = (AuthRes) marshaller.unmarshal(new StreamSource(new StringReader(responseXml)));
		} 
		catch (Exception excp) 
		{
			logger.info("Error in Class Name:AuthRequestProcessor Method Name: updateResponse in parsing the response XML",excp);
			return errorResponse("UNABLE_TO_PARSE_AUTH_RESPONSE",true, txnRecord,authReqObj);
		}
		authDaoOperations.insertASAAuthRecord(authReqObj, getResponseBean(authResObj, txnRecord));
		logger.info("Auth Response returned from ASA for Transaction ID:" +txnRecord.getTransactionId()+ "Status:" +txnRecord.getRet()+ "ErrorCode:"+txnRecord.getErrorCode());
		return authResponse;
	}

	/**
	 * 
	 * @param response
	 * @param txnRecord
	 * @return
	 */
	private AuthTransactionRecord getResponseBean(AuthRes response, AuthTransactionRecord txnRecord) 
	{
		String txn =txnRecord.getTransactionId();
		/*
		 * if ( extractUidToken ) {
		 * txnRecord.setUidToken(extractToken(response.getInfo(),txn)); }
		 */
		txnRecord.setRet(response.getRet().value());
		txnRecord.setTransactionId(txn);
		txnRecord.setErrorCode(response.getErr());
		txnRecord.setActn(response.getActn());
		Timestamp respForwardTime = new Timestamp(System.currentTimeMillis());
		txnRecord.setRespForwardTime(respForwardTime);
		if (response.getTs() != null) 
		{
			txnRecord.setRespPacketTime(new Timestamp(response.getTs().toGregorianCalendar().getTimeInMillis()));
		}
		txnRecord.setCode(response.getCode());
		return txnRecord;
	}

	/**
	 * 
	 * @param infoString
	 * @param txn
	 * @return
	 */
	public String extractToken(String infoString,String txn)	
	{
		String token = null;
		String info = null;
		String tokenArray[] = null;
		try 
		{
			if (infoString != null)
			{
				info = infoString.substring(infoString.indexOf("{")+1, infoString.indexOf("}")-1);
				tokenArray = info.split(",");

				if (tokenArray.length>0)	
				{
					token = tokenArray[0];
				}
			}
			else 
			{
				logger.info("Exception in extracting uid token: Txn: " +txn);
			}
		}
		catch (Exception e) 
		{
			logger.info("Exception in extracting uid token: Txn: " +txn,e);
			return null;
		}
		return token;
	}

	/**
	 * 
	 * @param reqXMLForward
	 * @param txnRecord
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String,String> callWebService(String reqXMLForward, AuthTransactionRecord txnRecord)
	{
		Map<String, String> mapResult=new HashMap<>();
		Map<String, String> response = new HashMap<>();
		switch (txnRecord.getServiceType()) 
		{
		case UIDAI_SERVICE:
			txnRecord.setReqForwardTime(new Timestamp(System.currentTimeMillis()));
			response = callRestService.callRestMethod(txnRecord.getUidaiUrl(), reqXMLForward,txnRecord.getTransactionId());
			txnRecord.setRespReceiptTime(new Timestamp(System.currentTimeMillis()));
			break;
		case CRYPTO_SERVICE:
			response = callRestService.callRestMethodCrypto(txnRecord.getCryptoServiceUrl(), reqXMLForward);
			break;
		default:
			break;
		}
		if (response.get("status").equalsIgnoreCase("n")) 
		{
			mapResult=getMapResult("n", response.get("code"));
		}
		else
		{
			mapResult=getMapResult("y", response.get("data"));
		}
		return mapResult;
	}

	/**
	 * 
	 * @param cryptoObj
	 * @return
	 * @throws Exception
	 */
	private String marshalCryptoXml(Request cryptoObj) throws Exception
	{
		String cryptoXml=null;;
		OutputStream out = new ByteOutputStream();
		Writer writer=null;
		writer = new OutputStreamWriter(out, "UTF-8");
		StreamResult result = new StreamResult(writer);
		marshaller.marshal(cryptoObj, result);
		cryptoXml = out.toString();
		return cryptoXml;
	}

}
