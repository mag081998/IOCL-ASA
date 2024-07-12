package in.cdac.aadhaarservices.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
/**
 * 
 * @author root
 *
 */
@Component
public final class Utility 
{
	private static final Logger logger = LogManager.getLogger(Utility.class);
	private static final SimpleDateFormat cisdf = new SimpleDateFormat("yyyyMMdd");

	
	public static XMLGregorianCalendar TimestampToXmlGregorian (Timestamp ts) throws DatatypeConfigurationException	
	{
		GregorianCalendar gCal = null;
		XMLGregorianCalendar xmlGreg = null;
		gCal = new GregorianCalendar();
		gCal.setTimeInMillis(ts.getTime());
		xmlGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCal);
		return xmlGreg;
	}
	/**
	 * 
	 * @return
	 */
	public static XMLGregorianCalendar generateTimeStampWithTimezone() 
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(System.currentTimeMillis());
		XMLGregorianCalendar timeStamp = null;
		try 
		{
			timeStamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		} 
		catch (DatatypeConfigurationException e) 
		{
			logger.info("Data type configuration exception : "+e.getMessage());
		}
		return timeStamp;
	}
	
	public Boolean validateDate(String ci)
	{
		boolean flag = false;
		try 
		{
			Date ciDte = cisdf.parse(ci);
			Date currentDate = new Date();
			String currentDateStr = cisdf.format(currentDate);
			Date formattedCurrDte = cisdf.parse(currentDateStr);
			int count = ciDte.compareTo(formattedCurrDte);
			if (count >= 0 )
				return flag=true;
			else
				return flag;
		}
		catch(Exception e)
		{
			logger.info("Error in Class: Utility Method: validateDate: Invalid Date ",e);
			return flag;
		}
	}

	/**
	 * @return XMLGregorianCalendar
	 * @description generates timestamp without timezone
	 */
	public static XMLGregorianCalendar generateTimeStamp()
	{
		GregorianCalendar gc = new GregorianCalendar();
		XMLGregorianCalendar currServTime = null;
		try 
		{
			currServTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			currServTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		} 
		catch (DatatypeConfigurationException e) 
		{
			logger.info("Data type configuration exception : "+e.getMessage());
		}
		return currServTime;
	}


	/**
	 * @return Timestamp
	 * @description generates timestamp for inserting in db
	 */
	public java.sql.Timestamp getCurrentTimeStamp() 
	{
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());

	}

	/**
	 * @param date
	 * @return XMLGregorianCalendar
	 * @description method to convert Date to XMLGregorianCalendar
	 */
	public XMLGregorianCalendar convertdatetoxmldate(Date date) throws DatatypeConfigurationException, Exception
	{
		XMLGregorianCalendar gregorianCalendar = null;
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		gregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return gregorianCalendar;

	}

	/**
	 * 
	 * @param ts
	 * @return
	 */
	public boolean validateTimestamp(XMLGregorianCalendar ts) 
	{

		Date d1 = ts.toGregorianCalendar().getTime();
		String formateddateOtp = new SimpleDateFormat("yyyy-MM-dd").format(d1);
		Date d2 = new Date();
		String currentFormateddateOtp = new SimpleDateFormat("yyyy-MM-dd").format(d2);
		boolean isValidTime = formateddateOtp.equalsIgnoreCase(currentFormateddateOtp);
		return isValidTime;
	}

	/**
	 * Verify Date in the received XML
	 * @param expiryDate
	 * @return
	 */
	public int validateDate(Date expiryDate) 
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dateForm = new Date();
		String format = dateFormat.format(dateForm);
		Date validTill = expiryDate;
		String format2 = dateFormat.format(validTill);
		int dateDiff = format.compareTo(format2);
		return dateDiff;
	}


	/**
	 * @param clazz
	 * @param obj
	 * @return String
	 * @description marshalling the object.
	 */
	@SuppressWarnings("rawtypes")
	public String marshallObj(Class clazz, Object obj) throws JAXBException,XMLStreamException, Exception
	{
		StringWriter xml = new StringWriter();
		JAXBContext.newInstance(clazz).createMarshaller().marshal(obj, xml);
		JAXBContext jc = JAXBContext.newInstance(clazz);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(obj, System.out);
		return xml.toString();
	}

	/**
	 * @param clazz
	 * @param xmlToParse
	 * @return Object
	 * @throws XMLStreamException 
	 * @throws JAXBException 
	 * @description unmarshallstringXml to the object.
	 */
	@SuppressWarnings({ "rawtypes" })
	public Object unmarshallXml(Class clazz, String xmlToParse) throws XMLStreamException, JAXBException, Exception 
	{
		{
			Object obj = null;
			JAXBContext jc=null;
			jc = JAXBContext.newInstance(clazz);
			XMLInputFactory xif = XMLInputFactory.newFactory();
			XMLStreamReader xsr = xif.createXMLStreamReader(new StreamSource(new StringReader(xmlToParse)));
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			obj =  unmarshaller.unmarshal(xsr);
			return obj;
		}
	}


}
