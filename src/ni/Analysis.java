package ni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Analysis{
	static double CLAT = 38.9071923000,CLNG = -77.0368707000;//中心点
	static double MLAT = 39.5906657000,MLNG = -77.0260880000;//最远点
	static double MD = Cluster.getLength(CLAT-MLAT,CLNG-MLNG);//最远允许距离
	static double SLAT = 38.9017650000,SLNG = -76.9133060000;
	static double CD = (MD) * 0.05;//区域范围
	/*
	 * 生成解析生成文件
	 */
	public static void analyze(int cou) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(new File("F:/MyWorkspace/ni/WebRoot/js/test.csv")));
		PrintWriter pw = new PrintWriter(new File("F:/MyWorkspace/ni/WebRoot/js/test1.csv"));
		PrintWriter pw2 = new PrintWriter(new File("F:/MyWorkspace/ni/WebRoot/js/cover.csv"));
		
		List<Cluster> list = new ArrayList<Cluster>();
		Cluster cluster = new Cluster(SLAT,SLNG);
		String s,sp[],head = null;
		int index=0,count = cou;
		
		for(;(s = br.readLine()) != null;index++){
			if(index == 0){head = s;continue;}
			s = s.trim();
			sp = s.split(",");
			if(isCovered(sp) && isInDistance(sp)){
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
		
		String head2 = "llat,llng,rlat,rlng,clat,clng,slat,slng,lslat,lslng,rslat,rslng,weight";
		pw2.println(head2);
		for(Cover cover : covers){
			for(Cluster clu : cover.cluster){
				pw2.print(clu.getLat()+","+clu.getLng()+",");
			}
			pw2.print(cluster.getLat()+","+cluster.getLng()+",");
			pw2.print(cover.lstart.getLat()+","+cover.lstart.getLng()+",");
			pw2.print(cover.rstart.getLat()+","+cover.rstart.getLng()+",");
			pw2.print("#");
			for(int i = 0;i<4;i++){
				pw2.print(Integer.toHexString((int)cover.weight));
			}
			pw2.println("00");
		}
		
		pw2.flush();
		pw2.close();
	}
	
	//判断是否过长
	public static boolean isInDistance(String[] sp){
		double entLat = SLAT;
		double extLat = Double.parseDouble(sp[2]);
		double entLng = SLNG;
		double extLng = Double.parseDouble(sp[3]);

		if(entLat*extLat <0 || entLng*extLng <0){
				return false;
		}
		if(entLat == extLat && entLng == extLng){
			return false;
		}
		double std = Cluster.getLength(entLat-extLat,entLng-extLng);
		if(std>MD){return false;}
		return true;
	}
	
	private static boolean isCovered(String[] sp){
		double clat = Double.parseDouble(sp[0]);
		double clng = Double.parseDouble(sp[1]);
		
		double l = Cluster.getLength(clat-SLAT,clng-SLNG);
		return (l<CD);
	}

}