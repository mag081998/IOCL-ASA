package in.cdac.aadhaarservices.daooperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import in.cdac.aadhaarservices.common.ConfigPara;
import in.cdac.aadhaarservices.common.ConfigParaKey;

@Component
public class DaoCommon
{
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	private static final Logger logger = LogManager.getLogger(DaoCommon.class);
	
	/**
	 * 
	 * @param ac
	 * @param schema
	 * @return
	 */
	@Cacheable(value="AUADETAILS",key="#ac+#schema",unless="!#result.status" )
	public AUADetails getAUADetails( String ac, String schema ) 
	{
		AUADetails auaDet = new AUADetails();
		try 
		{
			String sql = CommonDBQueries.getAUADetailsQuery(schema);
			auaDet = jdbcTemplate.query( sql, new Object[] { ac } , new AUADetailsExtractor() );
			
			if ( auaDet == null ) 
			{
				auaDet = new AUADetails();
				auaDet.setStatus(false);
				auaDet.setCode("AUA_DETAILS_NOT_FOUND");
				return auaDet;
			}
			
			if ( auaDet.getAua_code() == null || StringUtils.isEmpty(auaDet.getAua_code()) )
			{
				auaDet.setStatus(false);
				auaDet.setCode("AUA_DETAILS_NOT_FOUND");
				return auaDet;
			}
			auaDet.setStatus(true);
		}
		catch ( Exception excp )
		{
			logger.info("DB Exception in Class_Name: DaoCommon Method_Name: getAuaDetails for AC Code:-->"+ac + "in Schema:-->" +schema, excp);			
			auaDet.setCode("DB_EXCEPTION");
			auaDet.setStatus(false);
		}
		return auaDet;
	}
	
	/**
	 * 
	 * @param ac
	 * @param schema
	 * @param licensetype
	 * @return
	 */
	@Cacheable(value="ASALKDETAILS",key="#ac+#schema+#licensetype",unless="!#result.status" )
	public LKDetails asaLkDetails( String ac, String schema, String licensetype ) 
	{
		LKDetails lkdet = new LKDetails();
		try
		{
			String sql = CommonDBQueries.getASALkDetailsQuery( schema );
			lkdet = (LKDetails)jdbcTemplate.query(sql, new Object[] { ac, licensetype }, new ASALKDetailsExtractor());
			if ( lkdet == null )
			{
				lkdet = new LKDetails();
				lkdet.setStatus( false );
				lkdet.setCode( "ASA_LK_NOT_FOUND" );
				return lkdet;
			}
			if (StringUtils.isEmpty(lkdet.getAsa_license_key()))
			{
				lkdet.setStatus( false );
				lkdet.setCode( "ASA_LK_NOT_FOUND" );
				return lkdet;
			}
			lkdet.setStatus( true );
		}
		catch (Exception excp)
		{
			logger.info("DB Exception in Class_Name: DaoCommon Method_Name: asaLkDetails for AC Code:-->"+ac + "in Schema:-->" +schema, excp);			
			lkdet.setCode("DB_EXCEPTION");
			lkdet.setStatus( false );
		}
		return lkdet;
	}
	
	/**
	 * 
	 * @param sac
	 * @param schemaName
	 * @return
	 */
	@Cacheable(value="ASASCHEMADETAILS",key="#ac+#schemaName",unless="#result[status] == 'n'")
	public Map<String, Object> getSchemaName( String ac,String schemaName ) 
	{
		SchemaDetails schDet = new SchemaDetails();
		try
		{
			String sql = CommonDBQueries.getSchemaName( schemaName );
			schDet = (SchemaDetails)jdbcTemplate.query( sql, new Object[] { ac }, new SchemaDetailsExtractor() );
			
			if ( schDet == null )
			{
				logger.info("ASA Details not found in Class_Name: DaoCommon Method_Name: getSchemaName for AUA AC Code:-->" +ac );
				return getMapSchemaResult("n", "ASA_DETAILS_NOT_FOUND", null);
			}
			if (StringUtils.isBlank(schDet.getAc())) 
			{
				logger.info("ASA AC not found in Class_Name: DaoCommon Method_Name: getSchemaName for AUA AC Code:-->" +ac);
				return getMapSchemaResult("n", "ASA_DETAILS_NOT_FOUND", null);
			}
			return getMapSchemaResult( "y", null, schDet.getAc() );
		}
		catch (Exception excp)
		{
			logger.info("Exception while fetching ASA Details in Class_Name: DaoCommon Method_Name: getSchemaName for AUA AC Code:-->"+ac, excp);
			return getMapSchemaResult("n", "DB_EXCEPTION", ac);
		}
	}
	
