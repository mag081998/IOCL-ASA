package in.cdac.aadhaarservices.errorhandling;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * 
 * @author root
 *
 */

public interface IErrorHandler

{
	String errorResponse(String txnID, String errorCode,XMLGregorianCalendar respTimeStamp);
	
}
