package generovani;

public class Zapas
{
  private int domaci;
  private int hoste;
  
  public Zapas(int domaci, int hoste)
  {
    this.domaci = domaci;
    this.hoste = hoste;
  }
  
  public void prohodDomaciHosty()
  {
    int pomocna = this.domaci;
    this.domaci = this.hoste;
    this.hoste = pomocna;
  }
  
  public int getDomaci()
  {
    return this.domaci;
  }
  
  public int getHoste()
  {
    return this.hoste;
  }
  
  public String toString()
  {
    return this.domaci + " : " + this.hoste;
  }
}
