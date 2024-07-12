package in.cdac.aadhaarservices.common;

/**
 * This class is required for the purpose of storing the master config table details .
 */
final public class ConfigPara 
{
	/** The paraname. */
	private String paraname;

	/** The paravalue. */
	private String paravalue;

	/**
	 * Gets the paraname.
	 * @return the paraname
	 */
	public String getParaname() 
	{
		return paraname;
	}

	/**
	 * Sets the paraname.
	 *
	 * @param paraname the new paraname
	 */
	public void setParaname(String paraname) 
	{
		this.paraname = paraname;
	}

	/**
	 * Gets the paravalue.
	 * @return the paravalue
	 */
	public String getParavalue() 
	{
		return paravalue;
	}

	/**
	 * Sets the paravalue.
	 * @param paravalue the new paravalue
	 */
	public void setParavalue(String paravalue) 
	{
		this.paravalue = paravalue;
	}
}
