package com.banco.api.faturas.model.config;

import com.banco.api.faturas.model.dto.FaturaDTO;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;

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
}
