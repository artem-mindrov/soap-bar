package wsdl;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

public class WSDLReaderTest {
    @Test
    public void testPublicURI() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://public.uri.org");
        Assert.assertEquals(wr.username(), null);
        Assert.assertEquals(wr.password(), null);
    }

    @Test
    public void testURIWithUsername() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://user@public.uri.org");
        Assert.assertEquals(wr.username(), "user");
        Assert.assertEquals(wr.password(), null);
    }

    @Test
    public void testURIWithCredentials() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://user:p@ssw0rd@private.uri.org/some/service");
        Assert.assertEquals(wr.username(), "user");
        Assert.assertEquals(wr.password(), "p@ssw0rd");
    }

    @Test
    public void testExplicitCredentials() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://u:p@private.uri.org", "user1", "nopass");
        Assert.assertEquals(wr.username(), "user1");
        Assert.assertEquals(wr.password(), "nopass");
    }

    @Test
    public void testPublicService() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://www.webservicex.net/stockquote.asmx");
        wr.read();
    }

    @Test
    public void testSecuredService() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://user:pwd@www.webservicex.net/FedWire.asmx");
        wr.read();
    }
}