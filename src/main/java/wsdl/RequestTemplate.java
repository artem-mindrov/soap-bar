package wsdl;

import com.predic8.wsdl.Definitions;
import com.predic8.wstool.creator.RequestTemplateCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;

import java.io.StringWriter;

public class RequestTemplate {
    private final String port, binding, operation;
    private final StringWriter template = new StringWriter();
    private final Definitions wsdl;

    public RequestTemplate(Definitions wsdl, String port, String binding, String op) {
        this.binding = binding;
        this.port = port;
        this.operation = op;
        this.wsdl = wsdl;
    }

    public String get() {
        if (template.toString().isEmpty()) {
            SOARequestCreator srq = new SOARequestCreator(wsdl, new RequestTemplateCreator(), new MarkupBuilder(template));
            srq.createRequest(port, operation, binding);
        }

        return template.toString();
    }
}
