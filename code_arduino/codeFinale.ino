#include <WiFi.h>
#include <FirebaseESP32.h>
#include <LiquidCrystal.h>

#include "EEPROM.h"
#include "DHTesp.h"
#include <Keypad.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

WiFiClient espClient;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "europe.pool.ntp.org", 3600, 60000);

#define EEPROM_SIZE 4

#define FIREBASE_HOST "https://esp32-data-27fbf-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "eN2Dj2xZDJJlkbODmYelZ2jqRdVw41XYDf0J5ixs"
#define WIFI_SSID "HUAWEI Y8s"//Gnet-308187
#define WIFI_PASSWORD "123456789a"//73301118
//.........................
FirebaseData firebaseData;
String path1 = "/CapteurGaz";
String path2 = "/CapteurTemperatureEtHumidity";
String path3 = "/CapteurLazer";
String path4 = "/Etat_Porte";
String path5 = "/histoire";
String path6 = "";

//..............................................
const byte ROWS = 4; //four rows
const byte COLS = 3; //three columns
char keys[ROWS][COLS] = {
    {'1','2','3'},
    {'4','5','6'},
    {'7','8','9'},
    {'*','0','#'}
};
byte rowPins[ROWS] = {13 , 12, 14, 32}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {26, 25, 33}; //connect to the column pinouts of the keypad
// initialize the library with the numbers of the interface pins
Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );

//.........................................................................
LiquidCrystal lcd(19, 23, 18, 17, 16, 21);
//LiquidCrystal(rs, rw, enable, d0, d1, d2, d3, d4, d5, d6, d7)
//........................................
DHTesp dht;
//........

int etat_port = 36;
int capteurTemHum = 15;
int capteurGaz = 34;
int buzzer =22;
int buzzer1 =4;
String password_EEPROM="";
String password_input="";


TaskHandle_t Task1;
TaskHandle_t Task2;

void setup() {
  Serial.begin(115200); 

// connect to wifi. 
 WiFi.begin(WIFI_SSID, WIFI_PASSWORD); 
 Serial.print("connecting"); 
 while (WiFi.status() != WL_CONNECTED) { 
   Serial.print("."); 
   delay(500); 
 } 
 Serial.println(); 
 Serial.print("connected: "); 
 Serial.println(WiFi.localIP()); 
 Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);   
 
 timeClient.begin();
 
//..........................................
   lcd.begin(16, 2);
   
//.............................
  
   pinMode(buzzer,OUTPUT);
   pinMode(buzzer1,OUTPUT);
   pinMode(etat_port,INPUT);
//.....................................................
  dht.setup(15, DHTesp::DHT22);
//............................

  EEPROM.begin(EEPROM_SIZE);
   for (int i = 0; i < EEPROM_SIZE; i++)
    {
      byte x=(byte(EEPROM.read(i)));
      password_EEPROM=password_EEPROM+x;
     } 
     Serial.println(password_EEPROM);

  //create a task that will be executed in the Task1code() function, with priority 1 and executed on core 0
  xTaskCreatePinnedToCore(
                    Task1code,   /* Task function. */
                    "Task1",     /* name of task. */
                    10000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    1,           /* priority of the task */
                    &Task1,      /* Task handle to keep track of created task */
                    0);          /* pin task to core 0 */                  
  delay(500); 

  //create a task that will be executed in the Task2code() function, with priority 1 and executed on core 1
  xTaskCreatePinnedToCore(
                    Task2code,   /* Task function. */
                    "Task2",     /* name of task. */
                    10000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    1,           /* priority of the task */
                    &Task2,      /* Task handle to keep track of created task */
                    1);          /* pin task to core 1 */
    delay(500); 
}

