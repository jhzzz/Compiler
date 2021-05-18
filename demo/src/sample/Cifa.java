package sample;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Cifa {
    //Map<String,String> tokenTable = new LinkedHashMap<>();
    List<String> token = new LinkedList<>();
    List<String> value = new LinkedList<>();
    int column = 1;
    int row = 1;
    int begin = 0;
    int end = 0;
    String str;






    char ch ;
    String[][] typeCode = {{"char", "101"}, {"int", "102"}, {"float", "103"},
            {"break", "104"}, {"const", "105"}, {"return", "106"},
            {"void", "107"}, {"continue", "108"}, {"do", "109"},
            {"while", "110"}, {"if", "111"}, {"else", "112"},
            {"for", "113"}, {"{", "301"}, {"}", "302"},
            {";", "303"}, {",", "304"}, {"(", "201"},
            {")", "202"}, {"[", "203"}, {"]", "204"},
            {"!", "205"}, {"*", "206"}, {"/", "207"},
            {"%", "208"}, {"+", "209"}, {"-", "210"},
            {"<", "211"}, {"<=", "212"}, {">", "213"},
            {">=", "214"}, {"==", "215"}, {"!=", "216"},
            {"&&", "217"}, {"||", "218"}, {"=", "219"},
            {".", "220"}};


    public List<String> getToken(){
        return token;
    }
    public List<String> getValue(){
        return value;
    }

    public char GetNextChar() {
    column++;
    end++;
    return str.charAt(end);

    }
    public void error() {
        if (str.charAt(end) != '|' || str.charAt(end) != '&') {
            while (Character.isDigit(str.charAt(end)) || Character.isLetter(str.charAt(end))) {
                column++;
                end++;
            }
        }
        column--;
        end--;
    }
        public String GetToken() {
        String id = str.substring(begin, end + 1);
        for (int i = 0; i < typeCode.length; i++) {
            if (id.equals(typeCode[i][0])) {
                value.add(typeCode[i][1]);
                token.add(typeCode[i][0]);
                //tokenTable.put(typeCode[i][1],typeCode[i][0]);
                return typeCode[i][1] + "\t" + typeCode[i][0];
            }
        }
        return "";
    }


    public void RecognizeWS(char ch) {
        char state = '0';
        while (state != '2') {
            switch (state) {
                case '0':
                    if (ch == ' '||ch == '\t') {
                        state = '1';
                    } else error();
                    break;
                case '1':
                    if (end < str.length() - 1) {
                        ch = GetNextChar();
                        if (ch == ' '||ch == '\t') {
                            state = '1';
                        } else {
                            state = '2';
                        }
                    } else {
                        state = '2';
                    }
                    break;
            }
        }
        column--;
        end--;
    }


    public String Recognize1_3(char ch) {
        char state = '0';
        while (state != '2') {
            switch (state) {
                case '0':
                    if (Character.isLetter(ch) | ch == '_') {
                        state = '1';
                    } else error();
                    break;
                case '1':
                    if (end < str.length() - 1) {
                        ch = GetNextChar();
                        if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
                            state = '1';
                        } else {
                            state = '2';
                            column--;
                            end--;
                        }
                    } else {
                        state = '2';
                    }
                    break;
            }
        }
        String id = str.substring(begin, end + 1);
        for (int i = 0; i < typeCode.length; i++) {
            if (id.equals(typeCode[i][0])) {
                value.add(typeCode[i][1]);
                token.add(typeCode[i][0]);
                //tokenTable.put(typeCode[i][1],typeCode[i][0]);
                return typeCode[i][1] + "\t" + typeCode[i][0];
            }
        }
        value.add("700");
        token.add(id);
        //tokenTable.put("700",id);
        return "700\t" + id;
    }

    public boolean isEnd(char ch) {
        if (ch == ' '||ch=='\n') {
            return true;
        } else if (ch == '{' || ch == '}' || ch == ';' || ch == ',') {
            return true;
        } else if (ch == '(' || ch == ')' || ch == '[' || ch == ']' || ch == '*' || ch == '%' || ch == '+' ||
                ch == '-' || ch == '.' || ch == '!' || ch == '/' || ch == '<' || ch == '>' || ch == '=' || ch == '&' || ch == '|') {
            return true;
        } else return false;
    }

    public String Recognize1_4(char ch) {
        int state = 0;
        while (state != 2) {
            switch (state) {
                case 0:
                    if (ch == '0') {
                        state = 3;
                    } else if ('1' <= ch && ch <= '9') {
                        state = 1;
                    }
                    break;
                case 1:
                    ch = GetNextChar();
                    if ('0' <= ch && ch <= '9') {
                        state = 1;
                    } else if (ch == '.') {
                        state = 8;
                    } else if (ch == 'E' || ch == 'e') {
                        state = 10;
                    } else if (isEnd(ch)) {
                        state = 15;
                        column--;
                        end--;
                    } else state = 16;
                    break;
                case 3:
                    ch = GetNextChar();
                    if (ch == '.') {
                        state = 8;
                    } else if(isEnd(ch)){
                        state = 15;
                        column--;
                        end--;
                    } else state = 16;
                    break;
                case 8:
                    ch = GetNextChar();
                    if ('0' <= ch && ch <= '9') {
                        state = 9;
                    } else state = 16;
                    break;
                case 9:
                    ch = GetNextChar();
                    if ('0' <= ch && ch <= '9') {
                        state = 9;
                    } else if (ch == 'E' || ch == 'e') {
                        state = 10;
                    } else if (isEnd(ch)) {
                        state = 14;
                        column--;
                        end--;
                    } else state = 16;
                    break;
                case 10:
                    ch = GetNextChar();
                    if (ch == '+' || ch == '-') {
                        state = 11;
                    } else if ('0' <= ch && ch <= '9') {
                        state = 12;
                    } else state = 16;
                    break;
                case 11:
                    ch = GetNextChar();
                    if ('0' <= ch && ch <= '9') {
                        state = 12;
                    } else state = 16;
                    break;
                case 12:
                    ch = GetNextChar();
                    if ('0' <= ch && ch <= '9') {
                        state = 12;
                    } else if (isEnd(ch)) {
                        state = 13;
                        column--;
                        end--;
                    } else state = 16;
                    break;
                case 13:
                    value.add("800");
                    token.add(str.substring(begin, end + 1));
                    //tokenTable.put("800",str.substring(begin, end + 1));
                    return "800\t" + str.substring(begin, end + 1);
                case 14:
                    value.add("800");
                    token.add(str.substring(begin, end + 1));
                    //tokenTable.put("800",str.substring(begin, end + 1));
                    return "800\t" + str.substring(begin, end + 1);
                case 15:
                    value.add("400");
                    token.add(str.substring(begin, end + 1));
                    //tokenTable.put("400",str.substring(begin, end + 1));
                    return "400\t" + str.substring(begin, end + 1);
                case 16:
                    error();
                    state = 2;
                    break;
            }
        }
        return "";
    }

    public String Recognize1_5(char ch){
        char state = '0';
        ch = GetNextChar();
        while(state != '4'){
            switch (state){
                case '0':
                    if(ch == '/'){
                        state = '1';
                    }else if(ch == '*'){
                        state = '2';
                    }else state = '3';
                    break;
                case '1':
                    while(ch != '\n'){
                        ch = GetNextChar();
                    }
                    row++;
                    column = 0;
                    begin = end;
                    return "";
                case '2':
                    while(!(ch=='*' && str.charAt(end+1)=='/')){
                        ch = GetNextChar();
                        if(ch == '\n'){
                            row++;
                            column = 0;
                        }
                    }
                    begin = end + 2;
                    end = begin;
                    return "";
                case '3':
                    column--;
                    end--;
                    state = '4';
                    break;
            }
        }
        return GetToken();
    }

    public String Recognize1_8(char ch) {
        char state = '0';
        while (state != '5') {
            switch (state) {
                case '0':
                    if (ch == '<' || ch == '>' || ch == '!' || ch == '=') {
                        state = '1';
                    } else if (ch == '{' || ch == '}' || ch == ';' || ch == ',' || ch == '(' || ch == ')' ||
                            ch == '[' || ch == ']' || ch == '*' || ch == '%' || ch == '+' || ch == '-' || ch == '.') {
                        state = '2';
                    } else if (ch == '&') {
                        state = '3';
                    } else if (ch == '|') {
                        state = '4';
                    } else error();
                    break;
                case '1':
                    if (end < str.length() - 1) {
                        ch = GetNextChar();
                        if (ch == '=') {
                            state = '5';
                        } else {
                            state = '5';
                            column--;
                            end--;
                        }
                    } else {
                        state = '5';
                    }
                    break;
                case '2':
                    state = '5';
                    break;
                case '3':
                    if (end < str.length() - 1) {
                        ch = GetNextChar();
                        if (ch == '&') {
                            state = '5';
                        } else {
                            column--;
                            end--;
                            error();
                            state = '5';
                        }
                    } else {
                        error();
                        state = '5';
                    }
                    break;
                case '4':
                    if (end < str.length() - 1) {
                        ch = GetNextChar();
                        if (ch == '|') {
                            state = '5';
                        } else {
                            column--;
                            end--;
                            state = '5';
                            error();
                        }
                    } else {
                        error();
                        state = '5';
                    }
                    break;
            }
        }
        return GetToken();
    }

    public void SortFirstChar(String str) {
        this.str = str;
        ch = str.charAt(0);
        while (end < str.length()) {
            ch = str.charAt(begin);
            if (ch == ' '||ch == '\t') {
                RecognizeWS(ch);
            }else if(ch == '\n') {
                column = 0;
                row++;
            }else if (Character.isLetter(ch) || ch == '_') {
                Recognize1_3(ch);
            } else if (Character.isDigit(ch)) {
                Recognize1_4(ch);
            } else if(ch == '/'){
                Recognize1_5(ch);
            }else {
                Recognize1_8(ch);
            }
            begin = end + 1;
            end = begin;

        }
    }
    public static void main(String[] args) {

    }
}
