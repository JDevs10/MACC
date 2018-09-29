//the message to send to android screen
String message = "Decoded NEC(1): Value:FD807F (32 bits) Raw samples(68): Gap:40826 Head: m8850 s4450 0:m500 s600 1:m550 s550 2:m500 s600 3:m550 s600  4:m500 s600 5:m500 s600 6:m500 s600 7:m550 s550  8:m500 s1750 9:m500 s1700 10:m500 s1700 11:m550 s1650  12:m550 s1700 13:m500 s1700 14:m500 s600 15:m550 s1700 16:m500 s1700 17:m500 s600 18:m500 s600 19:m500 s600  20:m550 s600 21:m450 s650 22:m500 s600 23:m500 s600 adrduino is ending";

const int buttonPin = 2;     // the number of the pushbutton pin
const int ledPin =  13;      // the number of the LED pin

// variables will change:
int buttonState = 0;

void setup() {
  Serial.begin(9600);
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);
}

void loop() {
  // read the state of the pushbutton value:
  buttonState = digitalRead(buttonPin);

  // check if the pushbutton is pressed. If it is, send message and the buttonState is HIGH.
  if (buttonState == HIGH) {
    //send the message character by character with a fast delay about 50
    for(int index = -1; index != message.length(); index++){
        Serial.print(message[index]);
        delay(10);
      }
    // turn LED on:
    digitalWrite(ledPin, HIGH);
    delay(1000);
  } else {
    //send nothing
    // turn LED off:
    digitalWrite(ledPin, LOW);
    
  }
}
