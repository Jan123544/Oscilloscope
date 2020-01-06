/*
 * osci_data_structures.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_DATA_STRUCTURES_H_
#define INC_OSCI_DATA_STRUCTURES_H_

#include "main.h"
#include "osci_defines.h"

typedef struct osci_transceiver Osci_Transceiver;
typedef struct osci_application Osci_Application;

typedef struct osci_timersettings
{
	uint32_t psc;
	uint32_t arr;
} Osci_TimerSettings;

typedef struct osci_channelmeasurement
{
	uint16_t values [NUM_SAMPLES];
} Osci_ChannelMeasurement;

typedef struct osci_channelparameters
{
	float offset;
	float sensitivity;
	Osci_TimerSettings timerSettings;
	uint32_t triggerLevel;
	uint8_t voltageRange;
	uint8_t graticuleDivisions;
	float rangeWhenMeasured;
	float alpha;
} Osci_ChannelParameters;

typedef struct osci_channelevents
{
	uint8_t shutdown;
	uint8_t start_monitoring;
	uint8_t start_measuring;
	uint8_t measurement_complete;
} Osci_ChannelEvents;

typedef void (*ADC_callback) (Osci_Application*);
typedef void (*Measurement_complete_callback) (Osci_Application*);
typedef void (*Awd_threshold_callback) (Osci_Application*);

typedef struct osci_channelstatemachine
{
	Osci_ChannelParameters params;

	TIM_TypeDef* timer;
	DMA_TypeDef* dma;
	uint32_t dmaID;
	uint32_t dmaChannel;
	ADC_TypeDef* adc;
	uint32_t adcID;
	uint32_t awd;

	Osci_ChannelMeasurement measurementDMABuffer;
	Osci_ChannelMeasurement measurement;
	uint32_t state;

	Osci_ChannelEvents events;

	Osci_Transceiver* transceiver;

	uint16_t channelOpcode;

} Osci_ChannelStateMachine;

typedef struct osci_dataframe
{
	uint16_t opcode;
	Osci_ChannelMeasurement channelData;
} Osci_DataFrame;

typedef struct osci_settings
{
	float xOffset;
	float xSensitivity;
	float yOffset;
	float ySensitivity;
	float xTimePerDivision;
	float yTimePerDivision;
	float xThreshold;
	float yThreshold;
	uint32_t triggerCommand;
	uint8_t xVoltageRange;
	uint8_t yVoltageRange;
	uint8_t xGraticuleDivisions;
	uint8_t yGraticuleDivisions;
} Osci_Settings;

typedef struct osci_calculatedparameters
{
	float xAlpha;
	float yAlpha;
	float xOffset;
	float yOffset;
	float xSensitivity;
	float ySensitivity;
	uint32_t xDivisions;
	uint32_t yDivisions;
	float xRange;
	float yRange;
	float xRangeWhenMeasured;
	float yRangeWhenMeasured;
	uint32_t xThresholdInLevels;
	uint32_t yThresholdInLevels;
	Osci_TimerSettings xTimerSettings;
	Osci_TimerSettings yTimerSettings;
} Osci_CalculatedParameters;

typedef struct osci_transceiverevents
{
	uint8_t received_settings;
	uint8_t send_requested[2];
} Osci_TransceiverEvents;

struct osci_transceiver
{
	USART_TypeDef* usart;
	DMA_TypeDef* dma;
	TIM_TypeDef* timer; // periodic update timer
	uint32_t dmaReceiverChannel;
	uint32_t dmaTransmissionChannel;

	Osci_Settings recveiveBuffer; // dma writes to this
	Osci_Settings receiveCompleteBuffer; // after dma completes this gets updated
	Osci_DataFrame sendingBuffer;

	Osci_CalculatedParameters allReceivedParameters;

	Osci_ChannelStateMachine* x_channel_state_machine;
	Osci_ChannelStateMachine* y_channel_state_machine;

	uint32_t state;

	uint32_t continuousUpdateMask;

	Osci_TransceiverEvents events;
};

struct osci_application
{
	Osci_Transceiver transceiver;
	Osci_ChannelStateMachine xChannelStateMachine;
	Osci_ChannelStateMachine yChannelStateMachine;
};

#endif /* INC_OSCI_DATA_STRUCTURES_H_ */
