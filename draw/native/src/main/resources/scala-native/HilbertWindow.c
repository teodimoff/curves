//gradient code: https://github.com/Acry/SDL2-Surfaces/blob/master/src/7.c
//the other is mine.
#include "helper.h"
#include <SDL2/SDL.h>
#include "HilbertWindow.h"


void drawGradientAndPoints(int points,int order,int startFrom,int height,int width,int dotColor, int (*hashFunction)(int), long (*hilbertMapping)(int,int))
{
init();

SDL_SetWindowSize(Window,height,width);
SDL_SetWindowTitle(Window, "Hilbert Window");
SDL_SetWindowResizable(Window, SDL_FALSE);
SDL_SetWindowPosition(Window,SDL_WINDOWPOS_CENTERED,SDL_WINDOWPOS_CENTERED);
screen=SDL_GetWindowSurface(Window);


drawGradientAndPointsInternal(points,order,startFrom,dotColor,hashFunction,hilbertMapping);
SDL_UpdateWindowSurface(Window);

SDL_Event event;
int running=1;

while(running){
	while(SDL_PollEvent(&event)){
		if(event.type == SDL_QUIT){
			running=0;
		}

	}

	// lower loop latency for more updates
	SDL_Delay(16); // in milliseconds

}

exit_();
EXIT_SUCCESS;

}


/*
//this does not work well... we tried to scale the hash if the screen is not power of 2
    int sizeOrder = 1 << order;
    double scaleX = (double)screen->w/sizeOrder;
    double scaleY = (double)screen->h/sizeOrder;

    SDL_Log("%f, %f, %d",scaleX,scaleY,sizeOrder);

int scale(int x,int y,double scaleX,double scaleY){
    return (int)((x*scaleX) + ((y*scaleY) * (screen->pitch / screen->format->BytesPerPixel)));
}
*/

int point(int x, int y){
  return x+(y*(screen->pitch / screen->format->BytesPerPixel));
}

int unpackI32(long packed,int index){
  return (int)((packed & (0xFFFFFFFFL << 32 * index)) >> (32 * index));
}

void drawGradientAndPointsInternal(int points,int order,int startFrom,int dotColor, int (*hashFunction)(int), long (*hilbertMapping)(int,int)){
    unsigned int *ptr = screen->pixels;

	for (int x=0; x <screen->w; x++){
		for (int y=0; y <screen->h; y++){
			int xr = 255 * x / screen->w;
			int yr = 255 * y / screen->h;
			int row = y * (screen->pitch / screen->format->BytesPerPixel);
			ptr[row + x] = SDL_MapRGBA(screen->format, xr, 255-yr, 255-xr, 255);
		}
	}

    //determine dot color. 1=black,0=white.
    int dotColor_ = dotColor*255;

    for(int p=startFrom; p < points; p++){
         long resultPoint = hilbertMapping(hashFunction(p),order);        
          ptr[point(unpackI32(resultPoint,0),unpackI32(resultPoint,1))] = SDL_MapRGBA(screen->format, dotColor_, dotColor_,dotColor_, 255);
    }

}

