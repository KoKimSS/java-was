package codesquad.was.render;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListRender implements Render {
    public static String start = "\\[\\$";
    public static String end = "\\$\\]";


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
            html.substring(matcher.start(),matcher.end());

            String repeatBlock = matcher.group(1);


            System.out.println("리핏 블록 있음"+repeatBlock);


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
                System.out.println("리스트 렌더안의 싱글 렌더 실행");
                Model singleModel = new Model();
                System.out.println(o.getClass().getSimpleName());
                singleModel.addSingleData(object,o);
                String singleRenderBlock = singleRender.render(repeatBlock, singleModel);
                System.out.println(singleRenderBlock);
                resultHtml.append(singleRenderBlock);
            }
            System.out.println("렌더링 중"+resultHtml.toString());

            lastMatchEnd = matcher.end();
        }

        resultHtml.append(html, lastMatchEnd, html.length());

        System.out.println("렌더링 완료");
        System.out.println("렌더링 중"+resultHtml.toString());
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