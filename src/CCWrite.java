import cards.TiledCard;
import cards.UntiledCard;

public class CCWrite {

    public static void main(String[] args) throws Exception{
        UntiledCard untiledCard = new UntiledCard(50,100);
        untiledCard.fillCard();
        //System.out.println(untiledCard.toString());
        TiledCard tiledCard = new TiledCard(10, 20, 53732672,10256100,
                15,50,50,0);
        tiledCard.tileCard(untiledCard);
        tiledCard.writeInfoFile();
        tiledCard.writeTileFiles();
        System.out.println(tiledCard.toString());
    }
}
