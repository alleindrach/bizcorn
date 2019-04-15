package allein.bizcorn.service.db.mongo.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;

@Configuration
public class MongoConfiguration {

    @Bean
    public GridFSBucket gridFSBucket(MongoDbFactory mongoDbFactory) {
        //主要是配置GridFSBuckets用于下载
        return GridFSBuckets.create(mongoDbFactory.getDb());
    }
}

