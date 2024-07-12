package in.cdac.aadhaarservices.daooperations;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public final class SchemaDetailsExtractor implements ResultSetExtractor<SchemaDetails> 
{
	@Override
	public SchemaDetails extractData(ResultSet rs) throws SQLException, DataAccessException 
	{
		SchemaDetails sdet = new SchemaDetails();
		while (rs.next())
		{
			sdet.setAc(rs.getString("asa_code"));
		}
		return sdet;
	}
}