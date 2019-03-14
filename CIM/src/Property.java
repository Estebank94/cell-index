/**
 * Created by estebankramer on 12/03/2019.
 */
public class Property<T> {
    private T property;

    public Property(T property) {
        this.property = property;
    }

    public T getProperty() {
        return property;
    }

    public void setProperty(T property) {
        this.property = property;
    }
}
