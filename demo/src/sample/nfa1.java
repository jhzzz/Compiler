package sample;

import java.util.ArrayList;
import java.util.Stack;

class Digraph{//有向图
    private final int V;//顶点数目
    private int E;//边的数目
    private ArrayList<Integer>[] adj;//邻接表，adjoin
    public Digraph(int V){
        this.V = V;
        this.E = 0;
        adj =  (ArrayList<Integer>[]) new ArrayList[V];//规定不能new一个含泛型类型的数组（JVM把泛型擦除为Object，相当于没有）,但是可以将Object类型强制转换
        for(int v=0;v<V;v++)
            adj[v] = new ArrayList<Integer>();//Integer是固定的类型，不是和泛型那样是不确定的
    }
    public void addEdge(int v,int w){
        adj[v].add(w);
        E++;
    }
    public int V(){
        return V;
    }
    public Iterable<Integer> adj(int v){
        return (Iterable<Integer>)adj[v];//返回迭代器数据类型的数组，然后就可以用for each了
    }
}

class DirectedDFS{
    private boolean[] marked;//标记是否被访问过

    public DirectedDFS(Digraph G,int s){
        marked = new boolean[G.V()];
        dfs(G,s);
    }
    public DirectedDFS(Digraph G,Iterable<Integer> sources){
        marked = new boolean[G.V()];//source相当于一个迭代器类型的数组
        for(int s:sources)
            if(!marked[s])
                dfs(G,s);
    }
    private void dfs(Digraph G,int v){
        marked[v] = true;
        for(int w:G.adj(v))//每一层递归遍历当前节点的邻接表，如果邻接表里的结点没被访问过，就访问下去
            if(!marked[w])
                dfs(G,w);
    }
    public boolean marked(int v){
        return marked[v];
    }
}

class NFA{
    private char[] re;//用数组存储匹配转换（单一性）
    private Digraph G;//用有向图存储 ϵ 转换
    private int M;//状态数量
    public NFA(String regexp){//regexp是正则表达式的意思
        //根据给定的正则表达式构造NFA
        Stack<Integer> ops = new Stack<Integer>();
        re = regexp.toCharArray();
        M = re.length;
        G = new Digraph(M+1);
        for(int i=0;i<M;i++){
            int lp = i;
            if(re[i]=='(' || re[i] == '|')
                ops.push(i);//只把(和|的编号压入栈
            else if(re[i] == ')'){
                int or = ops.pop();
                if(re[or] == '|'){
                    lp = ops.pop();
                    G.addEdge(lp, or+1);//将所有的可能
                    G.addEdge(or, i);
                }
                else
                    lp = or;
            }

            if(i<M-1 && re[i+1] == '*'){//查看下一个字符
                G.addEdge(lp, i+1);
                G.addEdge(i+1, lp);
            }
            if(re[i]=='(' || re[i]=='*' || re[i]==')')
                G.addEdge(i, i+1);
        }
    }
    public boolean recognizes(String txt){//现在是
        ArrayList<Integer> pc = new ArrayList<Integer>();
        DirectedDFS dfs = new DirectedDFS(G, 0);
        for(int v = 0;v<G.V();v++){
            if(dfs.marked(v))//找出G中所有到达过的点
                pc.add(v);
        }
        for(int i=0;i<txt.length();i++){//计算txt[i+1]可能到达的所有NFA状态
            ArrayList<Integer> match = new ArrayList<Integer>();
            for(int v:pc)
                if(v<M)
                    if(re[v] == txt.charAt(i) || re[v] == '.')
                        match.add(v+1);
            pc = new ArrayList<Integer>();
            dfs = new DirectedDFS(G, match);//通过G找出所有的ϵ 转换集合里的点
            for(int v=0;v<G.V();v++)
                if(dfs.marked(v))
                    pc.add(v);     //每一次最外层的for循环，都会更新pc
        }
        for(int v:pc)
            if(v == M)
                return true;

        return false;
    }

}
public class nfa1 {

    public static void main(String[] args) {
        String regexp = "((A*B|AC)D)";//ABACD是不对的，因为是在A*B与AC中选一个
        NFA nfa = new NFA(regexp);
        System.out.println(nfa.recognizes("AAABD"));
    }

}
