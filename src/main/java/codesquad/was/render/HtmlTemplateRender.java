package codesquad.was.render;

import java.util.Map;

public class HtmlTemplateRender {
    public static final String authButtonHolder = "{{authButton}}";
    public static String render(String html, Model model) {
        html = authButtonRender(html, model);
        for (Map.Entry<String, String> entry : model.getData().entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            html = html.replace(placeholder, entry.getValue());
        }
        return html;
    }

    private static String authButtonRender(String html, Model model) {
        String authButton = """
          <li class="header__menu__item">
            <a class="btn btn_contained btn_size_s" href="/login">로그인</a>
          </li>
          <li class="header__menu__item">
            <a class="btn btn_ghost btn_size_s" href="/registration">
              회원 가입
            </a>
          </li>
        """;

        // 로그인이 되어 있다면
        String userName = model.getData().get("userName");

        if(userName !=null){
            authButton = """
            <li class="header__menu__item">
              <a class="btn btn_contained btn_size_s" href="/article">글쓰기</a>
            </li>
            <li class="header__menu__item">
              <button class="btn btn_ghost btn_size_s">""" + userName + """
            </li>
            <li class="header__menu__item">
              <button id="logout-btn" class="btn btn_ghost btn_size_s">로그아웃</button>
            </button>
            </li>
            """;
        }

        return html.replace(authButtonHolder, authButton);
    }
}
