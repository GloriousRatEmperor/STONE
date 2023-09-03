package components;

import jade.GameObject;
import jade.Prefabs;
import jade.Window;

public class QuestionBlock extends Block {
    public QuestionBlock Clone(){
        return new QuestionBlock();
    }
    private enum BlockType {
        Coin,
        Powerup,
        InvinciAbility
    }

    public BlockType blockType = BlockType.Coin;

    @Override
    void playerHit(PlayerController playerController) {
        switch(blockType) {
            case Coin:
                doCoin(playerController);
                break;
            case Powerup:
                doPowerup(playerController);
                break;
            case InvinciAbility:
                doInvinciAbility(playerController);
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("setInactive");
            this.setInactive();
        }
    }

    private void doInvinciAbility(PlayerController playerController) {
    }

    private void doPowerup(PlayerController playerController) {
        if (playerController.isSmall()) {
            spawnMushroom();
        } else {
            spawnFlower();
        }
    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(this.gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }

    private void spawnMushroom() {
        GameObject mushroom = Prefabs.generateMushroom();
        mushroom.transform.position.set(gameObject.transform.position);
        mushroom.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(mushroom);
    }

    private void spawnFlower() {
        GameObject flower = Prefabs.generateFlower();
        flower.transform.position.set(gameObject.transform.position);
        flower.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(flower);
    }
}
