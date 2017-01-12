package ni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HI{
	static double clat = 38.9071923000,clng = -77.0368707000;
	static double mlat = 39.5906657000,mlng = -77.0260880000;
	static double md = (clat-mlat)*(clat-mlat)+(clng-mlng)*(clng-mlng);
	static double cd = (md) * 0.01;
	
	public static void main1(int cou) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File("F:/MyWorkspace/ni/WebRoot/js/test.csv")));
		PrintWriter pw = new PrintWriter(new File("F:/MyWorkspace/ni/WebRoot/js/test1.csv"));
		PrintWriter pw2 = new PrintWriter(new File("F:/MyWorkspace/ni/WebRoot/js/cover.csv"));
		
		List<Cluster> list = new ArrayList<Cluster>();
		Cluster cluster = new Cluster();
		String s,sp[],head = null;
		int index=0,count = cou;
		
		for(;(s = br.readLine()) != null;index++){
			if(index == 0){head = s;continue;}
			s = s.trim();
			sp = s.split(",");
			if(index == 1){cluster = new Cluster(Double.parseDouble(sp[0]),Double.parseDouble(sp[1]));}
			if(isCovered(sp)){
				Cluster c = new Cluster(Double.parseDouble(sp[2]),Double.parseDouble(sp[3]));
				list.add(c);
			}
		}

		br.close();
		
		ArrayList<Cover> covers = new ArrayList<Cover>();
		try {
			Cluster.cluster(list,cluster,count,covers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		pw.println(head);
		for(Cluster c : list){
			pw.println(cluster.getLat()+","+cluster.getLng()+","+c.getLat()+","+c.getLng());
		}
		
		pw.flush();
		pw.close();
		
		String head2 = "llat,llng,rlat,rlng,clat,clng,slat,slng";
		pw2.println(head2);
		for(Cover cover : covers){
			for(Cluster clu : cover.cluster){
				pw2.print(clu.getLat()+","+clu.getLng()+",");
			}
			pw2.println(cluster.getLat()+","+cluster.getLng());
		}
		
		pw2.flush();
		pw2.close();
	}
	
	//判断是否过长
	public static boolean isCovered(String[] sp){
		double entLat = Double.parseDouble(sp[0]);
		double extLat = Double.parseDouble(sp[2]);
		double entLng = Double.parseDouble(sp[1]);
		double extLng = Double.parseDouble(sp[3]);

		if(entLat*extLat <0 || entLng*extLng <0){
				return false;
		}
		if(entLat == extLat && entLng == extLng){
			return false;
		}
		double std = (entLat-extLat)*(entLat-extLat)+(entLng-extLng)*(entLng-extLng);
		if(std>md){return false;}
		return true;
	}
	
	public static void main2() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File("C:/Users/y/Desktop/locc.txt")));
		BufferedReader br2 = new BufferedReader(new FileReader(new File("C:/Users/y/Desktop/st.csv")));
		PrintWriter pw = new PrintWriter(new File("C:/Users/y/Desktop/st1.csv"));
		
		HashMap<String,String> map = new HashMap<String,String>();
		String s,sp[];
		while((s = br.readLine()) != null){
			s = s.trim();
			sp = s.split(",");
			map.put(sp[0],sp[1]+","+sp[2]);
		}
		br.close();
		
		s= br2.readLine();
		pw.println("EntLat,EntLng,ExtLat,ExtLng");
		while((s = br2.readLine()) != null){
			s = s.trim();
			sp = s.split(",");
			if(map.containsKey(sp[0])){
				sp[0] = map.get(sp[0]);
			}
			if(map.containsKey(sp[1])){
				sp[1] = map.get(sp[1]);
			}
			pw.println(sp[0]+","+sp[1]);
		}
		br2.close();
		pw.flush();
		pw.close();
	}
	
}