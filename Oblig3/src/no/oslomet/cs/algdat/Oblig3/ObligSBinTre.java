package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.sql.SQLOutput;
import java.util.*;

public class ObligSBinTre<T> implements Beholder<T> {
  private static final class Node<T>  {  // en indre nodeklasse

    private T verdi;                   // nodens verdi
    private Node<T> venstre, hoyre;    // venstre og hoyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
      this.verdi = verdi;
      venstre = v; hoyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder) { // konstruktør

      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){
      return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }
  
  @Override
  public boolean leggInn(T verdi) {
    Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

    Node<T> p = rot, q = null;               // p starter i roten
    int cmp = 0;                             // hjelpevariabel

    while (p != null) {    // fortsetter til p er ute av treet

      q = p;                                 // q er forelder til p
      cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
      p = cmp < 0 ? p.venstre : p.hoyre;     // flytter p
    }

    // p er nå null, dvs. ute av treet, q er den siste vi passerte

    p = new Node<>(verdi,q); // oppretter en ny node. p = null, q er forelder.

    if (q == null) {
      rot = p; // p blir rotnode
    }
    else if (cmp < 0){
      q.venstre = p; // venstre barn til q
    }
    else {
      q.hoyre = p;  // hoyre barn til q
    }

    antall++;   // én verdi mer i treet
    endringer++;
    return true;
  }
  
  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null) {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.hoyre;
      else return true;
    }

    return false;
  }
  
  @Override
  public boolean fjern(T verdi){  // hører til klassen SBinTre

      if (verdi == null) return false;  // treet har ingen nullverdier

      Node<T> p = rot, q = null;   // q skal være forelder til p

      while (p != null){            // leter etter verdi
          int cmp = comp.compare(verdi,p.verdi);      // sammenligner

          if (cmp < 0) { // går til venstre
              q = p;
              p = p.venstre;
          }
          else if (cmp > 0) {  // går til hoyre
              q = p;
              p = p.hoyre;
          }
          else break;    // den søkte verdien ligger i p
      }

      if (p == null) return false;   // finner ikke verdi

      if (p.venstre == null || p.hoyre == null){  // Tilfelle 1) og 2)
          Node<T> b = p.venstre != null ? p.venstre : p.hoyre;  // b for barn

          if(b != null){
            b.forelder = q;
          }
          if (p == rot) {
              rot = b;
          }
          else if (p == q.venstre) {
              q.venstre = b;
          }
          else {
              q.hoyre = b;
          }
      }

      else{  // Tilfelle 3)
          Node<T> s = p, r = p.hoyre;   // finner neste i inorden

          while (r.venstre != null) {
              s = r;    // s er forelder til r
              r = r.venstre;
          }

          p.verdi = r.verdi;   // kopierer verdien i r til p

          if (s != p) {
              s.venstre = r.hoyre;  // Antar at r.hoyre er lik null her
              s.forelder = p;
          }
          else {
              s.hoyre = r.hoyre;  // Antar at r.hoyre er lik null her
          }
      }

      antall--;   // det er nå én node mindre i treet
      return true;

  }
  
  public int fjernAlle(T verdi) {
      if(tom()){
        return 0;
      }
      else if(antall(verdi) == 0){
        return 0;
      }

      int antallFjernet = 0; // hjelpe variabel for å telle antall forekomst
      Node<T> node = rot; // oppretter en ny node fra roten

      //Hvis verdien finnes
      if (inneholder(verdi)) {

          while (node != null) {

              int cmp = comp.compare(verdi, node.verdi); // bruker comparator for å sammenligne verdien med node sin verdi

              if (cmp < 0) { // returverdi er negativ hvis mindre
                  node = node.venstre;
              }
              else {
                  if (cmp == 0) { // returverdi er 0 hvis like
                      fjern(verdi);  //Fjerner noden når man finner match
                      antallFjernet++;
                      antall--;
                  }
                  node = node.hoyre; // returverdi er større
              }
          }
      }
      return antallFjernet; //returnerer antall slettede elementer.

  }
  
  @Override
  public int antall() {
    return antall;
  }
  
  public int antall(T verdi) {
    int forekomst = 0; // hjelpe variabel for å telle antall forekomst
    Node<T> node = rot; // oppretter en ny node fra roten

      //Hvis verdien finnes
      if (inneholder(verdi)) {

        while (node != null) {

          int cmp = comp.compare(verdi, node.verdi); // bruker comparator for å sammenligne verdien med node sin verdi

          if (cmp < 0) { // returverdi er negativ hvis mindre
            node = node.venstre;
          }
          else {
            if (cmp == 0) { // returverdi er 0 hvis like
              forekomst++;
            }
            node = node.hoyre; // returverdi er større
          }
        }
      }
    return forekomst; //returnerer antall forekomst av verdi i treet.

  }

  @Override
  public boolean tom() {
      return antall == 0;
  }
  
  @Override
  public void nullstill() {
      if(!tom()) {
          postOrdenNullstill(rot);
          rot = null;
          antall = 0;
          endringer++;
      }
  }

  //Traverserer igjennom alle elementene rekursivt via postorden og sletter dem
  private void postOrdenNullstill(Node<T> p){
      if(p.venstre != null) {
          postOrdenNullstill(p.venstre);
          p.venstre = null;
      }
      if(p.hoyre != null) {
          postOrdenNullstill(p.hoyre);
          p.hoyre = null;
      }
      p.verdi = null;
  }

  private static <T> Node<T> nesteInorden(Node<T> p) {
    // privat metode - tas for gitt at p ikke er null
    // må sikre at det ikke er nullreferanse når metoden brukes!
    // returnerer den noden som kommer etter p i inorden



    // Hvis p har et hoyre subtre, da vil den neste i inorden være den noden som ligger lengst ned til venstre i det subtreet
      if(p.hoyre != null){
         p = p.hoyre; // hopper til hoyre subtre
      while(p.venstre != null){ // hopper nedover i venstre subtre hvis det eksisterer
        p = p.venstre;
      }
    }
    // Hvis p ikke har et hoyre subtre og p ikke er den siste i inorden, da vil den neste i inorden være høyere opp i treet
    else {
      while(p.forelder != null && p == p.forelder.hoyre){
        p = p.forelder;
      }
      p = p.forelder;
    }

    return p;
  }

  @Override
  public String toString() {
    // husk å sjekke om p har nullverdier
    if (tom()) {
      return "[]";
    }

    // returnere en tegnstreng med treets verdier i inorden. skal rammes inn i []
    StringBuilder sb = new StringBuilder();
    sb.append("["); // starter stringen til SB
    // mellom verdiene skal det være komma og mellomrom / et tomt tre: []
    // start med å finne den første noden p i inorden.
    // deretter (while-løkke f.eks) p = nesteInorden(p) gi den neste osv til p blir null
    Node<T> p = rot;

    if(p.venstre == null && p.hoyre == null){
      sb.append(p.verdi);
    }
    else{
      while(p.venstre != null){
        p = p.venstre;
      }
      sb.append(p.verdi).append(", ");


      while(nesteInorden(p) != null) {
        p = nesteInorden(p);
        sb.append(p);

        if (nesteInorden(p) == null) {
          break;
        }
        sb.append(", ");
      }
    }

    sb.append("]"); // avslutter toStringen til SB.
    return sb.toString();
  }
  
  public String omvendtString() {
    if (tom()) {
      return "[]";
    }

    Stakk<Node<T>> nodeStakk = new TabellStakk<>();
    Node<T> p = rot; // starter i roten og går til venstre

    StringBuilder sb = new StringBuilder();
    sb.append("[");

    // går ned til hoyre
    for (; p.hoyre != null; p = p.hoyre){
      nodeStakk.leggInn(p); // legger til verdiene som blir passert i stakken
      // p er nå lik den siste verdien

    }
    sb.append(p.verdi);



    // sjekker om p har venstre barn
    while (true) {
      if (p.venstre != null) {

        // venstre barn funnet, p er lik venstre barnet
        p = p.venstre;

        // sjekker om det finnes et hoyre barn, om det fins, så legges det til stakken
        for (; p.hoyre != null; p = p.hoyre){
          nodeStakk.leggInn(p); // legger til verdiene i en stakk.
        }
      }
      // hvis det ikke finnes noe hoyre barn, blir den tatt ut av stakken og lagt inn i strengen
      else if(!nodeStakk.tom()) {
        p = nodeStakk.taUt(); // p.hoyre == null, henter fra stakken
      }

      else
        break; // stakken er tom, vi er ferdig

      // hopper ut av løkken og traversering er gjennomført.

      sb.append(", ").append(p.verdi);

    }
    sb.append("]");

    return sb.toString();
  }
  
  public String høyreGren() {
    //Skal returnere en tegnstreng med grenens verdier. Skal være innrammet med [] og separert med komma+" ".
    //hvis treet er tomt, altså ingen grener, da skal kun [] returneres.
    //a) denne metoden skal gi den grenen som ender i den bladnoden som ligger lengst til hoyre.
    //Pass på at hvis treet kun har én gren skal denne både være hoyre gren og lengste gren.
    //Dette gjelder også hvis treet kun har én node, dette er da en gren.
    if (tom()) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder();
    sb.append("[");

    //Putte inn masse fint i sb :)

    Node<T> p = rot;

    while(p.verdi != null){

      if (p.venstre == null && p.hoyre == null) { // p er en bladnode
        sb.append(p.verdi);
        break;
      }

      else if (p.hoyre != null) { // p har et høyrebarn
        sb.append(p.verdi).append(", ");
        p = p.hoyre;
      }

      else if (p.venstre != null) { //  p har et venstrebarn
        sb.append(p.verdi).append(", ");
        p = p.venstre;
      }
    }

    sb.append("]");

    return sb.toString();

  }

  public String lengstGren() {
    //henter mye av denne metoden fra kompendiet. Programkode 5.1.6 a)
    if (tom())
      return "[]";

    Deque<Node<T>> kø = new ArrayDeque<>();
    //Legger til rot-noden sist
    kø.addLast(rot);

    //Oppretter en tom node
    Node<T> p = null;

    while (!kø.isEmpty()) { //while-loop som går igjennom treet i nivåorden. Når loopen stopper er p lik den siste verdien i treet.

      p = kø.removeFirst();

      if (p.hoyre != null)
        kø.addLast(p.hoyre);
      if (p.venstre != null)
        kø.addLast(p.venstre);
    }

    return gren(p); //kaller hjelpemetoden gren for å få grenen som en streng.
  }

  private <T> String gren(Node<T> p) {

    Stack<T> stackA = new Stack<>();
    Stack<T> stackB = new Stack<>();

    while (p != null) { //legger p og alle dens foreldrenoder opp til rotnoden (altså hele grenen) på stack A
      stackA.push(p.verdi);
      p = p.forelder;
    }

    while (!stackA.isEmpty()) { //flytter grenen til Stack B, og dermed snur rekkefølgen.
      stackB.push(stackA.pop());
    }

    return stackB.toString();
  }


  public String[] grener() {
    List<String> grenListe = new ArrayList<>();

    grener(rot, grenListe);

    String [] grener = new String[grenListe.size()];
    System.out.println(grenListe.size());

    //Legger inn elementene fra en liste til en annen.
    for(int i = 0; i < grener.length; i++){
      grener[i] = grenListe.get(i);
    }

    return grener;
  }

  //hjelpemetode som brukes til å finne nodene i alle subtrærne til p og legger de til i en ArrayList
  private void grener(Node<T> p, List<String> nodeListe) {
    if (p.venstre == null && p.hoyre == null) { // tilfelle 1: p er en bladnode
      nodeListe.add(gren(p));
    }

    else if (p.venstre != null) { // tilfelle 2: p har et venstrebarn
      grener(p.venstre, nodeListe);
    }

    else if (p.hoyre != null) { // tilfelle 3: p har et høyrebarn
      grener(p.hoyre, nodeListe);
    }
  }

  public String bladnodeverdier() {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }

  public String postString() {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public Iterator<T> iterator() {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T> {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;
    
    private BladnodeIterator()  // konstruktør
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

  } // BladnodeIterator

} // ObligSBinTre
