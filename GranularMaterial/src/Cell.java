import java.util.HashSet;
import java.util.Set;

public class Cell {
    private int row;
    private int col;
    private Set<Particle> particles;

    public Cell(int row, int col){
        this.row = row;
        this.col = col;
        particles = new HashSet<>();
    }

    public Cell(){
        particles = new HashSet<>();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Set<Particle> getParticles() {
        return particles;
    }

    public void addParticle(Particle p) {
        particles.add(p);
    }

    public void removeParticles() {
        particles.clear();
    }
}
