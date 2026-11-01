package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class BarrierBlock {
    double x, y, width, height;
    boolean isDestroyed = false;
    
    BarrierBlock(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    void render(GraphicsContext gc) {
        if (isDestroyed) return;
        
        // Blocco della barriera con colore distintivo (grigio scuro)
        gc.setFill(Color.rgb(80, 80, 80));
        gc.fillRect(x, y, width, height);
        
        // Bordo per effetto 3D
        gc.setFill(Color.rgb(60, 60, 60));
        gc.fillRect(x + width - 3, y, 3, height); // Lato destro più scuro
        gc.fillRect(x, y + height - 3, width, 3); // Fondo più scuro
        
        // Bordo esterno
        gc.setStroke(Color.rgb(40, 40, 40));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
        
        // Dettagli per distinguerlo dai pacchi normali
        gc.setFill(Color.rgb(120, 120, 120));
        gc.fillRect(x + 2, y + 2, width - 4, 2); // Linea orizzontale in alto
        gc.fillRect(x + 2, y + 2, 2, height - 4); // Linea verticale a sinistra
    }
    
    boolean intersects(double px, double py, double pw, double ph) {
        if (isDestroyed) return false;
        return px < x + width && px + pw > x && py < y + height && py + ph > y;
    }
    
    void destroy() {
        isDestroyed = true;
    }
    
    boolean isDestroyed() {
        return isDestroyed;
    }
}

