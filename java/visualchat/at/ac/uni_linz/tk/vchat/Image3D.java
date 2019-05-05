package at.ac.uni_linz.tk.vchat;

import at.ac.uni_linz.tk.vchat.engine.*;
import java.awt.*;
import java.awt.image.*;

public class Image3D {
    private Polygon3D poly;
    private Image image;
    private String label, text;
    private Color col;

    public Image3D(Polygon3D polyParam, Image imgParam, String labelParam, String textParam, Color colParam) {
        poly = polyParam;
        image = imgParam;
        label = labelParam;
        text = textParam;
        col = colParam;
    }

    public void paint(Graphics g) {
        Rectangle clip;
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < poly.getNrOfPoints(); i++) {
          minX = Math.min(minX, poly.scrX[i]);
          maxX = Math.max(maxX, poly.scrX[i]);
          minY = Math.min(minY, poly.scrY[i]);
          maxY = Math.max(maxY, poly.scrY[i]);
        }
        // clip = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        if (image != null && image.getWidth(null) > 0 && image.getHeight(null) > 0) {
          clip = new Rectangle(minX, minY, image.getWidth(null) * (maxY - minY) / image.getHeight(null), maxY - minY);
          g.drawImage(image, clip.x, clip.y, clip.width, clip.height, null);
        }
        else {
          clip = new Rectangle(minX, minY, (maxY - minY) / 2, maxY - minY);
          ChatUtil.paintPattern(g, clip, Color.black);
        }
        g.setFont(ChatRepository.STANDARD_FONT);
        g.setColor(col);
        g.drawString(label, minX, maxY + ChatRepository.STANDARD_FONT.getSize());
        if (text != null && text.length() > 0 ) {
          int balloonWidth, balloonHeight;
          balloonHeight = clip.height;
          balloonWidth = ChatRepository.BALLOON_DIMENSION.width * balloonHeight / ChatRepository.BALLOON_DIMENSION.height;
          (new Balloon(new Rectangle(clip.x - balloonWidth - 10, clip.y - balloonHeight / 2, balloonWidth, balloonHeight), text, new Font(ChatRepository.STANDARD_FONT.getName(), ChatRepository.STANDARD_FONT.getStyle(), 8 + (balloonWidth / ChatRepository.BALLOON_DIMENSION.width * 4)), col, Balloon.FACING_RIGHT)).paint(g);
        }
    }

}