	/**
	 * 
	 * @param ac
	 * @param schema
	 * @return
	 */
	@Cacheable(value="ASASIGNDETAILS",key="#ac+#schema",unless="!#result.status")
	public SignDet getSignDet( String ac, String schema ) 
	{
		SignDet signDet = new SignDet();
		try 
		{
			String sql = CommonDBQueries.getSignDetQuery( schema );
			signDet = (SignDet) jdbcTemplate.query( sql, new Object[] { ac }, new SignDetExt());
			if ( signDet == null )
			{
				signDet = new SignDet();
				signDet.setStatus(false);
				signDet.setCode( "SIGN_DETAILS_NOT_FOUND" );
				return signDet;
			}
			signDet.setStatus(true);
			return signDet;
		}
		catch(Exception e)
		{
			logger.info("Class: OtpDaoOperations: method: getSignDet: DBException : ", e);
			signDet.setStatus( false );
			signDet.setCode( "DB_EXCEPTION" );
			return signDet;
		}
	}

	/**
	 * 
	 * @param status
	 * @param schemaName
	 * @param isGlobalAUA
	 * @param errorCode
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> getMapSchemaResult(String status, String errorCode, String ac ) 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status",status);
		map.put("errorCode",errorCode);
		map.put("ac", ac);
		return map;
	}
	
	/**
	 * 
	 * @param status
	 * @param code
	 * @param resp
	 * @return
	 */
	protected Map<String,String> getMapStringResult(String status, String code,String resp) 
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("status", status);
		map.put("code", code);
		map.put("data", resp);
		return map;
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<ConfigParaKey, ConfigPara> getMasterDBConfig()
	{
		String sql = CommonDBQueries.getMasterConfigDBQuery();
		HashMap<ConfigParaKey, ConfigPara> configParas = new HashMap<ConfigParaKey,ConfigPara>();
		try
		{
			List<Map<String, Object>> resultRecords = jdbcTemplate.queryForList(sql);
			for (Map<String, Object> row : resultRecords) {
				ConfigPara configPara = new ConfigPara();
				configPara.setParaname(row.get("para_name").toString());
				configPara.setParavalue(row.get("para_value").toString());
				configParas.put(ConfigParaKey.getConfigPara(configPara.getParaname()), configPara);
			}
			return configParas;
		}
		catch(Exception e)
		{
			logger.info("Error in Loading Config Para"+e.getMessage(),e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param ac
	 * @param schema
	 * @param opr
	 * @return
	 */
	@Cacheable(value="ASACRYPTOACLKCERTDET",key="#ac+#schema+#opr",unless="!#result.status")
	public CryptoACLKCert getCryptoAcLkCertDet( String ac, String schema, String opr ) 
	{
		CryptoACLKCert crypaclk = new CryptoACLKCert();
		try 
		{
			String sql = CommonDBQueries.getCryptoACLKCertQuery( schema );
			crypaclk = jdbcTemplate.query( sql , new Object[] { ac , opr  }, new CryptoACLKExt() );
			if ( crypaclk == null )
			{
				crypaclk = new CryptoACLKCert();
				crypaclk.setStatus(false);
				crypaclk.setCode("CRYPTO_DETAILS_NOT_FOUND");
				return crypaclk;
			}
			crypaclk.setStatus( true );
			return crypaclk;
		}
		catch(Exception e)
		{
			logger.info("Class: OtpDaoOperations : Method: getCryptoAcLkCertDet : DBException " , e);
			crypaclk.setStatus( false );
			crypaclk.setCode("DB_EXCEPTION");
			return crypaclk;
		}
	}
	
public Map<String, String> insertTxnAtStart(String TxnID,String reqType) 
{
		String query=CommonDBQueries.getTxnIdInsertQuery(reqType);
		String duplicateErrorCode=null;
		if(reqType.equalsIgnoreCase("otp")) 
		{
			duplicateErrorCode="OTP_DUPLICATE_TRANSACTION_ID";
		}
		else if(reqType.equalsIgnoreCase("auth")) 
		{
			duplicateErrorCode="AUTH_DUPLICATE_TRANSACTION_ID";
		}
		else if(reqType.equalsIgnoreCase("kyc")) 
		{
			duplicateErrorCode="KYC_DUPLICATE_TRANSACTION_ID";
		}
		else if(reqType.equalsIgnoreCase("bfd")) 
		{
			duplicateErrorCode="BFD_DUPLICATE_TRANSACTION_ID";
		}
		Map<String, String> resultMap=new HashMap<String,String>();
		try 
		{
			int select=jdbcTemplate.update(query,new Object[] {TxnID});
			if(select!=0) 
			{
				resultMap=getMapResult("y", "");
			}
		}
		catch (DuplicateKeyException e) 
		{
			logger.info("DuplicateKeyException For txn id::"+TxnID+"\n",e);
			resultMap=getMapResult("n", duplicateErrorCode);
		}
		catch (Exception e) 
		{
			resultMap=getMapResult("n","DB_EXCEPTION");
		}
		return resultMap;
	}
	
	 
	 
	
	private Map<String,String> getMapResult(String status, String code) 
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("status", status);
		resultMap.put("code", code);
		return resultMap;
	}

}