/*
 * osci_channel_state_machine.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */
#include "osci_channel_state_machine.h"

void osci_channel_init(Osci_ChannelStateMachine* csm, TIM_TypeDef* timer, DMA_TypeDef* dma, uint32_t dmaChannel, ADC_TypeDef* adc, uint32_t awd, void (*monitoring_callback) (Osci_Application*), void (*measuring_callback) (Osci_Application*), void (*measurement_complete_callback) (Osci_Application*), void (*awd_threshold_callback) (Osci_Application*),Osci_Transceiver* transceiver){
	// Assign pointers.
	csm->timer = timer;
	csm->adc = adc;
	csm->awd = awd;
	csm->dma = dma;
	csm->dmaChannel = dmaChannel;
	csm->monitoring_callback = monitoring_callback;
	csm->measuring_callback = measuring_callback;
	csm->measurement_complete_callback = measurement_complete_callback;
	csm->awd_threshold_callback = awd_threshold_callback;
	csm->transceiver = transceiver;

	// Set call-backs, which are dummies at this point, because enabling update event on timer generates an interrupt, so the call-backs must be non-null.
	osci_callback_set_monitoring_callbacks(csm);
	// Initialize nested structures.
	osci_timer_init(csm);
	osci_dma_channel_init(csm);
	osci_adc_init(csm);

	// Make sure state machine starts in the proper state.
	osci_channel_shutdown(csm);
	csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
}

void osci_channel_shutdown(Osci_ChannelStateMachine* csm){
	//osci_timer_stop(csm);
	osci_adc_stop(csm);
}

void osci_channel_start_monitoring(Osci_ChannelStateMachine* csm){
	osci_channel_shutdown(csm);
	osci_timer_reconfigure_for_monitoring(csm);
	osci_adc_reconfigure_for_monitoring(csm);
	osci_dma_channel_reconfigure_for_monitoring(csm);
	osci_callback_set_monitoring_callbacks(csm);
	osci_timer_start(csm);

	csm->state = OSCI_CHANNEL_STATE_MONITORING;
}

void osci_channel_start_measuring(Osci_ChannelStateMachine* csm){
	osci_channel_shutdown(csm);
	osci_timer_reconfigure_for_measuring(csm);
	osci_adc_reconfigure_for_measuring(csm);
	osci_dma_channel_reconfigure_for_measuring(csm);
	osci_callback_set_measuring_callbacks(csm);
	csm->measurements_left = NUM_SAMPLES;
	osci_timer_start(csm);

	csm->state = OSCI_CHANNEL_STATE_MEASURING;
}

void clear_csm_events(Osci_ChannelStateMachine* csm){
	csm->events.measurement_complete = FALSE;
	csm->events.shutdown = FALSE;
	csm->events.start_measuring = FALSE;
	csm->events.start_monitoring = FALSE;
}

void osci_channel_update(Osci_ChannelStateMachine* csm){
	switch(csm->state){
		case OSCI_CHANNEL_STATE_SHUTDOWN:
			// Clear invalid event flags
			csm->events.measurement_complete = FALSE;
			csm->events.shutdown = FALSE;
			csm->events.start_measuring = FALSE;

			// Check for transitions
			if(csm->events.start_monitoring) {
				osci_channel_start_monitoring(csm);
				csm->state = OSCI_CHANNEL_STATE_MONITORING;
				csm->events.start_monitoring = FALSE;
			}
			break;
		case OSCI_CHANNEL_STATE_MONITORING:
			// Clear invalid event flags
			csm->events.measurement_complete = FALSE;
			csm->events.start_monitoring = FALSE;

			// Check for transitions
			if(csm->events.shutdown) {
				osci_channel_shutdown(csm);
				csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
				csm->events.shutdown = FALSE;
				return;
			}
			if(csm->events.start_measuring) {
				osci_channel_start_measuring(csm);
				csm->state = OSCI_CHANNEL_STATE_MEASURING;
				csm->events.start_measuring = FALSE;
				return;
			}
			break;
		case OSCI_CHANNEL_STATE_MEASURING:
			// Clear invalid event flags
			csm->events.start_measuring = FALSE;
			csm->events.start_monitoring = FALSE;

			// Check for transitions
			if(csm->events.shutdown) {
				osci_channel_shutdown(csm);
				csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
				return;
			}
			if(csm->events.measurement_complete) {
				osci_channel_shutdown(csm);
				csm->state = OSCI_CHANNEL_STATE_SHUTDOWN;
				csm->transceiver->events.send_requested = TRUE;
				csm->events.measurement_complete = FALSE;
				return;
			}
			break;
	}
}

void osci_channel_monitoring_callback_x(Osci_Application* app){
	// Only applies in monitoring state
	if (app->xChannelStateMachine.state != OSCI_CHANNEL_STATE_MONITORING){
		return;
	}

	LL_ADC_REG_StartConversion(app->xChannelStateMachine.adc);
}

void osci_channel_measuring_callback_x(Osci_Application* app){
	// Only applies in measuring state
	if (app->xChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING){
		return;
	}

	if(app->xChannelStateMachine.measurements_left > 0){
		LL_ADC_REG_StartConversion(app->xChannelStateMachine.adc);
		app->xChannelStateMachine.measurements_left--;
	}else{
		//osci_timer_stop(&app->xChannelStateMachine);
	}
}

void osci_channel_measurement_complete_callback_x(Osci_Application* app){
	// Only applies in measuring state
	if (app->xChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING){
		return;
	}

	app->xChannelStateMachine.measurement = app->xChannelStateMachine.measurementDMABuffer;
	app->xChannelStateMachine.events.measurement_complete = TRUE;
}

void osci_channel_monitoring_callback_y(Osci_Application* app){
	// Only applies in monitoring state
	if (app->yChannelStateMachine.state != OSCI_CHANNEL_STATE_MONITORING){
		return;
	}

	LL_ADC_REG_StartConversion(app->yChannelStateMachine.adc);
}

void osci_channel_measuring_callback_y(Osci_Application* app){
	// Only applies in measuring state
	if (app->yChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING){
		return;
	}

	if(app->yChannelStateMachine.measurements_left > 0){
		LL_ADC_REG_StartConversion(app->yChannelStateMachine.adc);
		app->yChannelStateMachine.measurements_left--;
	}else{
		osci_timer_stop(&app->yChannelStateMachine);
	}
}

void osci_channel_measurement_complete_callback_y(Osci_Application* app){
	// Only applies in measuring state
	if (app->yChannelStateMachine.state != OSCI_CHANNEL_STATE_MEASURING){
		return;
	}

	app->yChannelStateMachine.measurement = app->yChannelStateMachine.measurementDMABuffer;
	app->yChannelStateMachine.events.measurement_complete = TRUE;
}

void osci_channel_awd_threshold_callback_x(Osci_Application* app){
	LL_ADC_DisableIT_AWD1(app->xChannelStateMachine.adc);
	//osci_timer_stop(&app->xChannelStateMachine);
	app->xChannelStateMachine.events.start_measuring = TRUE;
}

void osci_channel_awd_threshold_callback_y(Osci_Application* app){
	LL_ADC_DisableIT_AWD2(app->yChannelStateMachine.adc);
	//osci_timer_stop(&app->yChannelStateMachine);
	app->yChannelStateMachine.events.start_measuring = TRUE;
}
