package no.oslomet.cs.algdat.Oblig3;

import java.util.Comparator;

public class Main {

    public static void main(String[] args) {

        //Oppgave 1
       Integer [] a = {4,7,2,9,5,10,8,1,3,6};
       ObligSBinTre<Integer>tre = new ObligSBinTre<>(Comparator.naturalOrder());

        for(int verdi : a){
            tre.leggInn(verdi);
        }
        System.out.println("Oppgave 1");
        System.out.println(tre.antall());
        System.out.println("------------------------------");


        //Oppgave 2

        Integer[] b = {4, 7, 2, 9, 4, 10, 8, 7, 4, 6};
        ObligSBinTre<Integer> tre1 = new ObligSBinTre<>(Comparator.naturalOrder());
        for (int verdi : b) {
            tre1.leggInn(verdi);
        }
        System.out.println("Oppgave 2");

        // Forekomster av antall verdier i listen
        System.out.println(tre1.antall());         //Utskrift: 10
        System.out.println(tre1.antall(5));  //Utskrift: 0
        System.out.println(tre1.antall(4));  //Utskrift: 3
        System.out.println(tre1.antall(7));  //Utskrift: 2
        System.out.println(tre1.antall(10)); //Utskrift: 1
        System.out.println("------------------------------");

        //Oppgave 3
        int[] c = {4, 7, 2, 9, 4, 10, 8, 7, 4, 6, 1};
        ObligSBinTre<Integer> tre2 = new ObligSBinTre<>(Comparator.naturalOrder());

        for (int verdi : c) {
            tre2.leggInn(verdi);

        }

        System.out.println("Oppgave 3");
        System.out.println("toString: " + tre2.toString()); // Utskrift: [1, 2, 4, 4, 4, 6, 7, 7, 8, 9, 10]

        System.out.println("------------------------------");

        //Oppgave4
        System.out.println("Oppgave 4");
        System.out.println("omvendtString: " + tre2.omvendtString()); // Utskrift: [10, 9, 8, 7, 7, 6, 4, 4, 4, 2, 1]

    }
}
