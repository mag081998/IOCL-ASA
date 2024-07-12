package in.cdac.aadhaarservices.common;

import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.cdac.aadhaarservices.daooperations.DaoCommon;


@Component
final public class MasterConfig 
{
	private static final Logger logger = LogManager.getLogger(MasterConfig.class);
	private HashMap<ConfigParaKey, ConfigPara> hmpConfigParas = null;
	@Autowired
	DaoCommon daoCommon;
	
	public MasterConfig() 
	{
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public ConfigPara getConfigPara(ConfigParaKey key)
	{
		if(hmpConfigParas == null)
		{
			logger.info("Initialising List of configpara");
			getAllConfigPara();
		}
		if(hmpConfigParas.containsKey(key))
		{
			return hmpConfigParas.get(key);
		}
		return null;
	}
	
	private void getAllConfigPara()
	{
		hmpConfigParas = daoCommon.getMasterDBConfig();
	}

	public HashMap<ConfigParaKey, ConfigPara> getHmpConfigParas() 
	{
		return hmpConfigParas;
	}

	public void setHmpConfigParas(HashMap<ConfigParaKey, ConfigPara> hmpConfigParas) 
	{
		this.hmpConfigParas = hmpConfigParas;
	}
}