package it.infocert.eigor.api.conversion;

import it.infocert.eigor.api.EigorRuntimeException;
import it.infocert.eigor.api.configuration.EigorConfiguration;
import it.infocert.eigor.api.errors.ErrorMessage;
import it.infocert.eigor.model.core.datatypes.FileReference;
import it.infocert.eigor.org.springframework.core.io.DefaultResourceLoader;
import it.infocert.eigor.org.springframework.core.io.Resource;
import org.apache.commons.io.FileUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class AttachmentToFileReferenceConverter implements TypeConverter<Element, FileReference> {
    private final static Logger log = LoggerFactory.getLogger(AttachmentToFileReferenceConverter.class);
    private final File workdir;

    public static TypeConverter<Element, FileReference> newConverter(EigorConfiguration eigorConfiguration){
        return new AttachmentToFileReferenceConverter(eigorConfiguration);
    }

    private AttachmentToFileReferenceConverter(EigorConfiguration eigorConfiguration) {
        File workdir;
        String workdirS = eigorConfiguration.getMandatoryString("eigor.workdir");
        try {
            DefaultResourceLoader drl = new DefaultResourceLoader();
            Resource resource = drl.getResource(workdirS);
            workdir = resource.getFile();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            workdir = null;
        }
        this.workdir = workdir;
    }

    @Override
    public FileReference convert(Element element) {
        String content = element.getText();
        String mimeCode = element.getAttributeValue("mimeCode");
        String filename = element.getAttributeValue("filename");

        if (mimeCode == null || filename == null) {
            throw new EigorRuntimeException(ErrorMessage.builder().message(String.format("Attribute %s is missing", mimeCode == null? "mimeCode": "filename")).action("AttachmentToFileReferenceConversion").build());
        }

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String createTempFile(String filename, String content) throws IOException {
        File dest = new File(workdir + "/tmp");
        if (!dest.exists()) {
            dest.mkdirs();
        }
        File tempFile = new File(String.format("%s%s%s%s.tmp", dest.getAbsolutePath(), File.separator, filename,  UUID.randomUUID()));
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);
        log.debug("Stored temporary attachment at {}", tempFile.getAbsolutePath());
        return tempFile.getAbsolutePath();
    }
}
