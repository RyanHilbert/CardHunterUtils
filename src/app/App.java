package app;

// Class for holding static settings / state for a running app instance

import java.util.ArrayList;
import ui.IPersistViewState;
import ui.ViewState;

public class App {
  // <editor-fold defaultstate="collapsed" desc="Singleton implementation">
  private static App instance = null;
  
  private static App get() {
    if (instance == null)
      instance = new App();

      return instance;
  }
  
  public static App state() {
      return get();
  }
  // </editor-fold>
  
  public boolean compareCardArt = false;
  public boolean openExternally = false;
  
  public ViewState viewState = new ViewState();
  
  private final ArrayList<IPersistViewState> persistables = new ArrayList<>();
  
  public void register(IPersistViewState control) {
      persistables.add(control);
  }
  
  public void updateViewState() {
      for (IPersistViewState c : persistables)
          c.updateViewState();
  }
  
  public void refresh() {
      for (IPersistViewState c : persistables)
          c.refresh();
  }
}