package com.banco.api.faturas.reader;

import com.banco.api.faturas.model.dto.FaturaDTO;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;

import javax.sql.DataSource;

@Configuration
public class FaturaReader {

    @Bean("faturaReaderBean")
    @StepScope
    public ItemReader<FaturaDTO> reader(
            DataSource dataSource,
            @Value("#{jobParameters['mes']}") Integer mes,
            @Value("#{jobParameters['ano']}") Integer ano) {

        String sql = String.format(
                "SELECT conta_id, SUM(valor) as total FROM transacoes " +
                        "WHERE MONTH(data_transacao) = %d AND YEAR(data_transacao) = %d " +
                        "GROUP BY conta_id", mes, ano
        );

        return new JdbcCursorItemReaderBuilder<FaturaDTO>()
                .name("faturaReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(new DataClassRowMapper<>(FaturaDTO.class))
                .build();
    }
}
