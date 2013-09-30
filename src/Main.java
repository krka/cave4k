import sun.awt.X11GraphicsConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

public class Main extends JPanel implements KeyListener, WindowListener {
  private boolean running = true;
  private boolean pressed;
  private final JFrame frame;
  double x;

  public static void main(String[] args) {
    new Main().run();
  }

  private void run() {
    long prevTime = System.nanoTime();
    while (running) {
      long t = System.nanoTime();
      long delta = t - prevTime;
      prevTime = t;

      BufferStrategy bufferStrategy = frame.getBufferStrategy();
      Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
      x += delta * 0.0000001;
      if (pressed) x += delta * 0.0000001;
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 800, 600);
      g.setColor(Color.BLUE);
      g.fillRect((int) (100 + (x % 100)), (int) (100 + (x % 100)), 200, 200);
      g.dispose();
      bufferStrategy.show();
      Toolkit.getDefaultToolkit().sync();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
    }
  }

  public Main() throws HeadlessException {
    frame = new JFrame("foo");
    frame.addWindowListener(this);
    frame.setSize(800, 600);
    frame.setVisible(true);
    frame.createBufferStrategy(2);
    frame.add(this);
    addKeyListener(this);
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    pressed = true;
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    pressed = false;
  }

  @Override
  public void windowOpened(WindowEvent windowEvent) {
  }

  @Override
  public void windowClosing(WindowEvent windowEvent) {
    running = false;
  }

  @Override
  public void windowClosed(WindowEvent windowEvent) {
  }

  @Override
  public void windowIconified(WindowEvent windowEvent) {
  }

  @Override
  public void windowDeiconified(WindowEvent windowEvent) {
  }

  @Override
  public void windowActivated(WindowEvent windowEvent) {
  }

  @Override
  public void windowDeactivated(WindowEvent windowEvent) {
  }
}
