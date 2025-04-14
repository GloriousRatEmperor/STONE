package Multiplayer;

import Multiplayer.DataPacket.Ccast;
import Multiplayer.DataPacket.Cmessage;
import Multiplayer.DataPacket.Cmove;
import components.gamestuff.Message;
import jade.KeyListener;
import jade.MouseListener;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static components.gamestuff.KeyControls.getQmove;
import static components.gamestuff.KeyControls.setQmove;
import static jade.Window.get;
import static jade.Window.getScene;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;

public class Sender {

    public static void sendMove(Vector2f position, List<Integer> Ids){

        Cmove clientData = new Cmove();
        clientData.setTargetID(-1);
        clientData.setName("Move");
        clientData.setGameObjects(Ids);
        List<Float> pos = new ArrayList<>();
        pos.add(position.get(0));
        pos.add(position.get(1));
        clientData.setPos(pos);
        clientData.setShiftCommand(KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT));
        if(getQmove()){
            clientData.setQmove(true);
            setQmove(false);
        }else{
            clientData.setQmove(false);
        }
        get().requests.add(clientData);
    }
    public static void sendMove(Vector2f position,List<Integer> Ids,int targetId){

        Cmove clientData = new Cmove();
        clientData.setTargetID(targetId);
        clientData.setName("Move");
        clientData.setGameObjects(Ids);
        List<Float> pos = new ArrayList<>();
        pos.add(position.get(0));
        pos.add(position.get(1));
        clientData.setShiftCommand(KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT));
        if(getQmove()){
            clientData.setQmove(true);
            setQmove(false);
        }else{
            clientData.setQmove(false);
        }
        clientData.setPos(pos);
        get().requests.add(clientData);
    }
    public static void sendCast(List<Integer> Ids,int AbilityID){
        Ccast clientData = new Ccast();
        clientData.setGameObjects(Ids);
        clientData.setAbilityID(AbilityID);
        List<Float> pos= new ArrayList<Float>();
        pos.add(MouseListener.getWorld().x);
        pos.add(MouseListener.getWorld().y);
        clientData.setPos(pos);
        clientData.setShiftCommand(KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT));
        int id=get().imguiLayer.getPropertiesWindow().getPickingTexture().readPixel((int)MouseListener.getScreenX(),(int)MouseListener.getScreenY());
        if( getScene().getGameObject(id)!=null){
            clientData.setTarget(id);
        }else{
            clientData.setTarget(-1);
        }


        get().requests.add(clientData);
    }
    public static void sendMessage(Message message){
        sendMessage(message.msg,message.color);

    }
    public static void sendMessage(String message,int r,int g, int b,int a){ //0 to 255
        sendMessage(message,util.Img.color(r,g,b,a));
    }
    public static void sendMessage(String message,int color){
        Cmessage clientData=new Cmessage();
        clientData.setMessage(message);
        clientData.setColor(color);
        get().requests.add(clientData);
    }
    public static void sendCastNotarget(List<Integer> Ids,int AbilityID){

        Ccast clientData = new Ccast();
        clientData.setGameObjects(Ids);
        clientData.setAbilityID(AbilityID);
        List<Float> pos= new ArrayList<Float>();
        pos.add(MouseListener.getWorld().x);
        pos.add(MouseListener.getWorld().y);
        clientData.setShiftCommand(KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT));
        clientData.setTarget(-1);
        clientData.setPos(pos);

        get().requests.add(clientData);
    }
}
