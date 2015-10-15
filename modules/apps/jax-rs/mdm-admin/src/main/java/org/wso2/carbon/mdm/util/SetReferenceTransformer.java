package org.wso2.carbon.mdm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class SetReferenceTransformer<T>{
        private List<T> objectsToRemove;
        private List<T> objectsToAdd;

    /**
     * Use the Set theory to find the objects to delete and objects to add

     The difference of objects in existingSet and newSet needed to be deleted

     new roles to add = newSet - The intersection of roles in existingSet and newSet
     * @param currentList
     * @param nextList
     */
        public void transform(List<T> currentList, List<T> nextList){
            TreeSet<T> existingSet = new TreeSet<T>(currentList);
            TreeSet<T> newSet = new TreeSet<T>(nextList);;

            existingSet.removeAll(newSet);

            objectsToRemove = new ArrayList<T>(existingSet);

            // Clearing and re-initializing the set
            existingSet = new TreeSet<T>(currentList);

            newSet.removeAll(existingSet);
            objectsToAdd = new ArrayList<T>(newSet);
        }

        public List<T> getObjectsToRemove() {
            return objectsToRemove;
        }

        public List<T> getObjectsToAdd() {
            return objectsToAdd;
        }
}