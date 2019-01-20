package base;

public class Method {
    public enum Token {
        getTokens,
        useToken,
        reset
    }

    public enum Bank {
        createAccountWithBalance,
        getAccount,
        getAccountByCprNumber,
        getAccounts,
        retireAccount,
        transferMoneyFromTo
    }
}
