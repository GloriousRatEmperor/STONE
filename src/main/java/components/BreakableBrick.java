package components;

import util.AssetPool;

public class BreakableBrick extends Block {
    public BreakableBrick Clone(){
        return new BreakableBrick();
    }

    @Override
    void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()) {
            AssetPool.getSound("assets/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
