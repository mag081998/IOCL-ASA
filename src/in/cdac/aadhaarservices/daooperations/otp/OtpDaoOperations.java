package in.cdac.aadhaarservices.daooperations.otp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import in.cdac.aadhaarservices.constants.Constants;
import in.cdac.aadhaarservices.daooperations.DaoCommon;
import in.cdac.aadhaarservices.model.OtpTransactionRecord;
import in.cdac.otp_v1.Otp;

/**
 * This class carries out DB related operations for OTP Request-Response
 * @author root
 */

@Component
@EnableAsync
public  class OtpDaoOperations extends DaoCommon 
{
	private static final Logger logger = LogManager.getLogger(OtpDaoOperations.class);
	private static final Logger otpdbinfile = LogManager.getLogger("otpdbinfile");
	
	
	/**
	 * 
	 * @param otp
	 * @param schemaName
	 * @param reqTimestamp
	 * @param clientIP
	 * @return
	 */
	
public static String ipAddress = "0.0.0.0";
	
	@PostConstruct
	public String getIpAddress() 
	{
		InetAddress localhost;
		try 
		{
			localhost = InetAddress.getLocalHost();
			ipAddress=localhost.getHostAddress().trim();
			logger.info("Server IP Address : " + ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ipAddress;
	}
	

	
	@Async("asyncExecutor")
	public void insertOtpRecord( Otp otp,OtpTransactionRecord transactionRecord )
	{
		Map<String, String> map = new HashMap<String, String>();
		String ver = otp.getVer();
		String ac = otp.getAc();
		String type = null;
		String txn = transactionRecord.getTransactionId();
		String ret = transactionRecord.getRet();
		String err = transactionRecord.getErrorCode();
		Timestamp respForwardTime = transactionRecord.getRespForwardTime();
		Timestamp reqForwardTime = transactionRecord.getReqForwardTime();
		Timestamp respReceiptTime = transactionRecord.getRespReceiptTime();
		Timestamp respCidrTime = transactionRecord.getRespCidrTime();
		String respCode = transactionRecord.getCode();
		String schemaName=transactionRecord.getSchema();
		Timestamp reqTimestamp=transactionRecord.getReqReceiptTime();
		String clientIP=transactionRecord.getClientIP();
		String reqAC=transactionRecord.getReqAC();
		String reqSA=transactionRecord.getReqSA();
		String ch=otp.getOpts().getCh();


		if (otp.getType() != null)	
		{
			type = otp.getType().value();
		}
		if ((txn == null) || (txn == "")) 
		{
			txn = null;
		}
		Timestamp packetTimestamp = new Timestamp(otp.getTs().toGregorianCalendar().getTimeInMillis());
		try
		{
			String sql = OtpDBQueries.insertASAOtpQuery( schemaName );
			
			int statusoferror = this.jdbcTemplate.update(sql, new Object[] { txn, 
					reqAC, ver, reqSA, type, reqTimestamp, packetTimestamp, 
					clientIP,
					reqForwardTime,
					respReceiptTime,
					respForwardTime,respCidrTime,ret,respCode,err,ipAddress,ch});
			logger.info("OTP DB insertion status "+statusoferror);
			
			if (statusoferror != 0)
			
			{
				map = getMapStringResult("y", null,null);
			}
			else
			{
				logger.info("Problem in insertion of OTP Record in Class_Name: OtpDaoOperations Method_Name: insertOtpRecord for" +ac +"in Schema:" +schemaName +"Txn:" +txn);
				map = getMapStringResult("n", "OTP_DB_EXCEPTION",null);
				insertInFile(otp,transactionRecord, map);

			}
		}catch(Exception excp)
		{
			logger.info("Exception in Class_Name: OtpDaoOperations Method_Name: insertOtpRecord for:" +ac + "in Schema:" +schemaName +"Txn:"+ txn,excp);
			map = getMapStringResult("n", "OTP_DB_EXCEPTION",null);
			insertInFile(otp,transactionRecord, map);
		}
	}
	
	/**
	 * 
	 * @param oTxn
	 * @param map
	 */
	private void insertInFile(Otp otp, OtpTransactionRecord oTxn, Map<String, String> map ) 
	{
		
		if ( ((String)map.get(Constants.STATUS)).equalsIgnoreCase(Constants.NO) )
		{
			StringBuilder strbld = new StringBuilder();
			try 
			{
				String type = null;
				String txn = oTxn.getTransactionId();
				
				
				if (otp.getType() != null)	
				{
					type = otp.getType().value();
				}
				if ((txn == null) || (txn == "")) 
				{
					txn = null;
				} 
				strbld.append(txn).append(",").append(oTxn.getReqAC()).append(",").append(otp.getVer()).append(",")
				.append(oTxn.getReqSA()).append(",").append(type).append(",")
				.append(""+oTxn.getReqReceiptTime()).append(",")
				.append(""+otp.getTs().toGregorianCalendar().getTimeInMillis()).append(",")
				.append(oTxn.getClientIP()).append(",")
				.append(""+oTxn.getReqForwardTime()).append(",")
				.append(oTxn.getRespReceiptTime()).append(",")
				.append(""+oTxn.getRespForwardTime()).append(",")
				.append(""+oTxn.getRespCidrTime()).append(",").append(oTxn.getRet()).append(",")
				.append(oTxn.getCode()).append(",").append(oTxn.getErrorCode()).append(",").append(ipAddress).append(",").append(otp.getOpts().getCh());
				
				otpdbinfile.info(strbld.toString());
			}
			catch(Exception excp)
			{
				logger.info( " Exception occured while inserting into file : ", excp );
				logger.info( " otp db in file : " +strbld.toString() );
			}
		}
	
	}

}