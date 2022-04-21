package uk.ac.shef.uniManager.utils;

import com.vaadin.flow.component.combobox.ComboBox;
import uk.ac.shef.uniManager.DAO.DegDAO;
import uk.ac.shef.uniManager.model.Degree;
import uk.ac.shef.uniManager.model.User;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static boolean isEmpty(String str) {
        if(str==null || "".equals(str.trim())) {
            return true;
        }else {
            return false;
        }

    }
    public static boolean isNotEmpty(String str) {
        if(str==null || "".equals(str.trim())) {
            return false;
        }else {
            return true;
        }}

    public static ComboBox<String> fillComboBoxByList(String name, List<String> list) {
        ComboBox<String> result = new ComboBox<>(name);
        result.setAllowCustomValue(true);
        result.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            list.add(customValue);
            result.setItems(list);
            result.setValue(customValue);
        });
        result.setItems(list);
        return result;
    }
}
