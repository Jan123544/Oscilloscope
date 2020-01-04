/*
 * osci.c
 *
 *  Created on: 30. 11. 2019
 *      Author: dot
 */
#include "osci_defines.h"
#include "osci_data_structures.h"
#include "osci_transceiver.h"
#include "osci_channel_state_machine.h"
#include <stdio.h>
#include <stdlib.h>

Osci_Application* OSCI_init()
{
	Osci_Application* app = calloc(sizeof(app), 1);
	OSCI_transceiver_init(&app->transceiver, USART2, DMA1, LL_DMA_CHANNEL_6, LL_DMA_CHANNEL_7, &app->xChannelStateMachine, &app->yChannelStateMachine, TIM3);
	OSCI_channel_init(&app->xChannelStateMachine, TIM1, DMA1, LL_DMA_CHANNEL_1, ADC1, LL_ADC_AWD1, &OSCI_channel_measuring_callback_x, &OSCI_channel_measurement_complete_callback_x, &OSCI_channel_awd_threshold_callback_x, &app->transceiver);
	OSCI_channel_init(&app->yChannelStateMachine, TIM2, DMA1, LL_DMA_CHANNEL_2, ADC2, LL_ADC_AWD2, &OSCI_channel_measuring_callback_y, &OSCI_channel_measurement_complete_callback_y, &OSCI_channel_awd_threshold_callback_y, &app->transceiver);

	return app;
}
