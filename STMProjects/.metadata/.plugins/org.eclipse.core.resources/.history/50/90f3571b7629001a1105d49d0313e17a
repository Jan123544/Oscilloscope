/*
 * SignalAdjuster.cpp
 *
 *  Created on: Dec 27, 2019
 *      Author: dot
 */

#include "SignalAdjuster.h"

void c_putSignalToDAC(void*v){
	SignalAdjuster* p = (SignalAdjuster*) v;
	LL_DAC_ConvertData12LeftAligned(DAC1, LL_DAC_CHANNEL_1, p->getNextSample());
}

void c_execute(void*v){
	SignalAdjuster* p = (SignalAdjuster*) v;
	char cmd = USART2->RDR;
	p->execute(cmd);
}

void SignalAdjuster::increaseAmplitude(){
	if(amplitude/amplitudePerLevel < numberOfQuantizationLevels/2.0f){
		amplitude += numberOfQuantizationLevels/16;
		if (amplitude >= numberOfQuantizationLevels/2){
			amplitude = numberOfQuantizationLevels/2 - 1;
		}
	}else{
		amplitude = numberOfQuantizationLevels/2 - 1;
	}
}

void SignalAdjuster::decreaseAmplitude(){
	if(amplitude/amplitudePerLevel > 0){
		amplitude -= numberOfQuantizationLevels/16;
		if (amplitude < 0){
			amplitude = 0;
		}
	}else{
		amplitude = 0;
	}
}
void SignalAdjuster::increaseFrequency(){
	frequency++;
}
void SignalAdjuster::decreaseFrequency(){
	if(frequency > 1){
		frequency--;
	}
}

void SignalAdjuster::previousSignal(){
	switch(signal){
	case SIGNAL_SINE:
		signal = SIGNAL_RANDOM;
		break;
	case SIGNAL_COSINE:
		signal = SIGNAL_SINE;
		break;
	case SIGNAL_TANGENT:
		signal = SIGNAL_COSINE;
		break;
	case SIGNAL_RANDOM:
		signal = SIGNAL_TANGENT;
		break;
	}

	sample();
}
void SignalAdjuster::nextSignal(){
	switch(signal){
		case SIGNAL_SINE:
			signal = SIGNAL_COSINE;
			break;
		case SIGNAL_COSINE:
			signal = SIGNAL_TANGENT;
			break;
		case SIGNAL_TANGENT:
			signal = SIGNAL_RANDOM;
			break;
		case SIGNAL_RANDOM:
			signal = SIGNAL_SINE;
			break;
	}

	sample();
}

void SignalAdjuster::sample(){
	float signal_increment;
	sampleIndex = 0;
	switch(signal){
	case SIGNAL_SINE:
		signal_increment = 2*M_PI/(NUM_SAMPLES-1);
		for(uint32_t i = 0; i < NUM_SAMPLES; i++){
			signalBuffer[i] = quantize(offset + sin(signal_increment*i));
		}
		break;
	case SIGNAL_COSINE:
		signal_increment = 2*M_PI/(NUM_SAMPLES-1);
		for(uint32_t i = 0; i < NUM_SAMPLES; i++){
			signalBuffer[i] = quantize(cos(signal_increment*i));
		}
		break;
	default:
		return;
	}

	configureTimer();
}

void SignalAdjuster::configureTimer(){
	LL_TIM_DisableCounter(TIM1);
	LL_TIM_ClearFlag_UPDATE(TIM1);
	LL_TIM_SetCounter(TIM1, 0);

	// Asumes TIM1 is used with 32MHZ clock speed

	uint32_t arr = floor(TIM1_CLOCK_SPEED/frequency/(NUM_SAMPLES-1));
	if(arr > TIM1_MAX_ARR){
		TIM1->PSC = arr / TIM1_MAX_ARR;
		TIM1->ARR = arr % TIM1_MAX_ARR;
	}

	LL_TIM_EnableIT_UPDATE(TIM1);
	LL_TIM_EnableCounter(TIM1);
}

uint32_t SignalAdjuster::quantize(float v){
	return MAX(0, MIN(numberOfQuantizationLevels - 1, floor((numberOfQuantizationLevels-1)*amplitude*v)));
}




void SignalAdjuster::execute(char cmd){
	switch(cmd){
		case 'i':increaseAmplitude();
		break;
		case 'k':decreaseAmplitude();
		break;
		case 'j':increaseFrequency();
		break;
		case'l':decreaseFrequency();
		break;
		case't':nextSignal();
		break;
		case'r':previousSignal();
	}
}

uint32_t SignalAdjuster::getNextSample(){
	uint32_t ret = signalBuffer[sampleIndex];
	++sampleIndex;
	sampleIndex %= NUM_SAMPLES;
	return ret;
}
