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
#define SIGNAL_SINE 0
#define SIGNAL_COSINE 1
#define SIGNAL_TANGENT 2
#define SIGNAL_RANDOM 3
#define NUM_SAMPLES 1024

void c_putSignalToDAC(void *v);
void c_execute(void *v);

class SignalAdjuster {
private:
	uint32_t signalType;
	uint32_t signalBuffer[NUM_SAMPLES];
	uint32_t signal;
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
	void increaseOffset();
	void decreaseOffset();
	void previousSignal();
	void nextSignal();
	void sample();
	void configureTimer();
	uint32_t quantize(float v);

public:
	SignalAdjuster(float amplitude=1, float frequency=1, float offset=1,
			uint32_t numberOfQuantizationLevels=4096, float amplitudePerLevel=1.0f/2048,
			uint32_t startSampleIndex=0, uint32_t startSignal=0) :
				 signal(startSignal), amplitude(amplitude), frequency(frequency), offset(offset), numberOfQuantizationLevels(
					numberOfQuantizationLevels), amplitudePerLevel(
					amplitudePerLevel), sampleIndex(startSampleIndex) {
		tim1_callback = c_putSignalToDAC;
		usart2_rxne_callback = c_execute;
		sample();
	}

	void execute(char cmd);
	uint32_t getNextSample();
};

#endif /* INC_SIGNALADJUSTER_H_ */
