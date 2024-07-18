package codesquad.was.render;

public class HtmlTemplateRender {
    public static final String authButtonHolder = "{{authButton}}";
    private static final ListRender listRender = new ListRender();
    private static final SingleRender singleRender = new SingleRender();

    public static String render(String html, Model model) {
        html = authButtonRender(html, model);
        html = listRender.render(html, model);
        html = singleRender.render(html, model);
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

        String userName = (String) model.getSingleData().get("userName");
        if (userName != null) {
            authButton = """
                            <li class="header__menu__item">
                              <a class="btn btn_contained btn_size_s" href="/article">글쓰기</a>
                            </li>
                            <li class="header__menu__item">
                              <button class="btn btn_ghost btn_size_s"> {{userName}}</button>
                            </li>
                            <li class="header__menu__item">
                              <form action="/logout" method="POST" style="display: inline;">
                                <button type="submit" id="logout-btn" class="btn btn_ghost btn_size_s">로그아웃</button>
                              </form>
                            </li>
                    """;
        }

        return html.replace(authButtonHolder, authButton);
    }
}
