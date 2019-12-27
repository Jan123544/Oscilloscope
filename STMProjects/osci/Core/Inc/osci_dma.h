/*
 * osci_dma.h
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#ifndef INC_OSCI_DMA_H_
#define INC_OSCI_DMA_H_

#include "osci_defines.h"
#include "osci_data_structures.h"
#include "osci_error.h"

void osci_dma_channel_init(Osci_ChannelStateMachine*);
void osci_dma_channel_reconfigure_for_measuring(Osci_ChannelStateMachine*);
void osci_dma_channel_reconfigure_for_monitoring(Osci_ChannelStateMachine*);


void (*osci_dma_ch1_TC_callback) (Osci_Application*);
void (*osci_dma_ch2_TC_callback) (Osci_Application*);
void osci_dma_set_TC_callback(Osci_ChannelStateMachine*);

#endif /* INC_OSCI_DMA_H_ */