package com.elbigs.util;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

public abstract class WebImage {
    static class Kit extends HTMLEditorKit {
        public Document createDefaultDocument() {
            HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();
            doc.setTokenThreshold(Integer.MAX_VALUE);
            doc.setAsynchronousLoadPriority(-1);
            return doc;
        }
    }

    public static BufferedImage create(String src, int width, int height) {
        BufferedImage image = null;
        JEditorPane pane = new JEditorPane();
        Kit kit = new Kit();
        pane.setEditorKit(kit);
        pane.setEditable(false);
        pane.setMargin(new Insets(0, 0, 0, 0));
        try {
            pane.setPage(src);
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();
            Container c = new Container();
            SwingUtilities.paintComponent(g, pane, c, 0, 0, width, height);
            g.dispose();
        } catch (Exception e) {
            System.out.println(e);
        }
        return image;
    }

    public static void main(String[] args) {
        BufferedImage ire;
//        String url = "http://localhost:3000/template/sample1/template_sample.html";
        String url = "https://gds.synccommerce.co.kr/login";
        String path = "c:/image/tmp2.jpg";
        ire = WebImage.create(url, 1920, 1080);
        try {
            ImageIO.write(ire, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
