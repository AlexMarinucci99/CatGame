package com.master.bro.catdinogame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class Obstacle {
    double x, y, width, height, speed;
    boolean isPartOfDouble; // Se fa parte di un ostacolo doppio
    private Image normalBlockImage;
    private Image doubleBlockImage;

    Obstacle(double x, double y, double width, double height, double speed, boolean isPartOfDouble) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.isPartOfDouble = isPartOfDouble;
    }
    
    // Metodo per impostare le immagini dei blocchi
    void setBlockImages(Image normalImage, Image doubleImage) {
        this.normalBlockImage = normalImage;
        this.doubleBlockImage = doubleImage;
    }

    void update(double dt) {
        x -= speed * dt;
    }

    void render(GraphicsContext gc) {
        // === PRIORITÀ: IMMAGINE PERSONALIZZATA ===
        Image imageToUse = isPartOfDouble ? doubleBlockImage : normalBlockImage;
        
        if (imageToUse != null && !imageToUse.isError()) {
            // Usa l'immagine personalizzata caricata dall'utente
            gc.drawImage(imageToUse, x, y, width, height);
            return; // Esce qui se l'immagine è disponibile
        }
        // === PACCO AMAZON FEDELE ALL'ORIGINALE ===
        
        // Colore marrone Amazon esatto dall'immagine
        gc.setFill(Color.rgb(168, 124, 86)); // Marrone Amazon uniforme
        gc.fillRect(x, y, width, height); // Spigoli netti
        
        // === LOGO AMAZON SEMPLICE E FEDELE ===
        
        // Sorriso Amazon - curva semplice e pulita
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(Math.max(2, height * 0.08)); // Spessore proporzionale
        
        // Curva del sorriso che va da A a Z (sinistra a destra)
        double smileStartX = x + width * 0.2;
        double smileEndX = x + width * 0.8;
        double smileY = y + height * 0.5;
        
        // Sorriso curvo semplice
        gc.beginPath();
        gc.moveTo(smileStartX, smileY);
        gc.quadraticCurveTo(x + width * 0.5, smileY + height * 0.15, smileEndX, smileY - height * 0.05);
        gc.stroke();
        
        // Freccia finale pulita
        double arrowSize = Math.max(3, height * 0.12);
        gc.strokeLine(smileEndX - arrowSize, smileY - height * 0.05 - arrowSize/2, smileEndX, smileY - height * 0.05);
        gc.strokeLine(smileEndX - arrowSize, smileY - height * 0.05 + arrowSize/2, smileEndX, smileY - height * 0.05);
        
        // === ETICHETTA PRIME AUTENTICA (solo per pacchi doppi) ===
        if (isPartOfDouble) {
            // Etichetta Prime blu Amazon
            double primeWidth = width * 0.35;
            double primeHeight = height * 0.18;
            double primeX = x + width * 0.03;
            double primeY = y + height * 0.05;
            
            gc.setFill(Color.rgb(0, 113, 189)); // Blu Prime ufficiale
            gc.fillRect(primeX, primeY, primeWidth, primeHeight);
            
            // Testo "prime" bianco
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, Math.max(5, height * 0.1)));
            gc.fillText("prime", primeX + 2, primeY + primeHeight * 0.75);
            
            // Piccolo "day" sotto prime
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, Math.max(3, height * 0.06)));
            gc.fillText("day", primeX + primeWidth * 0.7, primeY + primeHeight * 0.75);
        }
        
        // === EFFETTO 3D REALISTICO ===
        // Faccia laterale destra (più scura)
        gc.setFill(Color.rgb(145, 105, 70)); // Marrone più scuro per lato
        gc.fillRect(x + width - 4, y, 4, height);
        
        // Faccia inferiore (più scura)
        gc.setFill(Color.rgb(145, 105, 70));
        gc.fillRect(x, y + height - 4, width, 4);
        
        // === TEXTURE CARTONE SOTTILE ===
        gc.setStroke(Color.rgb(150, 110, 75, 0.4));
        gc.setLineWidth(0.3);
        // Linee orizzontali molto sottili per texture cartone
        for (int i = 1; i < 4; i++) {
            double lineY = y + height * (i / 4.0);
            gc.strokeLine(x, lineY, x + width, lineY);
        }
        
        // === BORDO DEFINITO ===
        gc.setStroke(Color.rgb(130, 95, 60));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
    }

    boolean isOffScreen() {
        return x + width < 0;
    }

    boolean intersects(double px, double py, double pw, double ph) {
        return px < x + width && px + pw > x && py < y + height && py + ph > y;
    }
}

