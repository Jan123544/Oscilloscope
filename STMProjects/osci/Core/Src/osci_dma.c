/*
 * osci_dma.c
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#include "osci_dma.h"

void OSCI_dma_channel_init(Osci_ChannelStateMachine* csm)
{
	LL_DMA_SetPeriphAddress(csm->dma, csm->dmaChannel, LL_ADC_DMA_GetRegAddr(csm->adc, LL_ADC_DMA_REG_REGULAR_DATA));
	LL_DMA_EnableIT_TE(csm->dma, csm->dmaChannel);
	LL_DMA_EnableIT_TC(csm->dma, csm->dmaChannel);
}

void OSCI_dma_channel_reconfigure_for_measuring(Osci_ChannelStateMachine* csm)
{
	if(LL_DMA_IsEnabledChannel(csm->dma, csm->dmaChannel))
		OSCI_error_loop("dma enabled when reconfiguring for measurement");

	LL_DMA_SetDataLength(csm->dma, csm->dmaChannel, NUM_SAMPLES);
	LL_DMA_SetMemoryAddress(csm->dma, csm->dmaChannel, (uint32_t)&csm->measurementDMABuffer);
	LL_DMA_EnableChannel(csm->dma, csm->dmaChannel);
}

void OSCI_dma_channel_reconfigure_for_monitoring(Osci_ChannelStateMachine* csm)
{
	LL_DMA_DisableChannel(csm->dma, csm->dmaChannel);
}

void OSCI_dma_set_TC_callback(Osci_ChannelStateMachine* csm, Measurement_complete_callback measurement_complete_callback)
{
	switch(csm->dmaChannel)
	{
		case LL_DMA_CHANNEL_1:
			osci_dma_ch1_TC_callback = measurement_complete_callback;
			break;
		case LL_DMA_CHANNEL_2:
			osci_dma_ch2_TC_callback = measurement_complete_callback;
			break;
	}
}
