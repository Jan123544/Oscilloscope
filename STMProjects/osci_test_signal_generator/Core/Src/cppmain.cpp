/*
 * cppmain.cpp
 *
 *  Created on: Dec 27, 2019
 *      Author: dot
 */


#include "cppmain.h"
#include "main.h"
#include "SignalAdjuster.h"

void usart_init(){
	LL_USART_EnableIT_RXNE(USART2);
}

void dac_init(){
	LL_DAC_Enable(DAC, LL_DAC_CHANNEL_1);
}

void cppmain(){
	usart_init();
	dac_init();

	SignalAdjuster adj;
	adj_IT_reference = &adj;

	while(1){

	}
}

