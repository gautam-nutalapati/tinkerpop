package com.tinkerpop.gremlin.oltp.map;

import com.tinkerpop.gremlin.Path;
import com.tinkerpop.gremlin.Pipeline;
import com.tinkerpop.gremlin.util.FunctionRing;

import java.util.List;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class SelectPipe extends MapPipe<Object, Path> {

    public final FunctionRing functionRing;
    public final String[] asLabels;

    public SelectPipe(final Pipeline pipeline, final List<String> asLabels, Function... stepFunctions) {
        super(pipeline);
        this.functionRing = new FunctionRing(stepFunctions);
        this.asLabels = asLabels.toArray(new String[asLabels.size()]);
        this.setFunction(holder -> {
            final Path path = holder.getPath();
            if (this.functionRing.hasFunctions()) {
                final Path temp = new Path();
                if (this.asLabels.length == 0)
                    path.forEach((as, object) -> temp.add(as, this.functionRing.next().apply(object)));
                else
                    path.subset(this.asLabels).forEach((as, object) -> temp.add(as, this.functionRing.next().apply(object)));
                return temp;
            } else {
                return this.asLabels.length == 0 ? path : path.subset(this.asLabels);
            }
        });
    }
}
