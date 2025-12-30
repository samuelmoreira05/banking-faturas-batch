package com.banco.api.faturas.model.config;

import com.banco.api.faturas.model.Fatura;
import com.banco.api.faturas.model.dto.FaturaDTO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;
import java.time.LocalDateTime;

@Configuration
public class BatchConfig {

    @Bean
    public JdbcCursorItemReader<FaturaDTO> faturaReader (DataSource dataSource){
        return new JdbcCursorItemReaderBuilder<FaturaDTO>()
                .name("faturaReader")
                .dataSource(dataSource)
                .sql("SELECT conta_id, SUM(valor) as total FROM transacoes GROUP BY conta_id")
                .rowMapper(new DataClassRowMapper<>(FaturaDTO.class))
                .build();

    }

    @Bean
    public ItemProcessor<FaturaDTO, Fatura> faturaProcessor(){
        return dto -> {
            return Fatura.builder()
                    .contaId(dto.contaId())
                    .valorTotal(dto.total())
                    .dataCriacao(LocalDateTime.now())
                    .status("FECHADA")
                    .mes(LocalDateTime.now().getMonthValue())
                    .ano(LocalDateTime.now().getYear())
                    .build();
        };
    }

    @Bean
    public MongoItemWriter<Fatura> faturaWriter(MongoTemplate mongoTemplate){
        return new MongoItemWriterBuilder<Fatura>()
                .template(mongoTemplate)
                .collection("faturas")
                .build();
    }
}
