package codesquad.business.configuration;

import codesquad.was.util.UrlPathResourceMap;

public class UrlPathResourceMapConfig {
    private static final UrlPathResourceMap urlPathResourceMap = UrlPathResourceMap.factoryMethod();
    public static void setUrlPathResourceMap(){
        urlPathResourceMap.setResourcePathMap("/", "/static/index.html");
        urlPathResourceMap.setResourcePathMap("/index.html", "/static/index.html");
        urlPathResourceMap.setResourcePathMap("/login", "/static/login/index.html");
        urlPathResourceMap.setResourcePathMap("/user/login_failed.html", "/static/login/index.html");
        urlPathResourceMap.setResourcePathMap("/about", "about.html");
        urlPathResourceMap.setResourcePathMap("/contact", "contact.html");
        urlPathResourceMap.setResourcePathMap("/css/styles.css", "styles.css");
        urlPathResourceMap.setResourcePathMap("/js/main.js", "main.js");
        urlPathResourceMap.setResourcePathMap("/registration", "/static/registration/index.html");
    }
    private UrlPathResourceMapConfig() {}
}

