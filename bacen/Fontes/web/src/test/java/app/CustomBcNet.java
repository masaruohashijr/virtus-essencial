package app;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class CustomBcNet extends ContextHandler {

    public static final String RAIZ = "/";

    public CustomBcNet(Server server) {
        setServer(server);
        setContextPath(RAIZ);
        setResourceBase("http://www.bcnet.bcb.gov.br");
        setHandler(resourceHandler());
    }

    protected Handler resourceHandler() {
        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setCacheControl("max-age=3600,public");
        return resHandler;
    }

}