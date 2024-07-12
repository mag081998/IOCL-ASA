package in.cdac.aadhaarservices.invokews;

import java.util.Map;

/**
 * 
 * @author root
 *
 */
public interface ICallRestService
{
	/**
	 * 
	 * @param sendUrl
	 * @param xmlData
	 * @return
	 */
	public Map<String,String>  callRestMethod(String sendUrl, String xmlData,String TXID);
	public Map<String,String>  callRestMethodCrypto(String sendUrl, String xmlData);
	
}
