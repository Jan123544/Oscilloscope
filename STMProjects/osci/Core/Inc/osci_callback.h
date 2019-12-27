/*
 * osci_callbacks.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_CALLBACK_H_
#define INC_OSCI_CALLBACK_H_
#include "osci_channel_state_machine.h"

void OSCI_callback_set_monitoring_callbacks(Osci_ChannelStateMachine*);
void OSCI_callback_set_measuring_callbacks(Osci_ChannelStateMachine*);

#endif /* INC_OSCI_CALLBACK_H_ */
