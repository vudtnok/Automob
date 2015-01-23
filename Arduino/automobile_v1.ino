const int Fflag = 128;
const int Bflag = 64;
const int Lflag = 32;
const int Rflag = 16;
const int rotation = 8;
const int speed3 = 4;
const int speed2 = 2;
const int speed1 = 1;

int STBY = 10; //standby
char input = 0;
int Direction = 0;
int turn =0;
int Speed = 0;
int Rotate = 0;

//Motor A
int PWMA = 3; //Speed control 
int AIN1 = 9; //Direction
int AIN2 = 8; //Direction

//Motor B
int PWMB = 5; //Speed control
int BIN1 = 11; //Direction
int BIN2 = 12; //Direction

void setup(){
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }
  
  pinMode(STBY, OUTPUT);

  pinMode(PWMA, OUTPUT);
  pinMode(AIN1, OUTPUT);
  pinMode(AIN2, OUTPUT);

  pinMode(PWMB, OUTPUT);
  pinMode(BIN1, OUTPUT);
  pinMode(BIN2, OUTPUT);
  
}

void loop(){
  //turn = 3;
  //Direction = 3;
  //Rotate = 0;
  if(Serial.available()>0){
  input = Serial.read();
  if(input & Lflag)
  {    turn = 0;  }
  else if(input & Rflag)
  {    turn = 1;  }
  else
  {    turn = 3;  }

  if(input & rotation)
  {    Rotate = 255;  }
  else
  {    Rotate = 128;  }
 
  Speed = (input&7);
  Speed = Speed * 36;
 
  if(input & Fflag)
  {    Direction = 0;  }
  else if (input & Bflag)
  {    Direction = 1;  }
  else
  {    Direction = 3;  }
  }

  if( Direction != 3)
  {
    move(1,Speed,Direction);
  }
  else
  {
    move(1,0,3);
  }

  if(turn !=3)
  {
    move(2,Rotate,turn);
  }
  else
  {
    move(2,0,3);
  }
  
}


void move(int motor, int speed, int direction){
  //Move specific motor at speed and direction
  //motor: 0 for B 1 for A
  //speed: 0 is off, and 255 is full speed
  //direction: 0 clockwise, 1 counter-clockwise

  digitalWrite(STBY, HIGH); //disable standby

  boolean inPin1 = LOW;
  boolean inPin2 = HIGH;

  if(direction == 1){
    inPin1 = HIGH;
    inPin2 = LOW;
  }

  if(motor == 1){
    digitalWrite(AIN1, inPin1);
    digitalWrite(AIN2, inPin2);
    analogWrite(PWMA, speed);
  }
  else{
    digitalWrite(BIN1, inPin1);
    digitalWrite(BIN2, inPin2);
    analogWrite(PWMB, speed);
  }
}

void stop(){
  //enable standby  
  digitalWrite(STBY, LOW); 
}

