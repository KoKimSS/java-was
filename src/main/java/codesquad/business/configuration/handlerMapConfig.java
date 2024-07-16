package codesquad.business.configuration;

import codesquad.business.controller.*;
import codesquad.was.handler.HandlerMap;

public class handlerMapConfig {
    private static final HandlerMap handlerMap = HandlerMap.factoryMethod();


    public static void setHandlerMap(){
        handlerMap.setHandlerMap("/create", SingUpHandler.singUpHandler);
        handlerMap.setHandlerMap("/login", LoginHandler.loginHandler);
        handlerMap.setHandlerMap("/user/list", UserListHandler.userListHandler);
        handlerMap.setHandlerMap("/article", ArticleHandler.articleHandler);
        handlerMap.setHandlerMap("/article/list", ArticleListHandler.articleListHandler);
    }

    private handlerMapConfig() {}
}

