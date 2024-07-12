package codesquad.was.render;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleRender implements Render {
    public static String start = "\\{\\{";
    public static String end = "\\}\\}";

    @Override
    public String render(String html, Model model) {
        StringBuilder resultHtml = new StringBuilder();
        Pattern pattern = Pattern.compile(start + "(.*?)" + end);
        Matcher matcher = pattern.matcher(html);

        int lastMatchEnd = 0;

        while (matcher.find()) {
            resultHtml.append(html, lastMatchEnd, matcher.start());

            String fullExpression = matcher.group(1).trim();
            String[] parts = fullExpression.split("\\.");
            if (parts.length == 2) {
                String objectName = parts[0].trim();
                String fieldName = parts[1].trim();
                if (model.containsKey(objectName)) {
                    System.out.println(objectName);
                    Object obj = model.getSingleData(objectName);
                    try {
                        System.out.println("클래스이름"+obj.getClass().getName());
                        Field[] declaredFields = obj.getClass().getDeclaredFields();
                        Arrays.stream(declaredFields).forEach(System.out::println);
                        // obj.
                        Field field = obj.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        String fieldValue = String.valueOf(field.get(obj));
                        field.setAccessible(false);
                        resultHtml.append(fieldValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    resultHtml.append(matcher.group(0)); // Append the original {{ object.value }} if no replacement
                }
            }else if (parts.length == 1){
                String objectName = parts[0].trim();
                Object obj = model.getSingleData(objectName);
                resultHtml.append(obj.toString());

            } else {
                resultHtml.append(matcher.group(0)); // Append the original {{ object.value }} if no replacement
            }

            lastMatchEnd = matcher.end();
        }

        resultHtml.append(html, lastMatchEnd, html.length());
        return resultHtml.toString();
    }
}
