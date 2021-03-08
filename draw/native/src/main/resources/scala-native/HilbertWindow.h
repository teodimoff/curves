#include <stdlib.h>

void drawGradientAndPoints(int points,int order,int startFrom,int height,int width,int dotColor, int (*hashFunction)(int), long (*hilbertMapping)(int,int));

void drawGradientAndPointsInternal(int points,int order,int startFrom,int dotColor, int (*hashFunction)(int), long (*hilbertMapping)(int,int));

void drawFileInternal(const char* fileName, int order,int dotColor, long (*hilbertMapping)(int,int));

void drawFile(const char* fileName, int order,int height, int width, int color, long (*hilbertMapping)(int,int));

