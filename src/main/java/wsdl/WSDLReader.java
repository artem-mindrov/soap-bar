package wsdl;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WSDLReader {
    private final String baseUri;
    private String username, password;

    public WSDLReader(String baseUri) throws URISyntaxException {
        Objects.requireNonNull(baseUri);
        this.baseUri = baseUri.endsWith("?wsdl") ? baseUri : baseUri.concat("?wsdl");
        final URI uri = new URI(this.baseUri);
        final String authority = uri.getAuthority();

        if (authority != null) {
            final String[] userInfo = authority.split(":", 2);
            int passDelim = userInfo[userInfo.length - 1].lastIndexOf('@');

            if (userInfo.length > 1) {
                this.username = userInfo[0];

                if (passDelim != -1) {
                    this.password = userInfo[1].substring(0, passDelim);
                }
            } else if (passDelim != -1) {
                this.username = userInfo[0].substring(0, passDelim);
            }
        }
    }

    public WSDLReader(String baseUri, String username, String password) throws URISyntaxException {
        this(baseUri);
        this.username = username;
        this.password = password;
    }

    public String username() { return username; }

    public String password() { return password; }

    public Definitions read() {
        final WSDLParser parser = new WSDLParser();
        final CustomHttpResolver chr = new CustomHttpResolver();

        if (this.username != null) {
            final String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
            final Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.AUTHORIZATION, "Basic " + new String(encodedAuth));
            chr.setHttpHeaders(headers);
        }

        parser.setResourceResolver(chr);
        return parser.parse(baseUri);
    }
}
