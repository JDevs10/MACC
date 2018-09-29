// constants won't change. They're used here to set pin numbers:
const int buttonPin = 2;     // the number of the pushbutton pin
const int ledPin =  13;      // the number of the LED pin

//Une variable qui va stocker l'état de la valeur du bouton-poussoir :

// variables will change:
int buttonState = 0;         // variable for reading the pushbutton status

//Le void setup() execute un fois, ici permet etablir la vitesse 9600 baud, initialiser la broche LED en sortie et initialiser la broche bouton poussoir en entre.

void setup() {
  //initialize the serial speed
  Serial.begin(9600);
  // initialize the LED pin as an output:
  pinMode(ledPin, OUTPUT);
  // initialize the pushbutton pin as an input:
  pinMode(buttonPin, INPUT);
}

//Ici le void loop() où exécute dans une boucle la partie du code qui envoye le message et allumer la LED rouge : 

void loop() {

//Lire la valeur de l'état du bouton :

  // read the state of the pushbutton value:
  buttonState = digitalRead(buttonPin);

 //Si le bouton est appui envoie le message puis allume la LED:

  // check if the pushbutton is pressed. If it is, the buttonState is HIGH:
  if (buttonState == HIGH) {
    // turn LED on:
    digitalWrite(ledPin, HIGH);
    Serial.peek();
    Serial.print("ABCD");
    delay(5000);

//sinon la LED est éteint et arduino envoie rien :
  } else {
    // turn LED off:
    digitalWrite(ledPin, LOW);
  }
}
