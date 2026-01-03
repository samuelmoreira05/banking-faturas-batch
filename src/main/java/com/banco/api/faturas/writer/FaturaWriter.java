package com.banco.api.faturas.writer;

import com.banco.api.faturas.model.Fatura;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class FaturaWriter {

    @Bean
    public MongoItemWriter<Fatura> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<Fatura>()
                .template(mongoTemplate)
                .collection("faturas")
                .build();
    }
}
