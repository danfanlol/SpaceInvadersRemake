import processing.core.PApplet;

import java.util.ArrayList;

abstract class ship{
    public int x,y;
    public ship(){
        x = 0;
        y=  0;
    }
    public ship(int x, int y){
        this.x = x;
        this.y = y;
    }
    public abstract void moveRight();
    public abstract void moveLeft();
}
interface bulletBehavior{
    public boolean detectBulletCollision(bullet bullet);
    public boolean detectShipCollision(ship ship);
    public boolean outOfBounds();
}
abstract class bullet{
    public int x,y;
    public bullet(int x,int y){
        this.x = x;
        this.y = y;
    }
    public abstract void move();
}
class AlienBullet extends bullet implements bulletBehavior{
    public AlienBullet(int x, int y){
        super(x,y);
    }
    @Override
    public void move() {
        y += 5;
    }

    @Override
    public boolean detectBulletCollision(bullet bullet) {
        if (bullet.y <= this.y){
            return bullet.x - this.x <= 2;
        }
        return false;
    }

    public boolean detectShipCollision(ship ship) {
        int farLeft = ship.x;
        int farRight = ship.x + 60;
        int farUp = ship.y;
        int farDown = ship.y + 20;
        return this.x >= farLeft && this.x <= farRight && this.y <= farDown && this.y >= farUp;
    }
    @Override
    public boolean outOfBounds(){
        return this.y > 800;
    }
}
class PlayerBullet extends bullet implements bulletBehavior{
    public PlayerBullet(int x, int y){
        super(x,y);
    }
    @Override
    public void move() {
        y -= 5;
    }

    @Override
    public boolean detectBulletCollision(bullet bullet) {
        if (Math.abs(bullet.y - this.y) < 10){
            return Math.abs(bullet.x - this.x) <= 10;
        }
        return false;
    }

    @Override
    public boolean detectShipCollision(ship ship) {
        int farLeft = ship.x;
        int farRight = ship.x + 20;
        int farUp = ship.y;
        int farDown = ship.y + 20;
        return this.x >= farLeft && this.x <= farRight && this.y <= farDown && this.y >= farUp;
    }
    @Override
    public boolean outOfBounds(){
        return this.y < 0;
    }
}
class AlienShip extends ship{
    public AlienShip(int x,int y){
        super(x,y);
    }

    @Override
    public void moveRight() {
        x += .5;
    }

    @Override
    public void moveLeft() {
        x -= .5;
    }

}
class playerShip extends ship{
    public playerShip(){
        super(350,700);
    }

    @Override
    public void moveRight() {
        if (x < 735)
            x += 5;
    }

    @Override
    public void moveLeft() {
        if (x > 5)
            x -= 5;
    }

}

public class SpaceInvaders extends PApplet {
    ArrayList<AlienShip> alienships = new ArrayList();
    ArrayList<PlayerBullet> playerBullets = new ArrayList();
    ArrayList<AlienBullet> alienBullets = new ArrayList();
    playerShip player = new playerShip();
    public boolean alienMovingDown = false;
    public boolean alienMovingRight = true;
    public int timesMoved = 0;
    public int counter = 0;

    public static void main(String[] args) {
        PApplet.main("SpaceInvaders");
    }

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        frameRate(10);
        for (int i = 0; i < 760; i += 40) {
            for (int j = 40; j < 200; j += 40) {
                AlienShip alien = new AlienShip(i, j);
                alienships.add(alien);
                rect(i, j, 20, 20);
            }
        }
        rect(player.x, player.y, 60, 20);
    }

    public void draw() {
        counter += 1;
        background(20);
        rect(player.x, player.y, 60, 20);
        for (AlienShip alien : alienships) {
            if (counter % 4 == 0) {
                if (alienMovingDown) {
                    alien.y += 10;
                } else if (alienMovingRight)
                    alien.x += 5;
                else alien.x -= 5;
                int random = (int) ((Math.random() * (100 - 1)) + 1);
                if (random == 1)
                    alienBullets.add(new AlienBullet(alien.x,alien.y));

            }
            rect(alien.x, alien.y, 20, 20);
        }
        if (counter % 4 == 0){
            timesMoved += 1;
        }
        if (counter % 4 == 0 && alienMovingDown) {
            alienMovingDown = false;
            timesMoved -= 1;
        }
        if (timesMoved == 12 && alienMovingRight) {
            alienMovingRight = false;
            alienMovingDown = true;
            timesMoved = 0;
        } else if (timesMoved == 12) {
            alienMovingRight = true;
            alienMovingDown = true;
            timesMoved = 0;
        }
        int  i= 0;
        while (i < playerBullets.size()){
            boolean collides = false;
            PlayerBullet bullet = playerBullets.get(i);
            bullet.move();
            rect(bullet.x, bullet.y, 5, 5);
            if (bullet.outOfBounds()){
                playerBullets.remove(i);
                continue;
            }
            int j =0 ;
            while (j < alienBullets.size()){
                AlienBullet alienBullet = alienBullets.get(j);
                if (bullet.detectBulletCollision(alienBullet)){
                    alienBullets.remove(j);
                    collides = true;
                    break;
                }
                j += 1;
            }
            if (collides){
                playerBullets.remove(i);
                continue;
            }
            j= 0;
            while (j < alienships.size()){
                AlienShip alien = alienships.get(j);
                if (bullet.detectShipCollision(alien)){
                    alienships.remove(j);
                    collides = true;
                    break;
                }
                j += 1;
            }
            if (collides){
                playerBullets.remove(i);
                i -= 1;
            }
            i += 1;
        }
        i =0;
        while (i < alienBullets.size()){
            AlienBullet bullet = alienBullets.get(i);
            bullet.move();
            rect(bullet.x, bullet.y, 5, 5);
            if (bullet.outOfBounds()){
                alienBullets.remove(i);
                continue;
            }
            if (bullet.detectShipCollision(player)){
                exit();
            }
            i += 1;
        }
    }

    public void keyPressed() {
        if (Character.compare(key, 'F') == 0)
            playerBullets.add(new PlayerBullet(player.x + 30, player.y));
        if (Character.compare(key, 'D') == 0)
            player.moveRight();
        if (Character.compare(key, 'A') == 0)
            player.moveLeft();
    }
}
