package generovani;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jdom.Element;

public class Sezona
{
  private List<String> tymy;
  private int pocetPeriod;
  private int pocetTymu;
  private int pocetZapasuKola;
  private List<Kolo> rozpis;
  
  public Sezona(String[] tymy, int pocetPeriod, int pocetZapasuKola)
  {
    if (tymy.length < 2) {
      throw new IllegalArgumentException("Pocet tymu musi byt vetsi nebo roven 2!!!");
    }
    this.pocetZapasuKola = pocetZapasuKola;
    this.pocetTymu = tymy.length;
    this.pocetPeriod = pocetPeriod;
    
    nactiTymy(tymy);
    
    int pocetKol = this.pocetTymu % 2 == 0 ? this.pocetTymu - 1 : this.pocetTymu;
    this.rozpis = new ArrayList(pocetKol);
    vygenerujRozpis(pocetKol);
  }
  
  private void vygenerujRozpis(int pocetKol)
  {
    int[] tymyPomocnePole = vytvorPomocnePole();
    int pocetZapasu = this.tymy.size() / 2;
    for (int i = 0; i < pocetKol; i++)
    {
      this.rozpis.add(i, new Kolo(pocetZapasu));
      ((Kolo)this.rozpis.get(i)).vygenerujKolo(tymyPomocnePole);
      tymyPomocnePole = pootocPoleTymu(tymyPomocnePole);
    }
    vyrovnejBilanciPrvnihoTymu(pocetKol);
  }
  
  private int[] pootocPoleTymu(int[] tymyPomocnePole)
  {
    int pomocny = tymyPomocnePole[(this.tymy.size() - 1)];
    for (int i = this.tymy.size() - 1; i > 1; i--) {
      tymyPomocnePole[i] = tymyPomocnePole[(i - 1)];
    }
    tymyPomocnePole[1] = pomocny;
    return tymyPomocnePole;
  }
  
  private void vyrovnejBilanciPrvnihoTymu(int pocetKol)
  {
    for (int i = 0; i < pocetKol; i++) {
      if (i % 2 == 0) {
        ((Kolo)this.rozpis.get(i)).prohodDomaciHostyPrvnihoTymu();
      }
    }
  }
  
  private void nactiTymy(String[] tymy)
  {
    this.tymy = new ArrayList();
    for (int i = 0; i < tymy.length; i++) {
      this.tymy.add(i, tymy[i]);
    }
    if (tymy.length % 2 == 1) {
      this.tymy.add(tymy.length, "VOLNO");
    }
    Collections.shuffle(this.tymy);
  }
  
  private int[] vytvorPomocnePole()
  {
    int[] tymyPomocnePole = new int[this.tymy.size()];
    for (int i = 0; i < this.tymy.size(); i++) {
      tymyPomocnePole[i] = i;
    }
    return tymyPomocnePole;
  }
  
  private int getPocetKolPeriody()
  {
    int pocet = getPocetZapasuPeriody() / this.pocetZapasuKola;
    if (pocet * this.pocetZapasuKola != getPocetZapasuPeriody()) {
      pocet++;
    }
    return pocet;
  }
  
  private int getPocetZapasuPeriody()
  {
    return this.pocetTymu * (this.pocetTymu - 1) / 2;
  }
  
  public Element getKoren()
  {
    Element koren = new Element("rozpis");
    Element informace = getInformace();
    Element rozlosovani = getRozlosovani();
    koren.addContent(informace);
    koren.addContent(rozlosovani);
    return koren;
  }
  
  private Element getInformace()
  {
    Element informace = new Element("informace");
    Element periody = new Element("pocetPeriod").addContent("" + this.pocetPeriod);
    
    Element kola = new Element("pocetKolPeriody").addContent("" + getPocetKolPeriody());
    
    Element zapasyKola = new Element("pocetZapasuKola").addContent("" + this.pocetZapasuKola);
    
    Element tymy = getTymy();
    informace.addContent(periody);
    informace.addContent(kola);
    informace.addContent(zapasyKola);
    informace.addContent(tymy);
    return informace;
  }
  
