/*
 * osci_transceiver.c
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */


#include "osci_transceiver.h"
#include "osci_channel_state_machine.h"


void gather_data(Osci_Transceiver* ts){
	// Make sure dataframe has the start word
	ts->sendingBuffer.start_word = OSCI_DATA_START_WORD;

	// Copy last complete measurement
	ts->sendingBuffer.xChannel = ts->x_channel_state_machine->measurement;
	ts->sendingBuffer.yChannel = ts->y_channel_state_machine->measurement;
}

void transform_data(Osci_Transceiver* ts){
	osci_transform_apply(&ts->sendingBuffer, ts->x_channel_state_machine->params, ts->y_channel_state_machine->params);
}

void send_data(Osci_Transceiver* ts){
	if (LL_DMA_IsEnabledChannel(ts->dma, ts->dmaTransmissionChannel)) { osci_error_notify("new data send skip"); return; }; // Do not do anything if data is still being sent
	LL_DMA_SetDataLength(ts->dma, ts->dmaTransmissionChannel, sizeof(Osci_DataFrame));
	LL_DMA_SetMemoryAddress(ts->dma, ts->dmaTransmissionChannel, (uint32_t)&ts->sendingBuffer);
	LL_DMA_EnableChannel(ts->dma, ts->dmaTransmissionChannel);
}

void received_callback(Osci_Application* app){
	app->transceiver.receiveCompleteBuffer = app->transceiver.recveiveBuffer;
	app->transceiver.events.received_settings = TRUE;
}

void sent_callback(Osci_Application* app){
	LL_DMA_DisableChannel(app->transceiver.dma, app->transceiver.dmaTransmissionChannel);
}

void configure_usart(Osci_Transceiver* ts){
	// Configure USART channels, set buffer addresses etc.
	LL_USART_Disable(ts->usart);

	// Channel 6 (settings reading)
	LL_DMA_SetPeriphAddress(ts->dma, ts->dmaReceiverChannel, LL_USART_DMA_GetRegAddr(ts->usart, LL_USART_DMA_REG_DATA_RECEIVE));
	LL_DMA_SetMemoryAddress(ts->dma, ts->dmaReceiverChannel, (uint32_t)&ts->recveiveBuffer);

	LL_DMA_SetDataLength(ts->dma, ts->dmaReceiverChannel, sizeof(Osci_Settings));
	LL_DMA_EnableIT_TC(ts->dma, ts->dmaReceiverChannel);
	LL_DMA_EnableIT_TE(ts->dma, ts->dmaReceiverChannel);
	LL_USART_EnableDMAReq_RX(ts->usart);

	// Channel 7 (data writing)
	LL_DMA_SetPeriphAddress(ts->dma, ts->dmaTransmissionChannel, LL_USART_DMA_GetRegAddr(ts->usart, LL_USART_DMA_REG_DATA_TRANSMIT));
	//LL_DMA_SetMemoryAddress(DMA1, LL_DMA_CHANNEL_7, (uint32_t)&osci_dataframe_current);
	//LL_DMA_SetDataLength(DMA1, LL_DMA_CHANNEL_7, sizeof(Osci_DataFrame));

	LL_DMA_EnableIT_TE(ts->dma, ts->dmaTransmissionChannel);
	LL_DMA_EnableIT_TC(ts->dma, ts->dmaTransmissionChannel);
	LL_USART_EnableDMAReq_TX(ts->usart);

	LL_USART_Enable(ts->usart);
	}

void osci_transceiver_init(Osci_Transceiver* ts, USART_TypeDef* usart, DMA_TypeDef* dma, uint32_t dmaReceiverChannel, uint32_t dmaTransmissionChannel, Osci_ChannelStateMachine* x_channel_state_machine, Osci_ChannelStateMachine* y_channel_state_machine){
	ts->usart = usart;
	ts->dma = dma;
	ts->dmaReceiverChannel = dmaReceiverChannel;
	ts->dmaTransmissionChannel = dmaTransmissionChannel;
	ts->x_channel_state_machine = x_channel_state_machine;
	ts->y_channel_state_machine = y_channel_state_machine;

	// Configure USART channels, set buffer addresses etc.
	configure_usart(ts);

	// This unfortunately has to be set manually.
	osci_transceiver_received_callback = received_callback;
	osci_transceiver_sent_callback = sent_callback;

	// Fill in default settings (offsets, sensitivity, ...).
	osci_configurator_config_defaults_ts(ts);

	// Enable DMA channel for receiving new settings.
	LL_DMA_EnableChannel(ts->dma, ts->dmaReceiverChannel);

	ts->state = OSCI_TRANSCEIVER_STATE_IDLE;

}



void osci_transceiver_update(Osci_Transceiver* ts){
	switch(ts->state){
		case OSCI_TRANSCEIVER_STATE_IDLE:
			if (ts->events.received_settings) {
				ts->state = OSCI_TRANSCEIVER_STATE_SHUTTING_DOWN_CHANNELS;
				ts->events.received_settings = FALSE;
			}
			if (ts->events.send_requested){
				gather_data(ts);
				transform_data(ts);
				send_data(ts);
				ts->events.send_requested = FALSE;
			}
			break;
		case OSCI_TRANSCEIVER_STATE_SHUTTING_DOWN_CHANNELS:
			ts->x_channel_state_machine->events.shutdown = TRUE;
			ts->y_channel_state_machine->events.shutdown = TRUE;
			ts->state = OSCI_TRANSCEIVER_STATE_WAITING_FOR_CHANNELS_TO_SHUTDOWN;
			break;
		case OSCI_TRANSCEIVER_STATE_WAITING_FOR_CHANNELS_TO_SHUTDOWN:
				if( (ts->x_channel_state_machine->state == OSCI_CHANNEL_STATE_SHUTDOWN) && (ts->y_channel_state_machine->state == OSCI_CHANNEL_STATE_SHUTDOWN)) {
					ts->state = OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS;
				}
			break;
		case OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS:{
					Osci_Settings settingsCopy = ts->receiveCompleteBuffer;
					osci_configurator_recalculate_parameters(&settingsCopy, &ts->allReceivedParameters);
					osci_configurator_switch_relays(&settingsCopy, &ts->allReceivedParameters);
					osci_configurator_distribute_settings(ts->x_channel_state_machine, ts->y_channel_state_machine, &settingsCopy, &ts->allReceivedParameters);
					ts->state = OSCI_TRANSCEIVER_STATE_STARTING_CHANNELS;
			break;
		}
		case OSCI_TRANSCEIVER_STATE_STARTING_CHANNELS:
				ts->x_channel_state_machine->events.start_monitoring = TRUE;
				ts->y_channel_state_machine->events.start_monitoring = TRUE;
				ts->state = OSCI_TRANSCEIVER_STATE_IDLE;
			break;
	}
}


