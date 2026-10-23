package com.pfinancas.model;
import java.time.LocalDate;

public class Transacao {

    private String descricao;
    private double valor;
    private TipoTransacao tipo;
    private LocalDate data;
    private int id;

    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    public void setValor(double valor){

        if(valor<=0){
            throw new IllegalArgumentException("O valor da transação deve ser maior que zero.");
        }
        this.valor = valor;
    }
    public void setTipo(TipoTransacao tipo){
        this.tipo = tipo;
    }
    public void setData(LocalDate data){
        this.data = data;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getDescricao(){
        return descricao;
    }
    public double getValor(){
        return valor;
    }
    public TipoTransacao getTipo(){
        return tipo;
    }
    public LocalDate getData(){
        return data;    
    }
    public int getId(){
        return id;
    }

    public Transacao(String descricao, double valor, TipoTransacao tipo, LocalDate data){

        setDescricao(descricao);
        setValor(valor);
        setTipo(tipo);
        setData(data);
    }
}
