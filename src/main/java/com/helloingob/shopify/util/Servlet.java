package com.helloingob.shopify.util;

import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletConfiguration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * The main servlet for the application.
 * <p>
 * It is not mandatory to have the Servlet, since Flow will automatically register a Servlet to any app with at least one {@code @Route} to server root context.
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/*", name = "UIServlet", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false)
public class Servlet extends VaadinServlet implements SessionInitListener, SessionDestroyListener {

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(this);
        getService().addSessionDestroyListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        // Do session start stuff here
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        System.out.println("Servlet destroyed!");
        //HibernateUtil.getSessionFactory().close();
    }
}
