package ni;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private double lat;
	private double lng;
	public static int cou =270;
	public static boolean allowCover = false;
	public static double MAXLEN = Analysis.MD*0.7;
	public static Cluster start = new Cluster(0,0);
	public static double rad = 0.4;
	public static void main(String[] args) throws IOException{
		long start = System.currentTimeMillis();
		Analysis.analyze(cou);
		System.out.println((System.currentTimeMillis()-start)/100+"s");
	}
	public Cluster() {
		
	}

	public Cluster(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public Cluster(Cluster c){
		this.lat = c.lat;
		this.lng = c.lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public boolean equals(Object o) {
		Cluster c = (Cluster) o;
		return (lat == c.getLat() && lng == c.getLng());
	}
	// 接收一组同起点向量,返回count个向量[0],和区域[1]
	public static void cluster(List<Cluster> end,Cluster start,int count,List<Cover> covers) throws Exception{
		if (count > end.size()) {
			throw new Exception("Size Larger");
		}
		
		List<Cluster> list = getList(end, start);//变为相对坐标
		for (int index = 0; index < count; index++) {
			mergeMax(list,covers);
		}
		changeCoverStart(covers);
		fillColor(covers);
		recover(list,covers, start);//恢复绝对坐标
	}

	// 接收一组同起点向量,返回count个向量
/*	public static List<Cluster> cluster(List<Cluster> end, Cluster start,int count) throws Exception {
		if (count > end.size()) {
			throw new Exception("Size Larger");
		}

		List<Cluster> list = getList(end, start);//变为相对坐标
		for (int index = 0; index < count; index++) {
			mergeMax(list);
		}
		return recover(list, start);//恢复绝对坐标
	}*/

	private static void recover(List<Cluster> list, List<Cover> covers,Cluster start) {
		for (Cluster cluster : list) {
			cluster.setLat(cluster.getLat() + start.getLat());
			cluster.setLng(cluster.getLng() + start.getLng());
		}
		for (Cover cover : covers) {
			for(Cluster cluster  : cover.cluster){
				cluster.setLat(cluster.getLat() + start.getLat());
				cluster.setLng(cluster.getLng() + start.getLng());
			}
			cover.lstart.setLat(cover.lstart.getLat() + start.getLat());
			cover.lstart.setLng(cover.lstart.getLng() + start.getLng());
			cover.rstart.setLat(cover.rstart.getLat() + start.getLat());
			cover.rstart.setLng(cover.rstart.getLng() + start.getLng());
		}
	}

	private static List<Cluster> getList(List<Cluster> end,Cluster start) {
		for (Cluster cluster : end) {
			cluster.setLat(cluster.getLat() - start.getLat());
			cluster.setLng(cluster.getLng() - start.getLng());
		}
		return end;
	}
	
	//list : 向量集 ; covers : 区域集
	public static void mergeMax(List<Cluster> list,List<Cover> covers) {
		double error = Double.MAX_VALUE;
		double err = 0;
		Cluster c1 = null, c2 = null;
		for (Cluster cluster : list) {
			for (Cluster neig : list) {
				if (cluster != neig) {
					err = getError(cluster, neig);
					if(err>=0 && error > err){
						if(getLength(cluster) < MAXLEN && getLength(neig) < MAXLEN){
							error = err;
							c1 = cluster;
							c2 = neig;
						}
					
					}
				}
			}
		}

		if (c1 != null && c2 != null) {
			Cluster c = new Cluster();
			c.setLat((c1.getLat() + c2.getLat()) / 2);
			c.setLng((c1.getLng() + c2.getLng()) / 2);
			list.remove(c1);
			list.remove(c2);
			list.add(c);

			Cover cover = new Cover(c1,c2,c,c,start,start,2.0/cou);
			if(!allowCover){
				Cover cov1 = null,cov2 = null;
				if((cov1=contains(covers,c1)) != null && (cov2=contains(covers,c2)) != null){
					mergeCover(cover,cov1.cluster[0],cov1.cluster[1],c2,c1);
					cover.weight = cov1.weight + cov2.weight;
					covers.remove(cov1);
					covers.remove(cov2);
				}else if( (cov1=contains(covers,c1)) != null){
					mergeCover(cover,cov1.cluster[0],cov1.cluster[1],c2,c1);
					cover.weight = cov1.weight + 1.0/cou;
					covers.remove(cov1);
				}else if((cov2=contains(covers,c2)) != null){
					mergeCover(cover,cov2.cluster[0],cov2.cluster[1],c1,c2);
					cover.weight = cov2.weight + 1.0/cou;
					covers.remove(cov2);
				}
			}
			covers.add(cover);
		}
	}
	
	public static Cover contains(List<Cover> covers,Cluster flag){
		for(Cover cover : covers){
			if(cover.flag == flag){
				return cover;
			}
		}
		return null;
	}
	
	//c0,c1,c2,c3
	private static void mergeCover(Cover cover,Cluster ...clusters){
		int length = clusters.length - 1;
		for(int index = 0;index<clusters.length-1;index++){
			if(chooseCover(cover,clusters[index%clusters.length],
					clusters[(index+1)%clusters.length],
					clusters[(index+2)%clusters.length],
					clusters[(index+3)%clusters.length])){
				break;
			}
		}
		
		//changeLength(cover);
	}
	
	//判断clusters[3]是否在区域内
	private static boolean chooseCover(Cover cover ,Cluster ...clusters){
		int index = 0;//判断起点-----c0 | c1,c2
		for(;index<3;index++){
			double k0 = clusters[index].lng/clusters[index].lat;
			double sen0 = (k0*clusters[(index+2)%3].lat-clusters[(index+2)%3].lng)*(k0*clusters[(index+1)%3].lat-clusters[(index+1)%3].lng);
			if(sen0 > 0){
				break;
			}		
		}
		
		//判断c0 ----- c1 | 起点 c2
		cover.cluster[0] = new Cluster(clusters[index]);
		double k = (clusters[index].lng-clusters[(index+2)%3].lng)/(clusters[index].lat - clusters[(index+2)%3].lat);
		double b = clusters[index].lng - k*clusters[index].lat;
		double sen = b*(k*clusters[(index+1)%3].lat+b-clusters[(index+1)%3].lng);
		if(sen > 0){
			cover.cluster[2] = new Cluster(clusters[(index+2)%3]);
			cover.cluster[1] = new Cluster(clusters[(index+1)%3]);
		}else{
			cover.cluster[2] = new Cluster(clusters[(index+1)%3]);
			cover.cluster[1] = new Cluster(clusters[(index+2)%3]);
		}
		
		Cluster cluster = clusters[3];
		clusters[3] = new Cluster(0,0);
		
		if(isCover(cover.cluster[0],cover.cluster[2],clusters[3],cover.cluster[1])){
			cover.cluster[1] = new Cluster(cover.cluster[2]);
		}
		
		
		for(Cluster c1 : clusters){
			if(!isCover(clusters[3],cover.cluster[0],c1,cluster)){
				return false;
			}
			if(!isCover(clusters[3],cover.cluster[2],c1,cluster)){
				return false;
			}
			if(!isCover(cover.cluster[0],cover.cluster[1],c1,cluster)){
				return false;
			}
			if(!isCover(cover.cluster[1],cover.cluster[2],c1,cluster)){
				return false;
			}
		}
		
		return true;
	}
	
	private static void changeLength(Cover cover){
		double len0 = getLength(cover.cluster[0]),len1 = getLength(cover.cluster[1]);
		Cluster min = (len0<len1?cover.cluster[0]:cover.cluster[1]);
		
		double k = min.getLng()/min.getLat();
		int i = (min.getLat() < 0?-1:1); 
		double len = (len0<len1?len1:len0);
		
		min.setLat(len/Math.pow(1+k*k,0.5)*i);
		min.setLng(min.getLat()*k);
		
		min = cover.flag;
		i = (min.getLat() < 0?-1:1); 
		k = min.getLng()/min.getLat();
		
		min.setLat(len/Math.pow(1+k*k,0.5)*i);
		min.setLng(min.getLat()*k);
	}
	//c0-----c1 | c2,c3
	private static boolean isCover(Cluster... clusters){
		double k = (clusters[0].lng-clusters[1].lng)/(clusters[0].lat - clusters[1].lat);
		double b = clusters[0].lng - k*clusters[0].lat;
		double sen = (k*clusters[2].lat+b-clusters[2].lng)*(k*clusters[3].lat+b-clusters[3].lng);
		return (sen > 0);
	}
	
	private static double getError(Cluster cluster, Cluster neig) {
		double x1= neig.lat,y1 = neig.lng,xx = cluster.lat,yy = cluster.lng;
		//旋转变换
		double k = -Math.atan2(yy,xx);
		double x=(x1)*Math.cos(k) -(y1)*Math.sin(k);
		double y=(x1)*Math.sin(k) + (y1)*Math.cos(k);
		
		double l = getLength(cluster);
		double a =2*l,b = l,c=l;
		double e1 = (Math.pow(x-l,2) + Math.pow(a*y/b,2))*(Math.pow(a,2) - Math.pow(c,2));
		e1 = e1+Math.pow(c*(x-l),2);
		e1 = Math.pow(e1,0.5) / (Math.pow(a,2) - Math.pow(c,2));
		e1 = e1-( (c*(x-l)) / (Math.pow(a,2)-Math.pow(c,2)) );
		return e1;
	}
	
	private static void changeCoverStart(List<Cover> covers){
		for(Cover cover : covers){
			for(Cover cov : covers){
				if(cov != cover){
					changeStart(cover,cov);
				}
			}
		}
	}

	private static void changeStart(Cover cover,Cover cov){
		if(cover.lstart.getLat() != 0 || cover.lstart.getLng() != 0){
			return;
		}
		if(cov.lstart.getLat() != 0 || cov.lstart.getLng() != 0){
			return;
		}
		
		double y1 = cover.cluster[0].lng,y2 = cover.cluster[1].lng;
		double x1 = cover.cluster[0].lat,x2 = cover.cluster[1].lat;
		double k = (y1-y2)/(x1-x2),b = y1-k*x1;
		double k1 = cov.cluster[0].lng/cov.cluster[0].lat,k2 = cov.cluster[1].lng/cov.cluster[1].lat;
		
		Cluster c1 = new Cluster(b/(k1-k),k1*b/(k1-k)),c2 = new Cluster(b/(k2-k),k2*b/(k2-k));
		if(isEqual(getLength(y1-y2,x1-x2) , getLength(y1-c1.lng,x1-c1.lat) + getLength(y2-c1.lng,x2-c1.lat))){
			if(isEqual(getLength(y1-y2,x1-x2) , getLength(y1-c2.lng,x1-c2.lat) + getLength(y2-c2.lng,x2-c2.lat))){
				if(getLength(c1) < getLength(cov.cluster[0]) && getLength(c2) < getLength(cov.cluster[1])){
					System.out.println("iiiii");
					//使用余弦公式
					double xp = (x1+x2)/2,yp = (y1+y2)/2,cos1 = getLength(xp,yp)/getLength(c1),cos2 = getLength(xp,yp)/getLength(c2);
					double a = getLength(getLength(x1-x2,y1-y2)/2*rad,getLength(x1-x2,y1-y2)/2);
					double c = getLength(xp,yp) - getLength(x1-x2,y1-y2)/2*rad;
					double len1 = Util.getSolution(1,-(2*c*cos1),c*c-a*a),len2=Util.getSolution(1,-(2*c*cos2),c*c-a*a);
					//double len1 = a+c,len2 = len1;
					int i = (cov.flag.lat>0?1:-1);
					cov.lstart.lat = len1/Math.pow(1+k1*k1,0.5)*i;
					cov.lstart.lng = cov.lstart.lat*k1;
					
					cov.rstart.lat = len2/Math.pow(1+k2*k2,0.5)*i;
					cov.rstart.lng = cov.rstart.lat*k2;
				}
			}
		}
	}
	
	private static void fillColor(List<Cover> covers){
		double max = 0;
		for(Cover cover : covers){
			if(max<cover.weight){
				max = cover.weight;
			}
		}
		
		for(Cover cover : covers){
			cover.weight = (int)getColor(cover,max);
		}
	}
	
	private static double getColor(Cover cover,double max){
		return (-Math.pow((cover.weight-max)*3/max,2)+9)* (15/9.0);
	}
	
	
	private static Cluster getClusterByLength(Cluster clen,Cluster ck,Cluster cflag){
		double len = getLength(clen),k = ck.lng/ck.lat;
		int i = cflag.lat>0?1:-1;
		Cluster cluster = new Cluster();
		cluster.lat = len/Math.pow(1+k*k,0.5)*i;
		cluster.lng = cluster.lat * k;
		return cluster;
	}
	
	private static boolean isEqual(double a,double b){
		if(Math.abs(a-b) < 0.0000001){
			return true;
		}
		return false;
	}
	
	private static double getLength(Cluster cluster){
		return Math.pow(Math.pow(cluster.lat,2)+Math.pow(cluster.lng,2),0.5);
	}
	
	public static double getLength(double lat,double lng){
		return Math.pow(Math.pow(lat,2)+Math.pow(lng,2),0.5);
	}
}
