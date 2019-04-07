#include "mbed.h"
#include "WiiNunchuck.h"

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
Serial pc(USBTX, USBRX);
Serial bt(D1, D0);   //블루투스 설정 PA_13이 Tx, PA_14가 Rx

/* Private function prototypes -----------------------------------------------*/

/* Private functions ---------------------------------------------------------*/
/**
   * @brief  Main Function
   * @param  None
   * @retval None
   */
WiiNunchuck wi=WiiNunchuck::WiiNunchuck(A4,A5);
DigitalOut a(A3);
DigitalOut b(A2);
int main(void)
{
    a=1;
    b=0;
    pc.baud(9600);
    bt.baud(9600);
//    pc.printf("Hello World!\n\r");
//    bt.printf("Hello World!\r\n");
    printf("Hello");
    //pc.printf("ewew");
//    
    while(1){
//        pc.printf("ewew\n");
//        printf("Hello\n");
        printf("%d %d %d %d %d %d %d\n",wi.joyx(),wi.joyy(),wi.accx(),wi.accy(),wi.accz(),wi.buttonc(),wi.buttonz());
        bt.printf("%d %d %d %d %d %d %d\n",wi.joyx(),wi.joyy(),wi.accx(),wi.accy(),wi.accz(),wi.buttonc(),wi.buttonz());
        /*
        if(bt.readable())
        {
            ch=bt.getc();
            pc.printf("%c",ch);
            bt.printf("%c",ch);
        }
        
        else if(pc.readable())
        {
            ch=pc.getc();
            bt.printf("%c",ch);
            pc.printf("%c",ch); 
        }*/
    }
}
