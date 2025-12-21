import java.util.ArrayList;
import java.util.Stack;

/**
 * Representation of stack that contains redirects
 */
public class RedirectionStack<Optional> extends Stack<Optional> {
    private static int redirectCount;
    private ArrayList<String> backingArr = new ArrayList<>();
    private Optional previousResponse;
    public RedirectionStack(){

    }

    public RedirectionStack(Optional previousResponse) {
        this.previousResponse = previousResponse;
        redirectCount++;
        backingArr.add(previousResponse.toString());
    }

    protected int getRedirectCount() {
        return redirectCount;
    }

}
