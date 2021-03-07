#include <stdlib.h>

void drawGradientAndPoints(int points,int order,int startFrom,int height,int width,int dotColor, int (*hashFunction)(int), long (*hilbertMapping)(int,int));

void drawGradientAndPointsInternal(int points,int order,int startFrom,int dotColor, int (*hashFunction)(int), long (*hilbertMapping)(int,int));

