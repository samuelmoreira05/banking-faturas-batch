package com.banco.api.faturas.processor;

import com.banco.api.faturas.model.Fatura;
import com.banco.api.faturas.model.dto.FaturaDTO;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@StepScope // <- Pega os parametros dinamicos
public class FaturaProcessor implements ItemProcessor<FaturaDTO, Fatura> {

    @Value("#{jobParameters['requestId']}")
    private String requestId;

    @Value("#{jobParameters['mes']}")
    private Long mes;

    @Value("#{jobParameters['ano']}")
    private Long ano;

    @Override
    public Fatura process(FaturaDTO item) {
        return Fatura.builder()
                .contaId(item.contaId())
                .valorTotal(item.total())
                .dataCriacao(LocalDateTime.now())
                .status("GERADA")
                .mes(mes.intValue())
                .ano(ano.intValue())
                .requestId(requestId)
                .build();
    }
}
