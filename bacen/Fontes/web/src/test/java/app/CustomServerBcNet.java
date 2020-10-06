package app;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;

import br.gov.bcb.test.stuff.web.app.CustomSecurityHandler;
import br.gov.bcb.test.stuff.web.app.CustomSocketConnector;
import br.gov.bcb.test.stuff.web.app.CustomWebAppContext;
import br.gov.bcb.test.stuff.web.app.JettyConfiguration;

import com.google.common.base.Preconditions;

public class CustomServerBcNet {

    protected JettyConfiguration jettyConfiguration;
    protected Server server;

    public void executar(JettyConfiguration jettyConfiguration) {
        Preconditions.checkArgument(jettyConfiguration != null);
        this.jettyConfiguration = jettyConfiguration;

        getServer().setConnectors(new Connector[] {new CustomSocketConnector(getServer(), jettyConfiguration)});
        getServer().setHandler(handlers());
        // CHECKSTYLE:OFF
        try {
            getServer().start();
        } catch (Exception e) { // O Server lança Exception.
            throw new RuntimeException(e);
        }
        // CHECKSTYLE:ON
    }

    protected Handler handlers() {
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {webAppContext(), contextHandler()});
        return handlers;
    }

    protected Handler contextHandler() {
        return new CustomBcNet(getServer());
    }

    protected Handler webAppContext() {
        return new CustomWebAppContext(getServer(), jettyConfiguration, new CustomSecurityHandler(jettyConfiguration));
    }

    public Server getServer() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    protected void setServer(Server server) {
        this.server = server;
    }
}