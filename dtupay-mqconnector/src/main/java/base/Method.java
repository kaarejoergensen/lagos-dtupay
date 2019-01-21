package base;

public class Method {
    public enum Token {
        getTokens,
        useToken,
        reset,
        getUserIdFromToken
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
