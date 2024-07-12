package codesquad.was.render;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListRenderTest {
    ListRender listRender = new ListRender();


    @Test
    public void testListRender() {
        // Create a ListRender instance
        ListRender listRender = new ListRender();

        // Create a Model and add some test data
        Model model = new Model();
        List<Object> testData = new ArrayList<>();
        testData.add(new TestObject("Value1"));
        testData.add(new TestObject("Value2"));
        model.addListData("objects", testData);

        // Define the HTML template with repeatable blocks
        String html = "<ul>[$<li>{{ objects.value }}</li>$]</ul>";

        // Render the HTML with the model data
        String result = listRender.render(html, model);

        // Print the result
        System.out.println(result);
    }

    // A simple object class to use in the test
    class TestObject {
        public String value;

        public TestObject(String value) {
            this.value = value;
        }
    }
}