package ni;

public class Cover {
	Cluster flag;
	Cluster cluster[] = new Cluster[3];
	double weight = 0;
	Cluster lstart,rstart;
	
	public Cover(){
		
	}
	public Cover(Cluster c1,Cluster c2,Cluster cluster,Cluster flag,Cluster lstart,Cluster rstart,double weight){
		this.cluster[0] = new Cluster(c1);
		this.cluster[1] = new Cluster(c2);
		this.cluster[2] = new Cluster(cluster);
		this.lstart = new Cluster(lstart);
		this.rstart = new Cluster(rstart);
		this.flag = flag;
		this.weight = weight;
	}
	
}
