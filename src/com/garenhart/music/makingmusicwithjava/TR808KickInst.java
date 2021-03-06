package com.garenhart.music.makingmusicwithjava;

import jm.audio.io.*;
import jm.audio.synth.*;
import jm.audio.Instrument;
import jm.audio.synth.*;
import jm.music.data.Note;
import jm.audio.AudioObject;

/**
 * A basic analogue drum instrument implementation
 * which combines white noise and a sine wave 
 * as in the Roland TR808 drum machine.
 * @author Andrew Brown
 */

public final class TR808KickInst extends Instrument{
	//----------------------------------------------
	// Attributes
	//----------------------------------------------
	private int channels;
	private int sampleRate;
	private int noiseType;
	private int cutoff;
    protected Volume gain;
    private double gainLevel = 1.0; //5.0;
    private SampleOut sout;

	//----------------------------------------------
	// Constructor
	//----------------------------------------------

	public TR808KickInst() {
            this(22050, 100);
    }
    
    public TR808KickInst(int sampleRate) {
        this(sampleRate, 100);
    }
    
        
    public TR808KickInst(int sampleRate, int cutoff){
	    this(sampleRate, cutoff, 1);
	}
	

	public TR808KickInst(int sampleRate, int cutoff,  int channels){
		this(sampleRate, cutoff, channels, Noise.WHITE_NOISE);
	}
	
	
	public TR808KickInst(int sampleRate, int cutoff, int channels, int noiseType){
		this.sampleRate = sampleRate;
		this.channels = channels;
		this.noiseType = noiseType;
		this.cutoff = cutoff;
	}

	//----------------------------------------------
	// Methods 
	//----------------------------------------------
	   
	/**
	 * Initialisation method used to build a chain of the objects that
	 * this instrument will use.
	 */
	public void createChain(){
		//create noise source
		Noise noise = new Noise(this,  noiseType, this.sampleRate, this.channels);
        Volume noiseAmp = new Volume(noise, 0.3); // osc loudness
        // create oscillator tone
        Envelope pitchEnv = new Envelope(this, this.sampleRate, this.channels, 
            new double[] {0.0, 100.0, 0.5, 10.0, 1.0, 1.0});
		Oscillator osc = new Oscillator(pitchEnv, Oscillator.SINE_WAVE, Oscillator.FREQUENCY);

        // combine the sounds
        Add add = new Add(new AudioObject[] {noiseAmp, osc});
        // amp envelope
        //ADSR env = new ADSR(add, 1, 100, 0.5, 100);
        Envelope env = new Envelope(add, new double[] {0.0, 0.0, 0.05, 3.0, 0.2, 0.1, 1.0, 0.0});
        // filter the result
		Filter filt = new Filter(env, cutoff, Filter.LOW_PASS);
        // dynamic respose to note values
		Volume vol = new Volume(filt);
        // increase level
        gain = new Volume(vol, gainLevel);
        // pan if required
		//StereoPan span = new StereoPan(gain);
        // write to file
		if(output == RENDER) sout = new SampleOut(gain);
	}	
}
