/*
 * SignalAdjuster.h
 *
 *  Created on: Dec 27, 2019
 *      Author: dot
 */

#ifndef INC_SIGNALADJUSTER_H_
#define INC_SIGNALADJUSTER_H_
#include "main.h"
#include<stdint.h>
#include<math.h>
#define SIGNAL_SINE 'a'
#define SIGNAL_COSINE 'b'
#define SIGNAL_TANGENT 'c'
#define SIGNAL_RANDOM 'd'
#define NUM_SAMPLES 65536

void c_putSignalToDAC(void*v);
void c_execute(void*v);

class SignalAdjuster{
private:
	uint32_t signalType;
	uint32_t signalBuffer[NUM_SAMPLES];
	char signal;
	float amplitude;
	float frequency;
	float offset;
	uint32_t numberOfQuantizationLevels;
	float amplitudePerLevel;
	uint32_t sampleIndex;


	void increaseAmplitude();
	void decreaseAmplitude();
	void increaseFrequency();
	void decreaseFrequency();
	void previousSignal();
	void nextSignal();
	void sample();
	void configureTimer();
	uint32_t quantize(float);
	void putSignalToDAC();

public:
	SignalAdjuster(float amplitude=1.0f, float frequency=1.0f, float offset=1.0f, uint32_t numberOfQuantizationLevels=4096, float amplitudePerLevel = 1.0/2048, uint32_t startSampleIndex=0) :amplitude(amplitude), frequency(frequency), offset(offset), numberOfQuantizationLevels(numberOfQuantizationLevels), amplitudePerLevel(amplitudePerLevel), sampleIndex(startSampleIndex) {
		tim1_callback = c_putSignalToDAC;
		usart2_rxne_callback = c_execute;
	};

	uint32_t getNextSample();
	void execute(char cmd);
};


#endif /* INC_SIGNALADJUSTER_H_ */