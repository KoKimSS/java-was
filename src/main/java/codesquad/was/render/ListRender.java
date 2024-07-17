package codesquad.was.render;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListRender implements Render {
    public static final String start = "\\[\\$";
    public static final String end = "\\$\\]";


    /**
     * @param html
     * @param model
     * [$  html 코드 {{str}} $]
     * [[ ]] 안의 html 코드를 list 크기에 따라 렌더링 한다.
     * {{object.value}} -> model.get(object).getValue() 를 넣어준다.
     * @return
     */
    @Override
    public String render(String html, Model model) {
        StringBuilder resultHtml = new StringBuilder();
        Pattern pattern = Pattern.compile(start + "(.*?)" + end, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        System.out.println("리스트 렌더 실행");

        int lastMatchEnd = 0;
        while (matcher.find()) {
            resultHtml.append(html, lastMatchEnd, matcher.start());
            lastMatchEnd = matcher.end();

            String repeatBlock = matcher.group(1);
            // repeatBlock안에 {{ object. value}}가 있는지 찾고
            String attribute = extractAttribute(repeatBlock);
            if(attribute == null) {
                continue;
            }

            String[] attributeArr = attribute.trim().split("\\.");
            String object = attributeArr[0];

            List<Object> listData = model.getListData(object);
            if(listData == null) {
                continue;
            }

            // 있으면 List<Object> objects 에서 하나씩 꺼내서 getValue를 {{object.value }} 안에 넣어줘라
            SingleRender singleRender = new SingleRender();
            for(Object o : listData) {
                Model singleModel = new Model();
                singleModel.addSingleData(object,o);
                String singleRenderBlock = singleRender.render(repeatBlock, singleModel);
                resultHtml.append(singleRenderBlock);
            }
        }

        resultHtml.append(html, lastMatchEnd, html.length());

        return resultHtml.toString();
    }


    private String extractAttribute(String repeatBlock) {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = pattern.matcher(repeatBlock);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}