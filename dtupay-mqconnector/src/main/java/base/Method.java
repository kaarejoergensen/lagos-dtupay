package base;

public class Method {
    public enum Token {
        getTokens,
        useToken,
    }

    public enum Bank {
        createAccountWithBalance,
        getAccount,
        getAccountByCprNumber,
        getAccounts,
        retireAccount,
        transferMoneyFromTo;
    }
}
