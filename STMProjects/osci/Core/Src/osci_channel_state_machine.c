/*
 * osci_channel_state_machine.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */
#include "osci_channel_state_machine.h"

void OSCI_channel_init(Osci_ChannelStateMachine* csm, TIM_TypeDef* timer, DMA_TypeDef* dma, uint32_t dmaChannel, ADC_TypeDef* adc, uint32_t awd, ADC_callback measuring_callback, Measurement_complete_callback measurement_complete_callback, Awd_threshold_callback awd_threshold_callback, Osci_Transceiver* transceiver)
{
	csm->timer = timer;
	csm->adc = adc;
	csm->awd = awd;
	csm->dma = dma;
	csm->dmaChannel = dmaChannel;
	csm->transceiver = transceiver;
	csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
	csm->events.measurement_complete = FALSE;
	csm->events.shutdown = FALSE;
	csm->events.start_measuring = FALSE;
	csm->events.start_monitoring = FALSE;

	// Initialize static callbacks.
	OSCI_timer_set_update_callback(csm->timer, measuring_callback);
	OSCI_dma_set_TC_callback(csm, measurement_complete_callback);
	OSCI_adc_set_awd_callback(csm, awd_threshold_callback);

	// Initialize nested structures.
	OSCI_dma_channel_init(csm);
	OSCI_adc_init(csm);
}

void OSCI_channel_start_monitoring(Osci_ChannelStateMachine* csm)
{
	OSCI_adc_stop(csm);
	OSCI_timer_stop(csm->timer);
	LL_DMA_DisableChannel(csm->dma, csm->dmaChannel);

	OSCI_adc_reconfigure_for_monitoring(csm);

	csm->state = OSCI_CHANNEL_STATE_MONITORING;

	LL_ADC_REG_StartConversion(csm->adc);
}

void OSCI_channel_start_measuring(Osci_ChannelStateMachine* csm)
{
	OSCI_adc_stop(csm);
	OSCI_timer_stop(csm->timer);
	LL_DMA_DisableChannel(csm->dma, csm->dmaChannel);

	csm->timer->PSC = csm->params.timerSettings.psc;
	csm->timer->ARR = csm->params.timerSettings.arr;

	OSCI_adc_reconfigure_for_measuring(csm);
	OSCI_dma_channel_reconfigure_for_measuring(csm);

	csm->measurements_left = NUM_SAMPLES;
	csm->state = OSCI_CHANNEL_STATE_MEASURING;

	OSCI_timer_start(csm->timer);
}

void OSCI_channel_update(Osci_ChannelStateMachine* csm)
{
	switch(csm->state)
	{
		case OSCI_CHANNEL_STATE_SHUTDOWN:
		{
			// Clear invalid event flags
			csm->events.measurement_complete = FALSE;
			csm->events.shutdown = FALSE;

			// Check for transitions
			if (csm->events.start_monitoring)
			{
				OSCI_channel_start_monitoring(csm);
				csm->state = OSCI_CHANNEL_STATE_MONITORING;
				csm->events.start_monitoring = FALSE;
			}

			if(csm->events.start_measuring)
			{
				OSCI_channel_start_measuring(csm);
				csm->state = OSCI_CHANNEL_STATE_MEASURING;
				csm->events.start_measuring = FALSE;
				return;
			}
			break;
		}
		case OSCI_CHANNEL_STATE_MONITORING:
		{
			// Clear invalid event flags
			csm->events.measurement_complete = FALSE;
			csm->events.start_monitoring = FALSE;

			// Check for transitions
			if(csm->events.shutdown)
			{
				OSCI_adc_stop(csm);
				csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
				csm->events.shutdown = FALSE;
				return;
			}

			if(csm->events.start_measuring)
			{
				OSCI_channel_start_measuring(csm);
				csm->state = OSCI_CHANNEL_STATE_MEASURING;
				csm->events.start_measuring = FALSE;
				return;
			}
			break;
		}
		case OSCI_CHANNEL_STATE_MEASURING:
		{
			// Clear invalid event flags
			csm->events.start_measuring = FALSE;
			csm->events.start_monitoring = FALSE;

			// Check for transitions
			if(csm->events.shutdown)
			{
				OSCI_adc_stop(csm);
				csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
				return;
			}

			if(csm->events.measurement_complete)
			{
				OSCI_adc_stop(csm);
				csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
				csm->transceiver->events.send_requested = TRUE;
				csm->events.measurement_complete = FALSE;
				return;
			}
			break;
		}
	}
}

void OSCI_channel_measuring_callback_x(Osci_Application* app)
{
	// Only applies in measuring state
	if (app->xChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING)
		return;

	if(app->xChannelStateMachine.measurements_left > 0)
	{
		if(!LL_ADC_IsActiveFlag_ADRDY(app->xChannelStateMachine.adc))
			OSCI_error_loop("adc started when not ready");

		LL_ADC_REG_StartConversion(app->xChannelStateMachine.adc);
		app->xChannelStateMachine.measurements_left--;
	}
	else
	{
		OSCI_timer_stop(app->xChannelStateMachine.timer);
	}
}

void OSCI_channel_measurement_complete_callback_x(Osci_Application* app)
{
	// Only applies in measuring state
	if (app->xChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING)
		return;

	app->xChannelStateMachine.measurement = app->xChannelStateMachine.measurementDMABuffer;
	app->xChannelStateMachine.events.measurement_complete = TRUE;
}

void OSCI_channel_measuring_callback_y(Osci_Application* app)
{
	// Only applies in measuring state
	if (app->yChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING)
		return;

	if(app->yChannelStateMachine.measurements_left > 0)
	{
		if(!LL_ADC_IsActiveFlag_ADRDY(app->yChannelStateMachine.adc))
			OSCI_error_loop("adc started when not ready");

		LL_ADC_REG_StartConversion(app->yChannelStateMachine.adc);
		app->yChannelStateMachine.measurements_left--;
	}
	else
	{
		OSCI_timer_stop(app->yChannelStateMachine.timer);
	}
}

void OSCI_channel_measurement_complete_callback_y(Osci_Application* app)
{
	// Only applies in measuring state
	if (app->yChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING)
		return;

	app->yChannelStateMachine.measurement = app->yChannelStateMachine.measurementDMABuffer;
	app->yChannelStateMachine.events.measurement_complete = TRUE;
}

void OSCI_channel_awd_threshold_callback_x(Osci_Application* app)
{
	LL_ADC_DisableIT_AWD1(app->xChannelStateMachine.adc);

	app->xChannelStateMachine.events.start_measuring = TRUE;
}

void OSCI_channel_awd_threshold_callback_y(Osci_Application* app)
{
	LL_ADC_DisableIT_AWD2(app->yChannelStateMachine.adc);

	app->yChannelStateMachine.events.start_measuring = TRUE;
}
