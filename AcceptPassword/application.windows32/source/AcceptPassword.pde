import saito.objloader.*;
import JavaLib.*;
import processing.opengl.*;

OBJModel model1;
float rotY, destRotY, lastRotY;
int currIndex, currImage, addedImage, addedImageColor;
int sequence[], sequenceLength;
int sequenceX[], sequenceY[];

int translateZ, animateTranslateZ;
int animateStatus;
PImage img[];

String password, tempPassword;
boolean acceptPassword;
boolean acceptCCP;

PFont font; 

void setup()
{ 
  size(1000, 550, P3D);
  frameRate(30);
 
  try {
  model1 = new OBJModel(this);
  model1.load("arena.obj");
  new LoadForm();
  }catch(Exception e) {
    ;
  }

  img = new PImage[16];

  for(int i=0;i<16;i++) {
    img[i] = loadImage("" + i + ".PNG");
  }
  rotY = destRotY = lastRotY = 0;

  translateZ = 0;
  animateStatus = 0;
  animateTranslateZ = 5;

  currIndex = 0;
  currImage = -1;
  addedImage = -1;
  addedImageColor = 0;

  sequence = new int[100];
  sequenceX = new int[100];
  sequenceY = new int[100];
  sequenceLength = 0;

  password = "";
  tempPassword = "";

  acceptPassword = false;
  acceptCCP = false;

  font = loadFont("Arial.vlw"); 
  textFont(font, 10);
  textAlign(CENTER); 
}

void draw()
{
  background(0,0,50);
  lights( );

  pushMatrix();
  // fixed to center
  translate(width/2,height/2 + 20, animateTranslateZ);

  // according to user movements...  
  translate(0,20,translateZ);

  // fixed to flip model...
  rotateX(3.14);

  // rotate by user selected amount...
  rotateY(rotY);

  // calculate current image in front of user...
  acceptCCP = false;
  if(translateZ==60) {
    currImage = currIndex;
    while(currImage < 0) {
      currImage += 32;
    }
    currImage = currImage % 32;
    if((currImage%2)==0) {
      currImage = currImage / 2;
      acceptCCP = true;
    }
    else {
      currImage = -1;
    }
  }
  else {
    currImage = -1;
  }

  acceptPassword = false;
  for(int i=0;i<16;i++) {
    if(currImage==i) {

      if(i==0) {
        acceptPassword = true;
      }

      stroke(100,255,100);
    }
    else {
      stroke(100,100,100);
    }
    pushMatrix();
    rotateX(-3.14);
    rotateY(i * 6.28/16);  
    translate(0,0,-100);
    scale(0.1);
    rect(-img[i].width/2 - 10, -img[i].height/2 - 300 - 10, img[i].width + 20, img[i].height+20);

    if(addedImageColor>40) {
      if(addedImage==i) {
        stroke(addedImageColor,0,0);
        addedImageColor -= 10;
        rect(-img[i].width/2 - 5, -img[i].height/2 - 300 - 5, img[i].width + 10, img[i].height + 10);


      }
    }
    else {
      addedImage = -1;
      addedImageColor = 0;
    }

    image(img[i],-img[i].width/2,-img[i].height/2-300);

    if(acceptPassword) {
      fill(255,0,0);
      if(i==0) {
        text("Password:",60 + -img[i].width/2 - 10, 50 + -img[i].height/2 - 300 - 10, 20);
        tempPassword = "";
        for(int j=0;j<password.length();j++) {
          tempPassword += "*";
        }
        text(tempPassword,60 + -img[i].width/2 - 10, 70 + -img[i].height/2 - 300 - 10, 20);
      }
      noFill();
    }

    popMatrix();
  }

  scale(10);
  stroke(100,100);
  noStroke();
  model1.drawMode(POLYGON);
  model1.draw();
  popMatrix();

  pushMatrix();
  ellipseMode(CENTER);
  noFill();
  stroke(255,100,100,100);
  translate(75,height-75,0);
  ellipse(0,0,75,75);
  rotateZ(-rotY);
  line(0,0,0,-35);
  popMatrix();

  destRotY = (currIndex * 6.28/32);
  if(rotY != destRotY) {

    int diff = 0;    
    if(animateStatus==1) {
      // faster rotation...
      rotY += (destRotY-rotY)/3;
      diff = (int)(abs(rotY-destRotY) * 100);
    }
    else if(animateStatus==2) {
      // slower rotation
      rotY += (destRotY-rotY)/6;
      diff = (int)(abs(rotY-destRotY) * 1000);
    }

    if(diff==0) {
      rotY = destRotY;
    }
  }

  if(animateStatus==0 && animateTranslateZ<475 && millis() > 1000) {
    animateTranslateZ += 10;
    if(animateTranslateZ==475) {
      animateStatus = 1;
    }
  }

  if(animateStatus==2) {
    if(translateZ < 0) {
      translateZ += 5;
    }
    else if(translateZ > 0) {
      translateZ -= 5;
    }

    if(rotY==0 && translateZ==0) {
      animateTranslateZ -= 10;
      if(animateTranslateZ < 0) {
        saveSettings();

        delay(500);
        System.exit(0);
      }
    }
  }

}


void keyPressed() {
  if(animateStatus==0 || animateStatus==2) {
    return;
  }

  switch(keyCode) {
  case UP:
    if(translateZ < 60) {
      translateZ += 5;
    }
    break;
  case DOWN:
    if(translateZ > -105) {
      translateZ -= 5;
    }
    break;
  case LEFT:
    currIndex++;
    break; 
  case RIGHT:
    currIndex--;
    break; 
  case ENTER:
    animateStatus = 2;
    currIndex = 0;
    break;
  }

  if(acceptPassword) {
    char ch = (char)key;
    if(Character.isLetterOrDigit(ch)) {
      if(password.length() < 15) {
        password += ch; 
      }
    }

    if(keyCode==BACKSPACE) {
      if(password.length()>0) {
        password = password.substring(0,password.length()-1);
      }
    }
  }
  else {
    if(keyCode=='c' || keyCode=='C') {
      sequenceLength = 0;
    }
  }
}

public void saveBlankSettings() {
  String s[] = new String[1];
  s[0] = "";
  saveStrings("d:\\temp\\password.txt",s);
  saveStrings("d:\\temp\\ccp.txt",s);
}

public void saveSettings() {
  String s[] = new String[1];
  s[0] = password;
  saveStrings("d:\\temp\\password.txt",s);
  
  s = new String[sequenceLength];
  for(int i=0;i<sequenceLength;i++) {
    s[i] = "" + sequence[i] + "," + sequenceX[i] + "," + sequenceY[i];
  }
  saveStrings("d:\\temp\\ccp.txt",s);
}

public void stop() {
  saveSettings();
}


void mousePressed() {
  int x=-1, y=-1;
  if(acceptCCP) {
    x = mouseX - 442;
    y = mouseY - 304;

    if(x<0 || x>115 || y<0 || y>172) {
      x = y = -1;
    }

    if(x!=-1 && currImage!=-1 && currImage!=0) {
        if(sequenceLength < 100) {
          System.out.println("CURR IMAGE: " + currImage + ", MouseX: " + x + ", MouseY: " + y);
          sequence[sequenceLength] = currImage;
          sequenceX[sequenceLength] = x;
          sequenceY[sequenceLength] = y;
          
          sequenceLength++;
          addedImage = currImage;
          addedImageColor = 240;
        }
    }
  }
}


