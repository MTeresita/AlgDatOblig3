package no.oslomet.cs.algdat.Oblig3;

import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        //Oppgave 1
       /* Integer [] a = {4,7,2,9,5,10,8,1,3,6};
       ObligSBinTre<Integer>tre = new ObligSBinTre<>(Comparator.naturalOrder());

        for(int verdi : a){
            tre.leggInn(verdi);
        }
        System.out.println(tre.antall()); */

        //Oppgave 2
        Integer [] a = {4,7,2,9,4,10,8,7,4,6};
        ObligSBinTre <Integer> tre = new ObligSBinTre<>(Comparator.naturalOrder());
        for (int verdi : a){
            tre.leggInn(verdi);
        }

        System.out.println(tre.antall());         //Utskrift: 10
        System.out.println(tre.antall(5));  //Utskrift: 0
        System.out.println(tre.antall(4));  //Utskrift: 3
        System.out.println(tre.antall(7));  //Utskrift: 2
        System.out.println(tre.antall(10)); //Utskrift: 1


    }
}
