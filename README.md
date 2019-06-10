# FlappyBird IA
FlappyBird IA Neural Networks and Genectic Algorithm

## Demonstration

- Windows Desktop Resolution:
487px x 827px
![FlappyBird](https://github.com/difusao/FlappyBird/blob/DevCore/android/assets/images/FlappyBirdDesktop.PNG)

- Android Resolution:
2246px x 1080px
![FlappyBird](https://github.com/difusao/FlappyBird/blob/DevCore/android/assets/images/FlappyBirdAndroid.jpg)

## Installation
- AndroidStudio 3.4
- JRE 1.8.0_152

## How does it work
- uma população inicial de 100 individuos é gerada.
- Também é gerada 100 amostras de pesos aleatórios para a rede neural de 2 neurônios de entrada, 2 na camada oculta e 1 neurônio de saída e função de ativação LINEAR.
- Os neurônios da camada de entrada correspondem a distância horizontal do pássaro e a distância vertical do pássaro até o próximo tudo.
- Se o resultado do neurônio da camada de saída for maior que 0.5 então o pássaro pula.

## Genetic Algorithm

## Implementation

### Be aware of a game bug

## Screen Capture on Android
adb shell screenrecord /sdcard/flappybird.mp4
adb shell screencap -p > flappybird.jpg

## Credits
- [Geovani José Malaquias](https://github.com/difusao)
