import clients.TokenClient;
import com.dtupay.dtupayapi.customer.application.CustomerUtils;
import com.dtupay.dtupayapi.customer.models.TokenBarcodePathPair;
import com.google.zxing.WriterException;
import exceptions.ClientException;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CustomerUtilsTest {

    @Test
    public void requestTokens(){
        TokenClient tokenClient = null;
        try {
            tokenClient = new TokenClient("localhost", "rabbitmq","rabbitmq");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        CustomerUtils cu = new CustomerUtils(tokenClient);

    }

}
