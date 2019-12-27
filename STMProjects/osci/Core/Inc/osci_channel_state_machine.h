/*
 * osci_channel_state_machine.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_CHANNEL_STATE_MACHINE_H_
#define INC_OSCI_CHANNEL_STATE_MACHINE_H_

#include "main.h"
#include "osci_defines.h"
#include "osci_data_structures.h"
#include "osci_channel_state_machine.h"
#include "osci_timer.h"
#include "osci_dma.h"
#include "osci_adc.h"
#include "osci_callback.h"
#include "osci_configurator.h"

// Define possible states of the state machine.
#define OSCI_CHANNEL_STATE_SHUTDOWN 0
#define OSCI_CHANNEL_STATE_MONITORING 1
#define OSCI_CHANNEL_STATE_MEASURING 2
#define OSCI_CHANNEL_STATE_SHUTDING_DOWN 3

// State machine functions and call-backs.
void osci_channel_init(Osci_ChannelStateMachine* , TIM_TypeDef* , DMA_TypeDef* , uint32_t , ADC_TypeDef* , uint32_t , void (*) (Osci_Application*), void (*) (Osci_Application*), void (*) (Osci_Application*),  void (*) (Osci_Application*),Osci_Transceiver* );
void osci_channel_update(Osci_ChannelStateMachine* csm);
void osci_channel_shutdown(Osci_ChannelStateMachine* csm);
void osci_channel_monitoring_callback_x(Osci_Application*);
void osci_channel_measuring_callback_x(Osci_Application*);
void osci_channel_monitoring_callback_y(Osci_Application*);
void osci_channel_measuring_callback_y(Osci_Application*);
void osci_channel_measurement_complete_callback_x(Osci_Application*);
void osci_channel_measurement_complete_callback_y(Osci_Application*);
void osci_channel_awd_threshold_callback_x(Osci_Application*);
void osci_channel_awd_threshold_callback_y(Osci_Application*);

#endif /* INC_OSCI_CHANNEL_STATE_MACHINE_H_ */