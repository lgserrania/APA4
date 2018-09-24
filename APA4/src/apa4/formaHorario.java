/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apa4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2017.1.08.008
 */
public class formaHorario {
    
    private int numProf;
    private int numTurmas;
    private String[] turmas;
    private int numDias;
    private int numHorarios;
    
    public formaHorario(){
        
        try {
            lerArquivo();
        } catch (IOException ex) {
            Logger.getLogger(formaHorario.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void lerArquivo() throws FileNotFoundException, IOException{
        FileReader arq = new FileReader("nilda36.txt");
        BufferedReader lerArq = new BufferedReader(arq);
        
        //Pega o número de professores
        String linha = lerArq.readLine();
        String[] linhaPart = linha.split("\t");
        this.numProf = Integer.parseInt(linhaPart[0]);
        
        //Pega o número de turmas
        linha = lerArq.readLine();
        linhaPart = linha.split("\t");
        this.numTurmas = Integer.parseInt(linhaPart[0]);
        
        //Cria o vetor com o nome das turmas
        this.turmas = new String[this.numTurmas];
        
        //Preenche o vetor com o nome das turmas
        for(int i = 1; i < linhaPart.length - 1; i++){
            this.turmas[i] = linhaPart[i];
        }
        
        //Pega o número de dias e horários
        linha = lerArq.readLine();
        linhaPart = linha.split("\t");
        this.numDias = Integer.parseInt(linhaPart[0]);
        this.numHorarios = Integer.parseInt(linhaPart[1]);
        
    }
    
}
