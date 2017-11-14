/*
 * This class is for storing ID and name of dogs.
 *
 */
package assignment1;

/**
 * This class includes breedID and breedName of Dog
 * 
 * @author  Qing Ge Phoenix
 */
public class Dog {
    
    private int breedID;
    private String breedName;
    
    public Dog(int breedID, String breedName){
        this.breedID = breedID;
        this.breedName = breedName;
        
    }
    
    /**
     * Retrieves breedID
     *
     * @return breedID
     */
    public int getBreedID() {
        return this.breedID;
    }
    
    /**
     * Retrieves breedName
     *
     * @return breedName
     */
    public String getBreedName() {
        return this.breedName;
    }
    
    
}
