package no.oslomet.cs.algdat;

/* Gruppemedlemmer:
   s333745 - Ana-Maria Poljac
   s333783 - Camilla Hoelgaard
   s333781 - Signe Aanderaa Eide
   s326325 - Maria Teresita Halvorsen
 */


////////////////// class DobbeltLenketListe //////////////////////////////


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;



public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den forste i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {
        hode = hale = null;
        antall = 0;
        endringer = 0;
    }

    public DobbeltLenketListe(T[] a) {
        //Hvis a er 0, saa skal det kastet en NullPointerException
        Objects.requireNonNull(a,"Tabellen a er null!");

        hode = hale = new Node<>(null); // oppretter en midlertig node.
        int teller = 0;

        for (T verdi : a) {
            if (verdi != null) {
                if(teller == 0){
                    hode = hale = hale.neste = new Node<>(verdi, hale, null);
                    teller++;
                }
                else {
                    hale = hale.neste = new Node<>(verdi, hale, null); // ny verdi legges bakerst
                }
                antall++;
            }
            if (tom()) {
                hode = hale = new Node<>(null); // tom liste
            }
        }
    }

    private static void fratilKontroll(int antall, int fra, int til){
        // sjekkes om indeksene fra og til er lovlige - hvis ikke skal det kastes unntak (fratilkontroll())
        // bytt ut AIOB exception med IOOB excpetion siden vi ikke har noe array tabell her
        // bytt ut ordet tablengde med ordet antall (kalles med antall, fra og til som argumenter)
        if(fra < 0){
            throw new IndexOutOfBoundsException("fra(" + fra + ") er negativ!");
        }

        if(til > antall){
            throw new IndexOutOfBoundsException("til(" + til +") > antall ("+ antall + ")");
        }

        if(fra > til){
            throw new IllegalArgumentException("fra(" + fra + ") > til "+til+ ") - illegalt intervall");
        }
    }

    public Liste<T> subliste(int fra, int til){
        // returnere en liste som inneholder verdiene fra intervallet
        // tomt intervall er lovlig - faar en tom liste
        // endringer skal vaere 0
        fratilKontroll(antall,fra,til);
        DobbeltLenketListe<T> subliste = new DobbeltLenketListe<>();

        if(tom()){
            antall();
        }
        else{
            for(int i = fra; i < til; i++){
                T verdi = hent(i);
                subliste.leggInn(verdi);

            }
        }
        endringer = 0;
        return subliste;
    }

    @Override
    public int antall() {
       return antall;
    }

    @Override
    public boolean tom() {
        if (antall == 0 ){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Kan ikke legge til tomme verdier.");

        Node node = new Node(verdi);

        //Tilfelle 1: listen er tom
        if(tom()){
            hode = hale = node;
        }

        //Tilfelle 2: listen er ikke tom
        else{
            hale.neste = node;
            node.forrige = hale;
            hale = node;
        }
        antall++;
        endringer++;

        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        if(indeks < 0 || indeks > antall){
            throw new IndexOutOfBoundsException("Indeks er ikke gyldig");
        }
        else if(verdi == null) {
            Objects.requireNonNull(verdi, "Verdien er tom.");
        }

        else {
            Node<T> node = new Node<>(verdi);

            //Scenarioer:
            //1. tom liste
            if (tom()) {
                hode = hale = node;
            }

            //2. verdien skal legges forst - forrigepeker = null
            else if (indeks == 0) {
                node.neste = hode;
                hode.forrige = node;
                hode = node;
            }

            //3. verdien skal legges bakerst - nestepeker = null
            else if (indeks == antall) {
                node.forrige = hale;
                hale.neste = node;
                hale = node;
            }

            //4. verdien skal legges mellom to andre verdier
            else {
                Node<T> p = hode;
                Node<T> q;
                for (int i = 0; i < indeks; i++) {
                    p = p.neste;
                }
                q = p.forrige;
                node.forrige = q;
                node.neste = p;
                q.neste = node;
                p.forrige = node;
            }

            endringer++;
            antall++;
        }
    }

    @Override
    public boolean inneholder(T verdi) {
        return indeksTil(verdi) != -1;

    }

    @Override
    public T hent(int indeks) {
        indeksKontroll(indeks,false);
        return finnNode(indeks).verdi;
    }

    private Node<T> finnNode(int indeks){
        //Finne noden med den gitte indeksen.
        int midten = antall / 2;
        Node<T> p = hode;
        Node<T> q = hale;
        Node<T> temp = hode;

        if(antall == 1){
            return p; //Kun et element, returnerer her hodet (kunne vaert hale ogsaa, men de er jo det samme..)
        }

        //Hvis indeks er hoyere enn midten - saa skal man starte fra halen og gaa mot venstre, bruke forrige-pekere
        else if(indeks > midten){
            for(int i = antall-1; i >= indeks; i--){
                temp = q;
                q = q.forrige;
            }
            return temp;
        }

        //Hvis indeks er mindre eller lik midten - saa skal letingen starte fra hodet og gaa mot hoyre, bruke neste-pekere
        else{
            for(int i = 0; i <= indeks; i++){
                temp = p;
                p = p.neste;
            }
            return temp;
        }
    }


    @Override
    public int indeksTil(T verdi) {
        Node<T> node = hode;

        boolean funnet = false; //  sjekker om den er funnet
        int indeks = 0;

        while (node != null) { // saa lenge node ikke er null, skal while lokken kjore
            
            if (node.verdi.equals(verdi)) { // hvis node.neste.verdi er det samme som verdien som tas inn
                funnet = true; // setter denne variabelen til true
                break;
            }
            node = node.neste; // setter pekeren til neste node.
            indeks++; // og oker indeksen
        }

        if(funnet == true){ // er den funnet, returner indeks - som vil si, fordi den okes kun naar den er funnet, dermed faar vi 1 tilbake
            return indeks;


        }
        else {
            //hvis ikke returner -1, som vil si at den ikke eksistere
            return -1;
        }

    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        //Denne skal erstatte verdien paa indeks med en ny verdi og returnere det som laa der fra for
        //Indekskontroll og sjekk av null-verdier
        //Husk aa oke endringer

        Objects.requireNonNull(nyverdi,"Ikke tillatt med nullverdier.");
        indeksKontroll(indeks, false);

        Node<T> p = finnNode(indeks);
        T temp = p.verdi;
        p.verdi = nyverdi;
        endringer++;

        return temp;
    }

    @Override
    public boolean fjern(T verdi) {
        //Denne skal fjerne verdi fra lista og returnere true
        //hvis verdi ikke er i lista, skal metoden returnere false
        //Variabel antall skal reduseres og endringer skal okes
        //pass paa at tilfellet blir behandlet riktig hvis lista blir tom.

        if(tom()){
            return false;
        }

        else if(antall == 1 && hode.verdi.equals(verdi)){
            hode = hale = null;
            antall--;
            endringer++;
            return true;
        }

        else {
            Node<T> q = hode;

            for (int i = 0; i < antall; i++) {
                if (q.verdi.equals(verdi)) {
                    Node<T> p = q.forrige;
                    Node<T> r = q.neste;

                    //1. tilfelle: den forste fjernes
                    if (q == hode) {
                        r.forrige = null;
                        hode = r;
                    }

                    //2. tilfelle: den siste fjernes
                    else if (q == hale) {
                        p.neste = null;
                        hale = p;
                    }

                    //3. tilfelle: en verdi mellom to andre fjernes
                    else {
                        p.neste = r;
                        r.forrige = p;
                    }

                    antall--;
                    endringer++;

                    return true;
                }
                q = q.neste;
            }
            return false;
        }
    }

    @Override
    public T fjern(int indeks) {
        //Denne skal fjerne og returnere verdien paa posisjon indeks.
        //Obs: denne maa forst sjekkes
        //Variabel antall skal reduseres og endringer skal okes
        //pass paa at tilfellet blir behandlet riktig hvis lista blir tom.

        if(tom()){
            throw new IndexOutOfBoundsException("Tom liste!");
        }

        else if(indeks < 0 || indeks >= antall){
            throw new IndexOutOfBoundsException("Ikke gyldig indeks.");
        }

        else {
            Node<T> q = finnNode(indeks);
            Node<T> p = q.forrige;
            Node<T> r;

            //1a. tilfelle: den forste fjernes og det er bare et element i listen
            if(antall == 1){
                hode = hale = null;
            }

            //1b. tilfelle: den forste fjernes
            else if (indeks == 0) {
                r = q.neste;
                r.forrige = null;
                hode = r;
            }

            //2. tilfelle: den siste fjernes
            else if (indeks == (antall - 1)) {
                p.neste = null;
                hale = p;
            }

            //3. tilfelle: en verdi mellom to andre fjernes
            else {
                r = q.neste;
                p.neste = r;
                r.forrige = p;
            }

            antall--;
            endringer++;

            return q.verdi;
        }
    }

    @Override
    public void nullstill() {
        Node<T> node = hode;
        Node<T> temp;

        //1. maate: Start i ​hode​ og gaa mot ​hale​ ved hjelpe pekeren ​neste.​ For hver node «nulles»
        // nodeverdien og alle nodens pekere. Til slutt settes baade ​hode​ og ​hale​ til null, ​antall​
        // til 0 og ​endringer​ okes. Hvis du er i tvil om hva som det bes om her, kan du slaa opp
        /* i kildekodenformetoden​clear(​)​iklassen​LinkedList​iJava.
        for(int i = 0; i < antall; i++){
            temp = node.neste;
            node.verdi = null;
            node.neste = null;
            node.forrige = null;
            node = temp;
        }
        hode = hale = null;
        */

        //2. maate: Lag en lokke som inneholder metodekallet ​fjern(​0) ​(den forste noden fjernes) og
        // som gaar inntil listen er tom.
        for(int j = 0; j < antall-1; j++){
            fjern(0);
        }
        antall = 0;
        endringer++;

        //2. maate er raskest!
    }

    //Oppg 2a)
    @Override
    public String toString() {
        if(tom()){
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");//starter Stringen med klammeparentes og legger til forste elementet i lista.

        Node<T> node = hode;

        while (node != null) {
            if(node.verdi != null){
                if (node.neste == null) {
                    sb.append(node.verdi);
                } else {
                    sb.append(node.verdi).append(", "); //legger videre til de neste elementene.
                }
            }
            node = node.neste;
        }

        sb.append("]"); //avslutter Stringen med en klammeparentes.

        return sb.toString();//returnerer toStringen til StringBuilder'en.

    }

    //Oppg 2a)
    public String omvendtString() {
        if(tom()){
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");//starter Stringen med klammeparentes og legger til forste elementet i lista.

        Node<T> node = hale;

        while(node != null){
            if(node.verdi != null){
                if (node.forrige == null || node.forrige.verdi == null) {
                    sb.append(node.verdi);
                }
                else {
                    sb.append(node.verdi).append(", "); //legger videre til de neste elementene.
                }
            }
            node = node.forrige;
        }
        sb.append("]"); //avslutter Stringen med en klammeparentes.

        return sb.toString();//returnerer toStringen til StringBuilder'en.
    }

    @Override
    public Iterator<T> iterator() {
        DobbeltLenketListeIterator iterator = new DobbeltLenketListeIterator();
        return iterator;
    }

    public Iterator<T> iterator(int indeks) {
        //Sjekk lovlig indeks, indekskontroll
        //bruke ny konstruktor og returnere en instans av iteratorklassen

        indeksKontroll(indeks, false);
        return new DobbeltLenketListeIterator(indeks);
    }

    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne, temp;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator(){
            denne = hode;     // p starter paa den forste i listen
            fjernOK = false;  // blir sann naar next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks){
            //Sette "denne" til noden som horer til den oppgitte indeksen
            //Resten skal vaere likt som konstruktoren over.

            denne = finnNode(indeks);
            fjernOK = false;
            iteratorendringer = endringer;
        }

        @Override       
        public boolean hasNext(){
            return denne != null;
        }

        @Override
        public T next(){
            //Sjekke om iteratorendringer er lik endringe, kaste Exception
            //Om det hasNext() er false, er det ikke flere elementer i listen og det skal kastes noSuchElementException
            //Setter fjernOk til true
            //"denne" returneres og "denne" flyttes til neste node

            if(iteratorendringer != endringer){
                throw new ConcurrentModificationException("Feil i antall endringer.");
            }
            else if(!hasNext()){
                throw new NoSuchElementException("Ikke flere elementer igjen i listen.");
            }

            temp = denne;
            fjernOK = true;
            denne = denne.neste;
            return temp.verdi;
        }

        @Override
        public void remove(){
            if(!fjernOK){
                throw new IllegalStateException("Ulovlig tilstand!");
            }
            if(endringer != iteratorendringer){
                throw new ConcurrentModificationException("Feil i antall endringer.");
            }

            fjernOK = false;

            //4 Tilfeller:
            //1. antall == 1, nulles hode og hale
            if(antall == 1){
                hode = hale = null;
            }

            //2. siste element i listen, denne == null, hale oppdateres
            else if(denne == null){
                hale = temp.forrige;
                hale.neste = null;
            }

            //3. forste skal fjernes, denne.forrige == hode, hodet maa oppdateres
            else if(temp == hode){
                denne.forrige = null;
                hode = denne;
            }

            //4. Midt i listen, saa maa pekerne paa hver sin side oppdateres.
            else{
                Node<T> node = temp.forrige;
                denne.forrige = node;
                node.neste = denne;
                denne = denne.neste;
            }

            antall--;
            endringer++;
            iteratorendringer++;
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        // sortere 'liste' vha komparatoren c
        // kun metodene fra grensesnittet Liste som kan brukes
        // skal loses uten hjelpestrukturer - "paa plass" sortering,
        // m.a.o: ikke kopiere verdiene over i en tabell og sortere, for saa aa kopiere dem tilbake

        // To elementer til aa sammenligne
        T elem1;
        T elem2;

        int teller = 1;

        while(teller != 0) {
            teller = 0;

            for (int i = 0; i < liste.antall() - 1; i++) {
                elem1 = liste.hent(i);
                elem2 = liste.hent(i + 1);

                // hvis forste element er "storre" enn elementet det sammenlignes med, saa returneres en verdi hoyere enn 0
                if (c.compare(elem1,elem2) > 0) {
                    teller++;
                    // bytter plass paa elementene
                    liste.oppdater(i, elem2);
                    liste.oppdater(i+1,elem1);
                }
            }
        }
    }

} // class DobbeltLenketListe


