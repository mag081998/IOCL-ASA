package in.cdac.aadhaarservices.daooperations.auth;

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
import in.cdac.aadhaarservices.model.AuthTransactionRecord;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;

/**
 * 
 * This class persist the data related to authentication requests
 * 
 */
@Component
@EnableAsync
public  class AuthDaoOperations extends DaoCommon 
{
	private static final Logger logger = LogManager.getLogger(AuthDaoOperations.class);
	private static final Logger authdbinfile = LogManager.getLogger("authdbinfile");
	
	
public static String ipAddress = "0.0.0.0";
	
	@PostConstruct
	public String getIpAddress() {
		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			ipAddress=localhost.getHostAddress().trim();
			logger.info("Server IP Address : " + ipAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ipAddress;
	}


	
	@Async("asyncExecutor")
	public void insertASAAuthRecord(Auth auth,AuthTransactionRecord transactionRecord) 
	{
		Map<String, String> map = new HashMap<String, String>();
		String ac = auth.getAc();
		String rc = auth.getRc().name();
		String pi = null,pa = null,pfa = null,pin = null,bio = null;
		String bt = null,otp = null;
		String ver = auth.getVer();
		String schemaName=transactionRecord.getSchema();
		Timestamp reqTimestamp=transactionRecord.getReqReceiptTime();
		String clientIP=transactionRecord.getClientIP();
		String uidType=transactionRecord.getUidType();
		String txn = transactionRecord.getTransactionId();
		String ret = transactionRecord.getRet();
		String err = transactionRecord.getErrorCode();
		String actn = transactionRecord.getActn();
		Timestamp respForwardTime = transactionRecord.getRespForwardTime();
		Timestamp reqForwardTime = transactionRecord.getReqForwardTime();
		Timestamp respReceiptTime = transactionRecord.getRespReceiptTime();
		Timestamp respPacketTime = transactionRecord.getRespPacketTime();
		String respCode = transactionRecord.getCode();
		String reqAC=transactionRecord.getReqAC();
		String reqSA=transactionRecord.getReqSA();
		String ci=auth.getSkey().getCi();

		if(auth.isSetUses())
		{
			if(auth.getUses().isSetPi())
			{
				pi = auth.getUses().getPi().name();
			}
			if(auth.getUses().isSetPa())
			{
				pa = auth.getUses().getPa().name();
			}
			if(auth.getUses().isSetPfa())
			{
				pfa = auth.getUses().getPfa().name();
			}
			if(auth.getUses().isSetOtp())
			{
				otp = auth.getUses().getOtp().value();
			}
			if(auth.getUses().isSetPin())
			{   
				pin = auth.getUses().getPin().name();
			}
			if(auth.getUses().isSetBio())
			{   
				bio = auth.getUses().getBio().name();
			}
			bt = auth.getUses().getBt();
		}
		try
		{
			String sql = AuthDBQueries.insertASAAuthQuery(schemaName);
			int statusoferror = jdbcTemplate.update(sql, new Object[] { txn, 
					reqAC, ver, pi, pa, pfa, bio, bt, pin,otp, reqSA, rc, reqTimestamp,clientIP, uidType, respCode,actn,ret,err,reqForwardTime,respReceiptTime,respForwardTime,respPacketTime,ipAddress,ci});
			logger.info("AUTH DB insertion status "+statusoferror);

			if (statusoferror != 0)
			
			{
				map = getMapStringResult("y", null,null);
			}
			else
			{
				logger.info("Problem in insertion of Auth Record in Class_Name: AuthDaoOperations Method_Name: insertAuthRecord for:" +ac +"in Schema:" +schemaName +"Txn:" +txn);
				map = getMapStringResult("n", "AUTH_DB_EXCEPTION",null);
				insertInFile(auth, transactionRecord, map);

			}
		}
		catch (Exception excp)
		{
			logger.info("Exception in Class_Name: AuthDaoOperations Method_Name: insertAuthRecord for:" +ac + "in Schema:" +schemaName+"Txn:" +txn, excp);
			map = getMapStringResult("n", "AUTH_DB_EXCEPTION",null);
			insertInFile( auth,transactionRecord, map);

		}
	}
	/**
	 * 
	 * @param auth
	 * @param aTxn
	 * @param map
	 */
	private void insertInFile( Auth auth,AuthTransactionRecord aTxn, Map<String, String> map ) 
	{
		
		if ( ((String)map.get(Constants.STATUS)).equalsIgnoreCase(Constants.NO) )
		{
			StringBuilder strbld = new StringBuilder();
			try 
			{
				
				String pi = null,pa = null,pfa = null,pin = null,bio = null;
				String bt = null,otp = null; 
				
				if(auth.isSetUses())
				{
					if(auth.getUses().isSetPi())
					{
						pi = auth.getUses().getPi().name();
					}
					if(auth.getUses().isSetPa())
					{
						pa = auth.getUses().getPa().name();
					}
					if(auth.getUses().isSetPfa())
					{
						pfa = auth.getUses().getPfa().name();
					}
					if(auth.getUses().isSetOtp())
					{
						otp = auth.getUses().getOtp().value();
					}
					if(auth.getUses().isSetPin())
					{   
						pin = auth.getUses().getPin().name();
					}
					if(auth.getUses().isSetBio())
					{   
						bio = auth.getUses().getBio().name();
					}
					bt = auth.getUses().getBt();
				}
				strbld.append(aTxn.getTransactionId()).append(",").append(aTxn.getReqAC()).append(",").append(auth.getVer()).append(",")
				.append(pi).append(",").append(pa).append(",").append(pfa).append(",").append(bio).append(",")
				.append(bt).append(",").append(pin).append(",").append(otp).append(",").append(aTxn.getReqSA()).append(",")
				.append(auth.getRc().name()).append(",").append(aTxn.getReqReceiptTime()).append(",")
				.append(aTxn.getClientIP()).append(",").append(aTxn.getUidType()).append(",").append(aTxn.getCode()).append(",").append(aTxn.getActn()).append(",")
				.append(aTxn.getRet()).append(",")
				.append(aTxn.getErrorCode()).append(",").append(aTxn.getReqForwardTime()).append(",")
				.append(aTxn.getRespReceiptTime()).append(",").append(aTxn.getRespForwardTime()).append(",")
				.append(aTxn.getRespPacketTime()).append(",").append(ipAddress).append(",").append(auth.getSkey().getCi());
				authdbinfile.info(strbld.toString());
			}
			catch(Exception excp)
			{
				logger.info(" Exception occured while inserting into file : ",excp);
			}
		}
	
	}

}