package com.pfinancas.repositorio;
import java.util.ArrayList;

public class RepositorioGenerico<T>{

    private ArrayList<T> itens;

    public RepositorioGenerico(){
        itens = new ArrayList<>();
    }
    public void adicionar(T item){
        itens.add(item);
    }
    public void remover(T item){
        itens.remove(item);
    }
    public ArrayList<T> listar(){
        return itens;
    }
    public void adicionarTodods(ArrayList<? extends T> lista){
        for(T item: lista){
            adicionar(item);
        }
    }
    public void copia(ArrayList<? super T> destino){
        for(T item: itens){
            destino.add(item);
        }
    }
    public void tamanho(ArrayList<?> lista){

        System.out.println( lista.size());
    }
    
}

