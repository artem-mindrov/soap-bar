package wsdl;

import com.predic8.wsdl.Definitions;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.isEmptyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientBuilder.class, EntityUtils.class})
public class RequestTemplateTest {
    @Test
    public void testNonEmptyTemplate() throws Exception {
        WSDLReaderTest.Preparator.stubHttp();
        final WSDLReader wr = new WSDLReader("http://www.webservicex.net/stockquote.asmx");
        final Definitions defs = wr.read();

        final RequestTemplate rt = new RequestTemplate(defs, defs.getPortTypes().get(0).getName(),
                defs.getBindings().get(0).getName(), defs.getOperations().get(0).getName());

        Assert.assertThat(rt.get(), not(isEmptyString()));
    }
}