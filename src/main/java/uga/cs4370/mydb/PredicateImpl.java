package uga.cs4370.mydb;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Implementation of the Predicate interface.
 *
 */
public class PredicateImpl implements Predicate {

    private String predicates;
    private List<String> predicateValues;
    private Relation relation;

    public PredicateImpl(String predicates, Relation relation) {
        this.predicates = predicates;
        this.relation = relation;
        this.predicateValues = new ArrayList<>(3);
         if (!predicates.isEmpty()) {
            StringTokenizer delimiter = new StringTokenizer(predicates, " ");
            while (delimiter.hasMoreTokens()) {
                predicateValues.add(delimiter.nextToken());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean check(List<Cell> row) {
        int relAttrIndex = relation.getAttrIndex(predicateValues.get(0));

        String attrName = relation.getAttrs().get(relAttrIndex);

        if (relation.hasAttr(predicateValues.get(0))) {
            String algebra = predicateValues.get(1);
            switch (relation.getTypes().get(relAttrIndex)) {
                case DOUBLE:
                    switch (algebra) {
                        case "==":
                            return Double.parseDouble(predicateValues.get(2)) == row.get(relAttrIndex).getAsDouble();
                        case "!=":
                            return Double.parseDouble(predicateValues.get(2)) != row.get(relAttrIndex).getAsDouble();
                        case ">":
                            return Double.parseDouble(predicateValues.get(2)) < row.get(relAttrIndex).getAsDouble();
                        case "<":
                            return Double.parseDouble(predicateValues.get(2)) > row.get(relAttrIndex).getAsDouble();
                        case ">=":
                            return Double.parseDouble(predicateValues.get(2)) <= row.get(relAttrIndex).getAsDouble();
                        case "<=":
                            return Double.parseDouble(predicateValues.get(2)) >= row.get(relAttrIndex).getAsDouble();
                    }
                case INTEGER:
                    switch (algebra) {
                        case "==":
                            return Integer.parseInt(predicateValues.get(2)) == row.get(relAttrIndex).getAsInt();
                        case "!=":
                            return Integer.parseInt(predicateValues.get(2)) != row.get(relAttrIndex).getAsInt();
                        case ">":
                            return Integer.parseInt(predicateValues.get(2)) < row.get(relAttrIndex).getAsInt();
                        case "<":
                            return Integer.parseInt(predicateValues.get(2)) > row.get(relAttrIndex).getAsInt();
                        case ">=":
                            return Integer.parseInt(predicateValues.get(2)) <= row.get(relAttrIndex).getAsInt();
                        case "<=":
                            return Integer.parseInt(predicateValues.get(2)) >= row.get(relAttrIndex).getAsInt();
                    }
                case STRING:
                    switch (algebra) {
                        case "==":
                            return predicateValues.get(2).equals(row.get(relAttrIndex).getAsString());
                        case "!=":
                            return !predicateValues.get(2).equals(row.get(relAttrIndex).getAsString());
                    }
            }
        }
        return false;
    }
}


