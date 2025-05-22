package com.flowpay.ccp.credit.transfer.cross.border;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaBancaRichiesta;
import com.flowpay.ccp.credit.transfer.cross.border.dto.credit.transfer.InserisciBonificoExtraSepaClienteRichiesta;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestRequestObjects {

    @Inject
    ObjectMapper objectMapper;

    private InserisciBonificoExtraSepaClienteRichiesta postCliente;

    public InserisciBonificoExtraSepaClienteRichiesta getPostCliente() {
        return postCliente;
    }

    private InserisciBonificoExtraSepaBancaRichiesta postBanca;

    public InserisciBonificoExtraSepaBancaRichiesta getPostBanca() {
        return postBanca;
    }

    private InserisciBonificoExtraSepaBancaRichiesta postBancaConNotifica;

    public InserisciBonificoExtraSepaBancaRichiesta getPostBancaConNotifica() {
        return postBancaConNotifica;
    }

    /**
     * Load jsons fromt the resource folder to not include long examples in test
     * classes
     * 
     * @throws IOException
     */
    @PostConstruct
    public void readResources() throws IOException {
        postCliente = objectMapper.readValue(getClass().getResource("/jsons/cliente/post.json"),
                InserisciBonificoExtraSepaClienteRichiesta.class);
        postBanca = objectMapper.readValue(getClass().getResource("/jsons/banca/post.json"),
                InserisciBonificoExtraSepaBancaRichiesta.class);
        postBancaConNotifica = objectMapper.readValue(getClass().getResource("/jsons/banca/post_con_notifica.json"),
                InserisciBonificoExtraSepaBancaRichiesta.class);
    }
}
