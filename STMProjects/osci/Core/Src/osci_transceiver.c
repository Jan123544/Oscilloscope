/*
 * osci_transceiver.c
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#include "osci_transceiver.h"
#include "osci_channel_state_machine.h"
#include "osci_timer.h"

void Gather_data(Osci_Transceiver* ts, Osci_ChannelStateMachine* csm)
{
	// Make sure dataframe has the start word
	ts->sendingBuffer.opcode = csm->channelOpcode;

	// Copy last complete measurement
	ts->sendingBuffer.channelData = csm->measurement;
}

void Send_data(Osci_Transceiver* ts)
{
	if (LL_DMA_IsEnabledChannel(ts->dma, ts->dmaTransmissionChannel)) // Do not do anything if data is still being sent
	{
		OSCI_error_notify("new data send skip");
		return;
	};

	LL_DMA_SetDataLength(ts->dma, ts->dmaTransmissionChannel, sizeof(Osci_DataFrame));
	LL_DMA_SetMemoryAddress(ts->dma, ts->dmaTransmissionChannel, (uint32_t)&ts->sendingBuffer);
	LL_DMA_EnableChannel(ts->dma, ts->dmaTransmissionChannel);
}

void Send_data_blocking(Osci_Transceiver* ts)
{
	Send_data(ts);
	// Block until data is sent
	while(LL_DMA_IsEnabledChannel(ts->dma, ts->dmaTransmissionChannel)){};
}

void Received_callback(Osci_Application* app)
{
	app->transceiver.receiveCompleteBuffer = app->transceiver.recveiveBuffer;
	app->transceiver.events.received_settings = TRUE;
}

void Sent_callback(Osci_Application* app)
{
	LL_DMA_DisableChannel(app->transceiver.dma, app->transceiver.dmaTransmissionChannel);
}

void Configure_usart(Osci_Transceiver* ts)
{
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

void Clear_events_ts(Osci_Transceiver* ts)
{
	ts->events.received_settings = FALSE;
	ts->events.send_requested[0] = FALSE;
	ts->events.send_requested[1] = FALSE;
}

void OSCI_transceiver_init(Osci_Transceiver* ts, USART_TypeDef* usart, DMA_TypeDef* dma, uint32_t dmaReceiverChannel, uint32_t dmaTransmissionChannel, Osci_ChannelStateMachine* x_channel_state_machine, Osci_ChannelStateMachine* y_channel_state_machine, TIM_TypeDef* timer)
{
	ts->timer = timer;
	ts->usart = usart;
	ts->dma = dma;
	ts->dmaReceiverChannel = dmaReceiverChannel;
	ts->dmaTransmissionChannel = dmaTransmissionChannel;
	ts->x_channel_state_machine = x_channel_state_machine;
	ts->y_channel_state_machine = y_channel_state_machine;
	ts->continuousUpdateMask = 0;

	// Configure USART channels, set buffer addresses etc.
	Configure_usart(ts);

	// This unfortunately has to be set manually.
	osci_transceiver_received_callback = &Received_callback;
	osci_transceiver_sent_callback = &Sent_callback;
	OSCI_timer_set_update_callback(ts->timer, &OSCTI_update_timer_callback);

	// Fill in default settings (offsets, sensitivity, ...).
	OSCI_configurator_config_defaults_ts(ts);
	Clear_events_ts(ts);

	// Enable DMA channel for receiving new settings.
	LL_DMA_EnableChannel(ts->dma, ts->dmaReceiverChannel);

	ts->state = OSCI_TRANSCEIVER_STATE_IDLE;
}

void Send_shutdown_event(Osci_Transceiver* ts)
{
	// Send shutdown event to CSM's based on the trigger command.
	if (OSCI_is_channel_active(ts, CHANNEL_X))
		ts->x_channel_state_machine->events.shutdown = TRUE;

	if (OSCI_is_channel_active(ts, CHANNEL_Y))
		ts->y_channel_state_machine->events.shutdown = TRUE;
}

void Switch_to_reconfiguring_after_shutdown(Osci_Transceiver* ts)
{
	// Wait for CSM's to shutdown and then switch to reconfiguring state.

	// Only wait for X CSM to shutdown here.
	if (OSCI_is_channel_active(ts, CHANNEL_X) && !OSCI_is_channel_active(ts, CHANNEL_Y))
	{
		if(ts->x_channel_state_machine->state == OSCI_CHANNEL_STATE_SHUTDOWN)
			ts->state = OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS;
	}

	// Only wait for Y CSM to shutdown here.
	if (!OSCI_is_channel_active(ts, CHANNEL_X) && OSCI_is_channel_active(ts, CHANNEL_Y))
	{
		if(ts->x_channel_state_machine->state == OSCI_CHANNEL_STATE_SHUTDOWN)
			ts->state = OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS;
	}

	// Wait for both CSM's to shutdown.
	if (OSCI_is_channel_active(ts, CHANNEL_X) && OSCI_is_channel_active(ts, CHANNEL_Y))
	{
		if(ts->x_channel_state_machine->state == OSCI_CHANNEL_STATE_SHUTDOWN && ts->y_channel_state_machine->state == OSCI_CHANNEL_STATE_SHUTDOWN)
			ts->state = OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS;
	}
}

void Reconfigure_channels(Osci_Transceiver* ts)
{
	Osci_Settings settingsCopy = ts->receiveCompleteBuffer;
	OSCI_configurator_recalculate_parameters(ts, &settingsCopy);
	OSCI_configurator_switch_relays(ts, &settingsCopy);
	OSCI_configurator_distribute_settings(ts, &settingsCopy);
}

void Start_monitoring(Osci_Transceiver* ts)
{
	if (OSCI_is_channel_active(ts, CHANNEL_X))
	{
		if (ts->x_channel_state_machine->params.triggerLevel)
			ts->x_channel_state_machine->events.start_monitoring = TRUE;
		else
			ts->x_channel_state_machine->events.start_measuring = TRUE;
	}

	if (OSCI_is_channel_active(ts, CHANNEL_Y))
	{
		if (ts->y_channel_state_machine->params.triggerLevel)
			ts->y_channel_state_machine->events.start_monitoring = TRUE;
		else
			ts->y_channel_state_machine->events.start_measuring = TRUE;
	}
}

void OSCI_transceiver_update(Osci_Transceiver* ts)
{
	switch(ts->state)
	{
		case OSCI_TRANSCEIVER_STATE_IDLE:
		{
			if (ts->events.received_settings)
			{
				if(ts->receiveCompleteBuffer.triggerCommand)
				{
					if (ts->receiveCompleteBuffer.triggerCommand & (MEASURE_STOP)
							|| !(ts->receiveCompleteBuffer.triggerCommand & (MEASURE_CONTINUOUS_X | MEASURE_CONTINUOUS_Y)))
					{
						ts->continuousUpdateMask = 0;

						OSCI_timer_stop(ts->timer);
					}
					else
					{
						// activate periodic timer
						ts->continuousUpdateMask = ts->receiveCompleteBuffer.triggerCommand & (MEASURE_CONTINUOUS_X | MEASURE_CONTINUOUS_Y);

						OSCI_timer_start(ts->timer);
					}

					ts->state = OSCI_TRANSCEIVER_STATE_SHUTTING_DOWN_CHANNELS;
				}
				else
				{
					Reconfigure_channels(ts);
					ts->state = OSCI_TRANSCEIVER_STATE_GATHERING_TRANSFORMING_AND_SENDING;
				}

				ts->events.received_settings = FALSE;
			}

			if (ts->events.send_requested[0] || ts->events.send_requested[1])
				ts->state = OSCI_TRANSCEIVER_STATE_GATHERING_TRANSFORMING_AND_SENDING;
			break;
		}
		case OSCI_TRANSCEIVER_STATE_SHUTTING_DOWN_CHANNELS:
		{
			Send_shutdown_event(ts);
			ts->state = OSCI_TRANSCEIVER_STATE_WAITING_FOR_CHANNELS_TO_SHUTDOWN;
			break;
		}
		case OSCI_TRANSCEIVER_STATE_WAITING_FOR_CHANNELS_TO_SHUTDOWN:
		{
			Switch_to_reconfiguring_after_shutdown(ts);
			break;
		}
		case OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS:
		{
			Reconfigure_channels(ts);
			ts->state = OSCI_TRANSCEIVER_STATE_STARTING_CHANNELS;
			break;
		}
		case OSCI_TRANSCEIVER_STATE_STARTING_CHANNELS:
		{
			Start_monitoring(ts);
			ts->state = OSCI_TRANSCEIVER_STATE_IDLE;
			break;
		}
		case OSCI_TRANSCEIVER_STATE_GATHERING_TRANSFORMING_AND_SENDING:
		{
			if (ts->events.send_requested[0])
			{
				Gather_data(ts, ts->x_channel_state_machine);
				OSCI_transform_apply(&ts->sendingBuffer, ts->x_channel_state_machine->params);
				Send_data_blocking(ts);

				ts->events.send_requested[0] = FALSE;
			}

			if (ts->events.send_requested[1])
			{
				Gather_data(ts, ts->y_channel_state_machine);
				OSCI_transform_apply(&ts->sendingBuffer, ts->y_channel_state_machine->params);
				Send_data_blocking(ts);

				ts->events.send_requested[1] = FALSE;
			}

			ts->state = OSCI_TRANSCEIVER_STATE_IDLE;
			break;
		}
	}
}

void transition(uint32_t flag, uint32_t compare, void (*transition_call) (void*), void* data_ref)
{
	if(flag & compare)
		transition_call(data_ref);
}

void OSCTI_update_timer_callback(Osci_Application* ts)
{
	if (ts->transceiver.state == OSCI_TRANSCEIVER_STATE_IDLE)
		ts->transceiver.state = OSCI_TRANSCEIVER_STATE_STARTING_CHANNELS;
}

uint8_t OSCI_is_channel_active(Osci_Transceiver* ts, uint8_t channel)
{
	switch (channel)
	{
		case CHANNEL_X:
			return ts->receiveCompleteBuffer.triggerCommand & MEASURE_SINGLE_X
					|| ts->continuousUpdateMask & MEASURE_CONTINUOUS_X;
		case CHANNEL_Y:
			return ts->receiveCompleteBuffer.triggerCommand & MEASURE_SINGLE_Y
					|| ts->continuousUpdateMask & MEASURE_CONTINUOUS_Y;
	}

	return 0;
}
