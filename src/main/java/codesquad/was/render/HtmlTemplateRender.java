package codesquad.was.render;

import java.util.Map;

public class HtmlTemplateRender {
    public static String render(String html, Model model) {
        for (Map.Entry<String, String> entry : model.getData().entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            html = html.replace(placeholder, entry.getValue());
        }
        return html;
    }
}
