package in.cdac.aadhaarservices.daooperations.kyc;

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
import generated.Kyc;
import in.cdac.aadhaarservices.constants.Constants;
import in.cdac.aadhaarservices.daooperations.DaoCommon;
import in.cdac.aadhaarservices.model.KycTransactionRecord;
import in.gov.uidai.authentication.uid_auth_request._2.Auth;

/**
 * 
 * This class persist data related to KYC Request-Response
 *
 */
@Component
@EnableAsync
public   class KycDaoOperations extends DaoCommon 
{
	private static final Logger logger = LogManager.getLogger(KycDaoOperations.class);
	private static final Logger kycdbinfile = LogManager.getLogger("kycdbinfile");
	
	
public static String ipAddress = "0.0.0.0";
	
	@PostConstruct
	public String getIpAddress() {
		InetAddress localhost;
		try 
		{
			localhost = InetAddress.getLocalHost();
			ipAddress=localhost.getHostAddress().trim();
			logger.info("Server IP Address : " + ipAddress);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		return ipAddress;
	}

	@Async("asyncExecutor")
	public void insertASAKycRecord(Kyc kyc, Auth auth, KycTransactionRecord tRec) 
	{
		Map<String, String> map = new HashMap<String, String>();
		String authTxn = auth.getTxn();
		String ac = auth.getAc();
		String sa = auth.getSa();
		String pi = null,pa = null,pfa = null,pin = null,bio = null;
		String bt = null,otp = null;
		String authRc = auth.getRc().name();
		String authVer = auth.getVer();
		String ra = null;
		String rc = null;
		String lr = null;
		String de = null;
		String pfr = null;
		
		String ret = tRec.getRet().toUpperCase();
		String err = tRec.getErrorCode();
		String actn = tRec.getActn();
		Timestamp respForwardTime = tRec.getRespForwardTime();
		Timestamp reqForwardTime = tRec.getReqForwardTime();
		Timestamp respReceiptTime = tRec.getRespReceiptTime();
		Timestamp respPacketTime = tRec.getRespPacketTime();
		String status = tRec.getStatus();
		Timestamp ts = tRec.getTs();
		String ko = tRec.getKo();
		String respCode = tRec.getCode();
		String auth_actn = tRec.getAuthActn();
		String auth_err = tRec.getAuthErr();
		String kyc_err = tRec.getKycErr();
		String kyc_ret = tRec.getKycRet();
		
		String schemaName=tRec.getSchema();
		Timestamp reqTimestamp=tRec.getReqReceiptTime();
		String clientIP=tRec.getClientIP();
		String uidType=tRec.getUidType();
		String ci=auth.getSkey().getCi();
		
		
		if(auth.isSetUses()){
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
		
		if ( kyc.isSetRa() )
		{
			ra = kyc.getRa().name();
		}
		
		if ( kyc.isSetRc() )
		{
			rc = kyc.getRc().name();
		}
		
		if ( kyc.isSetLr() )
		{
			lr = kyc.getLr().name();
		}
		
		if ( kyc.isSetDe() )
		{
			de = kyc.getDe().name();
		}
		
		if ( kyc.isSetPfr() )
		{
			pfr = kyc.getPfr().name();
		}
		
		try
		{
			String sql = KycDBQueries.insertASAKycQuery(schemaName);
			int statusoferror = jdbcTemplate.update(sql, new Object[] { kyc.getVer(), ra, rc , lr, de, pfr,
					authTxn, authRc, ac,sa, authVer,authTxn, pi, pa, pfa, bio, bt, pin,otp, reqTimestamp, clientIP, uidType,
					respCode,status,ts,ko,actn,ret,err,reqForwardTime, respReceiptTime, respForwardTime, respPacketTime, auth_actn, auth_err, kyc_err, kyc_ret,ipAddress,ci});
			logger.info("KYC DB insertion status "+statusoferror);

			if (statusoferror != 0)
			
			{
				map = getMapStringResult("y", null,null);
			}
			else
			{
				logger.info("Problem in insertion of KYC Record in Class_Name: KYCDaoOperations Method_Name: insertKycRecord for:" +ac +"in Schema:" +schemaName +"Txn:" +authTxn);
				map = getMapStringResult("n", "KYC_DB_EXCEPTION",null);
				insertInFile(kyc,auth,tRec, map);

			}
		}
		catch (Exception excp)
		{
			logger.info("Exception in Class_Name: KYCDaoOperations Method_Name: insertKycRecord for:" +ac + "in Schema:" +schemaName +"Txn:" +authTxn,excp);
			map = getMapStringResult("n", "KYC_DB_EXCEPTION",null);
			insertInFile(kyc,auth,tRec, map);

		}
		
	}
	
	/**
	 * 
	 * @param kTxn
	 * @param map
	 */
	private void insertInFile( Kyc kyc,Auth auth,KycTransactionRecord kTxn, Map<String, String> map ) 
	{
		
		if ( ((String)map.get(Constants.STATUS)).equalsIgnoreCase(Constants.NO) )
		{
			StringBuilder strbld = new StringBuilder();
			try 
			{
				
				String pi = null,pa = null,pfa = null,pin = null,bio = null;
				String bt = null,otp = null;
				String ra = null;
				String rc = null;
				String lr = null;
				String de = null;
				String pfr = null;
				
				
				if(auth.isSetUses()){
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
				
				if ( kyc.isSetRa() )
				{
					ra = kyc.getRa().name();
				}
				
				if ( kyc.isSetRc() )
				{
					rc = kyc.getRc().name();
				}
				
				if ( kyc.isSetLr() )
				{
					lr = kyc.getLr().name();
				}
				
				if ( kyc.isSetDe() )
				{
					de = kyc.getDe().name();
				}
				
				if ( kyc.isSetPfr() )
				{
					pfr = kyc.getPfr().name();
				}

				
				strbld.append(kyc.getVer()).append(",").append(ra).append(",").append(rc).append(",").append(lr).append(",").append(de).append(",").append(pfr)
					.append(",").append(auth.getTxn()).append(",").append(auth.getRc().name()).append(",").append(auth.getAc()).append(",").append(auth.getSa())
					.append(",").append(auth.getVer()).append(",").append(auth.getTxn()).append(",").append(pi).append(",").append(pa).append(",")
					.append(pfa).append(",").append(bio).append(",").append(bt).append(",").append(pin).append(",").append( otp ).append(",")
					.append(kTxn.getReqReceiptTime()).append(",").append(kTxn.getClientIP()).append(",").append(kTxn.getUidType()).append(",")
					.append(kTxn.getCode()).append(",").append(kTxn.getStatus()).append(",").append(kTxn.getTs()).append(",")
					.append(kTxn.getKo()).append(",").append(kTxn.getActn()).append(",").append(kTxn.getRet()).append(",").append(kTxn.getErrorCode()).append(",")
					.append(kTxn.getReqForwardTime()).append(",").append(kTxn.getRespReceiptTime()).append(",")
					.append(kTxn.getRespForwardTime()).append(",").append(kTxn.getRespPacketTime()).append(kTxn.getAuthActn()).append(",")
					.append(kTxn.getAuthErr()).append(",").append(kTxn.getKycErr()).append(",").append(kTxn.getKycRet()).append(",").append(ipAddress).append(",").append(auth.getSkey().getCi());

				kycdbinfile.info(strbld.toString());
			}
			catch(Exception excp)
			{
				logger.info(" Exception occured while inserting into file : ",excp);
				logger.info(" kyc db in file : " +strbld.toString());
			}
		}
	
	}

}