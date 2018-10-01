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
    private int horasErradas = 0;
    private int contSemTrocas = 0;
    private boolean reinicia = false;
    
    
    public formaHorario(){
        
        do{
            System.out.println("Começou de novo!");
            try {
                this.lerArquivo();
            } catch (IOException ex) {
                Logger.getLogger(formaHorario.class.getName()).log(Level.SEVERE, null, ex);
            }

            //this.descricao();
            //this.descricaoProf(9);

            this.horarioTurmas = new int[this.numTurmas][this.numDias][this.numHorarios];

            for(int i = 0; i < this.numTurmas; i++){
                for(int j = 0; j < this.numDias; j++){
                    for(int k = 0; k < this.numHorarios; k++){
                        this.horarioTurmas[i][j][k] = -1;
                    }
                }
            }

            this.alocacao();

            this.calculaHorasErradas(); 
            
            this.contSemTrocas = 0;

            while(this.horasErradas > 0){
                int horasAntigas = this.horasErradas;
                this.swaps();
                if(horasAntigas == this.horasErradas) this.contSemTrocas++;
                else this.contSemTrocas = 0;
                if(this.contSemTrocas > 100000000) break;

            }
        }while(this.horasErradas > 0);
        
        
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
        
        this.horarioTurmas[8][0][0] = 9;
        this.horarioTurmas[8][0][1] = 9;
        this.profHorarios[9][0][0] = 1;
        this.profHorarios[9][0][1] = 1;
        this.profHorasDist[9][8] = 2;
        
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
    
    private void swaps(){
        
        Random gerador = new Random();
        
        int dia;
        int turma;
        int horario;
        int prof1;
        int prof2;
        int horarioTroca;
        int diaTroca;
        boolean achouHorarioTroca = false;
        
        dia = gerador.nextInt(this.numDias);
        turma = gerador.nextInt(this.numTurmas);
        horario = gerador.nextInt(this.numHorarios);
        
        do{
            prof1 = gerador.nextInt(this.numProf);
        }while(this.profHorasDist[prof1][turma] == 0);
        
        if(this.horarioTurmas[turma][dia][horario] == -1){
            
            prof2 = gerador.nextInt(this.numProf);
            
            do{

                horarioTroca = gerador.nextInt(this.numHorarios);
                diaTroca = gerador.nextInt(this.numDias);
                if(this.horarioTurmas[turma][diaTroca][horarioTroca] == prof1) achouHorarioTroca = true;

            }while(!achouHorarioTroca);
            
            if(this.mati[prof1][dia][horario] == 0 && this.profHorarios[prof1][dia][horario] == 0){

                if(this.mati[prof2][diaTroca][horarioTroca] == 0 && 
                        this.profHorarios[prof2][diaTroca][horarioTroca] == 0 &&
                        this.profHorasDist[prof2][turma] < this.mata[prof2][turma]){

                    this.horarioTurmas[turma][dia][horario] = prof1;
                    this.horarioTurmas[turma][diaTroca][horarioTroca] = prof2;

                    this.profHorarios[prof1][dia][horario] = 1;
                    this.profHorarios[prof1][diaTroca][horarioTroca] = 0;

                    this.profHorarios[prof2][diaTroca][horarioTroca] = 1;

                    this.profHorasDist[prof2][turma]++;

                    this.calculaHorasErradas();
                    //System.out.println("Horas erradas: " + this.horasErradas);

                }

            }
            
        }else{
            
            prof2 = this.horarioTurmas[turma][dia][horario];
            
            do{

                horarioTroca = gerador.nextInt(this.numHorarios);
                diaTroca = gerador.nextInt(this.numDias);
                if(this.horarioTurmas[turma][diaTroca][horarioTroca] == prof1) achouHorarioTroca = true;

            }while(!achouHorarioTroca);
            
            if(this.mati[prof1][dia][horario] == 0 && this.profHorarios[prof1][dia][horario] == 0){

                if(this.mati[prof2][diaTroca][horarioTroca] == 0 && this.profHorarios[prof2][diaTroca][horarioTroca] == 0){

                    this.horarioTurmas[turma][dia][horario] = prof1;
                    this.horarioTurmas[turma][diaTroca][horarioTroca] = prof2;

                    this.profHorarios[prof1][dia][horario] = 1;
                    this.profHorarios[prof1][diaTroca][horarioTroca] = 0;

                    this.profHorarios[prof2][diaTroca][horarioTroca] = 1;
                    this.profHorarios[prof2][dia][horario] = 0;

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
    
    private void calculaHorasErradas(){
        
        this.horasErradas = 0;
        
        for(int i = 0; i < this.numTurmas; i++){
            for(int j = 0; j < this.numDias; j++){
                for(int k = 0; k < this.numHorarios; k++){
                    if(this.horarioTurmas[i][j][k] == -1) this.horasErradas++;
                }
            }
        }
        
    }
            
    private void descricao(){
        
        int numDias;
        int numHorarios;
        int numAulas;
        int numTurmas;
        boolean achouDia;
        boolean achouTurma;
        
        for(int prof = 0; prof < this.numProf; prof++){
            
            numDias = 0;
            numHorarios = 0;
            numTurmas = 0;
            numAulas = 0;
            
            for(int dia = 0; dia < this.numDias; dia++){
                achouDia = false;
                for(int horario = 0; horario < this.numHorarios; horario++){
                    if(this.mati[prof][dia][horario] == 0){
                        numHorarios++;
                        achouDia = true;
                    }
                }
                if(achouDia) numDias++;
            }
            
            for(int turma = 0; turma < this.numTurmas; turma++){
                
                if(this.mata[prof][turma] > 0){
                    numAulas += this.mata[prof][turma];
                    numTurmas++;
                }
                
            }
            
            System.out.println("O professor " + prof + " possui " + numHorarios 
                    + " disponíveis distribuidos em " + numDias + " dias e precisa dar " 
                    + numAulas + " aulas para " + numTurmas + " turmas");
            
        }
        
    }
    
    private void descricaoProf(int prof){
        
        System.out.println("O professor " + prof + " tem os seguintes horários livres:");
        
        for(int dia = 0; dia < this.numDias; dia++){
            for(int horario = 0; horario < this.numHorarios; horario++){
                if(this.mati[prof][dia][horario] == 0){
                    System.out.println("Dia: " + dia + " - Horario: " + horario);
                }
            }
        }
        System.out.println("O mesmo precisa dar aula para as turmas: ");
        for(int turma = 0; turma < this.numTurmas; turma++){
            if(this.mata[prof][turma] > 0){
                System.out.println(turma + " - " + this.turmas[turma]);
            }
        }
        
    }
    
    private void informacoesFinais(){
        
        int total = 0;
        
        System.out.println("################## Horas faltando #################");
        for(int prof = 0; prof < this.numProf; prof++){     
            for(int turma = 0; turma < this.numTurmas; turma++){
                if(this.profHorasDist[prof][turma] < this.mata[prof][turma]){
                    System.out.println("Professor " + prof);
                    System.out.println("Faltou " + (this.mata[prof][turma] - this.profHorasDist[prof][turma]) + " horas na turma " + turma);
                    System.out.println("\n");
                    total += this.mata[prof][turma] - this.profHorasDist[prof][turma];
                }
            }
            
        }
        
        System.out.println("Total de horas não distribuídas: " + total);
        
    }
}
