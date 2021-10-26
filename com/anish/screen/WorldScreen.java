package com.anish.screen;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.HashSet;
import com.anish.calabashbros.BubbleSorter;
import com.anish.calabashbros.Calabash;
import com.anish.calabashbros.World;

import asciiPanel.AsciiPanel;

public class WorldScreen implements Screen {

    private World world;
    private Calabash[][] bros;
    String[] sortSteps;


    public WorldScreen() {
        world = new World();

        bros = new Calabash[16][16];
        Random random = new Random();


        HashSet<Integer> set = new HashSet<>();
		

        for(int i = 0; i < 16; ++i)
        {
            
            for(int j = 0; j < 16; ++j)
            {
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                bros[i][j] = new Calabash(new Color(r, g, b), r, world);
            }
        }
        
        for(int i = 0; i < 16; ++i)
        {
            for(int j = 0; j < 16; ++j)
            {
                world.put(bros[i][j], 4 + j*2, 4 + i*2);
            }
        }
        
        String plan = "";

        
        for(int i = 0; i < 16; ++i)
        {
            BubbleSorter<Calabash> b = new BubbleSorter<>();
            b.load(bros[i]);
            b.sort();
            plan = plan + b.getPlan() + "k" + "\n";
        }
        sortSteps = this.parsePlan(plan);
    }

    private String[] parsePlan(String plan) {
        return plan.split("\n");
    }

    private void execute(Calabash[] bros, String step) {
        String[] couple = step.split("<->");
        int rank = Integer.parseInt(couple[0]);
        Calabash a = getBroByRank(bros, rank);
        
        a.swap(getBroByRank(bros, Integer.parseInt(couple[1])));
    }

    private Calabash getBroByRank(Calabash[] bros, int rank) {
        for (Calabash bro : bros) {
            if (bro.getRank() == rank) {
                return bro;
            }
        }
        return null;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {

        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {

                terminal.write(world.get(x, y).getGlyph(), x, y, world.get(x, y).getColor());
            }
        }
    }

    int i = 0;
    int l = 0;
    @Override
    public Screen respondToUserInput(KeyEvent key) {

        if (i < this.sortSteps.length) {
            if(this.sortSteps[i].length() == 1){
                l++;
            }
            else{
                this.execute(bros[l], sortSteps[i]);
                
            }
            i++;
        }
        return this;
    }

}
