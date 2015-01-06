package com.example.automob;

public class Instruction {

	public static final byte FLAG_FRONT = -128;
	public static final byte FLAG_BACK = 64;
	public static final byte FLAG_LEFT = 32;
	public static final byte FLAG_RIGHT = 16;
	public static final byte FLAG_TOQUE = 8;
	public static final byte FLAG_SPEED = 7;
	
	public static final byte SFLAG_FRONT = 7;
	public static final byte SFLAG_BACK = 6;
	public static final byte SFLAG_LEFT = 5;
	public static final byte SFLAG_RIGHT = 4;
	public static final byte SFLAG_TOQUE = 3;

	private byte _inst;
	
	public Instruction(int front, int back, int left, int right, 
			int toque, int speed){
		_inst |= front << SFLAG_FRONT;
		_inst |= back << SFLAG_BACK;
		_inst |= left << SFLAG_LEFT;
		_inst |= right << SFLAG_RIGHT;
		_inst |= toque << SFLAG_TOQUE;
		_inst |= (speed & FLAG_SPEED);
		
		if(front+back >= 2 || left+right >= 2)
			_inst &= 0;
		
	}
	
	public Instruction() { _inst=0; }
	public Instruction(byte inst) { _inst=inst; }
	
	public byte get_inst(){ return (byte)(_inst&0xff);}
}
