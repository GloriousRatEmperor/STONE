import org.joml.Vector4i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FormImage {

    public static void main(String[] args) throws IOException {

//        AssetPool.addSpritesheet("assets/images/abilities.png",
//                new Spritesheet(AssetPool.getTexture("assets/images/abilities.png"),
//                        80, 80, 3, 0));
//
//        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
//                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
//                        16, 16, 81, 0));
//        spilt(80,80,"assets/images/abilities.png","ability");
//        spilt(16,16,"assets/images/spritesheets/decorationsAndBlocks.png","block");
//
//        System.exit(0);

        File directoryPath = new File("assets/images/ImagesSplit");

        File[] fileList = directoryPath.listFiles();

        BufferedImage join=new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        java.util.List<BufferedImage> imageList = new ArrayList<BufferedImage>();
        for (int i = 0; i < fileList.length; i++) {
            //fileList[i].renameTo(new File("assets/images/ImagesSplit/block"+ Integer.toString( i)+".png"));
            try {
                BufferedImage img1 = ImageIO.read(fileList[i]);
                imageList.add(img1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        join = joinBufferedImages(imageList, fileList);
        try {
            boolean success = ImageIO.write(join, "png", new File("assets/images/spritesheets/" + "joined.png"));
            System.out.println("saved success? " + success);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        }



    public static void spilt(int width, int height, String pathname, String resultname) throws IOException {
        final BufferedImage source = ImageIO.read(new File( pathname));
        int idx = 0;
        for (int x = 0; x < source.getWidth(); x += width) {
            for (int y = 0; y < source.getHeight(); y += height) {
                ImageIO.write(source.getSubimage(x, y, width, height), "png", new File("assets/images/ImagesSplit/"+resultname + idx++ + ".png"));
            }
        }
    }
        public static BufferedImage joinBufferedImage(BufferedImage img1,BufferedImage img2) {

            //do some calculate first
            int offset  = 0;
            int wid = img1.getWidth()+img2.getWidth()+offset;
            int height = Math.max(img1.getHeight(),img2.getHeight())+offset;
            //create a new buffer and draw two image into the new image
            BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newImage.createGraphics();
            Color oldColor = g2.getColor();

            //draw image
            g2.setColor(oldColor);
            g2.drawImage(img1, null, 0, 0);
            g2.drawImage(img2, null, img1.getWidth()+offset, 0);
            g2.dispose();
            return newImage;
        }

    public static BufferedImage joinBufferedImages(List<BufferedImage> image, File[] fileList) throws IOException {


        //aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaah fuck I fucked it, but don't wanna kill all the time on just this so im leaving it



        image.sort((a1, a2) -> {
            return a2.getHeight() - a1.getHeight(); // biggest to smallest
        });
        java.util.List<Vector4i> space=new ArrayList<>();
        java.util.List<Vector4i> imgpos=new ArrayList<>();
        int wid=0;
        int height= 128;

        int currentX=0;
        int currentY=0;
        java.util.List<Vector4i> cave=new ArrayList<>();
        boolean done;


            for (BufferedImage img : image) {
                done = false;
                for (Vector4i cav : cave) {
                    if (cav.x > img.getWidth()) {
                        if (cav.y > img.getHeight()) {
                            cav.x -= img.getWidth();
                            imgpos.add(new Vector4i(img.getWidth(), img.getHeight(), cav.z, cav.w));
                            cav.z += img.getWidth();
                        }
                    }
                }
                for (int c = 0; c < space.size(); c++) {
                    Vector4i v = space.get(c);
                    if (v.y() + img.getHeight() <= height) {
                        if (v.x() >= img.getWidth()) {
                            space.add(new Vector4i(img.getWidth(), img.getHeight() + v.y, v.z, v.w));
                            imgpos.add(new Vector4i(img.getWidth(), img.getHeight(), v.z, v.w + v.y));
                            if (v.x() - img.getWidth() > 0) {
                                space.add(new Vector4i(v.x() - img.getWidth(), v.y, v.z + img.getWidth(), v.w));
                            }
                            space.remove(v);

                        } else {
                            for (int u = c + 1; u < space.size(); u++) {
                                Vector4i o = space.get(u);
                                int ppo = Math.min(o.x, img.getHeight() - v.x);
                                v.x += ppo;
                                o.x -= ppo;
                                if (o.x == 0) {
                                    space.remove(o);
                                } else {
                                    break;
                                }

                            }

                            if (v.x < img.getWidth()) {
                                //make cave
                                cave.add(new Vector4i(img.getWidth() - v.x, v.y, v.z + v.x, v.w));

                                space.add(new Vector4i(img.getWidth() - v.x, img.getHeight() + v.y, v.z + v.x, currentY));
                                currentX += img.getWidth() - v.x;

                                imgpos.add(new Vector4i(img.getWidth(), img.getHeight(), v.z, currentY + v.y));

                            } else {
                                imgpos.add(new Vector4i(img.getWidth(), img.getHeight(), v.z, v.w + v.y));
                            }
                            v.y += img.getHeight();

                        }
                        done = true;
                        break;
                    }

                }
                if (!done) {
                    space.add(new Vector4i(img.getWidth(), img.getHeight(), currentX, currentY));
                    imgpos.add(new Vector4i(img.getWidth(), img.getHeight(), currentX, currentY));
                    currentX += img.getWidth();
                }

            }

        wid=0;
        for (Vector4i p:space){

            wid+=p.x;
        }
        int offset = 0;
//        int wid = img1.getWidth() + img2.getWidth() + offset;
//        int height = Math.max(img1.getHeight(), img2.getHeight()) + offset;
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setColor(oldColor);
        Boolean gud;
        //debug
//        BufferedImage debug = new BufferedImage(wid, height+1, BufferedImage.TYPE_INT_ARGB);
//        Graphics2D gd = debug.createGraphics();
//        g2.setColor(Color.PINK);
//
//        for (Vector4i vec: space) {
//            //draw image
//            gd.setColor(new Color(100, 100, 100,50));
//            gd.fillRect(vec.z, vec.w, vec.x, vec.y);
//            gd.drawRect(vec.z, vec.w, vec.x, vec.y);
//        }
//        boolean success = ImageIO.write(debug, "png", new File("assets/images/spritesheets/" + "debug.png"));
        //endbug
        HashMap<String, String> map = new HashMap<String, String>();

        for (File fil: fileList) {
            gud=false;
            BufferedImage img=ImageIO.read(fil);
            for (Vector4i iii:imgpos) {

                if(iii.x==img.getWidth() && iii.y==img.getHeight()) {
                    //draw image
                    map.put(iii.x +";"+ iii.y +";"+ iii.z +";"+ iii.w,fil.getName());
                    g2.drawImage(img, null, iii.z, iii.w);
                    imgpos.remove(iii);
                    gud=true;
                    break;
                }
            }
            if(!gud){
                System.out.println("WEEEEEEEEWOOOOOOOOOO FAILED TO PLACE IMAGE???!!!");
            }
        }
        g2.dispose();


        File Foil=new File( "assets/images/" + "key.txt");
        BufferedWriter bf = new BufferedWriter( new FileWriter(Foil));
        for (String key :map.keySet()) {

            // put key and value separated by a colon
            bf.write(key + ";"
                    + map.get(key).split("\\.")[0]);

            // new line
            bf.newLine();
        }

        bf.flush();

        bf.close();
        return newImage;
    }



        //full copy dunno how works, not needed now but later
    public static BufferedImage layerBufferedImage(BufferedImage img1,
                                                  BufferedImage img2) {
        //int offset = 2;
        int width = img1.getWidth();
        int height = img1.getHeight() + img2.getHeight();
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight());
        g2.dispose();
        return newImage;
    }

}
