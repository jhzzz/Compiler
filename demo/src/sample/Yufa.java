package sample;

import sample.table.MidCodeTable;
import sample.table.VariabeTable;
import sample.table.returnValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SplittableRandom;

public class Yufa {
    private String token = null;
    private int count = 0;
    private List<String> tokenList;
    private List<String> wordList;
    private List<String> errorList = new ArrayList<>();

    private int NXQ = 1;
    private int t = 0;
    private ArrayList<MidCodeTable> midCodeTables = new ArrayList<>();
    private HashMap<String,ArrayList<VariabeTable>> variabeTables;
    private HashMap<String,String> symbolTables;

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


    public void parser(List<String> tokenList, List<String> wordList) {
        this.tokenList = tokenList;
        this.wordList = wordList;
        midCodeTables.add(0,new MidCodeTable(null,null,null,null));
        token = nextToken();
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


    public void sentence() {
        if (token.equals("102") || token.equals("103") || token.equals("101") || token.equals("107") || token.equals("105")) {
            declarationSen();
        } else {
            executeSen();
        }
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
        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
        else match(token);
        if (!isIdentifier(token)) error();
        variableDeclTable();

    }

    public void variableDeclTable() {
        if (!isIdentifier(token)) error();
        SingleVariableDecl();
        if (token.equals("303")) {
            match(token);
        } else if (token.equals("304")) {
            match(token);
            if (isIdentifier(token)) {
                variableDeclTable();
            } else error();
        } else error();
    }

    public void SingleVariableDecl() {
        if (!isIdentifier(token)) error();
        else match(token);
        if (token.equals("219")) {
            match(token);
            expression();
        }
    }

    public void funcDecl() {
        if (!(token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107"))) error();
        else match(token);
        if (!isIdentifier(token)) error();
        else match(token);
        if (!token.equals("201")) error();
        else match(token);
        funcDeclParaList();
        if (!token.equals("202")) error();
        else match(token);
        if (!token.equals("303")) error();
        else match(token);

    }

    public void funcDeclParaList() {
        if (token.equals("102") || token.equals("101") || token.equals("103")) {
            funcDeclPara();
        }
    }

    public void funcDeclPara() {
        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
        else match(token);
        if (token.equals("304")) {
            match(token);
            funcDeclPara();
        }
    }

    public void executeSen() {
        if (token.equals("301")) {
            compoundSen();
        } else if (token.equals("111") || token.equals("113") || token.equals("110") || token.equals("109") || token.equals("106")) {
            controlSen();
        } else if (isIdentifier(token)) {
            dataManageSen();
        }
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

    public int assignSen() {
        int chain = assignExpr();
        if (!token.equals("303")) error();
        else match(token);
        return chain;
    }


    public int  assignExpr() {
        String id = "";
        if (!isIdentifier(token)) error();
        else {
            id = wordList.get(count-1);
            match(token);
        }
        if (!token.equals("219")) error();
        else {
            match(token);
            String e_place = aexpr().getPlace();
            gencode("=",e_place,"",id);
        }
        return NXQ-1;

    }

    public void funcSen() {
        funcTransfer();
        if (!token.equals("303")) error();
        else match(token);

    }

    public void funcTransfer() {
        if (!isIdentifier(token)) error();
        else match(token);
        if (!token.equals("201")) error();
        else match(token);
        realParaList();
        if (!token.equals("202")) error();
        else match(token);
    }

    public void realParaList() {
        if (!token.equals("202")) {
            realPara();
        }
    }

    public void realPara() {
        expression();
        if (token.equals("304")) {
            match(token);
            realPara();
        }

    }

    public void controlSen() {
        if (token.equals("111")) {
            ifs();
        } else if (token.equals("113")) {
            fors();
        } else if (token.equals("110")) {
            whiles();
        } else if (token.equals("109")) {
            doWhiles();
        } else if (token.equals("106")) {
            returnSen();
        } else error();
    }

    public void compoundSen() {
        if (!token.equals("301")) error();
        else match(token);
        senTable();
        if (!token.equals("302")) error();
        else match(token);
    }

    public void senTable() {
        if (!token.equals("302")) {
            sentence();
            senTable();
        }
    }

    public int ifs() {
        if (!token.equals("111")) error();//if
        else match(token);
        if (!token.equals("201")) error();//(
        else match(token);
        returnValue e = bexpr();
        if (!token.equals("202")) error();//)
        else match(token);
        //sentence();
        backpatch(Integer.parseInt(e.getTc()),Integer.toString(NXQ));
        int s1_chain = assignSen();
        if (token.equals("112")) {//else
            int q = NXQ;
            gencode("j","","","0");
            backpatch(Integer.parseInt(e.getFc()),Integer.toString(NXQ));
            int t_chain = merge(s1_chain,q);

            match(token);

            //sentence();
            int s2_chain = assignSen();
            return merge(t_chain,s2_chain);
        }else if(token.equals("303")){
            return merge(s1_chain,Integer.parseInt(e.getFc()));
        }
        return 0;
    }

    public void fors() {
        if (!token.equals("113")) error();
        else match(token);
        if (!token.equals("201")) error();
        else match(token);
        expression();
        if (!token.equals("303")) error();
        else match(token);
        expression();
        if (!token.equals("303")) error();
        else match(token);
        expression();
        if (!token.equals("202")) error();
        else match(token);
        compoundSen();
    }

    public void whiles() {
        if (!token.equals("110")) error();
        else match(token);
        if (!token.equals("201")) error();
        else match(token);
        expression();
        if (!token.equals("202")) error();
        else match(token);
        compoundSen();
    }

    public void doWhiles() {
        if (!token.equals("109")) error();
        else match(token);
        repeatCompoundSen();
        if (!token.equals("110")) error();
        else match(token);
        if (!token.equals("201")) error();
        else match(token);
        expression();
        if (!token.equals("202")) error();
        else match(token);
        if (!token.equals("303")) error();
        else match(token);
    }

    public void repeatSen() {
        if (token.equals("102") || token.equals("101") || token.equals("103") || token.equals("107") || token.equals("105")) {
            declarationSen();
        } else if (token.equals("301")) {
            repeatCompoundSen();
        } else if (token.equals("111") || token.equals("113") || token.equals("110") || token.equals("109") || token.equals("106") || token.equals("104") || token.equals("108")) {
            repeatExecuteSen();
        }
    }

    public void repeatCompoundSen() {
        if (!token.equals("301")) error();
        else match(token);
        repeatSenTable();
        if (!token.equals("302")) error();
        else match(token);
    }

    public void repeatSenTable() {
        if (!token.equals("302")) {
            repeatSen();
            repeatSenTable();
        }
    }

    public void repeatExecuteSen() {
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
        }
        expression();
        if (!token.equals("303")) error();
        else match(token);
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
        if (!token.equals("201")) error();
        else match(token);
        funcDefParaList();
        if (!token.equals("202")) error();
        else match(token);
        compoundSen();
    }

    public void funcDefParaList() {
        if (token.equals("102") || token.equals("101") || token.equals("103")) {
            funcDefPara();
        }
    }

    public void funcDefPara() {
        if (!(token.equals("102") || token.equals("101") || token.equals("103"))) error();
        else match(token);
        if (!isIdentifier(token)) error();
        else match(token);
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
        if(token.equals("213") | token.equals("214") | token.equals("215") |
                token.equals("211") | token.equals("212") | token.equals("216")) {
            match(token);
            String AE2 = aexpr().getPlace();
            String t = newtemp();
            gencode("jrop",AE1,AE2,t);
            return new returnValue(t,null,null);
            //gencode("j","","","0");
            //return new returnValue(null,Integer.toString(NXQ),Integer.toString(NXQ+1));
        }
        return new returnValue(AE1,null,null);
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
            funcTransfer();
        } else error();
        return place;
    }

    public void gencode(String operater, String ob1, String ob2, String result){
        midCodeTables.add(new MidCodeTable(operater,ob1,ob2,result));
        System.out.println(NXQ+"\t"+operater+","+ob1+","+ob2+","+result);
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