package codesquad.business.configuration;

import codesquad.business.controller.LoginHandler;
import codesquad.business.controller.SingUpHandler;
import codesquad.business.controller.UserListHandler;
import codesquad.was.handler.HandlerMap;

public class handlerMapConfig {
    private final HandlerMap handlerMap = HandlerMap.factoryMethod();

    {
        handlerMap.setHandlerMap("/create", SingUpHandler.singUpHandler);
        handlerMap.setHandlerMap("/login", LoginHandler.loginHandler);
        handlerMap.setHandlerMap("/user/list", UserListHandler.userListHandler);
    }

    private handlerMapConfig() {}
}

