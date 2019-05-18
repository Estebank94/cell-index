import java.util.*;
import java.util.function.Function;

public class NeighbourCalculator {
    private double cellWidth;
    private Cell[][] grid;
    private int gridWidth;
    private int gridHeight;
    private Set<Particle> outOfBounds;

    public NeighbourCalculator(double height, double width, double interactionRadius, double maxRadius) {
        this.cellWidth = 2 * maxRadius + interactionRadius;

        this.gridWidth = (int) Math.ceil(width / cellWidth);
        this.gridHeight = (int) Math.ceil(height / cellWidth);
        this.outOfBounds = new HashSet<>();

        grid = new Cell[gridWidth][gridHeight];

        for(int i = 0; i < gridWidth; i++) {
            for(int j = 0; j <  gridHeight; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }

    }


    public Map<Particle, Set<Particle>> getNeighbours(Set<Particle> allParticles) {
        Map<Particle, Set<Particle>> neighbours = new HashMap<>();

        clearGrid();

        addParticlesToGrid(allParticles);

        for(int i = 0; i < gridWidth; i++) {
            for(int j = 0; j < gridHeight; j++) {
                Set<Particle> cellNeighbours = getNearMolecules(grid[i][j]);
                for(Particle p : grid[i][j].getParticles()) {
                    neighbours.put(p, cellNeighbours);
                }
            }
        }
        for(Particle particle : outOfBounds){
            neighbours.put(particle, Collections.EMPTY_SET);
        }

        return neighbours;
    }

    private void addParticlesToGrid(Set<Particle> particles) {
        for(Particle p : particles) {
            int x = (int)(p.getPosition().getX() / cellWidth);
            int y = (int)(p.getPosition().getY() / cellWidth);

            if(x>=0 && x< gridWidth && y >=0 && y< gridHeight){
                grid[x][y].addParticle(p);
            }else{
                outOfBounds.add(p);
            }

        }
    }

    private Set<Particle> getNearMolecules(Cell field){
        Set<Particle> nearParticles = new HashSet<>();
        int x= field.getX();
        int y = field.getY();

        addParticles(nearParticles,x-1,y-1);
        addParticles(nearParticles,x,y-1);
        addParticles(nearParticles,x+1,y-1);

        addParticles(nearParticles,x-1,y);
        addParticles(nearParticles,x,y);
        addParticles(nearParticles,x+1,y);

        addParticles(nearParticles,x-1,y+1);
        addParticles(nearParticles,x,y+1);
        addParticles(nearParticles,x+1,y+1);

        return nearParticles;
    }

    public void addParticles(Set<Particle> nearParticles,int x , int y){
        if(x>=0 && x< gridWidth && y >=0 && y< gridHeight){
            nearParticles.addAll(grid[x][y].getParticles());
        }
    }

    private void clearGrid() {
        outOfBounds.clear();
        for(int i = 0; i < gridWidth; i++) {
            for(int j = 0; j < gridHeight; j++) {
                grid[i][j].clearParticles();
            }
        }
    }
}