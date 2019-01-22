import clients.BankClient;
import clients.TokenClient;
import com.dtupay.dtupayapi.customer.application.CustomerUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CustomerUtilsTest {

    @Test
    @Ignore
    public void requestTokens(){
        TokenClient tokenClient = null;
        BankClient bankClient = null;
        try {
            tokenClient = new TokenClient("localhost", "rabbitmq","rabbitmq");
            bankClient = new BankClient("localhost", "rabbitmq","rabbitmq");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        CustomerUtils cu = new CustomerUtils(tokenClient, bankClient);

    }

}
