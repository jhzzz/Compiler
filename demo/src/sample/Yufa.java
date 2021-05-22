package sample;

import sample.table.MidCodeTable;
import sample.table.VariableTable;
import sample.table.returnValue;
import sample.table.funcTable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Yufa {
    private String token = null;
    private int count = 0;
    private List<String> tokenList;
    private List<String> wordList;
    private List<String> errorList = new ArrayList<>();

    private int NXQ = 1;
    private int t = 0;
    private ArrayList<MidCodeTable> midCodeTables = new ArrayList<>();
    private HashMap<Integer,ArrayList<VariableTable>> variabeTables = new HashMap<>();
    private HashMap<Integer,funcTable> funcTables = new HashMap<>();
    private HashMap<String ,Integer> symbolTables = new HashMap<>();
    private Stack scopeStack = new Stack();
    private int scope = 0;
    private int variableCount = 0;
    private int functionCount = 0;

    public void match(String s) {
        if (token.equals(s)) {
            if (count >= tokenList.size())
                return;
            token = nextToken();
            System.out.println(s + "\t" + wordList.get(count - 2));
        } else {
            error();
        }
    }

    public void error() {
        System.out.println(token + "出错！！");
        errorList.add(wordList.get(count - 2)+"\t\t匹配失败！");
    }

    public String nextToken() {
        return tokenList.get(count++);
    }
    public List<String> getErrorList(){
        return errorList;
    }
    public ArrayList<MidCodeTable> getMidCodeTable(){
        return midCodeTables;
    }
    public int getNXQ(){
        return NXQ;
    }
    public String getSymbolTable(){
        String symbolTable= "";
        String variabeTable = "";
        String funcTable = "";

        variabeTable = variabeTable + "variableTable:\n";
        for(int i = 0; i < variabeTables.size(); i++){
            for(int j = 0; j < variabeTables.get(i).size(); j++) {
                String variabeTableItem = variabeTables.get(i).get(j).toString()+"\n";
                variabeTable = variabeTable + variabeTableItem;
            }
        }

        funcTable = funcTable + "\nfunctionTable:\n";
        for(int i = 0; i < funcTables.size(); i++){
            String funcTableItem = funcTables.get(i).toString()+"\n";
            funcTable = funcTable + funcTableItem;
        }

        symbolTable = variabeTable + funcTable;
        return symbolTable;
    }


    public void parser(List<String> tokenList, List<String> wordList) {
        this.tokenList = tokenList;
        this.wordList = wordList;
        midCodeTables.add(0,new MidCodeTable(null,null,null,null));
        token = nextToken();
        scopeStack.push(scope++);
        while (token.equals("102") || token.equals("101") || token.equals("103") || token.equals("105") || token.equals("107"))
            declarationSen();

        if (!token.equals("700")) error();//main
        else {
            match(token);
            gencode("main","","","");
        }
        if (!token.equals("201")) error();//(
        else match(token);
        if (!token.equals("202")) error();//)
        else match(token);
        compoundSen();
        gencode("sys","","","");

        if (token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107")) {
            funcBlock();
        }
    }

    public void funcBlock() {
        funcDefine();
        if (token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107")) {
            funcBlock();
        }else if(!token.equals("#") && !token.equals("102") && !token.equals("101") && !token.equals("103") && !token.equals("107")){
            error();
        }
    }

    public boolean isIdentifier(String token) {
        if (token.equals("700")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDigit(String token) {
        if (token.equals("400") || token.equals("800")) {
            return true;
        } else return false;
    }

    public returnValue sentence() {
        returnValue chain = new returnValue();
        if (token.equals("102") || token.equals("103") || token.equals("101") || token.equals("107") || token.equals("105")) {
            declarationSen();
        } else {
            chain = executeSen();
        }
        return chain;
    }

    public void declarationSen() {
        if (tokenList.get(count + 1).equals("201")) {
            funcDecl();
        } else if (token.equals("102") || token.equals("103") || token.equals("101") || token.equals("105")) {
            valuedecl();
        }
    }

    public void valuedecl() {
        if (token.equals("105")) {
            constDecl();
        } else if (token.equals("102") || token.equals("103") || token.equals("101")) {
            variableDecl();
        } else error();
    }

    public void constDecl() {
        if (!token.equals("105")) error();
        else match(token);
        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
        else match(token);
        if (!isIdentifier(token)) error();
        constDeclTable();
    }

    public void constDeclTable() {
        if (!isIdentifier(token)) error();
        else match(token);
        if (!token.equals("219")) error();
        else match(token);
        if (!isDigit(token)) error();
        else match(token);

        if (token.equals("303")) {
            match(token);
        } else if (token.equals("304")) {
            match(token);
            constDeclTable();
        } else error();
    }

    public void variableDecl() {
//        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
//        else match(token);
        String type = null;
        if(token.equals("102")){
            match(token);
            type = "int";
        }else if(token.equals("101")){
            match(token);
            type = "char";
        }else if(token.equals("103")){
            match(token);
            type = "float";
        }else error();
        if (!isIdentifier(token)) error();
        variableDeclTable(type);

    }

    public void variableDeclTable(String type) {
        if (!isIdentifier(token)) error();
        SingleVariableDecl(type);
        if (token.equals("303")) {
            match(token);
        } else if (token.equals("304")) {
            match(token);
            if (isIdentifier(token)) {
                variableDeclTable(type);
            } else error();
        } else error();
    }

    public void SingleVariableDecl(String type) {
        String value = null;
        if (!isIdentifier(token)) error();
        else match(token);
        String id = wordList.get(count-2);
        int entry = insertVariable(id);

        if (token.equals("219")) {
            match(token);
            value = aexpr().getPlace();
        }
        entryVar(entry,id,type,value,String.valueOf(scopeStack));
    }


    public void funcDecl() {
        ArrayList<String> paraType = new ArrayList<>();
        if (!(token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107"))) error();
        else match(token);
        String funcType = wordList.get(count-2);
        if (!isIdentifier(token)) error();
        else match(token);
        String funcName = wordList.get(count-2);
        int entry = insertFunction(funcName);
        if (!token.equals("201")) error();//(
        else match(token);
        paraType = funcDeclParaList(paraType);
        entryFunc(entry,funcName,funcType,paraType.size(),paraType);
        if (!token.equals("202")) error();//)
        else match(token);
        if (!token.equals("303")) error();//;
        else match(token);

    }

    public ArrayList<String> funcDeclParaList(ArrayList<String> paraType) {

        if (token.equals("102") || token.equals("101") || token.equals("103")) {
            paraType = funcDeclPara(paraType);
        }
        return paraType;
    }

    public ArrayList<String> funcDeclPara(ArrayList<String> paraType) {
//        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
//        else match(token);
        if(token.equals("102")){
            match(token);
            paraType.add("int");
        }else if(token.equals("101")){
            match(token);
            paraType.add("char");
        }else if(token.equals("103")){
            match(token);
            paraType.add("float");
        }else error();

        if (token.equals("304")) {//,
            match(token);
            funcDeclPara(paraType);
        }
        return paraType;
    }

    public returnValue executeSen() {
        returnValue chain = new returnValue();
        if (token.equals("301")) {
            chain = compoundSen();
        } else if (token.equals("111") || token.equals("113") || token.equals("110") || token.equals("109") || token.equals("106")) {
            chain = controlSen();
        } else if (isIdentifier(token)) {
            dataManageSen();
        }
        return chain;
    }

    public void dataManageSen() {
        if (!isIdentifier(token)) error();
        String token1 = tokenList.get(count);
        if (token1.equals("201")) {
            funcSen();
        } else if (token1.equals("219")) {
            assignSen();
        } else error();
    }

    public void assignSen() {
        assignExpr();
        if (!token.equals("303")) error();
        else match(token);
    }


    public returnValue assignExpr() {
        String id = "";
        returnValue e = new returnValue();
        if (!isIdentifier(token)) error();
        else {
            id = wordList.get(count-1);
            match(token);
        }
        if (!token.equals("219")) error();
        else {
            match(token);
            e = aexpr();
            gencode("=",e.getPlace(),"",id);
        }
        return e;

    }

    public void funcSen() {
        funcTransfer();
        if (!token.equals("303")) error();
        else match(token);

    }

    public String funcTransfer() {
        if (!isIdentifier(token)) error();
        else match(token);
        String id = wordList.get(count-2);
        if (!token.equals("201")) error();//(
        else match(token);
        realParaList();
        if (!token.equals("202")) error();//)
        else match(token);
        String t = newtemp();
        gencode("call",id,"",t);
        return t;
    }

    public void realParaList() {
        if (!token.equals("202")) {
            realPara();
        }
    }

    public void realPara() {
        String place = aexpr().getPlace();
        gencode("para",place,"","");
        if (token.equals("304")) {
            match(token);
            realPara();
        }

    }

    public returnValue controlSen() {
        returnValue chain = new returnValue();
        if (token.equals("111")) {
            chain = ifs();
        } else if (token.equals("113")) {
            chain = fors();
        } else if (token.equals("110")) {
            chain = whiles();
        } else if (token.equals("109")) {
            chain = doWhiles();
        } else if (token.equals("106")) {
            returnSen();
        } else error();
        backpatch(Integer.parseInt(chain.getChain()),Integer.toString(NXQ));
        return chain;
    }

    public returnValue compoundSen() {
        returnValue chain = new returnValue();
        if (!token.equals("301")) error();//{
        else match(token);
        scopeStack.push(scope++);
        chain = senTable();
        if (!token.equals("302")) error();//}
        else match(token);
        scopeStack.pop();
        return chain;
    }

    public returnValue senTable() {
        returnValue chain = new returnValue();
        if (!token.equals("302")) {
            chain = sentence();
            senTable();
        }
        return chain;
    }

    public returnValue ifs() {
        if (!token.equals("111")) error();//if
        else match(token);
        if (!token.equals("201")) error();//(
        else match(token);
        returnValue e = bexpr();
        if (!token.equals("202")) error();//)
        else match(token);
        backpatch(Integer.parseInt(e.getTc()),Integer.toString(NXQ));
        String c_chain = e.getFc();

        String s1_chain = sentence().getChain();

        //int s1_chain = assignSen();
        String s_chain = Integer.toString(merge(Integer.parseInt(c_chain),Integer.parseInt(s1_chain)));
        if (token.equals("112")) {//else
            match(token);

            int q = NXQ;
            gencode("j","","","0");
            backpatch(Integer.parseInt(c_chain),Integer.toString(NXQ));
            String t_chain = Integer.toString(merge(Integer.parseInt(s1_chain),q));

            String s2_chain = sentence().getChain();
            //int s2_chain = assignSen();
            s_chain = Integer.toString(merge(Integer.parseInt(t_chain),Integer.parseInt(s2_chain)));
            //backpatch(q,Integer.toString(NXQ));
            //return new returnValue("","0","0",s_chain);
        }
        //else backpatch(Integer.parseInt(c_chain),Integer.toString(NXQ));
//        else if(token.equals("303")){
//            s_chain = Integer.toString(merge(Integer.parseInt(s1_chain),Integer.parseInt(e.getFc())));
//            //return new returnValue("","0","0",s_chain);
//        }
        return new returnValue("","0","0",s_chain);
    }

    public returnValue fors() {
        if (!token.equals("113")) error();//for
        else match(token);
        if (!token.equals("201")) error();//(
        else match(token);
        String e1_place = assignExpr().getPlace();
        if (!token.equals("303")) error();//;
        else match(token);
        /********E(1)*********/
//        String f_place = newtemp();
//        gencode("=",e1_place,"",f_place);
        int f_test = NXQ;

        returnValue e2 = bexpr();
        if (!token.equals("303")) error();//;
        else match(token);
        /**********E(2)***********/
        String a_place = newtemp();
        //gencode("=",e2_place,"",a_place);
        int a_chain = NXQ;
        gencode("jz",a_place,"","0");
        int a_right = NXQ;
        gencode("j","","","0");
        int a_inc = NXQ;
        int a_test = f_test;

        assignExpr();
        if (!token.equals("202")) error();//)
        else match(token);
        /**********E(3)***********/
        gencode("j","","",Integer.toString(a_test));
        backpatch(a_right,Integer.toString(NXQ));
        int b_chain = a_chain;
        int b_inc = a_inc;

        String s1_chain = sentence().getChain();
        /**********S(1)***********/
        backpatch(Integer.parseInt(s1_chain),Integer.toString(NXQ));
        gencode("j","","",Integer.toString(b_inc));
        int s_chain = b_chain;

        return new returnValue("","0","0",Integer.toString(s_chain));

    }

    public returnValue whiles() {
        if (!token.equals("110")) error();//while
        else match(token);
        if (!token.equals("201")) error();//(
        else match(token);
        returnValue e = bexpr();
        if (!token.equals("202")) error();//)
        else match(token);
        int head = NXQ;
        backpatch(Integer.parseInt(e.getTc()),Integer.toString(head));
        compoundSen().getChain();
        return new returnValue("","0","0",e.getFc());
    }

    public returnValue doWhiles() {
        if (!token.equals("109")) error();//do
        else match(token);

        int d_head = NXQ;

        //String s1_chain = repeatCompoundSen().getChain();
        String s1_chain = sentence().getChain();
        if (!token.equals("110")) error();//while
        else match(token);

        int u_head = d_head;
        backpatch(Integer.parseInt(s1_chain),Integer.toString(NXQ));

        if (!token.equals("201")) error();//(
        else match(token);

        returnValue e = bexpr();
        backpatch(Integer.parseInt(e.getTc()),Integer.toString(u_head));
        String s_chain = e.getFc();

        if (!token.equals("202")) error();//)
        else match(token);
        if (!token.equals("303")) error();//;
        else match(token);
        //backpatch(Integer.parseInt(s_chain),Integer.toString(NXQ));

        return new returnValue("","0","0",s_chain);
    }

    public returnValue repeatSen() {
        returnValue chain = new returnValue();
        if (token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107") || token.equals("105")) {
            declarationSen();
        } else if (token.equals("301")) {
            chain = repeatCompoundSen();
        } else if (token.equals("111") || token.equals("113") || token.equals("110") || token.equals("109") || token.equals("106") || token.equals("104") || token.equals("108")) {
            chain = repeatExecuteSen();
        }
        return chain;
    }

    public returnValue repeatCompoundSen() {
        returnValue chain = new returnValue();
        if (!token.equals("301")) error();
        else match(token);
        chain = repeatSenTable();
        if (!token.equals("302")) error();
        else match(token);
        return chain;
    }

    public returnValue repeatSenTable() {
        returnValue chain = new returnValue();
        if (!token.equals("302")) {
            chain = repeatSen();
            repeatSenTable();
        }
        return chain;
    }

    public returnValue repeatExecuteSen() {
        returnValue chain = new returnValue();
        if (token.equals("111")) {
            repeatIfSen();
        } else if (token.equals("113")) {
            fors();
        } else if (token.equals("110")) {
            whiles();
        } else if (token.equals("109")) {
            doWhiles();
        } else if (token.equals("106")) {
            returnSen();
        } else if (token.equals("104")) {
            breakSen();
        } else if (token.equals("108")) {
            continueSen();
        } else error();
        return chain;
    }

    public void repeatIfSen() {
        if (!token.equals("111")) error();
        else match(token);
        if (!token.equals("201")) error();
        else match(token);
        expression();
        if (!token.equals("202")) error();
        else match(token);
        repeatSen();
        if (token.equals("112")) {
            match(token);
            repeatSen();
        }
    }

    public void returnSen() {
        if (!token.equals("106")) error();
        else match(token);
        if (token.equals("303")) {
            match(token);
            gencode("ret","","","");
        }else {
            String place = aexpr().getPlace();
            if (!token.equals("303")) error();
            else match(token);
            gencode("ret","","",place);
        }
    }

    public void breakSen() {
        if (!token.equals("104")) error();
        else match(token);
        if (!token.equals("303")) error();
        else match(token);
    }

    public void continueSen() {
        if (!token.equals("108")) error();
        else match(token);
        if (!token.equals("303")) error();
        else match(token);


    }

    public void funcDefine() {
        if (!(token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107"))) error();
        else match(token);
        if (!isIdentifier(token)) error();
        else match(token);
        String name = wordList.get(count-2);
        gencode(name,"","","");
        if (!token.equals("201")) error();//(
        else match(token);
        funcDefParaList();
        if (!token.equals("202")) error();//)
        else match(token);
        compoundSen();
        gencode("ret","","","");
    }

    public void funcDefParaList() {
        if (token.equals("102") || token.equals("101") || token.equals("103")) {
            funcDefPara();
        }
    }

    public void funcDefPara() {
        String type = null;
//        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
//        else match(token);
        if(token.equals("102")){
            match(token);
            type = "int";
        }else if(token.equals("101")){
            match(token);
            type = "char";
        }else if(token.equals("103")){
            match(token);
            type = "float";
        }else error();
        if (!isIdentifier(token)) error();
        else match(token);
        //
        String id = wordList.get(count-2);
        int entry = insertVariable(id);
        if(variabeTables.containsKey(entry)){
            variabeTables.get(entry).add(new VariableTable(id,null,type,null));
        }else {
            ArrayList<VariableTable> variabeTableList = new ArrayList<>();
            variabeTableList.add(new VariableTable(id,null,type,null));
            variabeTables.put(entry,variabeTableList);
        }
        //
        if (token.equals("304")) {
            match(token);
            funcDefPara();
        }
    }

    public void expression() {
        if (tokenList.get(count).equals("219")) {
            assignExpr();
        } else bexpr();
    }


    public returnValue bexpr() {
        returnValue bt = bterm();
        returnValue be = bt;
        if (token.equals("218")) {//||
            match(token);
            backpatch(Integer.parseInt(bt.getFc()),Integer.toString(NXQ));
            returnValue be1 = bexpr();
            be.setFc(be1.getFc());
            be.setTc(Integer.toString(merge(Integer.parseInt(bt.getTc()),Integer.parseInt(be1.getTc()))));
        }
        return be;
    }

    public returnValue bterm() {
        returnValue bf = bfactor();
        returnValue bt = bf;
        if (token.equals("217")) {//&&
            match(token);
            backpatch(Integer.parseInt(bf.getTc()),Integer.toString(NXQ));
            returnValue bt1 = bterm();
            bt.setTc(bt1.getTc());
            bt.setFc(Integer.toString(merge(Integer.parseInt(bf.getFc()),Integer.parseInt(bt1.getFc()))));
        }
        return bt;
    }

    public returnValue bfactor() {
        returnValue bf = new returnValue();
        if (token.equals("205")) {//!
            match(token);
            returnValue bf1 = bexpr();
            bf.setTc(bf1.getFc());
            bf.setFc(bf1.getTc());
        } else {
            String i = rexpr().getPlace();
            bf.setTc(Integer.toString(NXQ));
            bf.setFc(Integer.toString(NXQ+1));
            gencode("jnz",i,"","0");
            gencode("j","","","0");
        }
        return bf;
    }

    public returnValue rexpr(){
        String AE1 = aexpr().getPlace();
//        if(token.equals("213") | token.equals("214") | token.equals("215") |
//                token.equals("211") | token.equals("212") | token.equals("216")) {
//            match(token);
//            String AE2 = aexpr().getPlace();
//            String t = newtemp();
//            gencode("jrop",AE1,AE2,t);
//            return new returnValue(t,"0","0","0");
//        }
        if(token.equals("213")) {//>
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode(">",AE1,AE2,t);
            return new returnValue(t,"0","0","0");
        }else if(token.equals("214")) {//>=
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode(">=",AE1,AE2,t);
            return new returnValue(t,"0","0","0");
        }else if(token.equals("215")) {//==
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode("==",AE1,AE2,t);
            return new returnValue(t,"0","0","0");
        }else if(token.equals("211")) {//<
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode("<",AE1,AE2,t);
            return new returnValue(t,"0","0","0");
        }else if(token.equals("212")) {//<=
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode("<=",AE1,AE2,t);
            return new returnValue(t,"0","0","0");
        }else if(token.equals("216")) {//!=
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode("!=",AE1,AE2,t);
            return new returnValue(t,"0","0","0");
        }
        return new returnValue(AE1,"0","0","0");
    }


    public returnValue aexpr() {
        returnValue e1_place = term();
        returnValue place = e1_place;
        if (token.equals("209")) {//+
            match(token);
            String t_place = term().getPlace();
            String e_place = newtemp();
            gencode("+",e1_place.getPlace(),t_place,e_place);
            //place = e_place;
            place.setPlace(e_place);

        } else if (token.equals("210")) {//-
            match(token);
            String  t_place = term().getPlace();
            String e_place = newtemp();
            gencode("-",e1_place.getPlace(),t_place,e_place);
            place.setPlace(e_place);
        }
        return place;
    }

    public returnValue term() {
        returnValue t1_place = factor();
        returnValue place = t1_place;
        //String  place = t1_place;
        if (token.equals("206")) {//*
            match(token);
            String f_place = factor().getPlace();
            String t_place = newtemp();
            gencode("*",t1_place.getPlace(),f_place,t_place);
            place.setPlace(t_place);

        } else if (token.equals("207")) {
            match(token);
            String f_place = factor().getPlace();
            String t_place = newtemp();
            gencode("/",t1_place.getPlace(),f_place,t_place);
            place.setPlace(t_place);

        } else if (token.equals("208")) {
            match(token);
            String f_place = factor().getPlace();
            String t_place = newtemp();
            gencode("%",t1_place.getPlace(),f_place,t_place);
            place.setPlace(t_place);
        }
        return place;
    }

    public returnValue factor() {
        String token1 = tokenList.get(count);
        returnValue place = new returnValue();
        if (token.equals("201")) {
            match(token);
            place.setPlace(rexpr().getPlace());
            if (token.equals("202")) {
                match(token);
            } else error();
        } else if (isIdentifier(token) && !token1.equals("201")) {
            place.setPlace(wordList.get(count -1));
            match(token);
        } else if (isDigit(token)) {
            place.setPlace(wordList.get(count -1));
            match(token);
        } else if (isIdentifier(token) && token1.equals("201")) {
            //funcTransfer();
            place.setPlace(funcTransfer());
        } else error();
        return place;
    }

    public int insertVariable(String id){
        if(symbolTables.containsKey(id)){
            return symbolTables.get(id);
        }
        symbolTables.put(id,variableCount++);
        return variableCount-1;
    }

    public int insertFunction(String id){
        if(symbolTables.containsKey(id)){
            return symbolTables.get(id);
        }
        symbolTables.put(id,functionCount++);
        return functionCount-1;
    }

    public int findVarible(String id){
        for(int i = 0; i < symbolTables.size(); i++){
            if(symbolTables.get(i).equals(id)) return i;
        }
        return 0;
    }
    public void entryVar(int entry, String name, String type, String value, String scope){
        if(variabeTables.containsKey(entry)){
            variabeTables.get(entry).add(new VariableTable(name,scope,type,value));
        }else {
            ArrayList<VariableTable> variabeTableList = new ArrayList<>();
            variabeTableList.add(new VariableTable(name,scope,type,value));
            variabeTables.put(entry,variabeTableList);
        }
    }
    public void entryFunc(int entry, String name, String type, int paraCount, ArrayList<String> paras){
        funcTables.put(entry,new funcTable(name,type,paraCount,paras));
    }

    public void gencode(String operater, String ob1, String ob2, String result){
        midCodeTables.add(new MidCodeTable(operater,ob1,ob2,result));
        //System.out.println(NXQ+"\t"+operater+","+ob1+","+ob2+","+result);
        NXQ++;
    }

    public String newtemp(){
        return "t"+t++;
    }

    public int merge(int p1, int p2){
        if(p2 == 0) return p1;
        else {
            int p = p2;
            while(!midCodeTables.get(p).getResult().equals("0")){
                p = Integer.parseInt(midCodeTables.get(p).getResult());
            }
            midCodeTables.get(p).setResult(Integer.toString(p1));
            return p2;
        }
    }

    public void backpatch(int p, String t){
        int q = p;
        while(q != 0){
            int m = Integer.parseInt(midCodeTables.get(q).getResult());
            midCodeTables.get(q).setResult(t);
            q = m;
        }
    }
}