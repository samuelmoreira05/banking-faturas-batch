package com.banco.api.faturas.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "faturas")
public class Fatura {
    @Id
    private String id;

    private Long contaId;
    private Integer ano;
    private Integer mes;
    private BigDecimal valorTotal;
    private String status;
    private String requestId;
    private LocalDateTime dataCriacao;
}
