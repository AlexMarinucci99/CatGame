package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/** Immutable source regions; alpha bounds are computed once on loading. */
final class SpriteAtlas {
    private final Image image;
    private final int columns;
    private final double cellWidth, cellHeight;
    private final double[][] bounds;
    SpriteAtlas(Image image, int columns, int rows) {
        this.image=image; this.columns=columns;
        cellWidth=image.getWidth()/columns;cellHeight=image.getHeight()/rows;
        bounds=new double[columns*rows][4];
        for(int frame=0;frame<bounds.length;frame++) {
            int ox=(int)(frame%columns*cellWidth),oy=(int)(frame/columns*cellHeight);
            int minX=(int)cellWidth,minY=(int)cellHeight,maxX=-1,maxY=-1;
            for(int y=0;y<(int)cellHeight;y++)for(int x=0;x<(int)cellWidth;x++) {
                if((image.getPixelReader().getArgb(ox+x,oy+y)>>>24)>16) {
                    minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);
                }
            }
            bounds[frame]=maxX<0?new double[]{ox,oy,cellWidth,cellHeight}:
                    new double[]{ox+minX,oy+minY,maxX-minX+1,maxY-minY+1};
        }
    }
    void cell(GraphicsContext gc,int frame,double x,double y,double width,double height) {
        gc.drawImage(image,frame%columns*cellWidth,frame/columns*cellHeight,cellWidth,cellHeight,x,y,width,height);
    }
    void grounded(GraphicsContext gc,int frame,double x,double y,double width,double height) {
        double[] b=bounds[frame];double scale=Math.min(width/b[2],height/b[3]);
        double w=b[2]*scale,h=b[3]*scale;
        gc.drawImage(image,b[0],b[1],b[2],b[3],x+(width-w)/2,y+height-h,w,h);
    }
}
