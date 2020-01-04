/*
 * osci_receiver.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_TRANSCEIVER_H_
#define INC_OSCI_TRANSCEIVER_H_

#include "main.h"
#include "osci_data_structures.h"
#include "osci_transform.h"
#include "osci_configurator.h"

#define OSCI_TRANSCEIVER_STATE_IDLE 0
#define OSCI_TRANSCEIVER_STATE_SHUTTING_DOWN_CHANNELS 1
#define OSCI_TRANSCEIVER_STATE_WAITING_FOR_CHANNELS_TO_SHUTDOWN 2
#define OSCI_TRANSCEIVER_STATE_RECONFIGURING_CHANNELS 3
#define OSCI_TRANSCEIVER_STATE_STARTING_CHANNELS 4
#define OSCI_TRANSCEIVER_STATE_GATHERING_TRANSFORMING_AND_SENDING 5

void OSCI_transceiver_init(Osci_Transceiver*, USART_TypeDef*, DMA_TypeDef*, uint32_t, uint32_t, Osci_ChannelStateMachine*, Osci_ChannelStateMachine*, TIM_TypeDef*);
void OSCI_transceiver_update(Osci_Transceiver*);
void OSCTI_update_timer_callback(Osci_Application*);

ADC_callback osci_transceiver_received_callback;
ADC_callback osci_transceiver_sent_callback;

#endif /* INC_OSCI_TRANSCEIVER_H_ */
