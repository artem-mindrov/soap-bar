package wsdl;

import com.predic8.wsdl.Definitions;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;

public class RequestTemplateTest {
    @Test
    public void testNonEmptyTemplate() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://www.webservicex.net/stockquote.asmx");
        final Definitions defs = wr.read();

        final RequestTemplate rt = new RequestTemplate(defs, defs.getPortTypes().get(0).getName(),
                defs.getBindings().get(0).getName(), defs.getOperations().get(0).getName());

        Assert.assertThat(rt.get(), not(isEmptyString()));
    }
}