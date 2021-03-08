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

void drawFileInternal(const char* fileName,int order,int height, int width, int color, long (*hilbertMapping)(int,int))
{
init();

SDL_SetWindowSize(Window,height,width);
SDL_SetWindowTitle(Window, fileName);
SDL_SetWindowResizable(Window, SDL_FALSE);
SDL_SetWindowPosition(Window,SDL_WINDOWPOS_CENTERED,SDL_WINDOWPOS_CENTERED);
screen=SDL_GetWindowSurface(Window);


drawFileInternal(fileName,order,color,hilbertMapping);
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
//todo : provide denseing function to reduce datapoints for large files. Also a way to select endianess(as a function pointer). 
//void drawFileInternal(const char* fileName,int order,int dotColor,int (*denseFunc)(int,int),int denseSpan, long (*hilbertMapping)(int,int))
//int littleEendian = res[i+0] + (res[i+1] << 8) + (res[i+2] << 16) + (res[i+3] << 24);
//int bigEendian  = (res[i+0] << 24) + (res[i+1] << 16) + (res[i+2] << 8) + res[i+3];

void drawFileInternal(const char* fileName,int order,int dotColor, long (*hilbertMapping)(int,int))
{
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

        SDL_RWops *rw = SDL_RWFromFile(fileName, "rb");

        int res_size = SDL_RWsize(rw);
        char* res = (char*)malloc(res_size);

        int nb_read_total = 0, nb_read = 1;
        char* buf = res;
        while (nb_read_total < res_size && nb_read != 0) {
                nb_read = SDL_RWread(rw, buf, 1, (res_size - nb_read_total));
                nb_read_total += nb_read;
                buf += nb_read;
        }
        SDL_RWclose(rw);

        nb_read_total = nb_read_total - (nb_read_total % 4); //todo fix the truncated bytes by printing additional truncated int
        for(int i = 0; i < nb_read_total;i+=4){
          // printf("offset: %d\n",i);
           int integer = res[i+0] + (res[i+1] << 8) + (res[i+2] << 16) + (res[i+3] << 24);
           long resultPoint = hilbertMapping(integer,order);
           ptr[point(unpackI32(resultPoint,0),unpackI32(resultPoint,1))] = SDL_MapRGBA(screen->format, dotColor_, dotColor_,dotColor_, 255);
          //printf("%d\n",integer);
        }
        free(res);
        //if((nb_read_total % 4) != 0) ...
}

