package com.banco.api.faturas.model.dto;

import java.time.LocalDateTime;

public record RequisicaoFaturaDTO(
        int ano,
        int mes,
        String requestId,
        LocalDateTime dataExecucao
) {
}
