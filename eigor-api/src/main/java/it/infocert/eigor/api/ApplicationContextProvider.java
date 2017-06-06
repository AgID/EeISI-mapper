package it.infocert.eigor.api;

import org.springframework.context.ApplicationContext;

public class ApplicationContextProvider {

    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static void setApplicationContext(ApplicationContext ac) {
        context = ac;
    }
}