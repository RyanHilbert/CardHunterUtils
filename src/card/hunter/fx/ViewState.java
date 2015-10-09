package card.hunter.fx;

import java.util.HashMap;

// Represents the current state of all UI elements, for persistance between app runs
public class ViewState {
    private final HashMap<String, HashMap<String, String>> settingsByControlName = new HashMap<>();
    
    public void saveControlSettings(String controlName, HashMap<String, String> settings) {
        if (!gotKey(controlName))
            settingsByControlName.put(controlName, null);
        
        settingsByControlName.replace(controlName, settings);
    }
    
    public void saveControlSetting(String controlName, String settingName, String value) {
        if (!gotKey(controlName))
            settingsByControlName.put(controlName, new HashMap<>());
        
        settingsByControlName.get(controlName).put(settingName, value);
    }
    
    public HashMap<String, String> getControlSettings(String controlName) {
        return (gotKey(controlName) ? settingsByControlName.get(controlName) : new HashMap<>());
    }
    
    public String getControlSetting(String controlName, String settingName) {
        HashMap<String, String> settings = (gotKey(controlName) ? settingsByControlName.get(controlName) : new HashMap<>());
        return settings.get(settingName);
    }
    
    private boolean gotKey(String key) { return settingsByControlName.containsKey(key); }
    
    public static ViewState loadFrom(String savedSettings) {
        ViewState vs = new ViewState();
        
        if (savedSettings != null && !savedSettings.isEmpty()) {
            String[] lines = savedSettings.split("\n");
            String currCtrl = "";
            HashMap<String, String> settings = null;

            for (String line : lines) {
                String[] fields = line.split("\t");
                if (fields.length == 3) {
                    String ctrl = fields[0];

                    if (!currCtrl.equals(ctrl)) {
                        settings = new HashMap<>();
                        vs.saveControlSettings(ctrl, settings);
                        currCtrl = ctrl;
                    }

                    settings.put(fields[1], fields[2]);
                }
            }
        }
        
        return vs;
    }
    
    public String saveToText() {
        String output = "";
        
        for (String ctrl : settingsByControlName.keySet()) {
            HashMap<String, String> settings = settingsByControlName.get(ctrl);
            for (String setting : settings.keySet()) {
                String val = settings.get(setting);
                output += String.format("%s\t%s\t%s\n", ctrl, setting, val != null ? val : "");
            }
        }
                
        return output;
    }
}
