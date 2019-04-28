package allein.bizcorn.servicefeign.config;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;
import java.util.HashMap;

import static feign.form.ContentType.MULTIPART;
import static java.util.Collections.singletonMap;

public class FeignSpringFormEncoder extends FormEncoder {

    /**
     * Constructor with the default Feign's encoder as a delegate.
     */
    public FeignSpringFormEncoder() {
        this(new Encoder.Default());
    }


    /**
     * Constructor with specified delegate encoder.
     *
     * @param delegate delegate encoder, if this encoder couldn't encode object.
     */
    public FeignSpringFormEncoder(Encoder delegate) {
        super(delegate);

        MultipartFormContentProcessor processor = (MultipartFormContentProcessor) getContentProcessor(MULTIPART);
        processor.addFirstWriter(new SpringSingleMultipartFileWriter());
        processor.addFirstWriter(new SpringManyMultipartFilesWriter());
    }


    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {


        if (bodyType.equals(MultipartFile.class)) {
            MultipartFile file = (MultipartFile) object;
            HashMap data = new HashMap();
            data.put(file.getName(), object);
            super.encode(data, MAP_STRING_WILDCARD, template);
            return;
        } else if (bodyType.equals(MultipartFile[].class)) {
            MultipartFile[] file = (MultipartFile[]) object;
            if(file != null) {
                HashMap data = new HashMap();
                data.put(file.length == 0 ? "" : file[0].getName(), object);
                super.encode(data, MAP_STRING_WILDCARD, template);
                return;
            }
        }
        super.encode(object, bodyType, template);
    }
}