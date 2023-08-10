package model;

public class Pair
{
  private double a;
  private double b;

  public Pair(double a, double b)
  {
    this.a = a;
    this.b = b;
  }

  public double getA()
  {
    return a;
  }

  public void setA(double a)
  {
    this.a = a;
  }

  public double getB()
  {
    return b;
  }

  public void setB(double b)
  {
    this.b = b;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(a);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(b);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pair other = (Pair) obj;
    if (Double.doubleToLongBits(a) != Double.doubleToLongBits(other.a))
      return false;
    if (Double.doubleToLongBits(b) != Double.doubleToLongBits(other.b))
      return false;
    return true;
  }
  
  @Override
  public String toString()
  {
    return "(" + a + "," + b + ")";
  }
}
