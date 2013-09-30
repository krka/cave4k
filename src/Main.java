import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferStrategy;
import java.util.Random;

public class Main extends JPanel implements KeyListener, WindowListener {
  public static final int NUM_STARS = 200;
  private static final double ACCEL = 0.00000001;
  private static final double MAX_SPEED = 3.0;
  private boolean running = true;
  private final JFrame frame;
  double x = 130;
  double y = -150;
  double angle;
  double speedX, speedY;
  private int rotateLeft;
  private int rotateRight;
  private int throttle;

  private final Polygon[] walls = {
          new Polygon(new int[]{57, 559, 559, 57}, new int[]{83, 83, 100, 100}, 4),
          new Polygon(new int[]{57, 77, 77, 57}, new int[]{112, 112, 340, 340}, 4),
          new Polygon(new int[]{85, 433, 433, 85}, new int[]{309, 309, 331, 331}, 4),
          new Polygon(new int[]{557, 582, 582, 557}, new int[]{89, 89, 834, 834}, 4),
          new Polygon(new int[]{65, 427, 427, 65}, new int[]{498, 498, 515, 515}, 4),
          new Polygon(new int[]{54, 76, 76, 54}, new int[]{346, 346, 954, 954}, 4),
          new Polygon(new int[]{242, 556, 556, 242}, new int[]{775, 775, 800, 800}, 4),
          new Polygon(new int[]{42, 573, 573, 42}, new int[]{952, 952, 960, 960}, 4),
  };
  private final Polygon[] goals = {
          new Polygon(new int[]{554, 716, 716, 554}, new int[]{840, 840, 948, 948}, 4),
  };

  public static void main(String[] args) {
    new Main().run();
  }

  private void run() {
    boolean died = false;
    boolean win = false;
    double[] starX = new double[NUM_STARS];
    double[] starY = new double[NUM_STARS];
    Random random = new Random();
    for (int i = 0; i < NUM_STARS; i++) {
      starX[i] = random.nextDouble();
      starY[i] = random.nextDouble();
    }
    long prevTime = System.nanoTime();
    long startTime = prevTime;

    long totalTime = 0;
    long ticks = 0;

    while (running) {
      long t = System.nanoTime();
      long delta = t - prevTime;
      prevTime = t;

      BufferStrategy bufferStrategy = frame.getBufferStrategy();
      Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();

      if (win) {
        angle += 3 * delta * 0.00000001;
      } else if (!died) {
        angle += (rotateLeft + rotateRight) * delta * 0.00000001;
      }
      if (!win && !died) {
        speedX += Math.sin(angle) * throttle * delta * ACCEL;
        speedY += Math.cos(angle) * throttle * delta * ACCEL;
        speedY += Math.cos(Math.PI) * delta * ACCEL / 10;
        double foo = Math.sqrt(speedX * speedX + speedY * speedY);
        if (foo > MAX_SPEED) {
            speedX = speedX * MAX_SPEED / foo;
            speedY = speedY * MAX_SPEED / foo;
        }
        x += speedX;
        y += speedY;
      } else {
        speedX = 0;
        speedY = 0;
      }
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 800, 600);

      for (int i = 0; i < NUM_STARS; i++) {
        int level = (i / (NUM_STARS / 3));
        int scale = 10 + 2*level;
        int color = 60 + level * 60;
        g.setColor(new Color(color, color, color));
        int x2 = (int) (-x * scale * 0.1 + 800 * scale * starX[i]) % 800;
        int y2 = (int) (y * scale * 0.1 + 600 * scale * starY[i]) % 600;
        x2 = (800 + x2) % 800;
        y2 = (600 + y2) % 600;
        g.fillOval(x2, y2, 1 + level, 1 + level);
      }

      g.translate(400, 300);

      g.setColor(Color.LIGHT_GRAY);
      g.translate(-x, y);
      for (Polygon p : walls) {
        g.fillPolygon(p);
      }
      g.setColor(Color.YELLOW);
      for (Polygon p : goals) {
        g.fillPolygon(p);
      }
      g.translate(x, -y);

      g.setPaint(new RadialGradientPaint(0, -10, 30, new float[]{0.2f, 0.7f, 0.8f}, new Color[]{Color.BLUE, Color.CYAN, Color.WHITE}));
      if (!died) {
        g.rotate(angle);
        Polygon ship = new Polygon(new int[]{0, 15, 0, -15, 0}, new int[]{-20, 20, 10, 20, -20}, 5);
        g.fillPolygon(ship);
        if (!win && throttle > 0) {
          int color = (int) ((System.currentTimeMillis() / 50) % 3);
          Color[] throttleColors = new Color[]{Color.RED, Color.ORANGE, Color.YELLOW};
          g.setColor(throttleColors[color]);
          g.drawPolyline(new int[]{-5, 0, 5}, new int[]{15, 25, 15}, 3);
        }
        g.rotate(-angle);
      } else {
        g.setColor(Color.RED);
        for (int i = 0; i < 3; i++) {
          int offset = (int) ((System.currentTimeMillis() / 30) % 5);
          g.drawOval(-i*10 - offset, -i*10 - offset, 2*i*10 + offset, 2*i*10 + offset);
        }
      }

      g.setColor(Color.WHITE);
      g.translate(-400, -300);

      if (!died && !win) {
        totalTime = t - startTime;
        ticks = totalTime / 100000000;
      }
      g.drawString(String.format("%.1f", ticks / 10d), 40, 40);

      Ellipse2D.Double body = new Ellipse2D.Double(-10 + x, -10 - y, 20, 20);
      for (Polygon p : walls) {
        Area areaA = new Area(body);
        areaA.intersect(new Area(p));
        if (!areaA.isEmpty()) {
            died = true;
        }
      }
      for (Polygon p : goals) {
        Area areaA = new Area(body);
        areaA.intersect(new Area(p));
        if (!areaA.isEmpty()) {
          win = true;
        }
      }

      g.dispose();
      bufferStrategy.show();
      Toolkit.getDefaultToolkit().sync();
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
    }

    frame.dispose();
  }

  public Main() throws HeadlessException {
    frame = new JFrame("foo");
    frame.addWindowListener(this);
    frame.setSize(800, 600);
    frame.setVisible(true);
    frame.createBufferStrategy(2);
    frame.add(this);
    frame.addKeyListener(this);
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
      rotateLeft = -1;
    }
    if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
      rotateRight = 1;
    }
    if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
      throttle = 1;
    }
    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
      running = false;
    }
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
      rotateLeft = 0;
    }
    if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
      rotateRight = 0;
    }
    if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
      throttle = 0;
    }
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
