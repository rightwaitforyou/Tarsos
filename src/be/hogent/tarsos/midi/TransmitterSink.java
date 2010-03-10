/**
 * 
 */
package be.hogent.tarsos.midi;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * @author Joren Six
 * Sends messages to a list of Transmitters
 */
public class TransmitterSink implements Transmitter{
	
	private final Transmitter[] transmitters;
	public TransmitterSink(Transmitter... transmitters){
		this.transmitters = transmitters;
	}
	
	@Override
	public void close() {
		for(Transmitter transmitter:transmitters){
			transmitter.close();
		}
	}

	@Override
	public Receiver getReceiver() {
		Receiver receiver = null;
		if(transmitters.length != 0){
			receiver = transmitters[0].getReceiver();
			for(int i = 1; i < transmitters.length ; i++){
				if(transmitters[i].getReceiver() != receiver){
					throw new Error("Each Transmitter in the TransmitterSink should have the same Receiver");
				}
			}
		}
		return receiver;
	}

	@Override
	public void setReceiver(Receiver receiver) {
		for(Transmitter transmitter:transmitters){
			transmitter.setReceiver(receiver);
		}		
	}	
}