package at.ac.uni_linz.tk.vchat;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import at.ac.uni_linz.tk.vchat.engine.*;


/**
 * Displayes the User's three-dimensional view on the Room, other Users and their
 * messages.
 *
 * @author      Arno Huetter
 * (C)opyright 1997/98 by the Institute for Computer Science, Telecooperation Department, University of Linz
 */

public class ViewCanvas extends Canvas implements Runnable {

  private static final int ROOM_HEIGHT = 30;
  private static final double SPACE_BUFFER = 7.5;
  private static final double GRID_SIZE = 7.5;
  private static final int TILES_X = (int)((ChatRepository.ROOM_DIMENSION.width + 2 * GRID_SIZE) / (GRID_SIZE));
  private static final int TILES_Y = (int)((ChatRepository.ROOM_DIMENSION.height + 2 * GRID_SIZE) / (GRID_SIZE));

  private Image bufferImage;
  private Image worldImage;

  private ChatApplet chatApplet;

  private Object3D world;
  private Light light;
  private Plane3D frustum[];

  private Texture3D floorTexture;
  private Texture3D wallTexture[] = new Texture3D[4];
  private Texture3D frameTexture[] = new Texture3D[2];
  private Texture3D tileTexture[][] = new Texture3D[TILES_X][TILES_Y];
  private Polygon3D floor;

  private int lastUserId = -1;
  private int lastRoomId = -1;
  private int lastXPos = -1;
  private int lastYPos = -1;
  private int lastHeading = -1;
  private int lastWidth = -1;
  private int lastHeight = -1;

  private Object3D clippedworld = null;

/**
 * Constructs the ViewCanvas.
 *
 * @param chatParam      the ChatApplet which administrates the
 *                                Users
 * @param repositoryParam         the ChatRepository, where commonly used objects
 *                                are being stored
 */

