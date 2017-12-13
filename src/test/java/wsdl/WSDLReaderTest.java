package wsdl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientBuilder.class, EntityUtils.class})
public class WSDLReaderTest {
    static final class Preparator {
        static void stubHttp() throws Exception {
            mockStatic(HttpClientBuilder.class);
            mockStatic(EntityUtils.class);
            CloseableHttpClient hc = mock(CloseableHttpClient.class);
            HttpClientBuilder hcb = mock(HttpClientBuilder.class);
            when(HttpClientBuilder.create()).thenReturn(hcb);
            when(hcb.build()).thenReturn(hc);
            CloseableHttpResponse resp = mock(CloseableHttpResponse.class);
            StatusLine sl = mock(StatusLine.class);
            HttpEntity he = mock(HttpEntity.class);
            when(resp.getEntity()).thenReturn(he);
            when(sl.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            when(resp.getStatusLine()).thenReturn(sl);
            when(hc.execute(any(HttpGet.class))).thenReturn(resp);

            InputStream is = Preparator.class.getClassLoader().getResourceAsStream("sample-wsdl.xml");
            String wsdl = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            when(EntityUtils.toString(he)).thenReturn(wsdl);
        }
    }

    @Test
    public void testPublicURI() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://public.uri.org");
        assertEquals(wr.username(), null);
        assertEquals(wr.password(), null);
    }

    @Test
    public void testURIWithUsername() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://user@public.uri.org");
        assertEquals(wr.username(), "user");
        assertEquals(wr.password(), null);
    }

    @Test
    public void testURIWithCredentials() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://user:p@ssw0rd@private.uri.org/some/service");
        assertEquals(wr.username(), "user");
        assertEquals(wr.password(), "p@ssw0rd");
    }

    @Test
    public void testExplicitCredentials() throws URISyntaxException {
        final WSDLReader wr = new WSDLReader("http://u:p@private.uri.org", "user1", "nopass");
        assertEquals(wr.username(), "user1");
        assertEquals(wr.password(), "nopass");
    }

    @Test
    public void testPublicServiceURL() throws Exception {
        Preparator.stubHttp();
        assertNotNull(new WSDLReader("http://www.webservicex.net/stockquote.asmx").read());
    }

    @Test
    public void testSecuredServiceURL() throws Exception {
        Preparator.stubHttp();
        assertNotNull(new WSDLReader("http://user:pwd@www.webservicex.net/FedWire.asmx").read());
    }
}