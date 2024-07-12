package codesquad.was.render;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class HtmlTemplateRenderTest {


    @Test
    public void testRender_withValidPlaceholders() {
        String htmlTemplate = "<html><body><h1>{{title}}</h1><p>{{message}}</p></body></html>";
        Model model = new Model();
        model.addSingleData("title", "Hello, World!");
        model.addSingleData("message", "This is a dynamic message.");

        String expectedHtml = "<html><body><h1>Hello, World!</h1><p>This is a dynamic message.</p></body></html>";
        String renderedHtml = HtmlTemplateRender.render(htmlTemplate, model);

        assertEquals(expectedHtml, renderedHtml);
    }

    @Test
    public void testRender_withMissingPlaceholders() {
        String htmlTemplate = "<html><body><h1>{{title}}</h1><p>{{message}}</p><footer>{{footer}}</footer></body></html>";
        Model model = new Model();
        model.addSingleData("title", "Hello, World!");
        model.addSingleData("message", "This is a dynamic message.");

        String expectedHtml = "<html><body><h1>Hello, World!</h1><p>This is a dynamic message.</p><footer>{{footer}}</footer></body></html>";
        String renderedHtml = HtmlTemplateRender.render(htmlTemplate, model);

        assertEquals(expectedHtml, renderedHtml);
    }

    @Test
    public void testRender_withNoPlaceholders() {
        String htmlTemplate = "<html><body><h1>No placeholders here!</h1></body></html>";
        Model model = new Model();

        String expectedHtml = "<html><body><h1>No placeholders here!</h1></body></html>";
        String renderedHtml = HtmlTemplateRender.render(htmlTemplate, model);

        assertEquals(expectedHtml, renderedHtml);
    }

    @Test
    public void testRender_withEmptyModel() {
        String htmlTemplate = "<html><body><h1>{{title}}</h1><p>{{message}}</p></body></html>";
        Model model = new Model();

        String expectedHtml = "<html><body><h1>{{title}}</h1><p>{{message}}</p></body></html>";
        String renderedHtml = HtmlTemplateRender.render(htmlTemplate, model);

        assertEquals(expectedHtml, renderedHtml);
    }
}
