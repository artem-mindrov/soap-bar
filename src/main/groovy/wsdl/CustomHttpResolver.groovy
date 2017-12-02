package wsdl

import com.predic8.xml.util.ResourceDownloadException
import com.predic8.xml.util.ResourceResolver
import groovy.transform.Immutable
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils

@Immutable
class CustomHttpResolver extends ResourceResolver {
    String proxyHost
    int proxyPort
    Map<String, String> httpHeaders

    def resolve(String input, String baseDir) {
        URI uri = new URI(baseDir + input).normalize()
        try{
            HttpClient client = HttpClientBuilder.create().build()
            RequestConfig.Builder rc = RequestConfig.custom()
                    .setConnectionRequestTimeout(5000)
                    .setConnectTimeout(5000)

            if (proxyHost) {
                rc.setProxy(new HttpHost(proxyHost, proxyPort))
            }

            HttpGet method = new HttpGet(uri)
            method.setConfig(rc.build())
            method.setHeader("User-Agent", "SOAP Bar 1.0")

            if (httpHeaders) {
                httpHeaders.each(method.&setHeader)
            }

            HttpResponse response = client.execute(method)

            if(response.statusLine.statusCode != 200) {
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
}