  public ViewCanvas(ChatApplet chatAppletParam) {
    boolean toggle = true;
    chatApplet = chatAppletParam;

    world = new Object3D();
    light = new Light();
    frustum = new Plane3D[5];

    Vertex3D avertex[] = new Vertex3D[4];

    frustum[0] = new Plane3D();
    frustum[0].normVec = new Vertex3D(0.0D, 0.0D, 1.0D);
    frustum[0].dist = 2D;
    /*
    frustum[0] = new Plane3D();
    frustum[0].normVec = new Vertex3D(0.0D, -Math.sqrt(2D), Math.sqrt(2D));
    frustum[0].dist = 0.0D;
    frustum[1] = new Plane3D();
    frustum[1].normVec = new Vertex3D(-Math.sqrt(2D), 0.0D, Math.sqrt(2D));
    frustum[1].dist = 0.0D;
    frustum[2] = new Plane3D();
    frustum[2].normVec = new Vertex3D(Math.sqrt(2D), 0.0D, Math.sqrt(2D));
    frustum[2].dist = 0.0D;
    frustum[3] = new Plane3D();
    frustum[3].normVec = new Vertex3D(0.0D, Math.sqrt(2D), Math.sqrt(2D));
    frustum[3].dist = 0.0D;
    frustum[4] = new Plane3D();
    frustum[4].normVec = new Vertex3D(0.0D, 0.0D, 1.0D);
    frustum[4].dist = 2D;
    */

    light = new Light(-2, 2, -2);

    floor = new Polygon3D();
    floor.addPoint(new Vertex3D(-SPACE_BUFFER, 0, -SPACE_BUFFER));
    floor.addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0, -SPACE_BUFFER));
    floor.addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    floor.addPoint(new Vertex3D(-SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    floor.normVec = new Vertex3D(0, 1, 0);
    floor.col=Color.white;
    world.add(floor);

    double i = -SPACE_BUFFER;
    double j = -SPACE_BUFFER;

    for (int x = 0; x < TILES_X; x++) {
      j = -SPACE_BUFFER;
      for (int y = 0; y < TILES_Y; y++) {
          if (toggle) {
            tileTexture[x][y] = new Texture3D();
            tileTexture[x][y].addPoint(new Vertex3D(i, Double.MIN_VALUE, j));
            tileTexture[x][y].addPoint(new Vertex3D(i + GRID_SIZE, Double.MIN_VALUE, j));
            tileTexture[x][y].addPoint(new Vertex3D(i + GRID_SIZE, Double.MIN_VALUE, j + GRID_SIZE));
            tileTexture[x][y].addPoint(new Vertex3D(i, Double.MIN_VALUE, j + GRID_SIZE));
            tileTexture[x][y].normVec = new Vertex3D(0, 1, 0);
            world.add(tileTexture[x][y]);
          }
          toggle = !toggle;
          j += GRID_SIZE;
      }
      i += GRID_SIZE;
      if (TILES_X % 2 == 0) {
        toggle = !toggle;
      }
    }

    world.add(new Cube3D(new Vertex3D(-SPACE_BUFFER, 0, -SPACE_BUFFER), new Vertex3D(0, ROOM_HEIGHT, 0), Color.red));
    world.add(new Cube3D(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER), new Vertex3D(ChatRepository.ROOM_DIMENSION.width, ROOM_HEIGHT, ChatRepository.ROOM_DIMENSION.height), Color.blue));
    world.add(new Cube3D(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0, -SPACE_BUFFER), new Vertex3D(ChatRepository.ROOM_DIMENSION.width, ROOM_HEIGHT, 0), Color.yellow));
    world.add(new Cube3D(new Vertex3D(-SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER), new Vertex3D(0, ROOM_HEIGHT, ChatRepository.ROOM_DIMENSION.height), Color.green));

    wallTexture[0] = new Texture3D();
    wallTexture[0].addPoint(new Vertex3D(-SPACE_BUFFER, ROOM_HEIGHT, -SPACE_BUFFER));
    wallTexture[0].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, ROOM_HEIGHT, -SPACE_BUFFER));
    wallTexture[0].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0, -SPACE_BUFFER));
    wallTexture[0].addPoint(new Vertex3D(-SPACE_BUFFER, 0, -SPACE_BUFFER));
    wallTexture[0].normVec = new Vertex3D(0, 0, 1);
    world.add(wallTexture[0]);

    wallTexture[1] = new Texture3D();
    wallTexture[1].addPoint(new Vertex3D(-SPACE_BUFFER, ROOM_HEIGHT, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[1].addPoint(new Vertex3D(-SPACE_BUFFER, ROOM_HEIGHT, -SPACE_BUFFER));
    wallTexture[1].addPoint(new Vertex3D(-SPACE_BUFFER, 0, -SPACE_BUFFER));
    wallTexture[1].addPoint(new Vertex3D(-SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[1].normVec = new Vertex3D(1, 0, 0);
    world.add(wallTexture[1]);

    wallTexture[2] = new Texture3D();
    wallTexture[2].addPoint(new Vertex3D(-SPACE_BUFFER, ROOM_HEIGHT, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[2].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width, ROOM_HEIGHT, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[2].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[2].addPoint(new Vertex3D(-SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[2].normVec = new Vertex3D(0, 0, -1);
    world.add(wallTexture[2]);

    wallTexture[3] = new Texture3D();
    wallTexture[3].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, ROOM_HEIGHT, -SPACE_BUFFER));
    wallTexture[3].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, ROOM_HEIGHT, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[3].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER));
    wallTexture[3].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width + SPACE_BUFFER, 0,  -SPACE_BUFFER));
    wallTexture[3].normVec = new Vertex3D(-1, 0, 0);
    world.add(wallTexture[3]);

  }

  public Dimension getPreferredSize() {
    if (getParent() == null) {
      return super.getPreferredSize();
    }
    else {
      return new Dimension(getParent().getSize().width - getParent().getInsets().left - getParent().getInsets().right, getParent().getSize().height - getParent().getInsets().top - getParent().getInsets().bottom);
    }
  }


/**
 * Updates the ViewCanvas. Clearing the ViewCanvas is not necessary, as it uses
 * double-buffering.
 *
 * @param g       the graphics context
 */

  public void update(Graphics g) {
    paint(g);
  }

/**
 * Paints the ViewCanvas.
 *
 * @param g       the graphics context
 */

  public synchronized void paint(Graphics g) {
    // sync is important here!

    Graphics bufferGraphics;
    Graphics worldGraphics;
    Image image, userBackPortrait;

    Hashtable userTable;
    User user, currentUser;
    Vector vecCurrentSituation;
    int distance, angle;
    int canvasWidth, canvasHeight;

    Point pntPosition;

    HistoryEntry histEntry;

    if (lastRoomId != chatApplet.getCurrentRoomId()) {
        int width;
        boolean isRedirectionRoom = chatApplet.getCurrentRoom().getName().equals(chatApplet.getParameter("Room"));
        Texture3D.TextureBuffer wallBuf1, wallBuf2;
        Texture3D.TextureBuffer frameBuf1, frameBuf2;
        Texture3D.TextureBuffer tileBuf1, tileBuf2;
        if (chatApplet.getCurrentRoomId() == 0) {
            wallBuf1 = new Texture3D.TextureBuffer(chatApplet.getImage("wall1.jpg", true));
            wallBuf2 = new Texture3D.TextureBuffer(chatApplet.getImage("wall1.jpg", true));

            frameBuf1 = new Texture3D.TextureBuffer(chatApplet.getImage("frame1.jpg", true));
            frameBuf2 = new Texture3D.TextureBuffer(chatApplet.getImage("frame2.jpg", true));

            tileBuf1 = new Texture3D.TextureBuffer(chatApplet.getImage("tile1.jpg", true));
            tileBuf2 = new Texture3D.TextureBuffer(chatApplet.getImage("tile1.jpg", true));

        }
        else {
            wallBuf1 = new Texture3D.TextureBuffer(chatApplet.getImage(isRedirectionRoom && chatApplet.getParameter("Wall[1]") != null ? chatApplet.getParameter("Wall[1]") : "wall1.jpg", true));
            wallBuf2 = new Texture3D.TextureBuffer(chatApplet.getImage(isRedirectionRoom && chatApplet.getParameter("Wall[1]") != null ? chatApplet.getParameter("Wall[1]") : "wall1.jpg", true));
            frameBuf1 = new Texture3D.TextureBuffer(chatApplet.getImage(isRedirectionRoom && chatApplet.getParameter("Frame[1]") != null ? chatApplet.getParameter("Frame[1]") : "frame3.jpg", true));
            frameBuf2 = new Texture3D.TextureBuffer(chatApplet.getImage(isRedirectionRoom && chatApplet.getParameter("Frame[2]") != null ? chatApplet.getParameter("Frame[2]") : "frame3.jpg", true));
            tileBuf1 = new Texture3D.TextureBuffer(chatApplet.getImage(isRedirectionRoom && chatApplet.getParameter("Tile[1]") != null ? chatApplet.getParameter("Tile[1]") : "tile4.jpg", true));
            tileBuf2 = new Texture3D.TextureBuffer(chatApplet.getImage(isRedirectionRoom && chatApplet.getParameter("Tile[1]") != null ? chatApplet.getParameter("Tile[1]") : "tile4.jpg", true));
        }

        int pix = tileBuf1.buffer[0];
        floor.col = new Color((pix >> 16) & 0xff, (pix >> 8) & 0xff, pix & 0xff);

        width = frameBuf1.width * ROOM_HEIGHT / 2 / frameBuf1.height;
        if (frameTexture[0] != null) {
            world.remove(frameTexture[0]);
        }
        frameTexture[0] = new Texture3D(frameBuf1);
        frameTexture[0].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 - width / 2, ROOM_HEIGHT * 0.70, -SPACE_BUFFER + 1));
        frameTexture[0].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 + width / 2, ROOM_HEIGHT * 0.70, -SPACE_BUFFER + 1));
        frameTexture[0].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 + width / 2, ROOM_HEIGHT * 0.20, -SPACE_BUFFER + 1));
        frameTexture[0].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 - width / 2, ROOM_HEIGHT * 0.20, -SPACE_BUFFER + 1));
        frameTexture[0].normVec = new Vertex3D(0, 0, 1);
        world.add(frameTexture[0]);

        width = frameBuf2.width * ROOM_HEIGHT / 2 / frameBuf2.height;
        if (frameTexture[1] != null) {
            world.remove(frameTexture[1]);
        }
        frameTexture[1] = new Texture3D(frameBuf2);
        frameTexture[1].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 + width / 2, ROOM_HEIGHT * 0.70, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER - 1));
        frameTexture[1].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 - width / 2, ROOM_HEIGHT * 0.70, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER - 1));
        frameTexture[1].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 - width / 2, ROOM_HEIGHT * 0.20, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER - 1));
        frameTexture[1].addPoint(new Vertex3D(ChatRepository.ROOM_DIMENSION.width / 2 + width / 2, ROOM_HEIGHT * 0.20, ChatRepository.ROOM_DIMENSION.height + SPACE_BUFFER - 1));
        frameTexture[1].normVec = new Vertex3D(0, 0, -1);
        world.add(frameTexture[1]);

        wallTexture[0].setTextureBuffer(wallBuf1);
        wallTexture[1].setTextureBuffer(wallBuf2);
        wallTexture[2].setTextureBuffer(wallBuf1);
        wallTexture[3].setTextureBuffer(wallBuf2);

        frameTexture[0].setTextureBuffer(frameBuf1);
        frameTexture[1].setTextureBuffer(frameBuf2);

        boolean toggle = false;
        for (int x = 0; x < TILES_X; x++) {
            for (int y = 0; y < TILES_Y; y++) {
                toggle = !toggle;
                if (tileTexture[x][y] != null) {
                    tileTexture[x][y].setTextureBuffer(toggle ? tileBuf1 : tileBuf2);
                }
            }
            if (TILES_X == 1) {
                toggle = !toggle;
            }
        }
        // floorTexture.setTextureBuffer(tileBuf1);
    }

    currentUser = chatApplet.getCurrentUser();
    pntPosition = currentUser.getPosition();

    canvasWidth = getSize().width;
    canvasHeight = getSize().height;

    if (bufferImage == null || bufferImage.getWidth(this) != canvasWidth || bufferImage.getHeight(this) != canvasHeight) {
      bufferImage = createImage(canvasWidth, canvasHeight);
    }

    if (worldImage == null || worldImage.getWidth(this) != canvasWidth || worldImage.getHeight(this) != canvasHeight) {
      worldImage = createImage(canvasWidth, canvasHeight);
    }

    bufferGraphics = bufferImage.getGraphics();
    worldGraphics = worldImage.getGraphics();

    vecCurrentSituation = chatApplet.historyMode() ? chatApplet.getHistoryEntryVector(chatApplet.getHistoryDate()) : chatApplet.getCurrentSituationVector();

    for (int i = 0; i < vecCurrentSituation.size(); i++) {
      user = chatApplet.getUser(((HistoryEntry)vecCurrentSituation.elementAt(i)).userId);
      if (user == null || !chatApplet.inVisualRange(currentUser.getId(), ((HistoryEntry)vecCurrentSituation.elementAt(i)).position)) {
        vecCurrentSituation.removeElementAt(i);
        i--;
      }
    }
    Vector tmpVec = new Vector(vecCurrentSituation.size());
    /* Heap-Sort - should be enough for this purpose, not too much overhead... */
    for (int i = 0; i < vecCurrentSituation.size(); i++) {
      int dist = 0;
      int delta = tmpVec.size() / 2;
      int j = delta;
      boolean done = false;
      while (!done) {
        dist = chatApplet.getDistance(currentUser.getId(), ((HistoryEntry)vecCurrentSituation.elementAt(i)).position);
        delta = Math.max(1, delta / 2);
        if ((j - delta) >= 0 && dist > chatApplet.getDistance(currentUser.getId(), ((HistoryEntry)tmpVec.elementAt(j - 1)).position)) {
          j -= delta;
        }
        else if ((j + delta) <= tmpVec.size() && dist < chatApplet.getDistance(currentUser.getId(), ((HistoryEntry)tmpVec.elementAt(j)).position)) {
          j += delta;
        }
        else {
          done = true;
        }
      }
      tmpVec.insertElementAt(vecCurrentSituation.elementAt(i), j);
    }

    vecCurrentSituation = tmpVec;

    ViewPoint viewPoint;

    viewPoint = new ViewPoint(new Vertex3D(pntPosition.x, ChatRepository.USER_HEIGHT - 1, pntPosition.y), new Orientation(0, currentUser.getHeading() + 90, 0))   ;
    if (lastWidth != canvasWidth || lastHeight != canvasHeight || lastUserId != currentUser.getId() || lastXPos != pntPosition.x || lastYPos != pntPosition.y || lastHeading != currentUser.getHeading() || lastRoomId != chatApplet.getCurrentRoomId()) {
        Object3D viewedWorld = getTransformedWorld(viewPoint);
        clippedworld = viewedWorld.clip(frustum[0]);
        clippedworld.bringToScreen(getSize().width, getSize().height, light, true);
        clippedworld.orderByDepth();
        worldGraphics.setColor(Color.white);
        worldGraphics.fillRect(0, 0, canvasWidth, canvasHeight);
        clippedworld.paint(worldGraphics);
    }

    bufferGraphics.drawImage(worldImage, 0, 0, this);
    for (int i = 0; i < vecCurrentSituation.size(); i++) {
      histEntry = (HistoryEntry)vecCurrentSituation.elementAt(i);
      user = chatApplet.getUser(histEntry.userId);
      if (user != null && histEntry.userId != chatApplet.getCurrentUserId()) {
        image = chatApplet.getUserAvatar(user.getId(), ((HistoryEntry)vecCurrentSituation.elementAt(i)).mood);
        Object3D userObj = new Object3D();
        Polygon3D polyWorld = new Polygon3D();
        int width = image.getWidth(this) * ChatRepository.USER_HEIGHT / image.getWidth(this);
        polyWorld.addPoint(new Vertex3D(histEntry.position.x - width / 2, 0, histEntry.position.y));
        polyWorld.addPoint(new Vertex3D(histEntry.position.x - width / 2, ChatRepository.USER_HEIGHT, histEntry.position.y));
        polyWorld.addPoint(new Vertex3D(histEntry.position.x + width / 2, ChatRepository.USER_HEIGHT, histEntry.position.y));
        polyWorld.addPoint(new Vertex3D(histEntry.position.x + width / 2, 0, histEntry.position.y));

        Object3D littleWorld = new Object3D(getTransformedPoly(polyWorld, viewPoint)).clip(frustum[0]);

        littleWorld = littleWorld.clip(frustum[0]);
        littleWorld.bringToScreen(getSize().width, getSize().height, light, true);
        // littleWorld.paint(bufferGraphics);
        if (littleWorld.getNrOfPolygons() == 1) {
          Polygon3D poly = littleWorld.getPolygon(0);

          if (!chatApplet.inVisualRange(histEntry.position, histEntry.heading, currentUser.getPosition()) && chatApplet.getUserBackAvatar(user.getId()) != null) {
            image = chatApplet.getUserBackAvatar(user.getId());
          }
          new Image3D(poly, image, user.getName(), histEntry.text, user.getColor()).paint(bufferGraphics);
          }
        }
        if (chatApplet.historyMode() && histEntry.userId == chatApplet.getCurrentUserId()) {
            (new Balloon(new Rectangle(canvasWidth * 2 / 3, 10, canvasWidth * 1 / 3 - 10, canvasHeight - 20), histEntry.text, currentUser.getColor(), Balloon.FACING_LEFT)).paint(bufferGraphics);
        }
      }
      bufferGraphics.setColor(Color.black);
      bufferGraphics.setFont(ChatRepository.BOLD_FONT);
      bufferGraphics.drawString(chatApplet.getCurrentRoom().getName(), 10, getFontMetrics(getFont()).getMaxAscent() + 10);
      bufferGraphics.setFont(ChatRepository.STANDARD_FONT);

      g.drawImage(bufferImage, 0, 0, this);

        lastUserId = currentUser.getId();
        lastXPos = pntPosition.x;
        lastYPos = pntPosition.y;
        lastHeading = currentUser.getHeading();
        lastRoomId = chatApplet.getCurrentRoomId();
        lastWidth = canvasWidth;
        lastHeight= canvasHeight;
    }

    private Polygon3D getTransformedPoly(Polygon3D poly, ViewPoint viewpoint) {
      Polygon3D myPoly = new Polygon3D(poly);
      myPoly.translate(new Vertex3D(-viewpoint.from.x, -viewpoint.from.y, -viewpoint.from.z));
      myPoly.rotate(viewpoint.orient);
      return myPoly;
    }

    private Object3D getTransformedWorld(ViewPoint viewpoint) {
      Object3D viewworld = new Object3D(world);
      viewworld.translate(new Vertex3D(-viewpoint.from.x, -viewpoint.from.y, -viewpoint.from.z));
      viewworld.rotate(viewpoint.orient);
      return viewworld;
    }

    public void run() {
    }



}
