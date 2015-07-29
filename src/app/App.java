package app;

// Class for holding static settings / state for a running app instance
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
}