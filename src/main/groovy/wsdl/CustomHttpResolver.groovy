package wsdl

import com.predic8.xml.util.ResourceDownloadException
import com.predic8.xml.util.ResourceResolver
import org.apache.http.HttpHeaders
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

class CustomHttpResolver extends ResourceResolver {
    String proxyHost, baseUri
    int proxyPort
    Map<String, String> httpHeaders

    def resolve(input, baseDir) {
        if (input instanceof com.predic8.schema.Import || input instanceof com.predic8.schema.Include) {
            if (!input.schemaLocation) return
            input = input.schemaLocation
        } else if (input instanceof com.predic8.wsdl.Import) {
            if (!input.location) return
            input = input.location
        }

        if (!input instanceof String) return

        if (isAbsolute(baseDir)) {
            baseUri = baseDir
        } else if (baseDir.startsWith('/') || baseDir.startsWith('\\')) {
            baseUri = extractBase(baseUri) + baseDir
        }

        if (!isAbsolute(input)) {
            if (input.startsWith('/') || input.startsWith('\\')) {
                input = extractBase(baseUri) + input
            } else {
                input = baseUri + input
            }
        }

        URI uri = new URI(input).normalize()

        try {
            HttpClient client = HttpClientBuilder.create().build()
            RequestConfig.Builder rc = RequestConfig.custom()
                    .setConnectionRequestTimeout(5000)
                    .setConnectTimeout(5000)

            if (proxyHost) {
                rc.setProxy(new HttpHost(proxyHost, proxyPort))
            }

            HttpGet method = new HttpGet(uri)
            method.setConfig(rc.build())
            method.setHeader(HttpHeaders.USER_AGENT, "SOAP Bar 1.0")

            httpHeaders?.each(method.&setHeader)

            HttpResponse response = client.execute(method)

            if(response.statusLine.statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException(String.format("GET %s returned status code %d",
                        uri, response.statusLine.statusCode))
            }

            new StringReader(EntityUtils.toString(response.entity))
        } catch (ResourceDownloadException e) {
            throw e
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    private static boolean isAbsolute(String path) {
        path?.startsWith("file:") || path?.startsWith("http:") || path?.startsWith("https:")
    }

    private static String extractBase(String path) {
        final URL tempURI = new URL(path)
        String extracted = String.format("%s://%s", tempURI.getProtocol(), tempURI.getHost())

        if (tempURI.getPort() != -1) {
            extracted = [ extracted, tempURI.getPort().toString() ].join(":")
        }

        extracted
    }
}
