package codesquad.was.render;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SingleRenderTest {


    @Test
    public void testSingleRender() {
        // Create a SingleRender instance
        SingleRender singleRender = new SingleRender();

        // Create a Model and add some test data
        TestObject testObject = new TestObject("TestValue");
        Model model = new Model();
        model.addSingleData("object1",testObject);

        // Define the HTML
        String html = "<div>{{ object1.value }}</div>";

        // Render the HTML with the model data
        String result = singleRender.render(html, model);

        // Print the result
        Assertions.assertThat("<div>TestValue</div>").isEqualTo(result);
    }

    // TestObject class
    class TestObject {
        public String value;

        public TestObject(String value) {
            this.value = value;
        }
    }
}