
public class Verlet {


    private void verlet(Particle particle){
        double rx = particle.getPosition().getX();
        double ry = particle.getPosition().getX();
        double newX = (2*rx) - particle.getPrevX() + ((Math.pow(deltaT,2)*particle.getFx())/mass);
        double newY = (2*ry) - particle.getPrevY() + ((Math.pow(deltaT,2)*particle.getFy())/mass);
        double newVx = (newX - particle.getPrevX())/(2*deltaT);
        double newVy = (newY - particle.getPrevY())/(2*deltaT);
        particle.setX(newX);
        particle.setY(newY);
        particle.setVx(newVx);
        particle.setVy(newVy);
        particle.setPrevX(rx);
        particle.setPrevY(ry);
        particle.setFx(0);
        particle.setFy(0);
    }

    private void setPreviousPositionWithEuler(Particle p){

        double posX = p.getX() - deltaT * p.getVx();
        double posY = p.getY() - deltaT * p.getVy();
        posX += Math.pow(deltaT, 2) * p.getFx() / (2 * mass);
        posY += Math.pow(deltaT, 2) * p.getFy() / (2 * mass);
        p.setPrevX(posX);
        p.setPrevY(posY);
    }


}
