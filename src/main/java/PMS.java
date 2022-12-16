package main.java;

import main.java.controller.FrameController;
import main.java.controller.IController;
import main.java.controller.IFrameController;
import main.java.controller.TextController;
import main.java.model.AlphaVantageImpl;
import main.java.model.DataIO;
import main.java.model.DataIOImpl;
import main.java.model.IModel;
import main.java.model.Model;
import main.java.model.PriceProvider;
import main.java.view.IFrameView;
import main.java.view.IView;
import main.java.view.MainMenuView;
import main.java.view.TextView;

/**
 * This class represents the main class of the program. It has the main method which initialises the
 * model,view and controller and delegates the flow to controller.
 */
public class PMS {

  /**
   * Main method to start the program.
   *
   * @param args command line args that needs to be passed to the program.
   */
  public static void main(String[] args) {
    DataIO dataIO = new DataIOImpl();
    PriceProvider priceProvider = new AlphaVantageImpl();
    IModel iModel = new Model(dataIO, priceProvider);
    if (args.length > 0 && args[0].equalsIgnoreCase("text")) {
      IView iView = new TextView(System.out);
      IController iController = new TextController(iModel, iView, System.in);
      iController.start();
    } else if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
      IFrameController iController = new FrameController(iModel);
      IFrameView iView = new MainMenuView("Portfolio Management System", iController);
      iController.setView(iView);
    } else {
      System.out.println("Please a valid argument.\n For GUI the argument must be"
              + " \"gui\" \n For text based UI the argument must be \"text\"");
    }
  }
}