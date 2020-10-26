package projects.wsn1.nodes.nodeImplementations;

import java.awt.Color;

import projects.wsn1.nodes.messages.WsnMsg;
import projects.wsn1.nodes.timers.WsnMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;


public class SinkNode extends Node {

    private Node proximoNoAteEstacaoBase = null;
    
    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message message = inbox.next();
            if (message instanceof WsnMsg) {
                WsnMsg wsnMessage = (WsnMsg) message;
                       
                if (wsnMessage.tipoMsg == 1) {
                    if(wsnMessage.destino == this){
                	SimpleNode org = (SimpleNode) wsnMessage.origem;
                        System.out.println("Sink recebe dados do nó " + wsnMessage.origem.ID + " - pacote " + wsnMessage.sequenceID);
                	org.setColor(new Color(0,0,0));
                    }
                }
            }
        }	
    }

    @Override
    public void preStep() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @NodePopupMethod(menuText = "Construir Arvore de Roteamento")
    public void construirRoteamento() {
        this.proximoNoAteEstacaoBase = this;
        WsnMsg wsnMessage = new WsnMsg(1, this, null, this, 0); 
        WsnMessageTimer timer = new WsnMessageTimer(wsnMessage);
        timer.startRelative(1, this);
    }

    @Override
    public void init() {
    	this.setColor(new Color(106,90,205));
    }

    @Override
    public void neighborhoodChange() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void postStep() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void checkRequirements() throws WrongConfigurationException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
