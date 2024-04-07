package Abilitiess;

import util.Img;

public class Move extends Ability{
    @Override
    public Move Copy(){
        Move Move=new Move(this.name,id);
        return Move;
    }
    public Move(String a, int id) {
        super(a,id);
        mp=0;
        sprite = Img.get("ability0");
    }
}
