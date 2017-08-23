/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jcodeit;
/*

* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class FXMLDocumentController {
    private RSyntaxTextArea codeArea;
    
    private Tab codes;

    @FXML
    private Menu mfile;

    @FXML
    private MenuItem miAbout;

    @FXML
    private TabPane codeTabs;

    @FXML
    private MenuItem miCompile;

    @FXML
    private MenuItem miOpen;

    @FXML
    private MenuItem miExit;

    @FXML
    private MenuItem miNew;
    
    @FXML
    private MenuItem jDocs;

    @FXML
    private ListView<String> flist;

    private LinkedList <File> prev;
    
    @FXML
    private TabPane LogTabs;

    @FXML
    private MenuBar mb;

    @FXML
    private MenuItem miSaveAs;

    @FXML
    private MenuItem miSave;

    @FXML
    private MenuItem miRemove;

    @FXML
    private Menu mEdit;

    @FXML
    private MenuItem miRemoveAll;

    @FXML
    private Menu mExecute;
    
    @FXML
    private MenuItem miRun;
    
    private File f;
    
    boolean saved;
    
    private Map <Tab , File > codelist;
    
    private String inputcode;
    
    int currentTabid;
    String red = "-fx-background-color: #ff0000;";
    String green = "-fx-background-color: #00ff00;";
    void LoadListView(){
        if(prev == null){
            return ;
        }
        for(File r : prev){
                if(r.getName().endsWith(".html"))
                    openWebTab(r);
                else
                    LoadTab(r,1);
        }
    }
    boolean LoadTab(File topen,int status){
        codes = new Tab(topen.getName());   
        codes.setStyle(red);
        codeArea = new RSyntaxTextArea();
        codeArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        String name = topen.getName(),lang="",runCommand;
        try{
            lang = name.substring(name.lastIndexOf("."),name.length());
        }
        catch(Exception e){
        }
        switch(lang){		
            case ".c":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);break;
            case ".cpp":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);break;
            default :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);break;
            case ".py" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);break;
            case ".css" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);break;
            case ".html" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);break;
            case ".xml":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
            case ".txt":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
            case ".sql":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);break;
            case ".fxml":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
        }
        codeArea.setCodeFoldingEnabled(true);
        RTextScrollPane sp = new RTextScrollPane(codeArea);
        SwingNode sn=new SwingNode();
        sn.setContent(sp);
        codes.setContent(sn);
        codeArea.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent ke){
                String text =  ""+ke.getKeyChar();
                if (ke.isAltDown()||ke.isControlDown()||ke.isShiftDown()) {
                    text= "";
                }
                if (ke.getKeyCode() == KeyEvent.VK_RIGHT ) {
                        text = "";
                } else if (ke.getKeyCode() == KeyEvent.VK_LEFT ) {
                        text = "";
                } else if (ke.getKeyCode() == KeyEvent.VK_UP ) {
                        text = "";
                } else if (ke.getKeyCode() == KeyEvent.VK_DOWN ) {
                        text = "";
                }
                else if (ke.isMetaDown()) {
                    text= "meta";
                }
                if(saved ==true &&"".equals(text)){
                    saved= true;
                }
                else
                saved = inputcode!=null&&codeArea!=null&&inputcode.equals(codeArea.getText())&&"".equals(text);
            }

            @Override
            public void keyReleased(KeyEvent event) {
                if(saved!=true){
                    codes = codeTabs.getSelectionModel().getSelectedItem();
                    codes.setStyle(red);
                }
                else{
                    codes = codeTabs.getSelectionModel().getSelectedItem();
                    codes.setStyle(green);
                }
            }
        });
        codeTabs.getTabs().add(codes);
        codelist.put(codes, topen);
        codeTabs.getSelectionModel().select(codes);
        
        if(!topen.exists())
            return false;
        
        String data = "";
        FileReader dis =null;
        try{
            dis = new FileReader(topen);
        }
        catch(FileNotFoundException k){
        }
        int re=0;

        codes.setText(topen.getName());

        while(true){
            try{
                re=dis.read();
                if(re==-1)
                    break;
                data+=(char)re;
            }
            catch(IOException k){
                break;
            }
        }
        try{
            dis.close();
        }
        catch(IOException k){
        }
        if(status==1){
            if(flist.getItems().size()==20){
                flist.getItems().remove(0);
            }
            flist.getItems().add(topen.getName());
        }
        inputcode = data;
        codeArea.setText(""+data);
        codeTabs.getSelectionModel().select(codes);
        
        return true;
    }
    public void initialize(){
        codelist = new HashMap<Tab,File>();
        miNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        miOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        miExit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        miSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        miSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN));
        miRemove.setAccelerator(new KeyCodeCombination(KeyCode.D,KeyCombination.CONTROL_DOWN));
        miRemoveAll.setAccelerator(new KeyCodeCombination(KeyCode.E,KeyCombination.CONTROL_DOWN));
        miCompile.setAccelerator(new KeyCodeCombination(KeyCode.K,KeyCombination.CONTROL_DOWN));
        miRun.setAccelerator(new KeyCodeCombination(KeyCode.L,KeyCombination.CONTROL_DOWN));
        
        File cache = new File("Compiler.temp");
        if(!cache.exists()){
            prev = new LinkedList<File>();
        }
        else{
            FileInputStream fis;
            ObjectInputStream ois;
            try {
                fis = new FileInputStream(cache);
                ois = new ObjectInputStream(fis);
                prev = (LinkedList<File>) ois.readObject();
                LoadListView();
                fis.close();
                ois.close();
            } catch (FileNotFoundException fnf) {
            } catch (IOException | ClassNotFoundException ex) {
            }
        }
        flist.setOnMouseClicked(new EventHandler <MouseEvent>(){
            @Override
            public void handle(MouseEvent click) {
                if(flist.getSelectionModel().getSelectedIndex()==-1){
                    
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select any file from list !");

                    alert.showAndWait();
                    return ;
                }
                if(click.getClickCount()>=1){
                    int flag = 0;File tmp = null;
                    for(File  s : prev){
                        if(s.getName().equals(flist.getSelectionModel().getSelectedItem()))
                            tmp = s;
                    }
                    for(Tab key : codeTabs.getTabs()){
                        if(codelist.get(key)==tmp){
                            codeTabs.getSelectionModel().select(key);
                            flag = 1;
                        }
                    }
                    if(flag==0){
                        f = tmp;
                        if(f.getPath().endsWith(".html"))
                            openWebTab(f);
                        else
                            LoadTab(f,1);
                    }
                }
            }    
        });
    }
    void writeprev() {
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            fos = new FileOutputStream("Compiler.temp");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(prev);
            oos.close();
            fos.close();
        } catch (IOException e) {
        }
    }
    
    @FXML
    private void actionPerformed(ActionEvent event){
        MenuItem mi = (MenuItem) event.getSource();
        
        if(mi == miExit)
            System.exit(0);
        else if(mi == miNew){
            int numTabs = codeTabs.getTabs().size();
            saved = LoadTab(new File("untitled "+numTabs),0);
            codes.setStyle(red);
            System.out.println("From MINew");
            writeprev();
        }
        else if(mi == miOpen){
            f = null;
            saved = true;
            String data="";
            File file = null;
            FileChooser fileChooser = new FileChooser();
            f = fileChooser.showOpenDialog(null);
          
            if (f!=null) {
                String fname = f.getName();
                if(!(fname.endsWith(".c")||fname.endsWith(".C")||fname.endsWith(".cpp")||fname.endsWith(".CPP")||fname.endsWith(".py")||fname.endsWith(".java")||fname.endsWith(".xml")||fname.endsWith(".fxml")||fname.endsWith(".html")||fname.endsWith(".sql")||fname.endsWith(".css")||fname.endsWith(".js")))
                {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Operation Failed !");

                    alert.showAndWait();
                   // JOptionPane.showMessageDialog(null,""+"\t <File Open> \n\t\n",null, JOptionPane.INFORMATION_MESSAGE, null);  
                    return ;
                }
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("File Opened SuccessFully !");

                alert.showAndWait();
               // JOptionPane.showMessageDialog(null,""+"File Opened SuccessFully !\n" + file.getName(),null, JOptionPane.INFORMATION_MESSAGE, null);    
            }
             else{
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("<File Open> \n\tOperation Failed !");

                alert.showAndWait(); 
               // JOptionPane.showMessageDialog(null,""+"\t <File Open> \n\tOperation Failed !\n",null, JOptionPane.INFORMATION_MESSAGE, null);  
                 return ;
            }
            if(codelist.containsValue(f)){
                codeTabs.getSelectionModel().select(codes);
                return ;
            }
            if(prev!=null&&!prev.contains(f)){
                prev.add(f);
                System.out.println("From MIOpen");
            }
            LoadTab(f,1);
            writeprev();
        }
        else if(mi == miSave){
            
            codes = codeTabs.getSelectionModel().getSelectedItem();
            
            try{
                codeArea = (RSyntaxTextArea) ((RTextScrollPane) ((SwingNode)codes.getContent()).getContent()).getTextArea();
            }
            catch(ClassCastException cce){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Caution");
                alert.setContentText("Wrong file!");
                alert.showAndWait();
                return ;
            }
            
            f = codelist.get(codes);
            File tmp = f;
            if(!f.exists()){
                FileChooser fileChooser = new FileChooser();
                f = fileChooser.showSaveDialog(null);
                    if (f!=null) {
                            saved = true;
                            codes.setStyle(green);

                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Information Dialog");
                            alert.setHeaderText(null);
                            alert.setContentText("File Saved SuccessFully !\n");

                            alert.showAndWait();
                            //JOptionPane.showMessageDialog(null,""+"File Saved SuccessFully !\n" + f.getName(),null, JOptionPane.INFORMATION_MESSAGE);
                    }
                     else{
                        Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("Information Dialog");
                            alert.setHeaderText(null);
                            alert.setContentText(" <File SAVE> Operation Failed !\n");

                            alert.showAndWait();
                             
                        //JOptionPane.showMessageDialog(null,""+" <File SAVE> Operation Failed !\n",null, JOptionPane.INFORMATION_MESSAGE);  
                         return ;
                    }
                 
            String name =f.getName(),lang="",runCommand;
            try{
                lang = name.substring(name.lastIndexOf("."),name.length());
            }
            catch(Exception e){
            }
            switch(lang){		
                    case ".c":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);break;
                    case ".cpp":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);break;
                    case ".java":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);break;
                    case ".py" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);break;
                    case ".css" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);break;
                    case ".html" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);break;
                    case ".xml":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
                    case ".txt":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
                    case ".sql":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);break;
                    case ".fxml":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
                    default :;
            }
                codelist.remove(codes,tmp);
                codelist.put(codes, f);
                codes.setText(f.getName());
                if(flist.getItems().size()==20){
                    flist.getItems().remove(0);
                }
                flist.getItems().add(f.getName());
            }    
            try{
                codes.setStyle(green);
                FileWriter fw = new FileWriter(f);
                inputcode = codeArea.getText();
                fw.write(inputcode);
                fw.close();
            }
            catch(IOException io){
            }
            
            if(!prev.contains(f)){
                prev.add(f);
                System.out.println("From MISave");
            }
            writeprev();
            codeTabs.getSelectionModel().select(codes);
        }
        else if(mi == miAbout){
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Look, an javafx is so good !");
            alert.setContentText("This Application is made by \n\tOnkar J. Sathe \n\tUsing JavaFX");

            alert.showAndWait();
            //JOptionPane.showMessageDialog(null,"This Application is made by \n\tOnkar J. Sathe \n\tUsing JavaFX");
        
        }
        else if(mi == miSaveAs){
            codes = codeTabs.getSelectionModel().getSelectedItem();
            try{
                codeArea = (RSyntaxTextArea) ((RTextScrollPane) ((SwingNode)codes.getContent()).getContent()).getTextArea();
            }
            catch(ClassCastException cce){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Caution");
                alert.setContentText("Wrong file!");
                alert.showAndWait();
                return ;
            }
            f = codelist.get(codes);
            File tmp = f;
            FileChooser fileChooser = new FileChooser();
            f = fileChooser.showSaveDialog(null);
            if (f!=null) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("File Saved SuccessFully !\n");

                    alert.showAndWait();
                    //JOptionPane.showMessageDialog(null,""+"File Saved SuccessFully !\n" + f.getName(),null, JOptionPane.INFORMATION_MESSAGE);
                    saved = true;
                    codes.setStyle(green);
            }
             else{
                Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText(" <File SAVE> Operation Failed !\n");

                    alert.showAndWait();

                //JOptionPane.showMessageDialog(null,""+" <File SAVE> Operation Failed !\n",null, JOptionPane.INFORMATION_MESSAGE);  
                 return ;
            }    
            String name =f.getName(),lang="",runCommand;
            try{
                lang = name.substring(name.lastIndexOf("."),name.length());
            }
            catch(Exception e){
            }
            switch(lang){		
                    case ".c":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);break;
                    case ".cpp":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);break;
                    case ".java":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);break;
                    case ".py" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);break;
                    case ".css" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);break;
                    case ".html" :codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);break;
                    case ".xml":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
                    case ".txt":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
                    case ".sql":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);break;
                    case ".fxml":codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);break;
                    default :;
            }
            codes.setText(f.getName());
            for(String s : flist.getItems()){
                if(s.equals(tmp.getName())){
                    flist.getItems().remove(s);
                    break;
                }
            }
            prev.remove(tmp);
            codelist.remove(codes,tmp);
            codelist.put(codes, f);
            try{
                codes.setStyle(green);
                FileWriter fw = new FileWriter(f);
                inputcode = codeArea.getText();
                fw.write(inputcode);
                fw.close();
            }
            catch(IOException io){
            }
            
            if(!prev.contains(f)){
                prev.add(f);
                
                System.out.println("From MISaveAs");
            }
            flist.getItems().add(f.getName());
            writeprev();
            codeTabs.getSelectionModel().select(codes);
        }
        else if(mi == miCompile){
            if(saved==false){
                  Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText(" Please save File First !\n");

                    alert.showAndWait();
                    return ;
               
            }
            codes = codeTabs.getSelectionModel().getSelectedItem();
            try{
                codeArea = (RSyntaxTextArea) ((RTextScrollPane) ((SwingNode)codes.getContent()).getContent()).getTextArea();
            }
            catch(ClassCastException cce){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Caution");
                alert.setContentText("Wrong file!");
                alert.showAndWait();
                return ;
            }
            f = codelist.get(codes);
            
            System.out.println("File Path : "+f.getPath() ); 

            System.out.print("\nCompilation Results : ");
            int flag = 0;
            ProcessBuilder compile;
            String name = f.getName(),lang="",runCommand;
            try{
                lang = name.substring(name.lastIndexOf("."),name.length());
            }
            catch(StringIndexOutOfBoundsException sioobe){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("Language not installed");
                alert.setContentText("Sorry File Format Incorrect");

                alert.showAndWait();
                //JOptionPane.showMessageDialog(null, "Sorry File Format Incorrect", "Warning", JOptionPane.WARNING_MESSAGE);
                return ;
            }
            System.out.println(name+" " +lang);
            
            switch(lang){		
                case ".c": 	runCommand ="gcc";					break;
                case ".cpp":	runCommand ="g++";					break;
                case ".java":	runCommand ="javac";					break;
                default : return;
            }
            
            compile = new ProcessBuilder(runCommand,f.getPath());
            compile.redirectErrorStream(true);
            Process p =null;
            try{
                    p= compile.start();
            }
            catch(IOException k){
            }
            
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String l="",Collect= "";
            while(true){
                try{
                    l = r.readLine();
                }
                catch(IOException k){
                }
                if(l == null)break;
                
                flag = 1;	
                Collect+="\n"+l;
            }
            if(flag==0){
                Collect="Compilation results...\n---------------------------\n- Errors: 0\n- Warnings: 0\n- Output Filename: "+f.getPath();
                miRun.setDisable(false);
            }
            else 
                miRun.setDisable(true);
            produceLogs(Collect,"Compilation Log");
            codeTabs.getSelectionModel().select(codes);
        }
        else if(mi ==miRun){
            if(saved==false){
                  Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText(" Please save File First !\n");

                    alert.showAndWait();
                    return ;
               
            }
            codes = codeTabs.getSelectionModel().getSelectedItem();
            try{
                codeArea = (RSyntaxTextArea) ((RTextScrollPane) ((SwingNode)codes.getContent()).getContent()).getTextArea();
            }
            catch(ClassCastException cce){
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Caution");
                alert.setContentText("Wrong file!");
                alert.showAndWait();
                return ;
            }
            f = codelist.get(codes);
            
            System.out.println("\nBuild Results : ");
            String seP = f.getPath();
            System.out.println(seP);
            while(seP.charAt(seP.length()-1)!='\\'){
                    seP = seP.substring(0,seP.length()-1);
            } 
            seP = seP.substring(0,seP.length()-1);
            System.out.println(seP);
            
            String ClassName = "",FileName = f.getName();
            int i = 0;
            while(i<FileName.length()){
                if(FileName.charAt(i)=='.')
                    break;
                ClassName +=""+ FileName.charAt(i);
                    i++;
            }
            System.out.println(FileName+" "+ClassName);
            File run = new File("Run.bat");			
            String name = f.getName(),lang="";
            try{
                lang = name.substring(name.lastIndexOf("."),name.length());
            }
            catch(StringIndexOutOfBoundsException sioobe){
                JOptionPane.showMessageDialog(null, "Sorry File Format Incorrect", "Warning", JOptionPane.WARNING_MESSAGE);
                return ;
            }
            System.out.println(name+" " +lang);
            
            try{
                FileWriter fwrite = new FileWriter(run);
                
                switch(lang){		
                    case ".c":      fwrite.write("a.exe"+" \npause "+" \nexit");    					break;
                    case ".cpp":    fwrite.write("a.exe"+" \npause "+" \nexit");					break;
                    case ".java":   fwrite.write("java "+"-cp "+seP+" "+ClassName+" \npause "+" \nexit");                       break;
                    case ".py" :   LogTabs.getTabs().remove(0, LogTabs.getTabs().size()); fwrite.write("python "+f.getAbsolutePath()+" \npause "+" \nexit");                                break;
                    default : return;
                }
                fwrite.close();
                Runtime.getRuntime().exec("cmd.exe /c start Run.bat");
                produceLogs("Build SuccessFul","Build Log");
                run.deleteOnExit();
            }
            catch(IOException e1){}
            
            codeTabs.getSelectionModel().select(codes);
        }
        else if(mi == miRemove){
            int g = flist.getSelectionModel().getSelectedIndex();
            if(g == -1){
                JOptionPane.showMessageDialog(null, "Please select any file from list !");
                return ;
            }
            for(Tab  t: codeTabs.getTabs()){
                if(codelist.get(t).getName().equals(flist.getSelectionModel().getSelectedItem()))
                {
                    codeTabs.getTabs().remove(t);
                    prev.remove(codelist.get(t));
                    codelist.remove(t);
                    writeprev();
                    break;
                }
            }
            flist.getItems().remove(g); 
        }
        else if(mi == miRemoveAll){
            flist.getItems().remove(0, flist.getItems().size());
            codelist.clear();
            codeTabs.getTabs().remove(0, codeTabs.getTabs().size());
            prev.clear();
            writeprev();
        }
        else if(mi == jDocs){
            TextInputDialog dialog = new TextInputDialog("C:\\...");
            dialog.setTitle("Help");
            dialog.setHeaderText("Java Documentation Path");
            dialog.setContentText("Enter the java-docs index.html file path : ");

            // Traditional way to get the response value.
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                System.out.println("Your name: " + result.get());
            }
            // The Java 8 way to get the response value (with lambda expression).
            result.ifPresent(name -> System.out.println("Your name: " + name));
            File fr = new File(result.get());
            openWebTab(fr);
            prev.add(fr);
        }
    }
    void openWebTab(File f){
        WebView wb = new WebView();
        WebEngine e = wb.getEngine();
        e.load(f.getPath());
        Tab help = new Tab();  
        help.setText("Java Docs");
        help.setContent(wb);
        help.setStyle("-fx-background-color: #ffff4d;");
        codeTabs.getTabs().add(help);
        codelist.put(help,f);
        flist.getItems().add(f.getName());
        codeTabs.getSelectionModel().select(help);
        writeprev();
    }
    void produceLogs(String log,String a){
        int numTabs = LogTabs.getTabs().size();
        if(a.contains("Compilation")){
            LogTabs.getTabs().remove(0, numTabs);
        }
        Tab Build = new Tab(a);
        Build.setClosable(true);
        TextArea t = new TextArea();
        t.setEditable(false);
        t.setText(log);
        Build.setContent(t);
        LogTabs.getTabs().add(Build);
        LogTabs.getSelectionModel().select(Build);
    }
}