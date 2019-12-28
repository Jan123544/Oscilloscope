/*
 * osci_adc.c
 *
 *  Created on: 24. 12. 2019
 *      Author: dot
 */

#include "osci_channel_state_machine.h"
#include "osci_adc.h"

void OSCI_adc_init(Osci_ChannelStateMachine* csm)
{
	LL_ADC_Enable(csm->adc);
}

void OSCI_adc_stop(Osci_ChannelStateMachine* csm)
{
	LL_ADC_REG_StopConversion(csm->adc);

	while (LL_ADC_REG_IsConversionOngoing(csm->adc));
}

void OSCI_adc_reconfigure_for_monitoring(Osci_ChannelStateMachine* csm)
{
	csm->adc->CFGR &= ~0x1; // Disable DMA requests

	LL_ADC_DisableIT_EOS(csm->adc);
	LL_ADC_DisableIT_EOC(csm->adc);
	LL_ADC_DisableIT_EOSMP(csm->adc);

	switch(csm->awd)
	{
		case LL_ADC_AWD1:
		{
			LL_ADC_SetAnalogWDMonitChannels(csm->adc, csm->awd, LL_ADC_AWD_ALL_CHANNELS_REG);
			LL_ADC_SetAnalogWDThresholds(csm->adc, csm->awd, LL_ADC_AWD_THRESHOLD_HIGH, csm->params.triggerLevel);
			LL_ADC_ClearFlag_AWD1(csm->adc);
			LL_ADC_EnableIT_AWD1(csm->adc);
			break;
		}
		case LL_ADC_AWD2:
		{
			// AWD2 and AWD3 only 8 bit resolution that's why /16
			csm->adc->AWD2CR |= 0x2; // Enable monitoring of channel 1 of adc
			LL_ADC_SetAnalogWDThresholds(csm->adc, csm->awd, LL_ADC_AWD_THRESHOLD_HIGH, floor(csm->params.triggerLevel/16.0f));
			LL_ADC_ClearFlag_AWD2(csm->adc);
			LL_ADC_EnableIT_AWD2(csm->adc);
			break;
		}
	}
}

void OSCI_adc_reconfigure_for_measuring(Osci_ChannelStateMachine* csm)
{
	csm->adc->CFGR |= 0x1; // Enable DMA requests
	/*switch(csm->awd){
		case LL_ADC_AWD1:
			LL_ADC_DisableIT_AWD1(csm->adc);
			break;
		case LL_ADC_AWD2:
			LL_ADC_DisableIT_AWD2(csm->adc);
			break;
	}*/
}

void OSCI_adc_set_awd_callback(Osci_ChannelStateMachine* csm)
{
	switch(csm->awd)
	{
		case LL_ADC_AWD1:
			osci_adc_awd1_callback = csm->awd_threshold_callback;
			break;
		case LL_ADC_AWD2:
			osci_adc_awd2_callback = csm->awd_threshold_callback;
			break;
	}
}
