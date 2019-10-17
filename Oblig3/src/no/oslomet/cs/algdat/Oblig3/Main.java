package no.oslomet.cs.algdat.Oblig3;

import java.util.Comparator;

public class Main {

    public static void main(String[] args) {
        //Innledning
        ObligSBinTre<String> tre  = new ObligSBinTre<>(Comparator.naturalOrder());
        System.out.println(tre.antall());
    }
}