  private Element getTymy()
  {
    Collections.sort(this.tymy, Collator.getInstance(new Locale("cs", "CZ")));
    
    Element eTymy = new Element("tymy");
    eTymy.setAttribute("pocetTymu", "" + this.pocetTymu);
    Iterator<String> it = this.tymy.iterator();
    while (it.hasNext())
    {
      String t = (String)it.next();
      if (!t.equals("VOLNO"))
      {
        Element tym = new Element("tym");
        tym.setAttribute("jmeno", t);
        eTymy.addContent(tym);
      }
    }
    return eTymy;
  }
  
  private Element getRozlosovani()
  {
    int pocetKol = 1;
    int iVolno = getIndexVolnoTymu();
    boolean lichyPocetTymu = this.pocetTymu % 2 == 1;
    Element rozlosovani = new Element("rozlosovani");
    for (int i = 1; i <= this.pocetPeriod; i++)
    {
      Element perioda = getPerioda(Integer.valueOf(i));
      Iterator<Kolo> itKol = this.rozpis.iterator();
      Iterator<Zapas> itZapasy = ((Kolo)itKol.next()).getIteratorKola();
      for (int j = 1; j <= getPocetKolPeriody(); j++)
      {
        Element kolo = getKolo(Integer.valueOf(pocetKol++));
        for (int k = 1; k <= this.pocetZapasuKola; k++)
        {
          boolean zapsano = false;
          do
          {
            if (!itZapasy.hasNext())
            {
              if (!itKol.hasNext()) {
                break;
              }
              itZapasy = ((Kolo)itKol.next()).getIteratorKola();
            }
            Zapas z = (Zapas)itZapasy.next();
            if ((!lichyPocetTymu) || (jdeZapsatZapas(z.getDomaci(), z.getHoste(), iVolno)))
            {
              String hoste;
              String domaci;
              String hoste;
              if (i % 2 == 0)
              {
                String domaci = (String)this.tymy.get(z.getHoste());
                hoste = (String)this.tymy.get(z.getDomaci());
              }
              else
              {
                domaci = (String)this.tymy.get(z.getDomaci());
                hoste = (String)this.tymy.get(z.getHoste());
              }
              kolo.addContent(getZapas(Integer.valueOf(k), domaci, hoste));
              zapsano = true;
            }
          } while (!zapsano);
        }
        perioda.addContent(kolo);
      }
      rozlosovani.addContent(perioda);
    }
    return rozlosovani;
  }
  
  private int getIndexVolnoTymu()
  {
    if (this.pocetTymu % 2 == 1) {
      return this.tymy.indexOf("VOLNO");
    }
    return -1;
  }
  
  private boolean jdeZapsatZapas(int domaci, int hoste, int indexTymVolno)
  {
    if ((domaci != indexTymVolno) && (hoste != indexTymVolno)) {
      return true;
    }
    return false;
  }
  
  private Element getPerioda(Integer cislo)
  {
    return new Element("perioda").setAttribute("cisloPeriody", cislo.toString());
  }
  
  private Element getKolo(Integer cislo)
  {
    return new Element("kolo").setAttribute("cisloKola", cislo.toString());
  }
  
  private Element getZapas(Integer cislo, String dom, String hos)
  {
    Element zapas = new Element("zapas");
    zapas.setAttribute("cisloZapasu", cislo.toString());
    
    Element domaci = new Element("domaci");
    Element jmenoD = new Element("jmenoD");
    jmenoD.addContent(dom);
    Element golyD = new Element("golyD");
    golyD.addContent("");
    domaci.addContent(jmenoD);
    domaci.addContent(golyD);
    
    Element hoste = new Element("hoste");
    Element jmenoH = new Element("jmenoH");
    jmenoH.addContent(hos);
    Element golyH = new Element("golyH");
    golyH.addContent("");
    hoste.addContent(jmenoH);
    hoste.addContent(golyH);
    
    zapas.addContent(domaci);
    zapas.addContent(hoste);
    return zapas;
  }
}
