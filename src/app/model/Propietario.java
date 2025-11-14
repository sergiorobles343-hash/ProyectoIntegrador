/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.model;

import java.util.ArrayList;
import java.util.List;

public class Propietario extends Usuario {
    private String tipoCultivo;
    private List<Predio> predios;

    public Propietario(String id, String nombreCompleto, String documento, String correo, String password) {
        super(id, nombreCompleto, documento, correo, password, "Propietario");
        this.predios = new ArrayList<>();
    }

    public String getTipoCultivo() {
        return tipoCultivo;
    }

    public void setTipoCultivo(String tipoCultivo) {
        this.tipoCultivo = tipoCultivo;
    }

    public String getNombre() {
        return getNombreCompleto();
    }

    public List<Predio> getPredios() {
        return predios;
    }

    public void agregarPredio(Predio predio) {
        predios.add(predio);
    }

    public void setPredios(List<Predio> predios) {
        this.predios = predios;
    }
}

