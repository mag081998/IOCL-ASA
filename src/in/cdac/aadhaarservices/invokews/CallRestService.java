package in.cdac.aadhaarservices.invokews;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * This class call the external web-services
 * @author root
 *
 */
@Component
public final class CallRestService implements ICallRestService
{
	private static final Logger logger = LogManager.getLogger(CallRestService.class);
	@Qualifier("RestOperations")
	@Autowired
	RestTemplate restTemplate;
	//@Qualifier("cryptotemplate")
	@Autowired
	RestTemplate cryptoRestTemplate;
	
	/**
	 * 
	 */
	public Map<String,String> callRestMethod(String sendUrl, String req,String TXID)
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		try
		{
			logger.debug("ASA Req-XML:"+ req);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			HttpEntity<String> entity = new HttpEntity<String>(req,headers);
			String authResonse = (String)restTemplate.postForObject(sendUrl, entity, String.class);
			logger.debug("ASA Res-XML:"+ authResonse);
			if (authResonse != null)	
			{
				resultMap = getMapResult("y",null,authResonse);
			}
			else	
			{
				resultMap = getMapResult("n", "RECEIVED_NULL_RESPONSE",null);
			}
		}
		catch(RestClientException excp)
		{
			if(excp.getMessage().contains("SocketTimeoutException")) 
			{
				logger.info("Socket Timeout for TXID::"+TXID, excp);
				resultMap = getMapResult("n", "UIDAI_RESPONSE_TIME_OUT",null);
			}
			else if(excp.getMessage().contains("ConnectTimeoutException")) 
			{
				logger.info("ConnectTimeoutException Timeout for TXID::"+TXID, excp);
				resultMap = getMapResult("n", "UIDAI_CONNECT_TIME_OUT",null);
			}
			else
			{
				logger.info("Error in Class_Name: CallRestService Method_Name: callRestMethod for REST Connection", excp);
				resultMap = getMapResult("n", "UNABLE_TO_CONNECT_UIDAI_SERVICE",null);
			}
		}
		catch (Exception excp)
		{
			logger.info("Error in Class_Name: CallRestService Method_Name: callRestMethod for REST Connection", excp);
			resultMap = getMapResult("n", "UNABLE_TO_CONNECT_UIDAI_SERVICE",null);
		}
		return resultMap;
	}

	/**
	 * 
	 */
	@Override
	public Map<String,String> callRestMethodCrypto(String sendUrl, String req)
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		try
		{
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			HttpEntity<String> entity = new HttpEntity<String>(req,headers);
			String response = (String)cryptoRestTemplate.postForObject(sendUrl, entity, String.class);
			if (response != null)	
			{
				resultMap = getMapResult("y",null,response);
			}
			else	
			{
				resultMap = getMapResult("n", "CRYPTO_SERVICE_ISSUE",null);
			}
		}
		catch(RestClientException excp)
		{
			if(excp.getMessage().contains("SocketTimeoutException")) 
			{
				
				resultMap = getMapResult("n", "CRYPTO_RESPONSE_TIME_OUT",null);
			}
			else if(excp.getMessage().contains("ConnectTimeoutException")) 
			{
				
				resultMap = getMapResult("n", "CRYPTO_CONNECT_TIME_OUT",null);
			}
			else
			{
				logger.info("Error in Class_Name: CallRestService Method_Name: callRestMethod for REST Connection",excp);
				resultMap = getMapResult("n", "CRYPTO_SERVICE_ISSUE",null);
			}
		}
		catch (Exception excp)
		{
			logger.info("Error in Class_Name: CallRestService Method_Name: callRestMethodCrypto for REST Connection of Crypto or Vault Service",excp);
			resultMap = getMapResult("n", "CRYPTO_SERVICE_ISSUE",null);
		}
		return resultMap;
	}
	
	/**
	 * 
	 * @param status
	 * @param code
	 * @return
	 */
	private Map<String,String> getMapResult(String status, String code,String resp) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("status", status);
		resultMap.put("code", code);
		resultMap.put("data", resp);
		return resultMap;
	}
}
