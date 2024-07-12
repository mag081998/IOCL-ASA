package in.cdac.aadhaarservices.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="OtpRes", namespace="http://www.uidai.gov.in/authentication/otp/1.0")
@XmlRootElement(name="OtpRes")
public final  class OtpRes
{
  @XmlAttribute(name="txn")
  protected String txn;
  @XmlAttribute(name="err")
  protected String err;
  @XmlAttribute(name="code")
  protected String code;
  @XmlAttribute(name="ts")
  @XmlSchemaType(name="dateTime")
  protected XMLGregorianCalendar ts;
  @XmlAttribute(name="ret")
  protected OtpResult ret;
  @XmlAttribute(name="info")
  protected String info;
  
  public String getTxn()
  {
    return this.txn;
  }
  
  public void setTxn(String value)
  {
    this.txn = value;
  }
  
  public String getErr()
  {
    return this.err;
  }
  
  public void setErr(String value)
  {
    this.err = value;
  }
  
  public String getCode()
  {
    return this.code;
  }
  
  public void setCode(String value)
  {
    this.code = value;
  }
  
  public XMLGregorianCalendar getTs()
  {
    return this.ts;
  }
  
  public void setTs(XMLGregorianCalendar value)
  {
    this.ts = value;
  }
  
  public OtpResult getRet()
  {
    return this.ret;
  }
  
  public void setRet(OtpResult value)
  {
    this.ret = value;
  }
  
  public String getInfo()
  {
    return this.info;
  }
  
  public void setInfo(String value)
  {
    this.info = value;
  }
}
