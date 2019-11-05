package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import javax.xml.soap.Node;
import java.util.*;

public class ObligSBinTre<T> implements Beholder<T> {
  private static final class Node<T>  {  // en indre nodeklasse

    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
      this.verdi = verdi;
      venstre = v; høyre = h;
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
      p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
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
      q.høyre = p;  // høyre barn til q
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
      else if (cmp > 0) p = p.høyre;
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
          else if (cmp > 0) {  // går til høyre
              q = p;
              p = p.høyre;
          }
          else break;    // den søkte verdien ligger i p
      }

      if (p == null) return false;   // finner ikke verdi

      if (p.venstre == null || p.høyre == null){  // p har 1 eller ingen barn
          Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn

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
              q.høyre = b;
          }
      }

      else{  // Tilfelle 3)
          Node<T> s = p, r = p.høyre;   // finner neste i inorden

          while (r.venstre != null) {
              s = r;    // s er forelder til r
              r = r.venstre;
          }

          p.verdi = r.verdi;   // kopierer verdien i r til p

          if (r.høyre != null) {
              r.høyre.forelder = s;
          }
          if (s != p) {
              s.venstre = r.høyre;
          }
          else {
              s.høyre = r.høyre;
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
                  node = node.høyre; // returverdi er større
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
            node = node.høyre; // returverdi er større
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
      if(p.høyre != null) {
          postOrdenNullstill(p.høyre);
          p.høyre = null;
      }
      p.verdi = null;
  }

  private static <T> Node<T> nesteInorden(Node<T> p) {
    // privat metode - tas for gitt at p ikke er null
    // må sikre at det ikke er nullreferanse når metoden brukes!
    // returnerer den noden som kommer etter p i inorden


    // Hvis p har et høyre subtre, da vil den neste i inorden være den noden som ligger lengst ned til venstre i det subtreet
      if(p.høyre != null){
         p = p.høyre; // hopper til høyre subtre
      while(p.venstre != null){ // hopper nedover i venstre subtre hvis det eksisterer
        p = p.venstre;
      }
    }
    // Hvis p ikke har et høyre subtre og p ikke er den siste i inorden, da vil den neste i inorden være høyere opp i treet
    else {
      while(p.forelder != null && p == p.forelder.høyre){
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

    if(p.venstre == null && p.høyre == null){
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

    // går ned til høyre
    for (; p.høyre != null; p = p.høyre){
      nodeStakk.leggInn(p); // legger til verdiene som blir passert i stakken
      // p er nå lik den siste verdien

    }
    sb.append(p.verdi);



    // sjekker om p har venstre barn
    while (true) {
      if (p.venstre != null) {

        // venstre barn funnet, p er lik venstre barnet
        p = p.venstre;

        // sjekker om det finnes et høyre barn, om det fins, så legges det til stakken
        for (; p.høyre != null; p = p.høyre){
          nodeStakk.leggInn(p); // legger til verdiene i en stakk.
        }
      }
      // hvis det ikke finnes noe høyre barn, blir den tatt ut av stakken og lagt inn i strengen
      else if(!nodeStakk.tom()) {
        p = nodeStakk.taUt(); // p.høyre == null, henter fra stakken
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
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String lengstGren() {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String[] grener() {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }

  public String bladnodeverdier() {
    if (tom()) {
      return "[]";
    }

    ArrayList<T> bladnodeListe = new ArrayList<>(); //oppretter listen som brukes til å oppbevare alle bladnodene

    bladnoder(rot, bladnodeListe); //finner alle bladnodene og lagrer de i hver sin String

    StringBuilder sb = new StringBuilder();
    sb.append("[");

    int teller = 0;
    for (T verdi : bladnodeListe) { //går igjennom hvert element i listen og legger dem inn i sb
      teller++;
      if(teller == bladnodeListe.size()){
        sb.append(verdi.toString());
      }
      else{
        sb.append(verdi.toString()).append(", ");
      }
    }

    sb.append("]");

    return sb.toString();
  }

  //hjelpemetode som brukes til å finne bladnodene i alle subtrærne til p og legger de til i en ArrayList
  private void bladnoder(Node<T> p, List<T> bladnodeListe) {
    if (p.venstre == null && p.høyre == null) { // tilfelle 1: p er en bladnode
      bladnodeListe.add(p.verdi);
    }

    if (p.venstre != null) { // tilfelle 2: p har et venstrebarn
      bladnoder(p.venstre, bladnodeListe);
    }

    if (p.høyre != null) { // tilfelle 3: p har et høyrebarn
      bladnoder(p.høyre, bladnodeListe);
    }
  }
  
  public String postString() {
    if (tom()) {
      return "[]";
    }

    Node<T> p = førsteBladnode(rot);

    StringBuilder sb = new StringBuilder();
    sb.append("[").append(p.verdi); //Legger inn første bladnode lengst til venstre

    while (p.forelder != null) { //stopper løkken når vi kommer tilbake til roten
      p = neste(p); //setter p lik den neste noden i Postorden
      sb.append(", ").append(p.verdi);
    }

    sb.append("]");

    return sb.toString();
  }

  //Hjelpemetode til postString
  private Node<T> førsteBladnode(Node<T> p) {

    while (true) {
      if (p.venstre != null) { //så langt til venstre det går
        p = p.venstre;
      }
      else if (p.høyre != null) { //om det er tomt for venstrebarn går man én gang til høyre
        p = p.høyre;
      }
      else //ingen flere barn, node funnet
        break;
    }
    return p;
  }

  //Hjelpemetode til postString
  private Node<T> neste(Node<T> p) {
    Node<T> q = p.forelder; //forelserenode

    //Hvis q ikke har et høyrebarn, går opp til forelder
    if (q.høyre == null) {
      p = q;
    }
    //Hvis p er høyrebarnet til q, går opp til forelder
    else if(p == q.høyre){
      p = q;
    }

    //p er venstrebarn og q.høyrebarn finnes. Går videre til høyrebarnet
    else{
      p = førsteBladnode(q.høyre);
    }
    return p;
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
