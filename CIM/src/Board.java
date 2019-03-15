import java.util.*;

public class Board {
    private int l;
    private int m;
    private double rc;
    private double cellSize;
    private HashMap<Point,Set<Particle>> board;
    private boolean periodic;

    public Board(int l, int m, double rc, Set<Particle> particles,boolean periodic) {
        this.l = l;
        this.m = m;
        this.rc = rc;
        this.cellSize = (double)l/m;
        this.board = new HashMap<>();
        this.periodic = periodic;

        for(int i = 0; i < m ; i++){
            for(int j = 0 ; j < m ; j++) {
                board.put(new Point(i, j), new HashSet<>());
            }
        }

        for(Particle particle : particles){
            Point j = getCell(particle.getLocation());
            try {
                board.get(j).add(particle);
            }catch (Exception e){
                System.out.println(j + " " +particle);
            }

        }

    }

    public double getRc() {
        return rc;
    }


    /**
     * Returns in which cell of the board the particle is located
     *
     * @param position
     * @return A Point where the particle is located in the board
     */
    private Point getCell(Point position){
        int x = (int)(position.getX()/cellSize);
        int y = (int)(position.getY()/cellSize);
        return new Point(x,y);
    }


    /**
     * Reads X and Y coordinates and returns all particles in cell
     *
     * @param x
     * @param y
     * @return Set of all the particles in the cell
     */
    public Set<Particle> getParticlesInCell(int x , int y){
        if(x < 0){
            if(periodic){
                return getParticlesInCell(x+m,y);
            }else{
                return Collections.EMPTY_SET;
            }

        } if(x >= m){
            if(periodic){
                return getParticlesInCell(x-m,y);
            }else{
                return Collections.EMPTY_SET;
            }
        }
        if (y <0){
            if(periodic){
                return getParticlesInCell(x,y+m);
            }else{
                return Collections.EMPTY_SET;
            }
        }
        if( y>=m){
            if(periodic){
                return getParticlesInCell(x,y-m);
            }else{
                return Collections.EMPTY_SET;
            }

        }
        return board.get(new Point(x,y));
    }

    /**
     * Receives a cell and returns all the particles inside the cell and the particles of the surrounding cells
     *
     * @param cell
     * @return A Set of all the particles inside the cell and the surrounding cells
     */
    private Set<Particle> getSurroundingParticles(Point cell){
        Set<Particle> nearParticles = new HashSet<>();
        nearParticles.addAll(getParticlesInCell((int )cell.getX()      ,   (int) cell.getY()));
        nearParticles.addAll(getParticlesInCell((int )cell.getX()      ,(int) cell.getY()+1));
        nearParticles.addAll(getParticlesInCell((int )cell.getX()+1 ,(int) cell.getY()+1));
        nearParticles.addAll(getParticlesInCell((int )cell.getX()+1 ,   (int) cell.getY()));
        nearParticles.addAll(getParticlesInCell((int )cell.getX()+1 ,(int) cell.getY()-1));
        return nearParticles;
    }


    /**
     * Analyzes surroundingParticles around a specific particle and returns a set of particles that are inside the rc range
     * @param particle
     * @param analyzed
     * @param surroundingParticles
     * @return returns a set of particles that are inside the rc range
     */
    public Set<Particle> getNeighboursOfParticle(Particle particle, Set<Particle> analyzed, Set<Particle> surroundingParticles){

        Set<Particle> neighbours = new HashSet<>();

        for(Particle other : surroundingParticles){
            if(!other.equals(particle) && !analyzed.contains(other)) {
                if(particlesInRange(particle,other)){
                    neighbours.add(other);
                }
            }
        }

        return neighbours;
    }


    /**
     * Receives two particles and returns a boolean that defines whether the two particles are in the rc range
     * @param p1
     * @param p2
     * @return boolean that defines whether the two particles are in the rc range
     */
    private boolean particlesInRange(Particle p1, Particle p2) {

        if(Particle.borderDistanceBetweenParticles(p1,p2) <= rc) {
            return true;
        }

        if(periodic){
            double mx = p1.getLocation().getX();
            double my = p1.getLocation().getY();
            double ox = p2.getLocation().getX();
            double oy = p2.getLocation().getY();

            return Particle.borderDistanceBetweenParticles(new Particle(new Point(mx,my+l)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx + l,my)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx + l,my+l)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx + l,my - l)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx,my-l)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx - l ,my - l)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx - l,my)), new Particle(new Point(ox,oy))) <= rc
                    || Particle.borderDistanceBetweenParticles(new Particle(new Point(mx - l,my+l)), new Particle(new Point(ox,oy))) <= rc;
        }

        return false;

    }

    /**
     * Recieves a cell and returns a map that contains all the particles in the cell with it's neighbours
     * @param cell
     * @return Map with a a particle and all it's neighbours
     */

    public Map<Particle,Set<Particle>> analyzeCell(Point cell){
        Set<Particle> particles = board.get(cell);
        Set<Particle> surroundingParticles = getSurroundingParticles(cell);

        Set<Particle> analyzed = new HashSet<>();
        Map<Particle,Set<Particle>> cellNeighbours = new HashMap<>();

        for(Particle particle : particles){
            Set<Particle> neighbors = getNeighboursOfParticle(particle,analyzed, surroundingParticles);

            analyzed.add(particle);
            cellNeighbours.put(particle,neighbors);
        }
        return cellNeighbours;
    }
}
