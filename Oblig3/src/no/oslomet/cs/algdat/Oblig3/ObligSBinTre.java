package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////
/*
 * Maria Teresita Halvorsen : s326325
 * Signe Aanderaa Eide : s333781
 * Camilla Hoelgaard : s333783
 * Ana-Maria Poljac : s333745
 * Christian Dyrli: s333738
 * */

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T> {
  private static final class Node<T>  {  // en indre nodeklasse

    private T verdi;                   // nodens verdi
    private Node<T> venstre, hoyre;    // venstre og hoyre barn
    private Node<T> forelder;          // forelder

    // konstruktor
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
      this.verdi = verdi;
      venstre = v; hoyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder) { // konstruktor

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

  public ObligSBinTre(Comparator<? super T> c)    // konstruktor
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

    // p er naa null, dvs. ute av treet, q er den siste vi passerte

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
  public boolean fjern(T verdi){  // tilhorer til klassen SBinTre

      if (verdi == null) return false;  // treet har ingen nullverdier

      Node<T> p = rot, q = null;   // q skal vaere forelder til p

      while (p != null){            // leter etter verdi
          int cmp = comp.compare(verdi,p.verdi);      // sammenligner

          if (cmp < 0) { // gar til venstre
              q = p;
              p = p.venstre;
          }
          else if (cmp > 0) {  // gar til hoyre
              q = p;
              p = p.hoyre;
          }
          else break;    // den sokte verdien ligger i p
      }

      if (p == null) return false;   // finner ikke verdi

      if (p.venstre == null || p.hoyre == null){  // p har 1 eller ingen barn
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

          if (r.hoyre != null) {
              r.hoyre.forelder = s;
          }
          if (s != p) {
              s.venstre = r.hoyre;
          }
          else {
              s.hoyre = r.hoyre;
          }
      }

      antall--;// det er na én node mindre i treet
      endringer++; // det er na gjort en endring mer
      return true;

  }
  
  public int fjernAlle(T verdi) {
      if(tom()){
        return 0;
      }
      else if(antall(verdi) == 0){
        return 0;
      }

      int antallFjernet = 0; // hjelpe variabel for a telle antall forekomst
      Node<T> node = rot; // oppretter en ny node fra roten

      //Hvis verdien finnes
      if (inneholder(verdi)) {

          while (node != null) {

              int cmp = comp.compare(verdi, node.verdi); // bruker comparator for aa sammenligne verdien med node sin verdi

              if (cmp < 0) { // returverdi er negativ hvis mindre
                  node = node.venstre;
              }
              else {
                  if (cmp == 0) { // returverdi er 0 hvis like
                      fjern(verdi);  //Fjerner noden naar man finner match
                      antallFjernet++;
                      antall--;
                  }
                  node = node.hoyre; // returverdi er storre
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
    int forekomst = 0; // hjelpe variabel for aa telle antall forekomst
    Node<T> node = rot; // oppretter en ny node fra roten

      //Hvis verdien finnes
      if (inneholder(verdi)) {

        while (node != null) {

          int cmp = comp.compare(verdi, node.verdi); // bruker comparator for aa sammenligne verdien med node sin verdi

          if (cmp < 0) { // returverdi er negativ hvis mindre
            node = node.venstre;
          }
          else {
            if (cmp == 0) { // returverdi er 0 hvis like
              forekomst++;
            }
            node = node.hoyre; // returverdi er storre
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
    // maa sikre at det ikke er nullreferanse naar metoden brukes!
    // returnerer den noden som kommer etter p i inorden


    // Hvis p har et hoyre subtre, da vil den neste i inorden vaere den noden som ligger lengst ned til venstre i det subtreet
      if(p.hoyre != null){
         p = p.hoyre; // hopper til hoyre subtre
      while(p.venstre != null){ // hopper nedover i venstre subtre hvis det eksisterer
        p = p.venstre;
      }
    }
    // Hvis p ikke har et hoyre subtre og p ikke er den siste i inorden, da vil den neste i inorden vaere hoyere opp i treet
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
    // husk aa sjekke om p har nullverdier
    if (tom()) {
      return "[]";
    }

    // returnere en tegnstreng med treets verdier i inorden. skal rammes inn i []
    StringBuilder sb = new StringBuilder();
    sb.append("["); // starter stringen til SB
    // mellom verdiene skal det vaere komma og mellomrom / et tomt tre: []
    // start med aa finne den forste noden p i inorden.
    // deretter (while-lokke f.eks) p = nesteInorden(p) gi den neste osv til p blir null
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
    Node<T> p = rot; // starter i roten og gaar til venstre

    StringBuilder sb = new StringBuilder();
    sb.append("[");

    // gaar ned til hoyre
    for (; p.hoyre != null; p = p.hoyre){
      nodeStakk.leggInn(p); // legger til verdiene som blir passert i stakken
      // p er naa lik den siste verdien

    }
    sb.append(p.verdi);



    // sjekker om p har venstre barn
    while (true) {
      if (p.venstre != null) {

        // venstre barn funnet, p er lik venstre barnet
        p = p.venstre;

        // sjekker om det finnes et hoyre barn, om det fins, saa legges det til stakken
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

      // hopper ut av lokken og traversering er gjennomfort.

      sb.append(", ").append(p.verdi);

    }
    sb.append("]");

    return sb.toString();
  }
  public String hoyreGren() {
    //Skal returnere en tegnstreng med grenens verdier. Skal vaere innrammet med [] og separert med komma+" ".
    //hvis treet er tomt, altsaa ingen grener, da skal kun [] returneres.
    //a) denne metoden skal gi den grenen som ender i den bladnoden som ligger lengst til hoyre.
    //Pass paa at hvis treet kun har én gren skal denne baade vaere hoyre gren og lengste gren.
    //Dette gjelder ogsaa hvis treet kun har én node, dette er da en gren.
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

      else if (p.hoyre != null) { // p har et hoyrebarn
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

    Deque<Node<T>> ko = new ArrayDeque<>();
    //Legger til rot-noden sist
    ko.addLast(rot);

    //Oppretter en tom node
    Node<T> p = null;

    while (!ko.isEmpty()) { //while-loop som gaar igjennom treet i nivaaorden. Naar loopen stopper er p lik den siste verdien i treet.

      p = ko.removeFirst();

      if (p.hoyre != null)
        ko.addLast(p.hoyre);
      if (p.venstre != null)
        ko.addLast(p.venstre);
    }

    return gren(p); //kaller hjelpemetoden gren for aa faa grenen som en streng.
  }

  private <T> String gren(Node<T> p) {

    Stack<T> stackA = new Stack<>();
    Stack<T> stackB = new Stack<>();

    while (p != null) { //legger p og alle dens foreldrenoder opp til rotnoden (altsaa hele grenen) paa stack A
      stackA.push(p.verdi);
      p = p.forelder;
    }

    while (!stackA.isEmpty()) { //flytter grenen til Stack B, og dermed snur rekkefolgen.
      stackB.push(stackA.pop());
    }

    return stackB.toString();
  }

  public String[] grener() {
    if(tom()){
      return new String [0];
    }

    List<String> grenListe = new ArrayList<>();

    grener(rot, grenListe);

    String [] grener = new String[grenListe.size()];

    //Legger inn elementene fra en liste til en annen.
    for(int i = 0; i < grener.length; i++){
      grener[i] = grenListe.get(i);
    }

    return grener;
  }

  //hjelpemetode som brukes til aa finne nodene i alle subtraerne til p og legger de til i en ArrayList
  private void grener(Node<T> p, List<String> nodeListe) {
    if (p.venstre == null && p.hoyre == null) { // tilfelle 1: p er en bladnode
      nodeListe.add(gren(p));
    }

    if (p.venstre != null) { // tilfelle 2: p har et venstrebarn
      grener(p.venstre, nodeListe);
    }

    if (p.hoyre != null) { // tilfelle 3: p har et hoyrebarn
      grener(p.hoyre, nodeListe);
    }
  }


  public String bladnodeverdier() {
    if (tom()) {
      return "[]";
    }

    ArrayList<T> bladnodeListe = new ArrayList<>(); //oppretter listen som brukes til aa oppbevare alle bladnodene

    bladnoder(rot, bladnodeListe); //finner alle bladnodene og lagrer de i hver sin String

    StringBuilder sb = new StringBuilder();
    sb.append("[");

    int teller = 0;
    for (T verdi : bladnodeListe) { //gaar igjennom hvert element i listen og legger dem inn i sb
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

  //hjelpemetode som brukes til aa finne bladnodene i alle subtraerne til p og legger de til i en ArrayList
  private void bladnoder(Node<T> p, List<T> bladnodeListe) {
    if (p.venstre == null && p.hoyre == null) { // tilfelle 1: p er en bladnode
      bladnodeListe.add(p.verdi);
    }

    if (p.venstre != null) { // tilfelle 2: p har et venstrebarn
      bladnoder(p.venstre, bladnodeListe);
    }

    if (p.hoyre != null) { // tilfelle 3: p har et hoyrebarn
      bladnoder(p.hoyre, bladnodeListe);
    }
  }
  
  public String postString() {
    if (tom()) {
      return "[]";
    }

    Node<T> p = forsteBladnode(rot);

    StringBuilder sb = new StringBuilder();
    sb.append("[").append(p.verdi); //Legger inn forste bladnode lengst til venstre

    while (p.forelder != null) { //stopper lokken naar vi kommer tilbake til roten
      p = neste(p); //setter p lik den neste noden i Postorden
      sb.append(", ").append(p.verdi);
    }

    sb.append("]");

    return sb.toString();
  }

  //Hjelpemetode til postString
  private Node<T> forsteBladnode(Node<T> p) {

    while (true) {
      if (p.venstre != null) { //saa langt til venstre det gaar
        p = p.venstre;
      }
      else if (p.hoyre != null) { //om det er tomt for venstrebarn gaar man én gang til hoyre
        p = p.hoyre;
      }
      else //ingen flere barn, node funnet
        break;
    }
    return p;
  }

  //Hjelpemetode til postString
  private Node<T> neste(Node<T> p) {
    Node<T> q = p.forelder; //forelserenode

    //Hvis q ikke har et hoyrebarn, gaar opp til forelder
    if (q.hoyre == null) {
      p = q;
    }
    //Hvis p er hoyrebarnet til q, gaar opp til forelder
    else if(p == q.hoyre){
      p = q;
    }

    //p er venstrebarn og q.hoyrebarn finnes. Gaar videre til hoyrebarnet
    else{
      p = forsteBladnode(q.hoyre);
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


    private BladnodeIterator(){  // konstruktor
      //flytte pekeren ​p ​ til forste bladnode, dvs. til den som er lengst til venstre hvis det er flere bladnoder
      // Hvis treet er tomt, skal ikke ​p ​ endres

      if(tom()){
        return;
      }

      p = rot;

      // finner forste bladnode
      while(true){
        if(p.venstre != null){
          p = p.venstre;
        }
        else if(p.hoyre != null){
          p = p.hoyre;
        }
        else{
          break;
        }
      }

      removeOK = false;
      iteratorendringer = endringer;
    }


    @Override
    public boolean hasNext(){
      return p != null;  // Denne skal ikke endres!
    }

    @Override
    public T next() {
      if(endringer != iteratorendringer){
        throw new ConcurrentModificationException("Endringer og iteratorendringer er forskjellige!");
      }
      //kaste en ​NoSuchElementException ​hvis det ikke er flere bladnoder igjen
      if(!hasNext()){
        throw new NoSuchElementException("Ingen bladnoder igjen!");
      }

      //setter q lik p, saa vi ikke mister peker til p naar p forandres
      q = p;

      //traverserer til neste bladnode og inn i neste gren eller subgren
      while (p.forelder != null && (p == p.forelder.hoyre || p.forelder.hoyre == null)) {
        p = p.forelder;
      }

      if(p.forelder != null) {
        p = p.forelder.hoyre;

        // traverserer nedover mot riktig bladnode
        while (true) {
          if (p.venstre != null) {
            p = p.venstre;
          } else if (p.hoyre != null) {
            p = p.hoyre;
          } else {
            break;
          }
        }
      }
      else{
        p = null;
      }
      //setter removeOK til true, saa det den kan fjernes av remove()
      removeOK = true;
      // Hvis ikke, skal den returnere en ​bladnodeverdi.
      return q.verdi;
    }

    @Override
    public void remove() {
        if(!removeOK){
            throw new IllegalStateException("Ulovlig tilstand");
        }
        if(endringer != iteratorendringer) {
            throw new ConcurrentModificationException("Endringer og iteratorendringer er forskjellige!");
        }
        // Dersom q ikke er null og q sin forelder ikke er lik null,
        // slettes pekeren fra forelderen til q, og pekeren til q
        if (q != null) {
          if (q.forelder != null) {
            if (q.forelder.venstre != null && q == q.forelder.venstre) {
              q.forelder.venstre = null;
            } else if (q.forelder.hoyre != null && q == q.forelder.hoyre) {
              q.forelder.hoyre = null;
            }
            q = null;
          }
        }

        removeOK = false;
        antall--; // minker med en verdi i treet
        endringer++; // endring oker// ker ved fjerning av node i treet
        iteratorendringer++; // oppdaterer endring i iteratorklassen
    }

  } // BladnodeIterator

} // ObligSBinTre
