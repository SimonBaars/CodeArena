package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
// Java program to print connected components in  
// an undirected graph  
import java.util.LinkedList;
import java.util.List; 

public class Graph { 
    // A user define class to represent a graph. 
    // A graph is an array of adjacency lists. 
    // Size of array will be V (number of vertices 
    // in graph) 
    int V; 
    LinkedList<Integer>[] adjListArray; 
      
    // constructor 
    @SuppressWarnings("unchecked")
	public Graph(int V) { 
        this.V = V; 
        // define the size of array as 
        // number of vertices 
        adjListArray = new LinkedList[V]; 
  
        // Create a new list for each vertex 
        // such that adjacent nodes can be stored 
  
        for(int i = 0; i < V ; i++){ 
            adjListArray[i] = new LinkedList<Integer>(); 
        } 
    } 
      
    // Adds an edge to an undirected graph 
    public void addEdge(int src, int dest) { 
        // Add an edge from src to dest. 
        adjListArray[src].add(dest); 
  
        // Since graph is undirected, add an edge from dest 
        // to src also 
        adjListArray[dest].add(src); 
    } 
      
    private List<Integer> DFSUtil(int v, boolean[] visited) { 
    	List<Integer> connected = new ArrayList<>();
        // Mark the current node as visited and print it 
        visited[v] = true; 
        connected.add(v);
        // Recur for all the vertices 
        // adjacent to this vertex 
        for (int x : adjListArray[v]) { 
            if(!visited[x]) connected.addAll(DFSUtil(x,visited)); 
        } 
        return connected;
    } 
    
    public List<List<Integer>> connectedComponents() { 
    	List<List<Integer>> connected = new ArrayList<>();
        // Mark all the vertices as not visited 
        boolean[] visited = new boolean[V]; 
        for(int v = 0; v < V; ++v) { 
            if(!visited[v]) { 
                // print all reachable vertices 
                // from v 
                connected.add(DFSUtil(v,visited)); 
            } 
        } 
        return connected;
    } 
}    