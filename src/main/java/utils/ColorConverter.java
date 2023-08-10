package utils;

import java.awt.Color;

public class ColorConverter
{
  private static final double REF_X = 95.047; 
  private static final double REF_Y = 100.000; 
  private static final double REF_Z = 108.883; 
  
  public static double[] rgbParaXyz(Color cor)
  {
    double r = converter1(cor.getRed());
    double g = converter1(cor.getGreen());
    double b = converter1(cor.getBlue());
   
    double[] xyz = new double[3];
    
    xyz[0] = r * 0.4124 + g * 0.3576 + b * 0.1805;
    xyz[1] = r * 0.2126 + g * 0.7152 + b * 0.0722;
    xyz[2] = r * 0.0193 + g * 0.1192 + b * 0.9505;

    return xyz; 
  }
  
  public static double[] xyzParaLab(double[] xyz)
  {    
    double x = xyz[0] / REF_X;   
    double y = xyz[1] / REF_Y;  
    double z = xyz[2] / REF_Z;  
    
    x = converter2(x);
    y = converter2(y);
    z = converter2(z);
    
    double[] lab = new double[3];
    
    lab[0] = (116 * y) - 16;
    lab[1] = 500 * (x - y);
    lab[2] = 200 * (y - z);
    
    return lab;
  }
  
  public static double[] labParaXyz(double[] lab)
  {
    double y = (lab[0] + 16) / 116;
    double x = lab[1] / 500 + y;
    double z = y - lab[2] / 200;
    
    x = converter3(x);
    y = converter3(y);
    z = converter3(z);

    double[] xyz = new double[3];
    xyz[0] = REF_X * x;
    xyz[1] = REF_Y * y;
    xyz[2] = REF_Z * z;
 
    return xyz;
  }
  
  public static Color xyzParaRgb(double[] xyz)
  { 
    double x = xyz[0] / 100;        
    double y = xyz[1] / 100;        
    double z = xyz[2] / 100;        

    double r = x * 3.2406 + y * -1.5372 + z * -0.4986;
    double g = x * -0.9689 + y * 1.8758 + z * 0.0415;
    double b = x * 0.0557 + y * -0.2040 + z * 1.0570;

    r = Math.max(Math.min(converter4(r) * 255, 255), 0);
    g = Math.max(Math.min(converter4(g) * 255, 255), 0);
    b = Math.max(Math.min(converter4(b) * 255, 255), 0);
    
    Color cor = new Color((int) r, (int) g, (int) b);
    
    return cor;
  }
  
  public static double converter1(double d)
  {
    d /= 255.;
    
    if(d > 0.04045)
    {
      d = Math.pow((d + 0.055) / 1.055, 2.4); 
    }
    else
    {
      d /= 12.92;
    }
    
    d *= 100.;
    
    return d;
  }
  
  public static double converter2(double d)
  {
    if(d > 0.008856)
    {
      return Math.pow(d, 1./3.);
    }
    
    return (7.787 * d) + (16./116.);
  }
  
  public static double converter3(double d)
  {
    if(Math.pow(d, 3 ) > 0.008856)
    {
      return Math.pow(d, 3 );
    }
    
    return (d - 16. / 116.) / 7.787;
  }
  
  public static double converter4(double d)
  {
    if(d > 0.0031308)
    {
      return 1.055 * Math.pow(d, (1. / 2.4) ) - 0.055;
    }
    
    return 12.92 * d;
  }
}
