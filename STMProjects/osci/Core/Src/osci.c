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
#include "osci_callback.h"

Osci_Application* osci_init(){

	Osci_Application* app = calloc(sizeof *app, 1);
	osci_transceiver_init(&app->transceiver, USART2, DMA1, LL_DMA_CHANNEL_6, LL_DMA_CHANNEL_7, &app->xChannelStateMachine, &app->yChannelStateMachine);
	osci_channel_init(&app->xChannelStateMachine, TIM1, DMA1, LL_DMA_CHANNEL_1, ADC1, LL_ADC_AWD1, osci_channel_monitoring_callback_x, osci_channel_measuring_callback_x, osci_channel_measurement_complete_callback_x, osci_channel_awd_threshold_callback_x, &app->transceiver);
	osci_channel_init(&app->yChannelStateMachine, TIM2, DMA1, LL_DMA_CHANNEL_2, ADC2, LL_ADC_AWD2, osci_channel_monitoring_callback_y, osci_channel_measuring_callback_y, osci_channel_measurement_complete_callback_y, osci_channel_awd_threshold_callback_y, &app->transceiver);

	return app;
}
