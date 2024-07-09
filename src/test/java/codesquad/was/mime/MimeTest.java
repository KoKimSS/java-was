package codesquad.was.mime;


import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MimeTest {

    @Test
    public void testGetMimeType() {
        assertEquals("text/plain", Mime.TEXT_PLAIN.getMimeType());
        assertEquals("text/html", Mime.TEXT_HTML.getMimeType());
        assertEquals("application/json", Mime.APPLICATION_JSON.getMimeType());
        assertEquals("application/xml", Mime.APPLICATION_XML.getMimeType());
        assertEquals("image/png", Mime.IMAGE_PNG.getMimeType());
        assertEquals("image/jpeg", Mime.IMAGE_JPEG.getMimeType());
        assertEquals("image/gif", Mime.IMAGE_GIF.getMimeType());
        assertEquals("application/octet-stream", Mime.APPLICATION_OCTET_STREAM.getMimeType());
        assertEquals("multipart/form-data", Mime.MULTIPART_FORM_DATA.getMimeType());
    }

    @Test
    public void testFromString() {
        assertEquals(Mime.TEXT_PLAIN, Mime.fromString("text/plain"));
        assertEquals(Mime.TEXT_HTML, Mime.fromString("text/html"));
        assertEquals(Mime.APPLICATION_JSON, Mime.fromString("application/json"));
        assertEquals(Mime.APPLICATION_XML, Mime.fromString("application/xml"));
        assertEquals(Mime.IMAGE_PNG, Mime.fromString("image/png"));
        assertEquals(Mime.IMAGE_JPEG, Mime.fromString("image/jpeg"));
        assertEquals(Mime.IMAGE_GIF, Mime.fromString("image/gif"));
        assertEquals(Mime.APPLICATION_OCTET_STREAM, Mime.fromString("application/octet-stream"));
        assertEquals(Mime.MULTIPART_FORM_DATA, Mime.fromString("multipart/form-data"));
    }
//
//    @Test
//    public void testFromStringCaseInsensitive() {
//        assertEquals(Mime.TEXT_PLAIN, Mime.fromString("TEXT/PLAIN"));
//        assertEquals(Mime.TEXT_HTML, Mime.fromString("TEXT/HTML"));
//        assertEquals(Mime.APPLICATION_JSON, Mime.fromString("APPLICATION/JSON"));
//        assertEquals(Mime.APPLICATION_XML, Mime.fromString("APPLICATION/XML"));
//        assertEquals(Mime.IMAGE_PNG, Mime.fromString("IMAGE/PNG"));
//        assertEquals(Mime.IMAGE_JPEG, Mime.fromString("IMAGE/JPEG"));
//        assertEquals(Mime.IMAGE_GIF, Mime.fromString("IMAGE/GIF"));
//        assertEquals(Mime.APPLICATION_OCTET_STREAM, Mime.fromString("APPLICATION/OCTET-STREAM"));
//        assertEquals(Mime.MULTIPART_FORM_DATA, Mime.fromString("MULTIPART/FORM-DATA"));
//    }

    @Test
    public void testFromStringUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> {
            Mime.fromString("unknown/mime-type");
        });
    }

    @Test
    public void createSession() {
        String sessionId = UUID.randomUUID().toString();
        System.out.println(sessionId);
    }
}
