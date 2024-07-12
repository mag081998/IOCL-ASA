package in.cdac.aadhaarservices.util;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="OtpResult", namespace="http://www.uidai.gov.in/authentication/otp/1.0")
@XmlEnum
public enum OtpResult
{
	 @XmlEnumValue("y")
	    Y("y"),
	    @XmlEnumValue("n")
	    N("n");
  private final String value;
  
  private OtpResult(String v)
  {
    this.value = v;
  }
  
  public String value()
  {
    return this.value;
  }
  
  public static OtpResult fromValue(String v)
  {
    OtpResult[] arrayOfOtpResult;
    int j = (arrayOfOtpResult = values()).length;
    for (int i = 0; i < j; i++)
    {
      OtpResult c = arrayOfOtpResult[i];
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }
}
