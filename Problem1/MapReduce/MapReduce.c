/*
 * MapReduce.c
 *
 *  Created on: Sep 18, 2014
 *      Author: bkakran
 *
 */

#include <stdio.h>
#include<string.h>
#include <stdlib.h>

typedef struct MapReduce {
	int node;
	long degree;
} mapReduce;
mapReduce *mapReducePtr;
int* degreeArray;
int edges = 0, vertices = 0, edgeCounter = 0;
/**
 * Steps to compile
 * gcc -Wall -pg MapReduce.c  -o MapReduce_gprof
 * ./MapReduce_gprof home/bkakran/stanford/google.graph output
 * gprof MapReduce_gprof gmon.out > analysis.txt
 *
 */
int main(int argc, char **argv) {

	char* fileName;
	char* outputFileName;
	if (argc > 0) {
		fileName = argv[1];
		outputFileName = argv[2];
		//fileName = "/home/bkakran/stanford/google.graph";
		//outputFileName = "output";
	}
	MapperFunction(fileName);
	ReduceFunction(outputFileName);
	free(mapReducePtr);
	free(degreeArray);
}
void MapperFunction(char* fileName) {
	FILE * fp;
	//Open File
	printf("\nStarting Mapper function");
	printf("\nInput File is:%s\n", fileName);
	fp = fopen(fileName, "r");
	int count = 0;
	if (fp != NULL) {
		char line[256]; /* or other suitable maximum line size */
		while (fgets(line, sizeof line, fp) != NULL) /* read a line */
		{
			if (count == 0) {
				count++;
				initializeVerticesAndEdges(line);
				initializeMapperDataStructure();
			} else {
				count++;
				simulateMapperFunction(line);
			}
		}
		fclose(fp);
	} else {
		//file doesn't exist
	}
	printf("Processed %ld records in mapper Function", edges);
	printf("\nSaving the intermediate results to file");
	storeIntermediateResults();
	printf("\nEnd of MapperFunction");
	printf("\n------------------------------------------------\n");
}
void initializeVerticesAndEdges(char line[]) {
	char *split = strtok(line, " ");
	if (split) {
		char *garbage = NULL;
		//vertices = atoi(split);
		vertices = strtol(split, &garbage, 0);
		printf("No Of vertices are: %d\n", vertices);
	}
	split = strtok(NULL, ",");

	if (split) {
		char *garbage = NULL;
		//vertices = atoi(split);
		edges = strtol(split, &garbage, 0);
		printf("No Of edges are: %d\n", edges);
		mapReducePtr = (mapReduce*) malloc(sizeof(mapReduce) * (2 * edges));
	}
}
void initializeMapperDataStructure(void) {
	int var;
	for (var = 0; var < (2 * edges); ++var) {
		mapReducePtr[var].degree = 0;
		mapReducePtr[var].node = -1;

	}
}
void simulateMapperFunction(char line[]) {
	char *split = strtok(line, " ");
	int veretx = 0;
	if (split) {
		char *garbage = NULL;
		//vertices = atoi(split);
		veretx = strtol(split, &garbage, 0);
	}
	mapReducePtr[edgeCounter].degree = 1;
	mapReducePtr[edgeCounter++].node = veretx;
	split = strtok(NULL, ",");
	if (split) {
		char *garbage = NULL;
		//vertices = atoi(split);
		veretx = strtol(split, &garbage, 0);
	}
	mapReducePtr[edgeCounter].degree = 1;
	mapReducePtr[edgeCounter++].node = veretx;
}

void initializeDegreeArray(void) {
	int var;
	degreeArray = (int*) malloc(sizeof(int) * (vertices));
	for (var = 0; var < (vertices); ++var) {
		degreeArray[var] = 0;
	}
}

void ReduceFunction(char* outputFileName) {
	/**
	 *
	 int var = 0;
	 for (var = 0; var < (2 * edges); ++var) {
	 //printf("NodeId is : %d\n", mapReducePtr[var].node);
	 //printf("Degree is : %d\n", mapReducePtr[var].degree);
	 if (mapReducePtr[var].node != -1) {
	 degreeArray[mapReducePtr[var].node] += mapReducePtr[var].degree;
	 }
	 }*/
	printf("\nStarting Reduce operation");
	initializeDegreeArray();
	FILE * fp = fopen("results.txt", "r");
	int count = 0, node, degree;
	if (fp != NULL) {
		char line[256]; /* or other suitable maximum line size */
		while (fgets(line, sizeof line, fp) != NULL) /* read a line */
		{
			char *split = strtok(line, " ");
			int veretx = 0;
			if (split) {
				char *garbage = NULL;
				//vertices = atoi(split);
				node = strtol(split, &garbage, 0);
			}
			split = strtok(NULL, ",");
			if (split) {
				char *garbage = NULL;
				//vertices = atoi(split);
				degree = strtol(split, &garbage, 0);
			}
			degreeArray[node] += degree;
		}
		fclose(fp);
	} else {
		//file doesn't exist
	}
	outputResults(outputFileName);
	printf("\nEnding Reduce operation");
	printf("\n----------------------------\n");
}
void outputResults(char* fileName) {

	int totalDegree = 0, var;
	FILE *fp;
	int i;
	/* open the file */
	fp = fopen(fileName, "wb+");
	if (fp == NULL) {
		printf("I couldn't open file for writing.\n");
		exit(0);
	}
	printf("\nStoring reduce operations results to file:%s\n", fileName);

	for (var = 0; var < vertices; ++var) {
		//printf("Degree of node %d is %d\n", var, degreeArray[var]);
		totalDegree = totalDegree + degreeArray[var];
		fprintf(fp, "%d %d\n", var, degreeArray[var]);
	}
	/* close the file */
	fclose(fp);
	//printf("No of edges input is: %d\n", edges);
	//printf("No of edges calculated is: %d", totalDegree / 2);
}

void storeIntermediateResults(void) {
	FILE *fp;
	int i;
	/* open the file */
	fp = fopen("results.txt", "w");
	if (fp == NULL) {
		printf("I couldn't open results.dat for writing.\n");
		exit(0);
	}

	int var = 0;
	for (var = 0; var < (2 * edges); ++var) {
		if (mapReducePtr[var].node != -1) {
			fprintf(fp, "%d %d\n", mapReducePtr[var].node,
					mapReducePtr[var].degree);
		}
	}
	/* close the file */
	fclose(fp);
}

