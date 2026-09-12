public class App {
    public static void main(String[] args) {
        try{
            Canvas gameBoard = new Canvas();
        }catch(Exception e){
            System.err.println("Failed to start the game: " + e);
        }
    }
}
