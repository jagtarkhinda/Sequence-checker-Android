// This #include statement was automatically added by the Particle IDE.
#include <InternetButton.h>
#include "math.h"

//#include "InternetButton/InternetButton.h"


InternetButton b = InternetButton();
int seq[4];
int userseq[4];
String firstseq = "";
String secondseq = "";
float jumps;
int done;
int percentage;

int initialZ;

void setup() {
    b.begin();
    
    //making sequence
    seq[0] = 11;
    seq[1] = 3;
    seq[2] = 6;
    seq[3] = 9;
      
      //1
         for (int i = 1; i <= 3; i++) {
                  b.allLedsOn(0,50,50);
                  delay(200);
                  b.allLedsOff();
                  delay(200);
         }
         //2
         b.playSong("G3,8,A5,8,C3,8,G5,8,G3,8");
         //3
         b.allLedsOff();
         //4
         delay(800);
         //5
         b.ledOn(1, 0, 50, 50);
          b.ledOn(10, 0, 50, 50);
         
        // firstseq = String(firstseq + String(seq[i]));
                 
    firstseq = "11369";
     Particle.function("endGame", endGame);
     Particle.function("restart", reStartGame);
}

void loop(){

      if(b.buttonOn(1)){
          delay(300);
        secondseq = String(secondseq + "11");
          b.ledOn(11, 0, 50, 50);
     }
     
        if(b.buttonOn(2)){
             delay(300);
        secondseq = String(secondseq + "3");
         b.ledOn(3, 0, 50, 50);
     }
     
        if(b.buttonOn(3)){
               delay(300);
        secondseq = String(secondseq + "6");
          b.ledOn(6, 0, 50, 50);
     
     }
     
        if(b.buttonOn(4)){
             delay(300);
        secondseq = String(secondseq + "9");
         b.ledOn(9, 0, 50, 50);
     }
     

}
    //method to end the game
    int endGame(String command)
    {
    
        done = atoi(command.c_str());
       if(done == 1)
       {
                  Particle.publish("secondseq", secondseq);
                   Particle.publish("firstseq", firstseq);
        }
        //if jump goal already set, do not accept another input
        // Particle.publish("firstseq", firstseq);
        }
        
        int reStartGame(String command)
        {
             done = atoi(command.c_str());
            if(done == 1)
            {
                b.allLedsOff();
                delay(100);
                 
            
             for (int i = 0; i < 4; i++) {
                 b.ledOn(seq[i], 0, 50, 50);
                 delay(100);
             }
                  delay(800);
                    b.allLedsOff();
                    secondseq = "";
                 
            
            }
            if(done == 2)
            {
                 b.allLedsOff();
                delay(100);
                 
            
             for (int i = 0; i < 4; i++) {
                 b.ledOn(seq[i], 0, 50, 50);
                 delay(50);
             }
                  delay(50);
                    b.allLedsOff();
                    secondseq = "";
                 
            }
            // Particle.publish("firstseq", firstseq);
       
            
        }
       
    // }