
package bank.dtu.ws.fastmoney.service;

import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import models.Account;
import models.AccountInfo;
import models.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import java.math.BigDecimal;
import java.util.List;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "BankService", targetNamespace = "http://fastmoney.ws.dtu/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface BankService {


    /**
     * 
     * @param arg0
     * @return
     *     returns responses.Account
     * @throws BankServiceException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAccount", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.methods.GetAccount")
    @ResponseWrapper(localName = "getAccountResponse", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.responses.GetAccountResponse")
    public Account getAccount(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws BankServiceException_Exception
    ;

    /**
     * 
     * @param arg0
     * @return
     *     returns responses.Account
     * @throws BankServiceException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAccountByCprNumber", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.methods.GetAccountByCprNumber")
    @ResponseWrapper(localName = "getAccountByCprNumberResponse", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.responses.GetAccountByCprNumberResponse")
    public Account getAccountByCprNumber(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws BankServiceException_Exception
    ;

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     * @throws BankServiceException_Exception
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "createAccountWithBalance", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.methods.CreateAccountWithBalance")
    @ResponseWrapper(localName = "createAccountWithBalanceResponse", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.responses.CreateAccountWithBalanceResponse")
    public String createAccountWithBalance(
        @WebParam(name = "arg0", targetNamespace = "")
                User arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        BigDecimal arg1)
        throws BankServiceException_Exception
    ;

    /**
     * 
     * @param arg0
     * @throws BankServiceException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "retireAccount", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.methods.RetireAccount")
    @ResponseWrapper(localName = "retireAccountResponse", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.responses.RetireAccountResponse")
    public void retireAccount(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0)
        throws BankServiceException_Exception
    ;

    /**
     * 
     * @return
     *     returns java.util.List<responses.AccountInfo>
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getAccounts", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.methods.GetAccounts")
    @ResponseWrapper(localName = "getAccountsResponse", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.responses.GetAccountsResponse")
    public List<AccountInfo> getAccounts();

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @throws BankServiceException_Exception
     */
    @WebMethod
    @RequestWrapper(localName = "transferMoneyFromTo", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.methods.TransferMoneyFromTo")
    @ResponseWrapper(localName = "transferMoneyFromToResponse", targetNamespace = "http://fastmoney.ws.dtu/", className = "bank.dtu.ws.fastmoney.responses.TransferMoneyFromToResponse")
    public void transferMoneyFromTo(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        BigDecimal arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        String arg3)
        throws BankServiceException_Exception
    ;

}