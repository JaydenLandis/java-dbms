package uga.cs4370.mydb;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Relation Algebra Operators interface..
 *
 */
public class RAimpl implements RA {

    /**
     * {@inheritDoc}
     */
    public Relation select(Relation rel, Predicate p) {

        List<List<Cell>> selectRows = new ArrayList<>();
        Relation select = new RelationImpl(rel.getTypes(), rel.getAttrs());
        for(int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i);
            if(p.check(row)) {
                selectRows.add(row);
            }
        }
        for (List<Cell> row : selectRows) {
            select.insert(row);
        }

        return select;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relation project(Relation rel, List<String> attrs) {

        for (int i = 0; i < attrs.size(); i++) {
            String attr = attrs.get(i);
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException("Attributes do not match: " + attr);
            }
        }
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < attrs.size(); i++) {
            String attr = attrs.get(i);
            int index = rel.getAttrIndex(attr);
            Type type = rel.getTypes().get(index);
            types.add(type);
        }

        RelationImpl projectRelation = new RelationImpl(types, attrs);
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i);
            List<Cell> newRow = new ArrayList<>();

            for (int j = 0; j < attrs.size(); j++) {
                String attr = attrs.get(j);
                int index = rel.getAttrIndex(attr);
                Cell cell = row.get(index);
                newRow.add(cell);
            }

            if (!rowExists(projectRelation, newRow)) {
                projectRelation.insert(newRow);
            }
        }

        return projectRelation;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Relation union(Relation rel1, Relation rel2) {
            // Check to make sure rel1 and rel2 have the same attrs
            //List<String> atr1 = rel1.getAttrs();
            //List<String> atr2 = rel2.getAttrs();
            List<Type> atr1 = rel1.getTypes();
            List<Type> atr2 = rel2.getTypes();
            
            if (atr1.size() != atr2.size()) {
                throw new IllegalArgumentException("rel1 and rel2 are not the same size: not the " +
                        "same parity");
            }

            for(int i = 0; i < atr1.size(); i++) {
               if(!atr1.get(i).equals(atr2.get(i))) { 
                throw new IllegalArgumentException("rel1 and rel2 are not compatible: there is a type mismatch");
            }
        }
            
            // Make the new relation 
            Relation result = new RelationImpl(rel1.getTypes(), rel1.getAttrs());

            //Merge rows from rel1 and rel2
            for(int i = 0; i < rel1.getSize(); i++) {
                result.insert(rel1.getRow(i));
                
            }
            for(int i = 0; i < rel2.getSize(); i++) {
                if(!rowExists(result, rel2.getRow(i))) {
                    result.insert(rel2.getRow(i));
                }
            }
            return result;
        }   

    /**
     * {@inheritDoc}
     */
    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        if (!rel1.getAttrs().equals(rel2.getAttrs()) || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException("Relation One and Relation Two are not compatible.");
        }

        Relation diffRel = new RelationImpl(rel1.getTypes(), rel1.getAttrs());
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);
            boolean diffBool = false;
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);
                if (row1.equals(row2)) {
                    diffBool = true;
                    break;
                }
            }
            if (!diffBool) {
                diffRel.insert(row1);
            }
        }
        return diffRel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relation rename(Relation rel, List<String> origAttr, List<String> renamedAttr) {
        if (origAttr.size() != renamedAttr.size()) {
            throw new IllegalArgumentException("rel1 and rel2 are not compatible");
        }

        for (int i = 0; i < origAttr.size(); i++) {
            if (!rel.hasAttr(origAttr.get(i))) {
                throw new IllegalArgumentException("An attribute in the provided list does not exist");
            }

        }

       Relation new_rel = new RelationBuilder()
                .attributeNames(renamedAttr)
                .attributeTypes(rel.getTypes())
                .build();
        for(int i=0; i < rel.getSize(); i++) {
            new_rel.insert(rel.getRow(i));
        }
        
        return new_rel;



    }

    /**
     * {@inheritDoc}
     */
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        List<Type> totTypes = new ArrayList<>();
        totTypes.addAll(rel1.getTypes());
        totTypes.addAll(rel2.getTypes());

        List<String> totAttrs = new ArrayList<>();
        totAttrs.addAll(rel1.getAttrs());
        totAttrs.addAll(rel2.getAttrs());

        Relation cartRel = new RelationImpl(totTypes, totAttrs);

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);
            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> cartRow = new ArrayList<>();
                List<Cell> row2 = rel2.getRow(j);
                cartRow.addAll(row1);
                cartRow.addAll(row2);
                cartRel.insert(cartRow);
            }
        }
        return cartRel;
    }

    /**
     * {@inheritDoc}
     */
    public Relation join(Relation rel1, Relation rel2) {

        // [Attr and Type Arithmetic]

        // Initialize the attrs,types, and together attrs and types.
        List<String> rel1Attrs = rel1.getAttrs();
        List<String> rel2Attrs = rel2.getAttrs();
        List<Type> rel1Types = rel1.getTypes();
        List<Type> rel2Types = rel2.getTypes();
        List<String> togAttrs = new ArrayList<String>();
        List<Type> togTypes = new ArrayList<Type>();

        // Make a list of the common attrs
        for (int i = 0; i < rel1Attrs.size(); i++) {
            for (int j = 0; j < rel2Attrs.size(); j++) {
                if (rel1Attrs.get(i).equals(rel2Attrs.get(j))) {
                    togAttrs.add(rel1Attrs.get(i));
                }
            }
        }

        List<String> diffRelAttrs = new ArrayList<>();
        List<Type> diffRelTypes = new ArrayList<>();
        //Add all the attrs and remove the common attrs
        diffRelAttrs.addAll(rel1.getAttrs());
        diffRelAttrs.addAll(rel2.getAttrs());
        diffRelAttrs.removeAll(togAttrs);

        // Make a list of the types of those common attrs
        for (int i = 0; i < rel1Attrs.size(); i++) {
            for (int j = 0; j < rel2Attrs.size(); j++) {
                if (rel1Attrs.get(i).equals(rel2Attrs.get(j))) {
                    togTypes.add(rel1Types.get(i));
                }
            }
        }

        // Get the position of the rel1 attr that is not in common with togattrs
        // There is a difference between Attrs and Types since we
        // have to consider that types have an ordering.
        for (int i = 0; i < diffRelAttrs.size(); i++) {
            // first check if there is a common attr in the diffrel list.
            for (int j = 0; j < rel1Attrs.size(); j++) {
                if (diffRelAttrs.get(i).equals(rel1Attrs.get(j))) {
                    diffRelTypes.add(rel1Types.get(j));
                }
            }
        }
        for (int i = 0; i < diffRelAttrs.size(); i++) {
            // first check if there is a common attr in the diffrel list.
            for (int j = 0; j < rel2Attrs.size(); j++) {
                if (diffRelAttrs.get(i).equals(rel2Attrs.get(j))) {
                    diffRelTypes.add(rel2Types.get(j));
                }
            }
        }

        List<String> finRelAttrs = new ArrayList<>();
        List<Type> finRelTypes = new ArrayList<>();
        finRelAttrs.addAll(togAttrs);
        finRelAttrs.addAll(diffRelAttrs);
        finRelTypes.addAll(togTypes);
        finRelTypes.addAll(diffRelTypes);

        // [Common Rows with Common Elements Arithmetic]

        Relation commonRel = new RelationImpl(togTypes, togAttrs);
        // Placeholder for an array list containing a row
        List<Cell> togRow = new ArrayList<Cell>();
        // Gather the indexes of rel1 and rel2 where they do match in common values.
        ArrayList<Integer> rel1Idx = new ArrayList<>();
        ArrayList<Integer> rel2Idx = new ArrayList<>();

        boolean checker = false;
        for (int i = 0; i < rel1.getSize() / 2; i++) {
            for (int j = 0; j < rel2.getSize(); j++) {
                for (int k = 0; k < togAttrs.size(); k++) {
                    // If the value in the row in rel1 at the common attrs are the same
                    // as rel2's row, then we add this value. Otherwise, clear the list and break.
                    if (rel1.getRow(i).get(rel1.getAttrIndex(togAttrs.get(k))).equals(rel2.getRow(j).get(rel2.getAttrIndex(togAttrs.get(k))))) {
                        checker = true;
                    } else {
                        checker = false;
                        break;
                    }
                }
                if (checker) {
                    // Insert the indexes, should also be equal in size
                    rel1Idx.add(i);
                    rel2Idx.add(j);
                    break;
                }
            }
        }
        for (int i = 0; i < rel1.getSize() / 2; i++) {
            for (int j = 0; j < rel2.getSize(); j++) {
                for (int k = 0; k < togAttrs.size(); k++) {
                    // If the value in the row in rel1 at the common attrs are the same
                    // as rel2's row, then we add this value. Otherwise, clear the list and break.
                    if (rel1.getRow(i).get(rel1.getAttrIndex(togAttrs.get(k))).equals(rel2.getRow(j).get(rel2.getAttrIndex(togAttrs.get(k))))) {
                        togRow.add(rel1.getRow(i).get(rel1.getAttrIndex(togAttrs.get(k))));
                    } else {
                        togRow.clear();
                        break;
                    }
                }
                if (!togRow.isEmpty()) {
                    // Insert the row even if it is not empty.
                    commonRel.insert(togRow);
                    togRow.clear(); // Reset togRow.
                }
            }
        }

        // [Final Rows Arithmetic]
        List<List<Integer>> rowObject = new ArrayList<>(new ArrayList<>());

        for (int i = 0; i < commonRel.getSize(); i++) {
            for (int j = 0; j < rel1.getSize() / 2; j++) {
                for (int k = 0; k < rel2.getSize(); k++) {
                    // For each common element in a list
                    for (String attr : togAttrs) {
                        List<Cell> commonRow = commonRel.getRow(i);
                        List<Cell> rel1Row = rel1.getRow(j);
                        List<Cell> rel2Row = rel2.getRow(k);
                        if (rel1Row.containsAll(commonRow) && rel2Row.containsAll(commonRow)) {
                            List<Integer> idx = new ArrayList<>();
                            idx.add(i);
                            idx.add(j);
                            idx.add(k);
                            rowObject.add(idx);
                            break;
                        }
                    }
                }
            }
        }

        Relation finRel = new RelationImpl(finRelTypes, finRelAttrs);
        for (int i = 0; i < rowObject.size(); i++) {
            List<Cell> finRow = new ArrayList<>();
            finRow.addAll(commonRel.getRow(rowObject.get(i).get(0)));

            List<Cell> rel1Shortened = new ArrayList<>(rel1.getRow(rowObject.get(i).get(1)));
            List<Cell> rel2Shortened = new ArrayList<>(rel2.getRow(rowObject.get(i).get(2)));

            for (String attr : togAttrs) {
                rel1Shortened.remove(rel1.getAttrIndex(attr));
                rel2Shortened.remove(rel2.getAttrIndex(attr));
            }
            finRow.addAll(rel1Shortened);
            finRow.addAll(rel2Shortened);
             boolean unique = true;
             for (int k = 0; k < finRel.getSize(); k++) {
                 if (finRel.getRow(k).equals(finRow)) {
                     unique = false;
                     break;
                 }
             }
             if (unique) {
                 finRel.insert(finRow);
             }
        }

        return finRel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        for (int i = 0; i < rel1.getAttrs().size(); i++) {
            for (int j = 0; j < rel2.getAttrs().size(); j++) {
                if (rel1.getAttrs().get(i).equals(rel2.getAttrs().get(j))) {
                    throw new IllegalArgumentException("Rel1 and Rel2 have common attributes");
                }
            }
        }
        
        
        if (p instanceof PredicateImpl) {
            Relation selectRel = select(rel1, p);
            Relation select2Rel = select(rel2, p);
            Relation joinRel = cartesianProduct(selectRel, select2Rel);
            return joinRel;
        } else {
            Relation join = cartesianProduct(rel1, rel2);
            return select(join, p);
        }
        
        

        
        
    }

    private boolean rowExists(Relation rel, List<Cell> row) {
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> OriginalRow = rel.getRow(i);
            if (OriginalRow.equals(row)) {
                return true;
            }
        }
        return false;
    }
}
