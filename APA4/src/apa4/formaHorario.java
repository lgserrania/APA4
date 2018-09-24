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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2017.1.08.008
 */
public final class formaHorario {
    
    private int numProf;
    private int numTurmas;
    private String[] turmas;
    private int numDias;
    private int numHorarios;
    private int[][][] mati; //Matriz de Indisponibilidade
    private int[][] mata; //Matriz de Aulas
    private int[][] profHorasDist; //Matriz com as horas já distribuidas do professor pra certa turma
    private int[][][] profHorarios; //Matriz com os horários ocupados pelo professor
    private int[][][] horarioTurmas; //Matriz com o professor alocado em cada horário da turma
   
    
    
    public formaHorario(){
        
        try {
            this.lerArquivo();
        } catch (IOException ex) {
            Logger.getLogger(formaHorario.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.horarioTurmas = new int[this.numTurmas][this.numDias][this.numHorarios];
        
        for(int i = 0; i < this.numTurmas; i++){
            for(int j = 0; j < this.numDias; j++){
                for(int k = 0; k < this.numHorarios; k++){
                    this.horarioTurmas[i][j][k] = -1;
                }
            }
        }
        
        this.alocacao();
        
        this.escreveHorarios();
        
        //this.escreveHorasDistribuidas();
        
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
        for(int i = 1; i <= linhaPart.length - 1; i++){
            this.turmas[i - 1] = linhaPart[i];
        }
        
        //Pega o número de dias e horários
        linha = lerArq.readLine();
        linhaPart = linha.split("\t");
        this.numDias = Integer.parseInt(linhaPart[0]);
        this.numHorarios = Integer.parseInt(linhaPart[1]);
        
        //Inicializando variáveis
        this.profHorarios = new int[this.numProf][this.numDias][this.numHorarios];
        this.profHorasDist = new int[this.numProf][this.numTurmas];
         
        //Gerando a matriz de indisponibilidade
        this.mati = new int[this.numProf][this.numDias][this.numHorarios];
        
        linha = lerArq.readLine();
        linhaPart = linha.split("\t");
        
        //Os horários em que o professor não está disponível
        //Serão preenchidos com o valor 1
        //Os disponíveis terão o valor 0
        while("i".equals(linhaPart[0])){
            int dia = Integer.parseInt(linhaPart[1]);
            int horario = Integer.parseInt(linhaPart[2]);
            int prof = Integer.parseInt(linhaPart[3]);
            
            this.mati[prof][dia][horario] = 1;
            linha = lerArq.readLine();
            linhaPart = linha.split("\t");
        }
        
        //Gerando a matriz de aulas
        this.mata = new int[this.numProf][this.numTurmas];
        
        linha = lerArq.readLine();
        
        //As linhas são os professores, as colunas são as turmas
        //e o valor é o número de horas que ele precisa dar
        while(linha != null){          
            linhaPart = linha.split("\t");
            int prof = Integer.parseInt(linhaPart[1]);
            int turma = Integer.parseInt(linhaPart[2]);
            this.mata[prof][turma] = Integer.parseInt(linhaPart[3]);
            
            linha = lerArq.readLine();   
        }
        
        arq.close();

    }
    
    public void alocacao(){
        
        Random gerador = new Random();
        
        for(int i = 0; i < 1000000; i++){
            
            int dia = gerador.nextInt(this.numDias);
            int turma = gerador.nextInt(this.numTurmas);
            int horario = gerador.nextInt(this.numHorarios);          
            int prof = gerador.nextInt(this.numProf);
                
            if(this.horarioTurmas[turma][dia][horario] == -1){
                if(this.mati[prof][dia][horario] != 1){
                    if(this.profHorasDist[prof][turma] < this.mata[prof][turma]){
                        if(this.profHorarios[prof][dia][horario] != 1){
                            this.horarioTurmas[turma][dia][horario] = prof;
                            this.profHorarios[prof][dia][horario] = 1;
                            this.profHorasDist[prof][turma]++;
                        }
                    }
                }
            }
        }    
    }
    
    private void escreveHorarios(){
        
        for(int i = 0; i < this.numTurmas; i++){
            System.out.println("########### TURMA " + this.turmas[i] + " ###########");
            for(int j = 0; j < this.numDias; j++){
                System.out.print("Dia " + j + " -> ");
                System.out.print(this.horarioTurmas[i][j][0]);
                for(int k = 1; k < this.numHorarios; k++){
                    System.out.print(" - " + this.horarioTurmas[i][j][k]);
                }
                System.out.println("");
            }
        }
        
    }
    
    private void escreveHorasDistribuidas(){
        
        for(int i = 0; i < this.numProf; i++){
            System.out.println("############# Professor " + i + " #############");
            for(int j = 0; j < this.numTurmas; j++){
                System.out.println("Turma " + j + ": " + this.profHorasDist[i][j] + " horas");
                if(this.profHorasDist[i][j] == this.mata[i][j]){
                    System.out.println("Quantidade certa");
                }else if(this.profHorasDist[i][j] < this.mata[i][j]){
                    System.out.println("Faltou horas");
                }else{
                    System.out.println("Passou horas");
                }
            }
        }
    }
}
