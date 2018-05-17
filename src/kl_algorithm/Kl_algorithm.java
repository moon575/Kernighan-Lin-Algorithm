package kl_algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

class Edge{
	float weight;
	Vertex start;
	Vertex end;
	int id;
	Edge(){}
	Edge(Vertex start,Vertex end,float weight){
		this.start = start;
		this.end = end;
		this.weight = weight;
	}

}

class Vertex{
	String id;
	List<Edge> edges = new ArrayList<Edge>();
	Vertex(){
		
	}
	Vertex(String id){
		this.id = id;
	}
	void add_edge(Edge e) {
		edges.add(e);
	}
}

class Graph{
	int groupLength ;
	List<Edge> edges = new ArrayList<Edge>();
	List<Vertex> vertexs = new ArrayList<Vertex>();
	List<Vertex> verA = new ArrayList<Vertex>();
	List<Vertex> verB = new ArrayList<Vertex>();
	void setGroupLength() {
		this.groupLength = vertexs.size()/2;
	}
	Vertex add_v(Vertex v) {
		Vertex find = null;
		for(int i = 0;i<vertexs.size();i++) {
			Vertex ver = vertexs.get(i);
			if (ver.id .equals(v.id) ) {
				find = ver;
			}
		}
		if(find == null) {
			vertexs.add(v);
		}
		return find;
	}
	void add_edge(Edge e) {
		edges.add(e);
	}
	
	float compute_cost(Vertex v) {
		float outerCost = 0;
		float innerCost = 0;
		if(verA.contains(v)) {
			for (Edge e : v.edges) {
				if(verB.contains(e.end)) {
					outerCost += e.weight;
				}
			}
			for (Edge e : v.edges) {
				if(verA.contains(e.end)) {
					innerCost += e.weight;
				}
			}
		}else {
			for (Edge e : v.edges) {
				if(verB.contains(e.end)) {
					innerCost += e.weight;
				}
			}
			for (Edge e : v.edges) {
				if(verA.contains(e.end)) {
					outerCost += e.weight;
				}
			}
		}	
		return (outerCost - innerCost);
	}
	float compute_outer_cost(List<Vertex> vera,List<Vertex> verb) {
		float outer = 0;
		for(Vertex v:vera) {
			for (Edge e : v.edges) {
				if(verb.contains(e.end)) {
					outer += e.weight;
				}
			}
		}
		return outer;
	}
	float compute_inter_cost(Vertex a,Vertex b) {
		float cost = 0;
		for (Edge e : a.edges) {
			if(e.end==b||e.start==b) {
				cost += e.weight;
			}
		}
		return cost;
	}
	void kl(List<Vertex> verA,List<Vertex> verB) {
		
		//循环，寻找可交换的点
		System.out.println("当前外部成本："+compute_outer_cost(verA,verB));
		float G = 0;
		List<Vertex> tempA = new ArrayList<Vertex>();
		List<Vertex> tempB = new ArrayList<Vertex>();
		for(int i = 0;i<verA.size();i++) {
			Map<String,Float> cost = new HashMap<String,Float>();
			System.out.println("打印本次循环的D值：");
			for(Vertex v:vertexs) {
				cost.put(v.id, compute_cost(v));
				System.out.println("D"+v.id+"="+compute_cost(v));
			}
			Map<Vertex[], Float> g = new HashMap<Vertex[],Float>();
			float max = -1;
			Vertex[] change_key = null;
			
			for(Vertex va:verA) {
				for(Vertex vb:verB) {
					float g_ab = compute_cost(va)+compute_cost(vb)-2*compute_inter_cost(va, vb);
					g.put(new Vertex[] {va,vb}, g_ab);
				}
			}
			for (Entry<Vertex[], Float> entry:g.entrySet()) {
				if(entry.getValue()>max) {
					max = entry.getValue();
					change_key = entry.getKey();
				}
			}
			if(max<=0) {
				System.out.println(max);
				break;
			}
			System.out.println("交换"+change_key[0].id+change_key[1].id+",g="+max);
			tempA.add(change_key[0]);
			verA.remove(change_key[0]);
			tempB.add(change_key[1]);
			verB.remove(change_key[1]);
			G += max;
			System.out.println("G:"+G);
		}
		System.out.println("判断G");
		if(G>0) {
			System.out.println("G:"+G+"G>0,对交换过的A，B再次运行KL算法");
			verA.addAll(tempB);
			verB.addAll(tempA);
			kl(verA,verB);
		}else {
			System.out.println("G<0,输出集合");
			verA.addAll(tempA);
			verB.addAll(tempB);
			System.out.println("最终外部成本："+compute_outer_cost(verA, verB));
			System.out.println("集合A：");
			for(Vertex v : verA) {
				System.out.print(v.id+" ");
			}
			System.out.println();
			System.out.println("集合B：");
			for(Vertex v : verB) {
				System.out.print(v.id+" ");
			}
			System.out.println();
		}
	}
}

public class Kl_algorithm {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new FileReader("kl.csv"));
		
		String line = null;
		int edgeId = 0;
		Graph graph = new Graph();
		Edge e = null;
		//读文件，建立Graph对象
		while((line = reader.readLine())!=null) {
			String item[] = line.split(",");
			Vertex start = new Vertex(item[0]);
			Vertex end = new Vertex(item[1]);
			if(graph.add_v(start)!=null) {
				start = graph.add_v(start);
			}
			if(graph.add_v(end)!=null) {
				end = graph.add_v(end);
			}
			e = new Edge(start,end,Float.parseFloat(item[2]));
			e.id = edgeId ++;
			start.add_edge(e);			
			graph.add_edge(e);
			graph.setGroupLength();
		}
		//获取输入分组
		Scanner sc = new Scanner(System.in);
	
		for(int i = 0 ; i<graph.groupLength ;i++ ) {
			System.out.println("请输入A组顶点"+(i+1));
			Vertex ver = new Vertex(sc.nextLine());
			for(Vertex v :graph.vertexs) {
				if(ver.id.equals(v.id)) {
					ver = v;
				}
			}
			graph.verA.add(ver);
		}
		
		for(Vertex v:graph.vertexs) {
			boolean flag = false;
			for(Vertex va:graph.verA) {
				if((va.id.equals(v.id))) {
					flag = true;
				}
			}
			if(!flag) {
				graph.verB.add(v);
			}
		}
		//运行kl算法
		graph.kl(graph.verA,graph.verB);
	}

}
