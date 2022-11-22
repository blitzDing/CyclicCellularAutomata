package someMath;

public class RealNrManipulation 
{
	
	
	public static double m = 0.0;
	public final static int depthRefining = 40;
	
	public static double superRootOf(double z)
	{
		
		double tn= z;
		double p = Math.log(z);
		for(int n =0;n<depthRefining;n++)
		{
			
			//System.out.println("tn: "+tn);
			//System.out.println("log(tn): "+Math.log(tn));
			tn = (0.5)*(tn+(p/Math.log(tn)));
		}
		
		return tn;
	}
}
