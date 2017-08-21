package it.infocert.eigor.api.conversion;

import it.infocert.eigor.model.core.datatypes.FileReference;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AttachmentToFileReferenceConverter implements TypeConverter<Element, FileReference> {
    private final static Logger log = LoggerFactory.getLogger(AttachmentToFileReferenceConverter.class);

    @Override
    public FileReference convert(Element element) {
        String content = element.getText();
        String mimeCode = element.getAttributeValue("mimeCode");
        String filename = element.getAttributeValue("filename");

        try {
            return new FileReference(
                    createTempFile(filename, content),
                    new MimeType(mimeCode),
                    filename
            );
        } catch (IOException | MimeTypeParseException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Class<FileReference> getTargetClass() {
        return FileReference.class;
    }

    @Override
    public Class<Element> getSourceClass() {
        return Element.class;
    }

    private String createTempFile(String filename, String content) throws IOException {
        File tempFile = File.createTempFile(filename, ".tmp");
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);
        log.debug("Stored temporary attachment at {}", tempFile.getAbsolutePath());
        return tempFile.getAbsolutePath();
    }
}
