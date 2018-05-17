package mul_kl_algorithm;

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
	int groups;
	List<Edge> edges = new ArrayList<Edge>();
	List<Vertex> vertexs = new ArrayList<Vertex>();
	List<List<Vertex>> vers = new ArrayList<List<Vertex>>();
	public Graph() {
		// TODO Auto-generated constructor stub
	}
	Graph(int groups,int items){
		if(  Math.log((double)groups)/Math.log((double)2)%1!=0) {
			this.groups = (int)Math.pow(2,Math.ceil(Math.log((double)groups)/Math.log((double)2)));
		}
		else {
			this.groups = groups;
		}
			
		this.groupLength = items;
	}
	void addZeros() {
		int num = groups*groupLength;
		for(int i = vertexs.size()+1;i<=num;i++) {
			vertexs.add(new Vertex("t"+i));
		}
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
		int num = vers.size();
		for(int i=0;i<num;i++) {
			if(vers.get(i).contains(v)) {
				for(Edge e:v.edges) {
					if(vers.get(i).contains(e.end)) {
						innerCost+=e.weight;
					}else {
						outerCost+=e.weight;
					}
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
	List<List<Vertex>> kl(List<Vertex> verA,List<Vertex> verB,int times) {
		List<List<Vertex>> result = new ArrayList<List<Vertex>>();
		//循环，寻找可交换的点
		System.out.println("当前外部成本："+compute_outer_cost(verA,verB));
		float G = 0;
		List<Vertex> tempA = new ArrayList<Vertex>();
		List<Vertex> tempB = new ArrayList<Vertex>();
		for(int i = 0;i<verA.size();i++) {
			Map<String,Float> cost = new HashMap<String,Float>();
			System.out.println("打印本次循环的D值：");
			for(Vertex v:verA) {
				cost.put(v.id, compute_cost(v));
				System.out.print("D"+v.id+"="+compute_cost(v)+"  ");
			}
			for(Vertex v:verB) {
				cost.put(v.id, compute_cost(v));
				System.out.print("D"+v.id+"="+compute_cost(v)+"  ");
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
				System.out.println();
				System.out.println("max_g:"+max);
				break;
			}
			System.out.println();
			System.out.println("max_g:"+max);
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
			kl(verA,verB,times);
		}else {
			result.add(verA);
			result.add(verB);
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
		times++;
		if(times<Math.ceil(Math.log((double)groups)/Math.log((double)2))) {
			System.out.println("对子集再次进行二划分：");
			result = null;
			result=kl(verA.subList(0, verA.size()/2),verA.subList(verA.size()/2, verA.size()),times);
			result.addAll(kl(verB.subList(0, verB.size()/2),verB.subList(verB.size()/2, verB.size()),times)); 
		}
		return result;
	}
	
}

public class multi_kl {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader reader = new BufferedReader(new FileReader("kl.csv"));
		String line = null;
		int edgeId = 0;
		Edge e = null;
		int groups = 0;
		int items = 0;
		int times = 0;
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入希望得到的分组数和组内最大个数：");
		groups = sc.nextInt();
		items = sc.nextInt();
		//读文件，建立Graph对象
		Graph graph = new Graph(groups,items);
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
		}
		
		graph.addZeros();
		
		//获取输入分组	
		sc.nextLine();
		for(int j = 0;j<graph.groups;j++) {
			List<Vertex> temp = new ArrayList<Vertex>();
			System.out.println("请输入第"+(j+1)+"组顶点");
			for(int i = 0 ; i<items ;i++ ) {
				Vertex ver = new Vertex(sc.nextLine());
				for(Vertex v :graph.vertexs) {
					if(ver.id.equals(v.id)) {
						ver = v;
					}
				}
				temp.add(ver);
			}
			graph.vers.add(temp);
		}

		int gr = graph.groups/2;
		for(int i = 1;i<gr;i++) {
			graph.vers.get(0).addAll(graph.vers.get(i));
		}
		for(int i = gr+1;i<graph.groups;i++) {
			graph.vers.get(gr).addAll(graph.vers.get(i));
		}
		int n = graph.groups;
		while(n>2){
			for(int i=0;i<3;i++) {
				if(graph.vers.get(i).size()!=items*gr) {
					graph.vers.remove(i);
					n--;
				}
			}
			
		}
		
//		运行kl算法
		List<List<Vertex>> result = graph.kl(graph.vers.get(0),graph.vers.get(1),times);
		int finalcost =0;
		for(int i=0;i<result.size();i++) {
			for(int j=0;j<result.size();j++) {
				if(i!=j) {
					finalcost+=graph.compute_outer_cost(result.get(i), result.get(j));
				}
			}
		}
//		输出结果
		for(int i =0;i<graph.groups;i++) {
			System.out.println("第"+(i+1)+"组：");
			for(int j=0;j<items;j++) {
				if(result.get(i).get(j).edges.size()!=0) {
					System.out.print(result.get(i).get(j).id+"  ");
				}
			}
			System.out.println();
		}
		System.out.println("cost="+finalcost);
	}

}