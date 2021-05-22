package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import sample.table.MidCodeTable;

import java.awt.*;
import java.io.*;
import java.nio.Buffer;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Controller {

    @FXML
    private MenuItem recentFile;

    @FXML
    private MenuItem exit;

    @FXML
    private MenuItem openFile;

    @FXML
    private AnchorPane rightPane;

    @FXML
    private AnchorPane leftPane;

    @FXML
    public TextArea outputText_up;

    @FXML
    public TextArea outputText_down;

    @FXML
    private Menu menuFile;

    @FXML
    public TextArea inputText;

    @FXML
    private MenuItem saveFile;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private MenuItem saveFileTo;

    String filePath;
    Map<String,Integer> tokenTable = new LinkedHashMap<>();
    int column = 1;
    int row = 1;
    int begin = 0;
    int end = 0;
    String str;
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

    @FXML
    void onOpenFile(ActionEvent event) throws Exception{
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开文件");
        File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
        filePath = file.getPath();
        BufferedReader read = new BufferedReader(new FileReader(filePath));
        String line;
        StringBuffer sb = new StringBuffer();
        while((line = read.readLine()) != null){
            sb.append(line+"\n");
        }
        read.close();
        inputText.setText(String.valueOf(sb));
    }

    @FXML
    void onSaveFile(ActionEvent event) throws Exception{
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
        bw.write(inputText.getText());
        bw.flush();
        bw.close();
    }

    @FXML
    void onSaveFileTo(ActionEvent event) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("文本文件(*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        File saveFile = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
        BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
        bw.write(inputText.getText());
        bw.flush();
        bw.close();
    }

    @FXML
    void onRecentFile(ActionEvent event) {
        outputText_up.setText(inputText.getText());
        outputText_down.setText(inputText.getText());
    }

    @FXML
    void onExit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void onHelp(ActionEvent event) throws Exception{
        Desktop.getDesktop().open(new File("src/sample/help.chm"));
    }

    @FXML
    void onGetObjectCode(ActionEvent event) throws Exception{

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
        outputText_down.appendText(row+"   "+str.substring(begin, end + 1)+'\n');
    }
    public String GetToken() {
        String id = str.substring(begin, end + 1);
        for (int i = 0; i < typeCode.length; i++) {
            if (id.equals(typeCode[i][0])) {
                return typeCode[i][1] + "\t" + typeCode[i][0];
            }
        }
        return "";
    }

    @FXML
    void onMidCode(ActionEvent event) {
        outputText_up.clear();
        str = inputText.getText();
        Cifa cifa = new Cifa();
        Yufa yufa = new Yufa();
        cifa.SortFirstChar(str);
        List<String> tokenList = cifa.getValue();
        List<String> wordList = cifa.getToken();
        ArrayList<MidCodeTable> midCodeTables = yufa.getMidCodeTable();
        tokenList.add("#");
        System.out.println(tokenList);
        yufa.parser(tokenList,wordList);
        for(int i = 1; i < midCodeTables.size(); i++){
            outputText_up.appendText(i+"\t");
            outputText_up.appendText(midCodeTables.get(i).getOperater()+", ");
            outputText_up.appendText(midCodeTables.get(i).getObject1()+", ");
            outputText_up.appendText(midCodeTables.get(i).getObject2()+", ");
            outputText_up.appendText(midCodeTables.get(i).getResult()+"\n");
        }
    }

    @FXML
    public void onSymbolTable(ActionEvent event) {
        outputText_down.clear();
        str = inputText.getText();
        Cifa cifa = new Cifa();
        Yufa yufa = new Yufa();
        cifa.SortFirstChar(str);
        List<String> tokenList = cifa.getValue();
        List<String> wordList = cifa.getToken();
        tokenList.add("#");
        yufa.parser(tokenList,wordList);
        String symbolTable = yufa.getSymbolTable();
        outputText_down.appendText(symbolTable);
    }

    @FXML
    void onYufa(ActionEvent event) {
        outputText_down.clear();
        str = inputText.getText();
        Cifa cifa = new Cifa();
        Yufa yufa = new Yufa();
        cifa.SortFirstChar(str);
        List<String> tokenList = cifa.getValue();
        List<String> wordList = cifa.getToken();
        List<String> errorList = yufa.getErrorList();
        tokenList.add("#");
        System.out.println(tokenList);
        yufa.parser(tokenList,wordList);
        if(errorList.isEmpty()){
            outputText_down.appendText("未发现语法错误！");
        } else {
            outputText_down.appendText("——Error——\n");
            for(int i = 0; i < errorList.size(); i++) {
                outputText_down.appendText(errorList.get(i)+"\n");
            }
        }
    }

    @FXML
    void onCifa(ActionEvent event) {
        outputText_up.clear();
        outputText_up.appendText("——Token——\n");
        str = inputText.getText();
        Cifa cifa = new Cifa();
        cifa.SortFirstChar(str);
        List<String> TokenList = cifa.getToken();
        List<String> ValueList = cifa.getValue();
        for(int i = 0; i < TokenList.size(); i++){
            outputText_up.appendText(TokenList.get(i)+"\t\t\t\t"+ValueList.get(i)+"\n");
        }
    }

}
