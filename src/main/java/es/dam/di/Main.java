package es.dam.di;

import es.dam.di.infrastructure.BaseDatos;

public class Main {
    public static void main(String[] args) {
        BaseDatos.inicializar();
        System.out.println("Funciona, nigga");
    }
}
