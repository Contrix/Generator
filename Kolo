package generovani;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Kolo
{
  private List<Zapas> zapasy;
  private int pocetZapasuKola;
  
  public Kolo(int pocetZapasuKola)
  {
    this.pocetZapasuKola = pocetZapasuKola;
    this.zapasy = new ArrayList(pocetZapasuKola);
  }
  
  public void vygenerujKolo(int[] tymy)
  {
    int pocetTymu = tymy.length;
    for (int i = 0; i < this.pocetZapasuKola; i++)
    {
      boolean sude = i % 2 == 0;
      if (sude) {
        this.zapasy.add(i, new Zapas(tymy[i], tymy[(pocetTymu - i - 1)]));
      } else {
        this.zapasy.add(i, new Zapas(tymy[(pocetTymu - i - 1)], tymy[i]));
      }
    }
  }
  
  public void prohodDomaciHostyPrvnihoTymu()
  {
    ((Zapas)this.zapasy.get(0)).prohodDomaciHosty();
  }
  
  public Iterator<Zapas> getIteratorKola()
  {
    return this.zapasy.iterator();
  }
  
  public String toString()
  {
    return this.zapasy.toString();
  }
}
