/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package projects.wsn1.nodes.nodeImplementations;

import java.awt.Color;
import java.util.Random;

import projects.wsn1.nodes.messages.WsnMsg;
import projects.wsn1.nodes.timers.WsnMessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.nodes.Node;
import sinalgo.nodes.Node.NodePopupMethod;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;

public class SimpleNode extends Node {

    public Node proximoNoAteEstacaoBase = null; //nó que é usado para chegar a estação base
    public Integer sequenceNumber = 0; //armazena o número de sequencia da ultima msg recebida
    public Boolean flagEnviar = false; 
            
    @Override
    public void handleMessages(Inbox inbox) {
        while (inbox.hasNext()) {
            Message message = inbox.next();
            
            if (message instanceof WsnMsg) {
                Boolean encaminhar = Boolean.TRUE;
                Boolean auxRoteamento = Boolean.TRUE;
                WsnMsg wsnMessage = (WsnMsg) message;     
                
                if (wsnMessage.forwardingHop.equals(this)) {
                    encaminhar = Boolean.FALSE;
                    
                } else if (wsnMessage.tipoMsg.equals(0)) {//tipo 0: criar rota
                    if (proximoNoAteEstacaoBase == null) {//primeira vez recebendo o pacote de rota
                        proximoNoAteEstacaoBase = inbox.getSender(); // salva a referencia
                        sequenceNumber = wsnMessage.sequenceID;
                        
                    } else if (sequenceNumber < wsnMessage.sequenceID) { //se o num é diferente atualiza a seq.
                        sequenceNumber = wsnMessage.sequenceID;
                        
                    } else { //se não é pacote novo, nem sequencial, dropa o pacote
                        encaminhar = Boolean.FALSE;
                    }
                    
                } else if(wsnMessage.tipoMsg.equals(1)) { //tipo 1: pacote de dados, se for pra mim, continuo roteando 
                    if(wsnMessage.destino.equals(this)){
                        wsnMessage.destino = this.proximoNoAteEstacaoBase;
                        auxRoteamento = Boolean.FALSE;
                        //broadcast(wsnMessage);
                    }else{
                        encaminhar = Boolean.FALSE;
                    }
                }
                
                if (encaminhar) {
                    if(auxRoteamento){//aceita o pacote 0, envia broadcast
                        System.out.println("Nó " + this.ID + " recebe dados do nó " + wsnMessage.origem.ID);
                    }
                    wsnMessage.forwardingHop = this;
                    broadcast(wsnMessage);
                }
            }
        }
    }
    
    public void setFlag(Boolean bool) { //ativado pelo WsnMessageTimer
    	this.setColor(new Color(255,0,0));
    	this.flagEnviar = bool;
    }
        
    public Node getProximoNoAteEstacaoBase() {
    	return this.proximoNoAteEstacaoBase;
    }

    @Override
    public void preStep() { //criação da mensagem
    	if (proximoNoAteEstacaoBase != null && !flagEnviar) {
            this.sequenceNumber += 1;
            WsnMsg msg = new WsnMsg(this.sequenceNumber, this, this.proximoNoAteEstacaoBase, this, 1);
            WsnMessageTimer timer = new WsnMessageTimer(msg);
            timer.startRelative(new Random().nextInt(100)+100, this);
            flagEnviar = true;
    	}
    }

    @Override
    public void init() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
    public int numSequencePacote(){
        return sequenceNumber;
    }

}
