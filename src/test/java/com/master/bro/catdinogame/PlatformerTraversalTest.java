package com.master.bro.catdinogame;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

/** Recorded input only: no position writes, invulnerability or checkpoint teleport. */
class PlatformerTraversalTest {
    @Test void originalMapCanBeTraversedFromSpawnToExitWithoutDeaths() throws Exception {
        String replay;
        try(var in=getClass().getResourceAsStream("/levels/sorting-replay.txt")) {
            assertNotNull(in); replay=new String(in.readAllBytes(),StandardCharsets.UTF_8).strip();
        }
        for(int fps:new int[]{30,60,144}) {
            var level=new PlatformerLevel();
            for(char command:replay.toCharArray()) {
                int action=command-'0',direction=action/2-1;
                boolean jump=(action%2)==1;
                level.left=direction<0; level.right=direction>0;
                if(jump && !level.jumpHeld)level.jumpPressed();
                if(!jump && level.jumpHeld)level.jumpReleased();
                double remaining=.1;
                while(remaining>1e-12) {
                    double dt=Math.min(remaining,1.0/fps);level.advance(dt);remaining-=dt;
                }
                assertEquals(0,level.deaths,"Lethal contact at "+level.time+" seconds, "+fps+" fps");
            }
            assertTrue(level.finished,"Exit must be reached at "+fps+" fps");
            assertTrue(level.checkpoint>=0);
            assertTrue(level.time>20 && level.time<22);
        }
    }
}
