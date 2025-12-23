import java.util.Stack;

/**
 * Representation of stack that contains redirects
 */
public class RedirectionStack<Optional> extends Stack<Optional> {

    public RedirectionStack() {}

    protected int getRedirectCount() {
        return this.elementCount;
    }

    protected Object[] getRedirects() {
        return this.elementData;
    }



}
