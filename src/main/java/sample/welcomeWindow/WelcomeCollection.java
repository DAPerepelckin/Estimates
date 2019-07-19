package sample.welcomeWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class WelcomeCollection {
    private ObservableList<WelcomeRow> rowList = FXCollections.observableArrayList();

    void add(WelcomeRow row){rowList.add(0,row);}

    void clear(){rowList.clear();}

    ObservableList<WelcomeRow> getRowList() {return rowList;}

}
