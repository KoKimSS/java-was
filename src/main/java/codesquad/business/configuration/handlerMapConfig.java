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
        handlerMap.setHandlerMap("/article/detail", ArticleDetailHandler.articleDetailHandler);
        handlerMap.setHandlerMap("/article/comment", CommentHandler.commentHandler);
        handlerMap.setHandlerMap("/logout", LogoutHandler.logoutHandler);
        
        // 성능 테스트 API 추가
        handlerMap.setHandlerMap("/api/performance-test", PerformanceTestHandler.performanceTestHandler);
    }

    private handlerMapConfig() {}
}

