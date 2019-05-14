import java.util.*;


public class CIM {

    private Cell[][] grid;
    private double cellSize;
    private int rows;
    private int cols;
    private Set<Particle> outOfBounds;

    public CIM(double W, double L, double radius){
        this.cellSize = 2 * radius;
        this.rows = (int) Math.ceil(W / cellSize);
        this.cols = (int) Math.ceil(L / cellSize);
        this.outOfBounds = new HashSet<>();
        this.grid = new Cell[rows][cols];

        /* Initialize grid */
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                grid[i][j] = new Cell(i,j);
            }
        }
    }

    public Map<Particle, Set<Particle>> getNeighbours(Set<Particle> allParticles) {

        removeAllParticlesFromGrid();

        Map<Particle, Set<Particle>> neighbours = new HashMap<>();

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                Set<Particle> nearParticles = getNearParticles(grid[i][j]);
                for(Particle p : grid[i][j].getParticles()){
                    neighbours.put(p, nearParticles);
                }
            }
        }

        for(Particle p : outOfBounds){
            neighbours.put(p, Collections.emptySet());
        }

        return neighbours;

    }

    /* Ubica las particulas en cada celda */
    public void addParticlesToGrid(Set<Particle> allParticles) {
        for(Particle p : allParticles){
            int i = (int)(p.getX()/cellSize);
            int j = (int)(p.getY()/cellSize);

            if(i>=0 && i< rows && j >=0 && j < cols){
                grid[i][j].addParticle(p);
            }else{
                outOfBounds.add(p);
            }
        }
    }

    /* Me da las particulas de las celdas aledañas */
    public Set<Particle> getNearParticles(Cell cell){
        Set<Particle> nearParticles = new HashSet<>();
        int i = cell.getRow();
        int j = cell.getCol();

        addParticles(nearParticles,i-1,j-1);
        addParticles(nearParticles,i+0,j-1);
        addParticles(nearParticles,i+1,j-1);

        addParticles(nearParticles,i-1,j+0);
        addParticles(nearParticles,i+0,j+0);
        addParticles(nearParticles,i+1,j+0);

        addParticles(nearParticles,i-1,j+1);
        addParticles(nearParticles,i+0,j+1);
        addParticles(nearParticles,i+1,j+1);

        return nearParticles;

    }

    public void addParticles(Set<Particle> nearParticles,int i , int j){
        if(i>=0 && i< rows && j >=0 && j< cols){
            nearParticles.addAll(grid[i][j].getParticles());
        }
    }

    private void removeAllParticlesFromGrid() {
        outOfBounds.clear();
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                grid[i][j].removeParticles();
            }
        }
    }



}