//Task1code: blinks an LED every 1000 ms
void Task1code( void * pvParameters ){
  Serial.print("Task1 running on core ");
    Serial.println(xPortGetCoreID());
  lcd.setCursor(0,0);
  lcd.print(" <<Entrer Code>> ");
   lcd.setCursor(0,1);
  for(;;){
     char key = keypad.getKey(); 
 
  
if(key){
 Serial.print(key);
  digitalWrite(buzzer, HIGH);   
  delay(100);            
  digitalWrite(buzzer, LOW);  
                 
if(key == '*') {
  password_input = "";
  lcd.clear();
   lcd.setCursor(0,0);
  lcd.print(" <<Entrer Code>> ");
   lcd.setCursor(0,1);
 
        
    } else if(key == '#') {
             
             if( password_EEPROM == password_input) {
           
              
              lcd.clear();
              lcd.setCursor(3,1);
              lcd.print("<<Success>>");
              delay(50);
               for( int i = 1; i <= 2; i++ ){
                  digitalWrite(buzzer, HIGH);   
                  delay(70);            
                  digitalWrite(buzzer, LOW);  
                  delay(70);      
                } 
              
               lcd.clear();
               lcd.setCursor(0,0);
                lcd.print(" <<Entrer Code>> ");
                 lcd.setCursor(0,1);
            } else {
              lcd.clear();
              lcd.setCursor(5,1);
                  lcd.print("incorrect");
                  delay(50);
                    for( int i = 1; i <= 2; i++ ){
                       digitalWrite(buzzer, HIGH);   
                       delay(200);            
                       digitalWrite(buzzer, LOW);  
                       delay(100);      
                     } 
                    lcd.clear();
                     lcd.setCursor(0,0);
                     lcd.print(" reessayer!!");
                     lcd.setCursor(0,1);
                  }
          password_input = ""; // clear input password
      } else {
              
               password_input += key;
               
                lcd.print("*");   
            }
            }
  } 
}

//Task2code: blinks an LED every 700 ms
void Task2code( void * pvParameters ){
  Serial.print("Task2 running on core ");
  Serial.println(xPortGetCoreID());
 int etat_port_local = 0;
  for(;;){
  
 if (digitalRead(etat_port) == LOW){
  if(etat_port_local == 0){
   Firebase.setString(firebaseData, path4,"Porte fermée");
  
               timeClient.update();
               String date = "fermée le :"+timeClient.getFormattedTime();
               Firebase.pushString(firebaseData,path5,date);
                  etat_port_local = 1;
                  delay(100);
               }
               }
                           
  if (digitalRead(etat_port) == HIGH){
  if(etat_port_local == 1){
  Firebase.setString(firebaseData, path4 ,"Porte ouverte");
  
               timeClient.update();
               String date ="ouverte le :" +timeClient.getFormattedTime();
               Firebase.pushString(firebaseData,path5,date);
               etat_port_local = 0;
               delay(100);
               }
               }


  
 if (Firebase.getString(firebaseData, "/mdp")) {
      String trame = firebaseData.stringData();
   
   Serial.println(trame);
   changer_pwd(trame);
 }


   TemHum(capteurTemHum);
  Gaz(capteurGaz);
  }
}

void loop() {
  
}

void changer_pwd( String N_mdp){
  
      for (int i = 0 ; i < EEPROM.length() ; i++) {
       EEPROM.write(i, 0);
       }
       int A = String(N_mdp.charAt(0)).toInt();
       int B = String(N_mdp.charAt(1)).toInt();
       int C = String(N_mdp.charAt(2)).toInt();
       int D = String(N_mdp.charAt(3)).toInt();
         EEPROM.write(0,A);
         EEPROM.write(1,B);
         EEPROM.write(2,C);
         EEPROM.write(3,D);
         EEPROM.commit();
      
  }
  
 

void Gaz(int pin){
 int capteur = analogRead(pin);
 
  Serial.print("Gaz: ");
  Serial.println(capteur);
  Firebase.setInt(firebaseData, path1,capteur);
 if(capteur>3000)
 {
  digitalWrite(buzzer1, HIGH); 
  
  }else {
    digitalWrite(buzzer1,LOW);
    }
 }
  
  
void TemHum(int pin){
 int capteur = analogRead(pin);
  float temperature = dht.getTemperature();
  float humidity = dht.getHumidity();
  Serial.print("Temperature: ");
  Serial.println(temperature);
  Serial.print("Humidity: ");
  Serial.println(humidity);
  
    Firebase.setInt(firebaseData, path2 + "/Temperature",temperature);
    Firebase.setInt(firebaseData, path2 + "/Humidity",humidity);
     }
  

  
