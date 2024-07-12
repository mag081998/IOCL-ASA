package in.cdac.aadhaarservices.daooperations;
import java.io.Serializable;

/**
 * @author admin
 *
 */
public final class SchemaDetails implements Serializable 
{

	private static final long serialVersionUID = 3L;
	
	private String ac;
	private Boolean status;
	private String code;
	
	public String getCode() 
	{
		return code;
	}
	public void setCode(String code) 
	{
		this.code = code;
	}
	
	public Boolean getStatus() 
	{
		return status;
	}
	public void setStatus(Boolean status)
	{
		this.status = status;
	}
	
	public String getAc() {
		return ac;
	}
	public void setAc(String ac) {
		this.ac = ac;
	}
		
}