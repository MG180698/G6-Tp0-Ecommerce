package com.uade.TPO_Ecommerce_Grupo6.exception;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String producto, Integer solicitado, Integer disponible) {
        super(
                "Stock insuficiente para '" + producto +
                        "'. Solicitado: " + solicitado +
                        ", disponible: " + disponible
        );
    }
}
