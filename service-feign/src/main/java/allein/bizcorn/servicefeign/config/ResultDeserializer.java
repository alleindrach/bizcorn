package allein.bizcorn.servicefeign.config;

import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ResultDeserializer    extends JsonDeserializer<Result> {

        @Override
        public Result deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            String s = node.toString();
            Result parse =(Result) JSON.parseObject(s,Result.class);
            return parse;
        }
    }

